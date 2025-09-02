package com.jaquadro.minecraft.storagedrawers.integration;

import net.minecraft.block.BlockWood;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.BlockSortingCompDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.BlockSortingDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.BlockSortingTrim;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.ItemSortingCompDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.ItemSortingDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.ItemSortingTrim;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.ItemUpgradeSorting;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.SortingBlockRegistry;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.TileSortingDrawersComp;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.TileSortingDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.TileSortingTrim;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RefinedRelocation extends IntegrationModule {

    public static BlockSortingDrawers fullDrawers1;
    public static BlockSortingDrawers fullDrawers2;
    public static BlockSortingDrawers fullDrawers4;
    public static BlockSortingDrawers halfDrawers2;
    public static BlockSortingDrawers halfDrawers4;
    public static BlockSortingCompDrawers compDrawers;
    public static BlockSortingTrim trim;

    public static ItemUpgradeSorting upgradeSorting;

    public static final CreativeTabs tabStorageDrawers = new CreativeTabs("storageDrawersSorting") {

        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return getTabItem();
        }
    };

    @Override
    public String getModID() {
        return "RefinedRelocation";
    }

    @Override
    protected String versionPattern() {
        return "[1.0.8b,)";
    }

    @Override
    public void init() throws Throwable {
        fullDrawers1 = new BlockSortingDrawers("fullDrawers1", 1, false);
        fullDrawers2 = new BlockSortingDrawers("fullDrawers2", 2, false);
        fullDrawers4 = new BlockSortingDrawers("fullDrawers4", 4, false);
        halfDrawers2 = new BlockSortingDrawers("halfDrawers2", 2, true);
        halfDrawers4 = new BlockSortingDrawers("halfDrawers4", 4, true);
        compDrawers = new BlockSortingCompDrawers("compDrawers");
        trim = new BlockSortingTrim("trim");

        upgradeSorting = new ItemUpgradeSorting(ModItems.makeName("upgradeSorting"));

        SortingBlockRegistry.register(ModBlocks.fullDrawers1, fullDrawers1);
        SortingBlockRegistry.register(ModBlocks.fullDrawers2, fullDrawers2);
        SortingBlockRegistry.register(ModBlocks.fullDrawers4, fullDrawers4);
        SortingBlockRegistry.register(ModBlocks.halfDrawers2, halfDrawers2);
        SortingBlockRegistry.register(ModBlocks.halfDrawers4, halfDrawers4);
        SortingBlockRegistry.register(ModBlocks.compDrawers, compDrawers);
        SortingBlockRegistry.register(ModBlocks.trim, trim);

        ConfigManager config = StorageDrawers.config;

        if (config.isBlockEnabled("fulldrawers1"))
            GameRegistry.registerBlock(fullDrawers1, ItemSortingDrawers.class, "fullDrawersSort1");
        if (config.isBlockEnabled("fulldrawers2"))
            GameRegistry.registerBlock(fullDrawers2, ItemSortingDrawers.class, "fullDrawersSort2");
        if (config.isBlockEnabled("fulldrawers4"))
            GameRegistry.registerBlock(fullDrawers4, ItemSortingDrawers.class, "fullDrawersSort4");
        if (config.isBlockEnabled("halfdrawers2"))
            GameRegistry.registerBlock(halfDrawers2, ItemSortingDrawers.class, "halfDrawersSort2");
        if (config.isBlockEnabled("halfdrawers4"))
            GameRegistry.registerBlock(halfDrawers4, ItemSortingDrawers.class, "halfDrawersSort4");
        if (config.isBlockEnabled("compdrawers"))
            GameRegistry.registerBlock(compDrawers, ItemSortingCompDrawers.class, "compDrawersSort");
        if (config.isBlockEnabled("trim")) GameRegistry.registerBlock(trim, ItemSortingTrim.class, "trimSort");

        if (config.cache.enableSortingUpgrades) GameRegistry.registerItem(upgradeSorting, "upgradeSorting");

        StorageDrawers.proxy.registerDrawer(fullDrawers1);
        StorageDrawers.proxy.registerDrawer(fullDrawers2);
        StorageDrawers.proxy.registerDrawer(fullDrawers4);
        StorageDrawers.proxy.registerDrawer(halfDrawers2);
        StorageDrawers.proxy.registerDrawer(halfDrawers4);
        StorageDrawers.proxy.registerDrawer(compDrawers);

        GameRegistry.registerTileEntityWithAlternatives(
                TileSortingDrawersStandard.class,
                ModBlocks.getQualifiedName("tileSortingDrawersStandard"),
                ModBlocks.getQualifiedName(fullDrawers1),
                ModBlocks.getQualifiedName(fullDrawers2),
                ModBlocks.getQualifiedName(fullDrawers4),
                ModBlocks.getQualifiedName(halfDrawers2),
                ModBlocks.getQualifiedName(halfDrawers4));

        GameRegistry.registerTileEntityWithAlternatives(
                TileSortingDrawersComp.class,
                ModBlocks.getQualifiedName("tileSortingDrawersComp"),
                ModBlocks.getQualifiedName(compDrawers));

        GameRegistry.registerTileEntityWithAlternatives(
                TileSortingTrim.class,
                ModBlocks.getQualifiedName("tileSortingTrim"),
                ModBlocks.getQualifiedName(trim));
    }

    @Override
    public void postInit() {
        ConfigManager config = StorageDrawers.config;

        for (int i = 0; i < BlockWood.field_150096_a.length; i++) {
            if (config.isBlockEnabled("fulldrawers1")) GameRegistry.addRecipe(
                    new ItemStack(fullDrawers1, 1, i),
                    "x x",
                    " y ",
                    "x x",
                    'x',
                    Items.gold_nugget,
                    'y',
                    new ItemStack(ModBlocks.fullDrawers1, 1, i));
            if (config.isBlockEnabled("fulldrawers2")) GameRegistry.addRecipe(
                    new ItemStack(fullDrawers2, 1, i),
                    "x x",
                    " y ",
                    "x x",
                    'x',
                    Items.gold_nugget,
                    'y',
                    new ItemStack(ModBlocks.fullDrawers2, 1, i));
            if (config.isBlockEnabled("halfdrawers2")) GameRegistry.addRecipe(
                    new ItemStack(halfDrawers2, 1, i),
                    "x x",
                    " y ",
                    "x x",
                    'x',
                    Items.gold_nugget,
                    'y',
                    new ItemStack(ModBlocks.halfDrawers2, 1, i));
            if (config.isBlockEnabled("fulldrawers4")) GameRegistry.addRecipe(
                    new ItemStack(fullDrawers4, 1, i),
                    "x x",
                    " y ",
                    "x x",
                    'x',
                    Items.gold_nugget,
                    'y',
                    new ItemStack(ModBlocks.fullDrawers4, 1, i));
            if (config.isBlockEnabled("halfdrawers4")) GameRegistry.addRecipe(
                    new ItemStack(halfDrawers4, 1, i),
                    "x x",
                    " y ",
                    "x x",
                    'x',
                    Items.gold_nugget,
                    'y',
                    new ItemStack(ModBlocks.halfDrawers4, 1, i));
            if (config.isBlockEnabled("trim")) GameRegistry.addRecipe(
                    new ItemStack(trim, 1, i),
                    "x x",
                    " y ",
                    "x x",
                    'x',
                    Items.gold_nugget,
                    'y',
                    new ItemStack(ModBlocks.trim, 1, i));
        }

        if (config.isBlockEnabled("compdrawers")) GameRegistry.addRecipe(
                new ItemStack(compDrawers, 1),
                "x x",
                " y ",
                "x x",
                'x',
                Items.gold_nugget,
                'y',
                new ItemStack(ModBlocks.compDrawers, 1));

        if (config.cache.enableSortingUpgrades) GameRegistry.addRecipe(
                new ItemStack(upgradeSorting),
                "y y",
                " z ",
                "y y",
                'y',
                Items.gold_nugget,
                'z',
                ModItems.upgradeTemplate);
    }

    private static Item getTabItem() {
        ConfigManager config = StorageDrawers.config;

        if (config.isBlockEnabled("fulldrawers2") && fullDrawers1 != null) return Item.getItemFromBlock(fullDrawers2);
        if (config.isBlockEnabled("fulldrawers4") && fullDrawers2 != null) return Item.getItemFromBlock(fullDrawers4);
        if (config.isBlockEnabled("fulldrawers1") && fullDrawers4 != null) return Item.getItemFromBlock(fullDrawers1);
        if (config.isBlockEnabled("halfdrawers2") && halfDrawers2 != null) return Item.getItemFromBlock(halfDrawers2);
        if (config.isBlockEnabled("halfdrawers4") && halfDrawers4 != null) return Item.getItemFromBlock(halfDrawers4);
        if (config.isBlockEnabled("trim") && trim != null) return Item.getItemFromBlock(trim);

        return Item.getItemFromBlock(Blocks.chest);
    }
}
