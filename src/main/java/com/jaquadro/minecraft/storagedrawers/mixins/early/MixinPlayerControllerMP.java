package com.jaquadro.minecraft.storagedrawers.mixins.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.network.BlockClickMessage;

@Mixin(value = PlayerControllerMP.class, priority = 10000)
public abstract class MixinPlayerControllerMP {

    @Final
    @Shadow
    private Minecraft mc;

    @Inject(
            method = "onPlayerDamageBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)F"))
    private void StorageDrawers$checkIfClickingDrawer(int x, int y, int z, int side, CallbackInfo ci) {
        if (mc.theWorld.getTileEntity(x, y, z) instanceof TileEntityDrawers ted) {
            if (ted.getDirection() == side) {
                final int reach = 5;
                double eyeX = mc.thePlayer.posX;
                double eyeY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
                double eyeZ = mc.thePlayer.posZ;

                Vec3 look = mc.thePlayer.getLookVec();

                Vec3 end = Vec3.createVectorHelper(
                        eyeX + look.xCoord * reach,
                        eyeY + look.yCoord * reach,
                        eyeZ + look.zCoord * reach);

                MovingObjectPosition mop = mc.theWorld.rayTraceBlocks(Vec3.createVectorHelper(eyeX, eyeY, eyeZ), end);
                float hitX = (float) (mop.hitVec.xCoord - mop.blockX);
                float hitY = (float) (mop.hitVec.yCoord - mop.blockY);
                float hitZ = (float) (mop.hitVec.zCoord - mop.blockZ);
                boolean invertShift = StorageDrawers.config.cache.invertShift;
                StorageDrawers.network
                        .sendToServer(new BlockClickMessage(x, y, z, side, hitX, hitY, hitZ, invertShift));
            }
        }
    }
}
