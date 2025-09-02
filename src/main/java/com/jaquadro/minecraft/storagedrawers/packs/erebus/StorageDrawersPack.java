package com.jaquadro.minecraft.storagedrawers.packs.erebus;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.core.DataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.packs.erebus.core.RefinedRelocation;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = StorageDrawersPack.MOD_ID,
        name = StorageDrawersPack.MOD_NAME,
        version = StorageDrawers.MOD_VERSION,
        dependencies = "required-after:StorageDrawers;")
public class StorageDrawersPack {

    public static final String MOD_ID = "StorageDrawersErebus";
    public static final String MOD_NAME = "Storage Drawers: Erebus Pack";
    public static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.packs.erebus.";

    public DataResolver resolver = new DataResolver(MOD_ID);

    public ModBlocks blocks = new ModBlocks();

    @Mod.Instance(MOD_ID)
    public static StorageDrawersPack instance;

    @SidedProxy(clientSide = SOURCE_PATH + "CommonProxy", serverSide = SOURCE_PATH + "CommonProxy")
    public static CommonProxy proxy;

    public static boolean LOAD = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (StorageDrawers.config.userConfig.packsConfig().autoEnablePacks()) {
            if (!Loader.isModLoaded("erebus")) {
                LOAD = false;
            }
        } else if (!StorageDrawers.config.userConfig.packsConfig().isErebusPackEnabled()) {
            LOAD = false;
        }

        if (!LOAD) return;

        blocks.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (!LOAD) return;

        RefinedRelocation.init();
        resolver.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (!LOAD) return;

        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api != null) {
            api.registerStandardPackRecipes(resolver);
            api.packFactory().registerResolver(resolver);
        }
    }
}
