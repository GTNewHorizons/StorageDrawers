package com.jaquadro.minecraft.storagedrawers.integration;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWood;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockDrawersPack;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockSortingDrawersPack;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockSortingTrimPack;
import com.jaquadro.minecraft.storagedrawers.block.pack.BlockTrimPack;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

public class ChiselIntegrationModule extends IntegrationModule {

    private static final String chiselModID = "chisel";
    private static final String addVariation = "variation:add";

    private static final boolean chiselEnabled = (Loader.isModLoaded(chiselModID)
            && StorageDrawers.config.integrationConfig.isChiselEnabled());

    public static boolean isEnabled() {
        return chiselEnabled;
    }

    public static void registerPackBlock(Block block) {
        if (!chiselEnabled) return;

        final String blockGroupName;
        final IPackDataResolver resolver;

        if (block instanceof BlockDrawersPack) {
            resolver = ((BlockDrawersPack) block).getDataResolver();
            if (((BlockDrawersPack) block).halfDepth) {
                blockGroupName = "basicHalf" + ((BlockDrawersPack) block).drawerCount;
            } else {
                blockGroupName = "basicFull" + ((BlockDrawersPack) block).drawerCount;
            }
        } else if (block instanceof BlockSortingDrawersPack) {
            resolver = ((BlockSortingDrawersPack) block).getDataResolver();
            if (((BlockSortingDrawersPack) block).halfDepth) {
                blockGroupName = "sortingHalf" + ((BlockSortingDrawersPack) block).drawerCount;
            } else {
                blockGroupName = "sortingFull" + ((BlockSortingDrawersPack) block).drawerCount;
            }
        } else {
            blockGroupName = "trim";
            if (block instanceof BlockTrimPack) {
                resolver = ((BlockTrimPack) block).getDataResolver();
            } else {
                if (block instanceof BlockSortingTrimPack) {
                    resolver = ((BlockSortingTrimPack) block).getDataResolver();
                } else return;
            }
        }

        // If block is not enabled its resolver is null
        if (resolver == null) return;

        for (int meta = 0; meta <= 16; meta++) {
            if (!resolver.isValidMetaValue(meta)) continue;

            registerBlock(block, meta, blockGroupName);
        }
    }

    public static void registerBlock(final Block block, final int meta, final String blockGroupName) {
        FMLInterModComms.sendMessage(
                chiselModID,
                addVariation,
                String.join(
                        "|",
                        "StorageDrawers_" + blockGroupName,
                        GameRegistry.findUniqueIdentifierFor(block).toString(),
                        Integer.toString(meta)));
    }

    @Override
    public String getModID() {
        return chiselModID;
    }

    @Override
    public void init() throws Throwable {
        final ConfigManager config = StorageDrawers.config;

        for (int meta = 0; meta < BlockWood.field_150096_a.length; meta++) {
            if (config.isBlockEnabled("fulldrawers1")) registerBlock(ModBlocks.fullDrawers1, meta, "basicFull1");
            if (config.isBlockEnabled("fulldrawers2")) registerBlock(ModBlocks.fullDrawers2, meta, "basicFull2");
            if (config.isBlockEnabled("fulldrawers4")) registerBlock(ModBlocks.fullDrawers4, meta, "basicFull4");
            if (config.isBlockEnabled("halfdrawers2")) registerBlock(ModBlocks.halfDrawers2, meta, "basicHalf2");
            if (config.isBlockEnabled("halfdrawers4")) registerBlock(ModBlocks.halfDrawers4, meta, "basicHalf4");
            if (config.isBlockEnabled("trim")) registerBlock(ModBlocks.trim, meta, "trim");
        }
    }

    @Override
    public void postInit() {
        /* nothing */
    }
}
