package com.jaquadro.minecraft.storagedrawers.mixins.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

    @Shadow
    private Minecraft mc;

    @Inject(
            method = "onPlayerDamageBlock",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/block/Block;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;III)F"))
    private void mixin(int x, int y, int z, int side, CallbackInfo ci) {
        if (mc.theWorld.getTileEntity(x, y, z) instanceof TileEntityDrawers ted
                && mc.theWorld.getBlock(x, y, z) instanceof BlockDrawers bd) {
            if (ted.getDirection() == side) {
                ted.onClick(bd, mc.thePlayer, x, y, z, side, mc.theWorld);
            }
        }
    }
}
