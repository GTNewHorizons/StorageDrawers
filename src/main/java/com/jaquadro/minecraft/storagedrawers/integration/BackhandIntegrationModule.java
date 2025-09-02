package com.jaquadro.minecraft.storagedrawers.integration;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;

import cpw.mods.fml.common.Loader;

public class BackhandIntegrationModule extends IntegrationModule {

    private static final boolean BACKHAND_ENABLED = Loader.isModLoaded("backhand")
            && StorageDrawers.config.integrationConfig.isBackhandEnabled();

    @Override
    public String getModID() {
        return "backhand";
    }

    public static boolean isEnabled() {
        return BACKHAND_ENABLED;
    }

    @Override
    public void init() throws Throwable {

    }

    @Override
    public void postInit() {

    }
}
