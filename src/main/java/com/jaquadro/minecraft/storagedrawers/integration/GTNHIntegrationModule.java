package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.gtnh.ModRecipes;

import cpw.mods.fml.common.Loader;

public class GTNHIntegrationModule extends IntegrationModule {

    private static final boolean GTNHEnabled = (Loader.isModLoaded("dreamcraft")
            && StorageDrawers.config.integrationConfig.isGTNHEnabled());

    public static boolean isEnabled() {
        return GTNHEnabled;
    }

    @Override
    public String getModID() {
        return "dreamcraft";
    }

    @Override
    public void init() throws Throwable {
        ModRecipes.init();
    }

    @Override
    public void postInit() {}
}
