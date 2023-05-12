package com.jaquadro.minecraft.storagedrawers.core;

import static com.jaquadro.minecraft.storagedrawers.StorageDrawers.config;

import net.minecraft.block.BlockWood;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.jaquadro.minecraft.storagedrawers.core.recipe.FallbackShapedOreRecipe;
import com.jaquadro.minecraft.storagedrawers.core.recipe.TemplateRecipe;
import com.jaquadro.minecraft.storagedrawers.integration.ChiselIntegrationModule;
import com.jaquadro.minecraft.storagedrawers.integration.GTNHIntegrationModule;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModRecipes {

    public void init() {
        OreDictionary.registerOre("chestWood", new ItemStack(Blocks.chest)); // Remove when porting to 1.8

        if (GTNHIntegrationModule.isEnabled()) return;
        RecipeSorter.register(
                "StorageDrawers:FallbackShapedOreRecipe",
                FallbackShapedOreRecipe.class,
                RecipeSorter.Category.SHAPED,
                "after:forge:shapedore");
        if (!ChiselIntegrationModule.isEnabled()) {
            for (int i = 0; i < BlockWood.field_150096_a.length; i++) {
                if (config.isBlockEnabled("fulldrawers1")) GameRegistry.addRecipe(
                        new ShapedOreRecipe(
                                new ItemStack(ModBlocks.fullDrawers1, config.getBlockRecipeOutput("fulldrawers1"), i),
                                "xxx",
                                " y ",
                                "xxx",
                                'x',
                                new ItemStack(Blocks.planks, 1, i),
                                'y',
                                "chestWood"));
                if (config.isBlockEnabled("fulldrawers2")) GameRegistry.addRecipe(
                        new ShapedOreRecipe(
                                new ItemStack(ModBlocks.fullDrawers2, config.getBlockRecipeOutput("fulldrawers2"), i),
                                "xyx",
                                "xxx",
                                "xyx",
                                'x',
                                new ItemStack(Blocks.planks, 1, i),
                                'y',
                                "chestWood"));
                if (config.isBlockEnabled("halfdrawers2")) GameRegistry.addRecipe(
                        new ShapedOreRecipe(
                                new ItemStack(ModBlocks.halfDrawers2, config.getBlockRecipeOutput("halfdrawers2"), i),
                                "xyx",
                                "xxx",
                                "xyx",
                                'x',
                                new ItemStack(Blocks.wooden_slab, 1, i),
                                'y',
                                "chestWood"));
                if (config.isBlockEnabled("fulldrawers4")) GameRegistry.addRecipe(
                        new ShapedOreRecipe(
                                new ItemStack(ModBlocks.fullDrawers4, config.getBlockRecipeOutput("fulldrawers4"), i),
                                "yxy",
                                "xxx",
                                "yxy",
                                'x',
                                new ItemStack(Blocks.planks, 1, i),
                                'y',
                                "chestWood"));
                if (config.isBlockEnabled("halfdrawers4")) GameRegistry.addRecipe(
                        new ShapedOreRecipe(
                                new ItemStack(ModBlocks.halfDrawers4, config.getBlockRecipeOutput("halfdrawers4"), i),
                                "yxy",
                                "xxx",
                                "yxy",
                                'x',
                                new ItemStack(Blocks.wooden_slab, 1, i),
                                'y',
                                "chestWood"));
                if (config.isBlockEnabled("trim")) {
                    GameRegistry.addRecipe(
                            new ShapedOreRecipe(
                                    new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), i),
                                    "xyx",
                                    "yyy",
                                    "xyx",
                                    'x',
                                    "stickWood",
                                    'y',
                                    new ItemStack(Blocks.planks, 1, i)));
                }
            }
        }
        if (config.cache.enableFallbackRecipes || ChiselIntegrationModule.isEnabled()) {
            if (config.isBlockEnabled("fulldrawers1")) GameRegistry.addRecipe(
                    new FallbackShapedOreRecipe(
                            new ItemStack(ModBlocks.fullDrawers1, config.getBlockRecipeOutput("fulldrawers1"), 0),
                            "xxx",
                            " y ",
                            "xxx",
                            'x',
                            "plankWood",
                            'y',
                            "chestWood"));
            if (config.isBlockEnabled("fulldrawers2")) GameRegistry.addRecipe(
                    new FallbackShapedOreRecipe(
                            new ItemStack(ModBlocks.fullDrawers2, config.getBlockRecipeOutput("fulldrawers2"), 0),
                            "xyx",
                            "xxx",
                            "xyx",
                            'x',
                            "plankWood",
                            'y',
                            "chestWood"));
            if (config.isBlockEnabled("halfdrawers2")) GameRegistry.addRecipe(
                    new FallbackShapedOreRecipe(
                            new ItemStack(ModBlocks.halfDrawers2, config.getBlockRecipeOutput("halfdrawers2"), 0),
                            "xyx",
                            "xxx",
                            "xyx",
                            'x',
                            "slabWood",
                            'y',
                            "chestWood"));
            if (config.isBlockEnabled("fulldrawers4")) GameRegistry.addRecipe(
                    new FallbackShapedOreRecipe(
                            new ItemStack(ModBlocks.fullDrawers4, config.getBlockRecipeOutput("fulldrawers4"), 0),
                            "yxy",
                            "xxx",
                            "yxy",
                            'x',
                            "plankWood",
                            'y',
                            "chestWood"));
            if (config.isBlockEnabled("halfdrawers4")) GameRegistry.addRecipe(
                    new FallbackShapedOreRecipe(
                            new ItemStack(ModBlocks.halfDrawers4, config.getBlockRecipeOutput("halfdrawers4"), 0),
                            "yxy",
                            "xxx",
                            "yxy",
                            'x',
                            "slabWood",
                            'y',
                            "chestWood"));
            if (config.isBlockEnabled("trim")) GameRegistry.addRecipe(
                    new FallbackShapedOreRecipe(
                            new ItemStack(ModBlocks.trim, config.getBlockRecipeOutput("trim"), 0),
                            "xyx",
                            "yyy",
                            "xyx",
                            'x',
                            "stickWood",
                            'y',
                            "plankWood"));
        }

        if (config.isBlockEnabled("compdrawers")) GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModBlocks.compDrawers, config.getBlockRecipeOutput("compdrawers")),
                        "xxx",
                        "zwz",
                        "xyx",
                        'x',
                        new ItemStack(Blocks.stone),
                        'y',
                        "ingotIron",
                        'z',
                        new ItemStack(Blocks.piston),
                        'w',
                        "drawerBasic"));

        if (config.isBlockEnabled("controller")) GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModBlocks.controller),
                        "xxx",
                        "yzy",
                        "xwx",
                        'x',
                        new ItemStack(Blocks.stone),
                        'y',
                        Items.comparator,
                        'z',
                        "drawerBasic",
                        'w',
                        "gemDiamond"));

        if (config.isBlockEnabled("controllerSlave")) GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModBlocks.controllerSlave),
                        "xxx",
                        "yzy",
                        "xwx",
                        'x',
                        new ItemStack(Blocks.stone),
                        'y',
                        Items.comparator,
                        'z',
                        "drawerBasic",
                        'w',
                        "ingotGold"));

        // GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.upgradeTemplate, 2), "xxx", "xyx", "xxx",
        // 'x', "stickWood", 'y', "drawerBasic"));

        if (config.cache.enableStorageUpgrades) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgrade, 1, 2),
                            "xyx",
                            "yzy",
                            "xyx",
                            'x',
                            "ingotIron",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgrade, 1, 3),
                            "xyx",
                            "yzy",
                            "xyx",
                            'x',
                            "ingotGold",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgrade, 1, 4),
                            "xyx",
                            "yzy",
                            "xyx",
                            'x',
                            Blocks.obsidian,
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgrade, 1, 5),
                            "xyx",
                            "yzy",
                            "xyx",
                            'x',
                            "gemDiamond",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgrade, 1, 6),
                            "xyx",
                            "yzy",
                            "xyx",
                            'x',
                            "gemEmerald",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));

            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgradeDowngrade, 1, 0),
                            "xyx",
                            "yzy",
                            "xyx",
                            'x',
                            Items.flint,
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
        }

        if (config.cache.enableIndicatorUpgrades) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgradeStatus, 1, 1),
                            "wyw",
                            "yzy",
                            "xyx",
                            'w',
                            new ItemStack(Blocks.redstone_torch),
                            'x',
                            "dustRedstone",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgradeStatus, 1, 2),
                            "wyw",
                            "yzy",
                            "xyx",
                            'w',
                            Items.comparator,
                            'x',
                            "dustRedstone",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
        }

        if (config.cache.enableLockUpgrades) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgradeLock),
                            "xy ",
                            " y ",
                            " z ",
                            'x',
                            "nuggetGold",
                            'y',
                            "ingotGold",
                            'z',
                            ModItems.upgradeTemplate));
        }

        if (config.cache.enableVoidUpgrades) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgradeVoid),
                            "yyy",
                            "xzx",
                            "yyy",
                            'x',
                            Blocks.obsidian,
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
        }

        if (config.cache.enableRedstoneUpgrades) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgradeRedstone, 1, 0),
                            "xyx",
                            "yzy",
                            "xyx",
                            'x',
                            "dustRedstone",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgradeRedstone, 1, 1),
                            "xxx",
                            "yzy",
                            "yyy",
                            'x',
                            "dustRedstone",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.upgradeRedstone, 1, 2),
                            "yyy",
                            "yzy",
                            "xxx",
                            'x',
                            "dustRedstone",
                            'y',
                            "stickWood",
                            'z',
                            ModItems.upgradeTemplate));
        }

        if (config.cache.enableShroudUpgrades) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.shroudKey), ModItems.upgradeLock, Items.ender_eye);
        }

        if (config.cache.enablePersonalUpgrades) {
            GameRegistry.addShapelessRecipe(new ItemStack(ModItems.personalKey), ModItems.upgradeLock, Items.name_tag);
        }

        if (config.cache.enableTape) {
            GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModItems.tape),
                            " x ",
                            "yyy",
                            'x',
                            "slimeball",
                            'y',
                            Items.paper));
        }

        if (config.cache.enableFramedDrawers) {
            GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.framingTable), "xxx", "x x", 'x', ModBlocks.trim);

            if (config.isBlockEnabled("fulldrawers1")) GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModBlocks.fullCustom1, config.getBlockRecipeOutput("fulldrawers1"), 0),
                            "xxx",
                            " y ",
                            "xxx",
                            'x',
                            "stickWood",
                            'y',
                            "chestWood"));
            if (config.isBlockEnabled("fulldrawers2")) GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModBlocks.fullCustom2, config.getBlockRecipeOutput("fulldrawers2"), 0),
                            "xyx",
                            "xzx",
                            "xyx",
                            'x',
                            "stickWood",
                            'y',
                            "chestWood",
                            'z',
                            "plankWood"));
            if (config.isBlockEnabled("halfdrawers2")) GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModBlocks.halfCustom2, config.getBlockRecipeOutput("halfdrawers2"), 0),
                            "xyx",
                            "xzx",
                            "xyx",
                            'x',
                            "stickWood",
                            'y',
                            "chestWood",
                            'z',
                            "slabWood"));
            if (config.isBlockEnabled("fulldrawers4")) GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModBlocks.fullCustom4, config.getBlockRecipeOutput("fulldrawers4"), 0),
                            "yxy",
                            "xzx",
                            "yxy",
                            'x',
                            "stickWood",
                            'y',
                            "chestWood",
                            'z',
                            "plankWood"));
            if (config.isBlockEnabled("halfdrawers4")) GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModBlocks.halfCustom4, config.getBlockRecipeOutput("halfdrawers4"), 0),
                            "yxy",
                            "xzx",
                            "yxy",
                            'x',
                            "stickWood",
                            'y',
                            "chestWood",
                            'z',
                            "slabWood"));
            if (config.isBlockEnabled("trim")) GameRegistry.addRecipe(
                    new ShapedOreRecipe(
                            new ItemStack(ModBlocks.trimCustom, config.getBlockRecipeOutput("trim"), 0),
                            "yxy",
                            "xyx",
                            "yxy",
                            'x',
                            "stickWood",
                            'y',
                            "plankWood"));
        }

        RecipeSorter.register(
                "StorageDrawers:UpgradeTemplate",
                TemplateRecipe.class,
                RecipeSorter.Category.SHAPED,
                "after:minecraft:shaped");

        CraftingManager.getInstance().getRecipeList().add(new TemplateRecipe());
    }
}
