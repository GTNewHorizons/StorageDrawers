package com.jaquadro.minecraft.storagedrawers.packs.forestry.core;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.ExtendedDataResolver;

import cpw.mods.fml.common.registry.GameRegistry;

public class DataResolver extends ExtendedDataResolver {

    public static String[][] textureBank = new String[][] {
            new String[] { "larch", "teak", "acacia", "lime", "chestnut", "wenge", "baobab", "sequoia", "kapok",
                    "ebony", "mahogany", "balsa", "willow", "walnut", "greenheart", "cherry" },
            new String[] { "mahoe", "poplar", "palm", "papaya", "pine", "plum", "maple", "citrus", "giganteum", "ipe",
                    "padauk", "cocobolo", "zebrawood" } };

    private int bank;

    public DataResolver(String modID, int bankNumber) {
        super(modID, textureBank[bankNumber]);
        bank = bankNumber;
    }

    @Override
    public CreativeTabs getCreativeTabs(BlockType type) {
        return ModCreativeTabs.getTabStorageDrawers();
    }

    @Override
    public Block getBlock(BlockConfiguration blockConfig) {
        switch (blockConfig.getBlockType()) {
            case Drawers:
                if (blockConfig.getDrawerCount() == 1 && ModBlocks.fullDrawers1 != null)
                    return ModBlocks.fullDrawers1[bank];
                if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth() && ModBlocks.fullDrawers2 != null)
                    return ModBlocks.fullDrawers2[bank];
                if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth() && ModBlocks.fullDrawers4 != null)
                    return ModBlocks.fullDrawers4[bank];
                if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth() && ModBlocks.halfDrawers2 != null)
                    return ModBlocks.halfDrawers2[bank];
                if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth() && ModBlocks.halfDrawers4 != null)
                    return ModBlocks.halfDrawers4[bank];
                break;
            case DrawersSorting:
                if (blockConfig.getDrawerCount() == 1 && RefinedRelocation.fullDrawers1 != null)
                    return RefinedRelocation.fullDrawers1[bank];
                if (blockConfig.getDrawerCount() == 2 && !blockConfig.isHalfDepth()
                        && RefinedRelocation.fullDrawers2 != null)
                    return RefinedRelocation.fullDrawers2[bank];
                if (blockConfig.getDrawerCount() == 4 && !blockConfig.isHalfDepth()
                        && RefinedRelocation.fullDrawers4 != null)
                    return RefinedRelocation.fullDrawers4[bank];
                if (blockConfig.getDrawerCount() == 2 && blockConfig.isHalfDepth()
                        && RefinedRelocation.halfDrawers2 != null)
                    return RefinedRelocation.halfDrawers2[bank];
                if (blockConfig.getDrawerCount() == 4 && blockConfig.isHalfDepth()
                        && RefinedRelocation.halfDrawers4 != null)
                    return RefinedRelocation.halfDrawers4[bank];
                break;
            case Trim:
                if (ModBlocks.trim != null) return ModBlocks.trim[bank];
                break;
            case TrimSorting:
                if (RefinedRelocation.trim != null) return RefinedRelocation.trim[bank];
                break;
        }
        return null;
    }

    @Override
    public void init() {
        int startingOffset = 0;

        if (bank == 1) startingOffset = textureBank[0].length;

        for (int meta = 0, plankMeta = startingOffset; meta < textureBank[bank].length; meta++, plankMeta++) {
            setPlankSlab(
                    meta,
                    GameRegistry.findBlock("Forestry", "planks"),
                    plankMeta,
                    GameRegistry.findBlock("Forestry", "slabs"),
                    plankMeta);
        }
    }
}
