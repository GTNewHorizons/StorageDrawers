package com.jaquadro.minecraft.storagedrawers.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(value = PlayerControllerMP.class, priority = 10000)
public abstract class MixinPlayerControllerMP {

    @Final
    @Shadow
    private Minecraft mc;

    @Shadow
    public float curBlockDamageMP;

    @Unique
    private boolean storageDrawers$isHoldingClick = false;

    @ModifyExpressionValue(
            method = { "clickBlock", "onPlayerDamageBlock" },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)F"))
    private float storageDrawers$checkIfClickingDrawer(float originalHardness, int x, int y, int z, int clickedSide) {
        final TileEntity thisTile = mc.theWorld.getTileEntity(x, y, z);
        final Block thisBlock = mc.theWorld.getBlock(x, y, z);
        if (!(thisBlock instanceof BlockDrawers blockDrawer) || !(thisTile instanceof TileEntityDrawers drawer)
                || drawer.getDirection() != clickedSide) {
            return originalHardness;
        }

        MovingObjectPosition mop = mc.objectMouseOver;
        float hitX = (float) (mop.hitVec.xCoord - mop.blockX);
        float hitY = (float) (mop.hitVec.yCoord - mop.blockY);
        float hitZ = (float) (mop.hitVec.zCoord - mop.blockZ);
        IDrawer clickedDrawer = drawer
                .getDrawer(blockDrawer.getDrawerSlot(clickedSide, drawer.getRotation(), hitX, hitY, hitZ));
        if (clickedDrawer == null || clickedDrawer.isEmpty()) {
            return originalHardness;
        }
        boolean invertShift = StorageDrawers.config.cache.invertShift;
        boolean isHolding = this.storageDrawers$isHoldingClick;

        this.storageDrawers$isHoldingClick = true;
        StorageDrawers.network
                .sendToServer(new BlockClickMessage(x, y, z, clickedSide, hitX, hitY, hitZ, invertShift, isHolding));

        // Set curBlockDamageMP to 0 by cancelling itself out
        return -curBlockDamageMP;
    }

    @Inject(method = "resetBlockRemoving", at = @At(value = "HEAD"))
    private void storageDrawers$resetHoldingClick(CallbackInfo ci) {
        this.storageDrawers$isHoldingClick = false;
    }

    @Inject(method = "clickBlock", at = @At(value = "HEAD"), cancellable = true)
    private void storageDrawers$creativeClickDrawer(int x, int y, int z, int clickedSide, CallbackInfo ci) {
        if (mc.thePlayer == null || !mc.thePlayer.capabilities.isCreativeMode) return;

        final TileEntity thisTile = mc.theWorld.getTileEntity(x, y, z);
        final Block thisBlock = mc.theWorld.getBlock(x, y, z);
        if (!(thisBlock instanceof BlockDrawers blockDrawer) || !(thisTile instanceof TileEntityDrawers drawer)
                || drawer.getDirection() != clickedSide) {
            return;
        }

        // Creative left-click would instantly destroy the block; intercept clicks on the drawer front so the gesture
        // takes items (like survival) instead. The break itself is already prevented by ForgeEventHandler, so an empty
        // slot simply does nothing here.
        MovingObjectPosition mop = mc.objectMouseOver;
        float hitX = (float) (mop.hitVec.xCoord - mop.blockX);
        float hitY = (float) (mop.hitVec.yCoord - mop.blockY);
        float hitZ = (float) (mop.hitVec.zCoord - mop.blockZ);

        IDrawer clickedDrawer = drawer
                .getDrawer(blockDrawer.getDrawerSlot(clickedSide, drawer.getRotation(), hitX, hitY, hitZ));
        if (clickedDrawer == null || clickedDrawer.isEmpty()) {
            ci.cancel();
            return;
        }

        boolean invertShift = StorageDrawers.config.cache.invertShift;
        StorageDrawers.network
                .sendToServer(new BlockClickMessage(x, y, z, clickedSide, hitX, hitY, hitZ, invertShift, false));

        ci.cancel();
    }
}
