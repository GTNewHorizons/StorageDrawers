package com.jaquadro.minecraft.storagedrawers.packs.misc;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.IStorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.StorageDrawersApi;
import com.jaquadro.minecraft.storagedrawers.api.pack.IExtendedDataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.misc.core.DataResolver;
import com.jaquadro.minecraft.storagedrawers.packs.misc.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.packs.misc.core.RefinedRelocation;

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

    public static final String MOD_ID = "StorageDrawersMisc";
    public static final String MOD_NAME = "Storage Drawers: Misc Pack";
    public static final String SOURCE_PATH = "com.jaquadro.minecraft.storagedrawers.packs.misc.";

    public DataResolver[] resolvers = new DataResolver[] { new DataResolver(MOD_ID, 0), new DataResolver(MOD_ID, 1) };

    public ModBlocks blocks = new ModBlocks();

    @Mod.Instance(MOD_ID)
    public static StorageDrawersPack instance;

    @SidedProxy(clientSide = SOURCE_PATH + "CommonProxy", serverSide = SOURCE_PATH + "CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (!StorageDrawers.config.userConfig.packsConfig().isMiscPackEnabled()) {
            return;
        }

        blocks.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (!StorageDrawers.config.userConfig.packsConfig().isMiscPackEnabled()) {
            return;
        }

        RefinedRelocation.init();
        for (DataResolver resolver : resolvers) resolver.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (!StorageDrawers.config.userConfig.packsConfig().isMiscPackEnabled()) {
            return;
        }

        IStorageDrawersApi api = StorageDrawersApi.instance();
        if (api != null) {
            for (IExtendedDataResolver resolver : resolvers) {
                api.registerStandardPackRecipes(resolver);
                api.packFactory().registerResolver(resolver);
            }
        }
    }
}
