package com.jaquadro.minecraft.storagedrawers.mixins.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

@Mixin(PlayerControllerMP.class)
public abstract class MixinPlayerControllerMP {

    @Shadow
    private float curBlockDamageMP;

    @ModifyExpressionValue(
            method = "clickBlock",
            at = @At(value = "INVOKE", target = "Ltarget/Class;curBlockDamageMPZ"))

    private boolean withClickOnDrawer(boolean original, @Local Minecraft mc, @Local int x, @Local int y, @Local int z,
            @Local int side) {
        if (mc.theWorld.getTileEntity(x, y, z) instanceof TileEntityDrawers) {
            TileEntityDrawers ted = (TileEntityDrawers) mc.theWorld.getTileEntity(x, y, z);
            if (ted.getDirection() == side) {
                curBlockDamageMP = 0.0F;
                return true;
            }
        }
        return original;
    }
}
