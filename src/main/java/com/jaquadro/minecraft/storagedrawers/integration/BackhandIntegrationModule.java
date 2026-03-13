package com.jaquadro.minecraft.storagedrawers.integration;

import javax.annotation.Nonnull;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;

public final class BackhandIntegrationModule extends IntegrationModule {

    @Nonnull
    @Override
    public String getModID() {
        return "backhand";
    }

    @Override
    protected boolean moduleConfig() {
        return StorageDrawers.config.integrationConfig.isBackhandEnabled();
    }
}
