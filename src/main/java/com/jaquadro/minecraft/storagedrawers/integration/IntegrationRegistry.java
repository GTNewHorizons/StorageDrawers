package com.jaquadro.minecraft.storagedrawers.integration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;

public final class IntegrationRegistry {

    private static IntegrationRegistry INSTANCE;
    private final List<IntegrationModule> registry = new ArrayList<>();
    private final Set<String> loadedMods = new HashSet<>();

    public static IntegrationRegistry instance() {
        if (INSTANCE == null) INSTANCE = new IntegrationRegistry();
        return INSTANCE;
    }

    private IntegrationRegistry() {
        if (Loader.isModLoaded("appliedenergistics2")) this.register(new AppliedEnergistics());
        if (Loader.isModLoaded("Waila")) this.register(new Waila());
        if (Loader.isModLoaded("Thaumcraft")) this.register(new Thaumcraft());
        if (Loader.isModLoaded("MineTweaker3")) this.register(new MineTweaker());
        if (Loader.isModLoaded("RefinedRelocation")) this.register(new RefinedRelocation());
        if (Loader.isModLoaded("NotEnoughItems")) this.register(new NotEnoughItems());
        if (Loader.isModLoaded("ThermalExpansion")) this.register(new ThermalExpansion());
        if (Loader.isModLoaded("ThermalFoundation")) this.register(new ThermalFoundation());
        if (Loader.isModLoaded("chisel")) this.register(new ChiselIntegrationModule());
        if (Loader.isModLoaded("dreamcraft")) this.register(new GTNHIntegrationModule());
        if (Loader.isModLoaded("backhand")) this.register(new BackhandIntegrationModule());
    }

    private void register(IntegrationModule module) {
        if (module.shouldLoadModule()) {
            registry.add(module);
        }
    }

    public void init() {
        for (int i = 0; i < registry.size(); i++) {
            IntegrationModule module = registry.get(i);
            try {
                module.init();
                loadedMods.add(module.getModID());
            } catch (Throwable t) {
                registry.remove(i--);
                FMLLog.log(
                        StorageDrawers.MOD_ID,
                        Level.INFO,
                        "Could not load integration module: " + module.getClass().getName());
            }
        }
    }

    public void postInit() {
        for (IntegrationModule module : registry) {
            module.postInit();
        }
    }

    public boolean isModuleLoaded(String modId) {
        return this.loadedMods.contains(modId);
    }
}
