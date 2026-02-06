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

    @Shadow
    public float curBlockDamageMP;

    @Inject(
            method = "onPlayerDamageBlock",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;curBlockDamageMP:F",
                    opcode = org.objectweb.asm.Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER))
    private void storageDrawers$checkIfClickingDrawer(int x, int y, int z, int clickedSide, CallbackInfo ci) {
        final var thisTile = mc.theWorld.getTileEntity(x, y, z);
        if (!(thisTile instanceof TileEntityDrawers drawer)) return;
        if (drawer.getDirection() != clickedSide) return;

        final int reach = 5;
        double eyeX = mc.thePlayer.posX;
        double eyeY = mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double eyeZ = mc.thePlayer.posZ;

        Vec3 lookVec = mc.thePlayer.getLookVec();

        Vec3 end = Vec3.createVectorHelper(
                eyeX + lookVec.xCoord * reach,
                eyeY + lookVec.yCoord * reach,
                eyeZ + lookVec.zCoord * reach);

        MovingObjectPosition mop = mc.theWorld.rayTraceBlocks(Vec3.createVectorHelper(eyeX, eyeY, eyeZ), end);
        float hitX = (float) (mop.hitVec.xCoord - mop.blockX);
        float hitY = (float) (mop.hitVec.yCoord - mop.blockY);
        float hitZ = (float) (mop.hitVec.zCoord - mop.blockZ);
        boolean invertShift = StorageDrawers.config.cache.invertShift;
        curBlockDamageMP = 0.0F;
        StorageDrawers.network.sendToServer(new BlockClickMessage(x, y, z, clickedSide, hitX, hitY, hitZ, invertShift));
    }
}
