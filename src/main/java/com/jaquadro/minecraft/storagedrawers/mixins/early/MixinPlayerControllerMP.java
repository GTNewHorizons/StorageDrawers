package com.jaquadro.minecraft.storagedrawers.mixins.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.MovingObjectPosition;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
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

    private boolean isHoldingClick = false;

    @ModifyExpressionValue(
            method = "onPlayerDamageBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)F"))
    private float storageDrawers$checkIfClickingDrawer(float originalHardness, int x, int y, int z, int clickedSide) {
        final var thisTile = mc.theWorld.getTileEntity(x, y, z);
        if (!(thisTile instanceof TileEntityDrawers drawer) || drawer.getDirection() != clickedSide) {
            return originalHardness;
        }

        MovingObjectPosition mop = mc.objectMouseOver;
        float hitX = (float) (mop.hitVec.xCoord - mop.blockX);
        float hitY = (float) (mop.hitVec.yCoord - mop.blockY);
        float hitZ = (float) (mop.hitVec.zCoord - mop.blockZ);
        boolean invertShift = StorageDrawers.config.cache.invertShift;
        boolean isHolding = this.isHoldingClick;

        this.isHoldingClick = true;
        StorageDrawers.network
                .sendToServer(new BlockClickMessage(x, y, z, clickedSide, hitX, hitY, hitZ, invertShift, isHolding));

        // Set curBlockDamageMP to 0 by cancelling itself out
        return -curBlockDamageMP;
    }

    @Inject(method = "resetBlockRemoving", at = @At(value = "HEAD"))
    private void storageDrawers$resetHoldingClick(CallbackInfo ci) {
        this.isHoldingClick = false;
    }
}
