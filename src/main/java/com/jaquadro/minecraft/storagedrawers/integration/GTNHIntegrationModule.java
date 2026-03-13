package com.jaquadro.minecraft.storagedrawers.integration;

import javax.annotation.Nonnull;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.gtnh.ModRecipes;

public final class GTNHIntegrationModule extends IntegrationModule {

    @Nonnull
    @Override
    public String getModID() {
        return "dreamcraft";
    }

    @Override
    protected boolean moduleConfig() {
        return StorageDrawers.config.integrationConfig.isGTNHEnabled();
    }

    @Override
    public void init() throws Throwable {
        ModRecipes.init();
    }
}
