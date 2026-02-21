package com.jaquadro.minecraft.storagedrawers.integration;

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.integration.thermalfoundation.CoFHSecurityProvider;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.registry.GameRegistry;

public final class ThermalFoundation extends IntegrationModule {

    private Method methodPlayerHasAccess;

    @Nonnull
    @Override
    public String getModID() {
        return "ThermalFoundation";
    }

    @Override
    protected boolean moduleConfig() {
        return StorageDrawers.config.cache.enableThermalFoundationIntegration;
    }

    @Override
    public void init() throws Throwable {
        Class<?> classRegistrySocial;
        try {
            classRegistrySocial = Class.forName("cofh.core.RegistrySocial");
        } catch (ClassNotFoundException e) {
            classRegistrySocial = Class.forName("cofh.core.util.SocialRegistry");
        }

        methodPlayerHasAccess = classRegistrySocial
                .getDeclaredMethod("playerHasAccess", String.class, GameProfile.class);
    }

    @Override
    public void postInit() {
        StorageDrawers.securityRegistry.registerProvider(new CoFHSecurityProvider(this));
        if (StorageDrawers.config.cache.enablePersonalUpgrades) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.personalKey, 1, 1),
                            "xxx",
                            "xyx",
                            "xxx",
                            'x',
                            "nuggetSignalum",
                            'y',
                            ModItems.personalKey));
        }
    }

    public boolean playerHasAccess(String playerName, GameProfile owner) {
        try {
            return (Boolean) methodPlayerHasAccess.invoke(null, playerName, owner);
        } catch (Throwable t) {
            return false;
        }
    }
}
