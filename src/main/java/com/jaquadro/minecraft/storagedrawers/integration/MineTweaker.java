package com.jaquadro.minecraft.storagedrawers.integration;

import javax.annotation.Nonnull;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.minetweaker.Compaction;
import com.jaquadro.minecraft.storagedrawers.integration.minetweaker.OreDictionaryBlacklist;
import com.jaquadro.minecraft.storagedrawers.integration.minetweaker.OreDictionaryWhitelist;

import minetweaker.MineTweakerAPI;

public final class MineTweaker extends IntegrationModule {

    @Nonnull
    @Override
    public String getModID() {
        return "MineTweaker3";
    }

    @Override
    protected boolean moduleConfig() {
        return StorageDrawers.config.cache.enableMineTweakerIntegration;
    }

    @Override
    public void init() throws Throwable {
        MineTweakerAPI.registerClass(OreDictionaryBlacklist.class);
        MineTweakerAPI.registerClass(OreDictionaryWhitelist.class);
        MineTweakerAPI.registerClass(Compaction.class);
    }
}
