package com.jaquadro.minecraft.storagedrawers.integration.gtnh;

import static com.jaquadro.minecraft.storagedrawers.StorageDrawers.config;
import static gregtech.api.recipe.RecipeMaps.assemblerRecipes;
import static gregtech.api.util.GT_RecipeBuilder.MINUTES;
import static gregtech.api.util.GT_RecipeBuilder.SECONDS;
import static net.minecraftforge.fluids.FluidRegistry.getFluidStack;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.google.common.collect.ImmutableList;
import com.jaquadro.minecraft.storagedrawers.core.ModBlocks;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GT_Utility;

public class ModRecipes {

    private static final String MOD_ID_CB = "CarpentersBlocks";
    private static final String FLUID_NAME_refinedglue = "refinedglue";
    private static final String ORE_boltGold = "boltGold";
    private static final String ORE_chestWood = "chestWood";
    private static final String ORE_circuitGood = "circuitGood";
    private static final String ORE_craftingToolSaw = "craftingToolSaw";
    private static final String ORE_craftingToolScrewdriver = "craftingToolScrewdriver";
    private static final String ORE_drawerBasic = "drawerBasic";
    private static final String ORE_fenceWood = "fenceWood";
    private static final String ORE_plateAluminium = "plateAluminium";
    private static final String ORE_plateBronze = "plateBronze";
    private static final String ORE_plateDiamond = "plateDiamond";
    private static final String ORE_plateDenseObsidian = "plateDenseObsidian";
    private static final String ORE_plateEnderEye = "plateEnderEye";
    private static final String ORE_plateEmerald = "plateEmerald";
    private static final String ORE_plateGold = "plateGold";
    private static final String ORE_plateIron = "plateIron";
    private static final String ORE_plateObsidian = "plateObsidian";
    private static final String ORE_plateRedstone = "plateRedstone";
    private static final String ORE_plateRubber = "plateRubber";
    private static final String ORE_plateRuby = "plateRuby";
    private static final String ORE_plateStainlessSteel = "plateStainlessSteel";
    private static final String ORE_plateSteel = "plateSteel";
    private static final String ORE_plateTantalum = "plateTantalum";
    private static final String ORE_plateTanzanite = "plateTanzanite";
    private static final String ORE_plateTitanium = "plateTitanium";
    private static final String ORE_screwSteel = "screwSteel";
    private static final String ORE_slabWood = "slabWood";
    private static final String ORE_stickDiamond = "stickDiamond";
    private static final String ORE_stickEmerald = "stickEmerald";
    private static final String ORE_stickGold = "stickGold";
    private static final String ORE_stickIron = "stickIron";
    private static final String ORE_stickObsidian = "stickObsidian";
    private static final String ORE_stickRubber = "stickRubber";
    private static final String ORE_stickRuby = "stickRuby";
    private static final String ORE_stickTanzanite = "stickTanzanite";
    private static final String ORE_stickWood = "stickWood";
    private static final String ORE_stoneConcrete = "stoneConcrete";
    private static final String NAME_blockCarpentersBlock = "blockCarpentersBlock";

    // Lists of Wildcard Meta slabWood ItemStacks suitable to optimize the number of Gregtech Assembler recipes
    // registered
    private static final ImmutableList<ItemStack> slabWood8WList;
    private static final ImmutableList<ItemStack> slabWood7WList;
    private static final ImmutableList<ItemStack> slabWood5WList;
    private static final ImmutableList<ItemStack> slabWood4WList;

    // Ingredients for Drawers recipes
    private static final ItemStack chest1 = new ItemStack(Blocks.chest, 1);
    private static final ItemStack chest2 = new ItemStack(Blocks.chest, 2);
    private static final ItemStack chest4 = new ItemStack(Blocks.chest, 4);
    private static final ItemStack chest5 = new ItemStack(Blocks.chest, 5);
    private static final ItemStack integratedCircuit1 = GT_Utility.getIntegratedCircuit(1);
    private static final ItemStack integratedCircuit2 = GT_Utility.getIntegratedCircuit(2);
    private static final ItemStack integratedCircuit4 = GT_Utility.getIntegratedCircuit(4);
    private static final ItemStack integratedCircuit12 = GT_Utility.getIntegratedCircuit(12);
    private static final ItemStack integratedCircuit14 = GT_Utility.getIntegratedCircuit(14);

    // Ingredients for Framed Drawers recipes
    private static final ItemStack carpentersBlock8 = GameRegistry
            .findItemStack(MOD_ID_CB, NAME_blockCarpentersBlock, 8);
    private static final ItemStack carpentersBlock7 = GameRegistry
            .findItemStack(MOD_ID_CB, NAME_blockCarpentersBlock, 7);
    private static final ItemStack carpentersBlock5 = GameRegistry
            .findItemStack(MOD_ID_CB, NAME_blockCarpentersBlock, 5);
    private static final ItemStack carpentersBlock4 = GameRegistry
            .findItemStack(MOD_ID_CB, NAME_blockCarpentersBlock, 4);
    private static final ItemStack carpentersBlock1 = GameRegistry
            .findItemStack(MOD_ID_CB, NAME_blockCarpentersBlock, 1);

    // Ingredients for Trims
    private static final ItemStack stick4 = new ItemStack(Items.stick, 4);

    // Ingredients for Controller
    private static final ItemStack comparatorGate = makeItemStack(
            "ProjRed|Integration:projectred.integration.gate",
            26,
            1,
            null);

    // Ingredients for Upgrades recipes
    private static final ItemStack redAlloyWire = makeItemStack(
            "ProjRed|Transmission:projectred.transmission.wire",
            0,
            1,
            null);
    private static final ItemStack upgradeLock = new ItemStack(ModItems.upgradeLock);
    private static final ItemStack piston = new ItemStack(Blocks.piston, 1);
    private static final ItemStack upgradeTemplate = new ItemStack(ModItems.upgradeTemplate);
    private static final ImmutableList<ItemStack> drawerBasicW;

    static {
        final ImmutableList.Builder<String> slabWoodRegistryNameListBuilder = new ImmutableList.Builder<>();
        final ImmutableList.Builder<ItemStack> slabWood8WListBuilder = new ImmutableList.Builder<>();
        final ImmutableList.Builder<ItemStack> slabWood7WListBuilder = new ImmutableList.Builder<>();
        final ImmutableList.Builder<ItemStack> slabWood5WListBuilder = new ImmutableList.Builder<>();
        final ImmutableList.Builder<ItemStack> slabWood4WListBuilder = new ImmutableList.Builder<>();

        // Build a list of unique slabWood Items's Registry Names ("MOD_ID:ItemName")
        for (ItemStack slabWood : OreDictionary.getOres(ORE_slabWood)) {
            slabWoodRegistryNameListBuilder.add(GameRegistry.findUniqueIdentifierFor(slabWood.getItem()).toString());
        }

        // Build lists of unique slabWood ItemStacks of different size with Wildcard Meta from the RegistryNames list
        // above
        for (String slabName : slabWoodRegistryNameListBuilder.build()) {
            slabWood8WListBuilder.add(makeItemStack(slabName, OreDictionary.WILDCARD_VALUE, 8, null));
            slabWood7WListBuilder.add(makeItemStack(slabName, OreDictionary.WILDCARD_VALUE, 7, null));
            slabWood5WListBuilder.add(makeItemStack(slabName, OreDictionary.WILDCARD_VALUE, 5, null));
            slabWood4WListBuilder.add(makeItemStack(slabName, OreDictionary.WILDCARD_VALUE, 4, null));
        }

        slabWood8WList = slabWood8WListBuilder.build();
        slabWood7WList = slabWood7WListBuilder.build();
        slabWood5WList = slabWood5WListBuilder.build();
        slabWood4WList = slabWood4WListBuilder.build();

        final ImmutableList.Builder<ItemStack> drawerBasic1RegistryNameListBuilder = new ImmutableList.Builder<>();
        final ImmutableList.Builder<String> drawerBasicRefs = new ImmutableList.Builder<>();

        // Build a list of unique drawerBasic Items's Registry Names ("MOD_ID:ItemName")
        for (ItemStack drawerBasicStack : OreDictionary.getOres(ORE_drawerBasic)) {
            UniqueIdentifier drawerID = GameRegistry.findUniqueIdentifierFor(drawerBasicStack.getItem());
            // Keeps only fullDrawer1 to limit the amount of distinct items
            if (drawerID.name.matches("fullDrawers1.*")) drawerBasicRefs.add(drawerID.toString());
        }

        // Build the list of unique drawerBasic ItemStacks with Wildcard Meta from the RegistryNames list above
        for (String drawerName : drawerBasicRefs.build()) {
            final ItemStack drawerStack = makeItemStack(drawerName, OreDictionary.WILDCARD_VALUE, 1, null);
            // Be sure to only keep the ItemDrawers and not some other mod's drawerBasic1
            if (drawerStack.getItem() instanceof ItemDrawers) {
                drawerBasic1RegistryNameListBuilder.add(drawerStack);
            }
        }

        drawerBasicW = drawerBasic1RegistryNameListBuilder.build();
    }

    private ModRecipes() {
        throw new IllegalStateException("Utility class");
    }

    public static void init() {

        // Drawer Recipes

        // Drawer Full
        fullDrawers1Recipes(); // Drawer Full 1x1
        fullDrawers2Recipes(); // Drawer Full 1x2
        fullDrawers4Recipes(); // Drawer Full 2x2

        // Drawer Half
        halfDrawers2Recipes(); // Drawer Half 1x2
        halfDrawers4Recipes(); // Oak Drawer Half 2x2

        // Trim Recipes
        trimRecipes(); // Drawer trim

        // Drawer Machines Recipes
        compDrawersRecipes(); // Compacting Drawer
        controllerRecipes(); // Parent Controller recipe
        controllerSlaveRecipes(); // Child Controller recipe
        framingTableRecipes(); // Framing Table recipe

        // Upgrades recipes
        upgradeTemplateRecipes(); // Upgrade Template
        storageUpgradeRecipes(); // Iron, Gold, Obsidian, Diamond, Emerald Storage Upgrades
        statusUpgradeRecipes(); // Status Upgrade I & II
        redstoneUpgradeRecipes(); // Redstone, Max, Min Upgrades
        voidUpgradeRecipes(); // Void Upgrade
        lockUpgradeRecipes(); // Drawer key
        shroudKeyUpgradeRecipes(); // Concealment Key
        personalKeyUpgradeRecipes(); // Personal Key

        // Packing Tape
        packingTapeRecipes(); // Packing Tape
    }

    private static void fullDrawers1Recipes() {
        if (!config.isBlockEnabled("fulldrawers1")) return;

        // Oak Drawer Full 1x1

        final int recipeOutput = config.getBlockRecipeOutput("fulldrawers1");

        // Oak Drawer Full 1x1 Workbench recipe
        final ItemStack fullDrawer1 = new ItemStack(ModBlocks.fullDrawers1, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(fullDrawer1, "sss", "scs", "sss", 's', ORE_slabWood, 'c', ORE_chestWood));

        // Oak Drawer Full 1x1 Gregtech Assembler recipes
        for (ItemStack slabWood8W : slabWood8WList)
            GT_Values.RA.stdBuilder().itemInputs(slabWood8W, chest1, integratedCircuit1).itemOutputs(fullDrawer1)
                    .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);

        // Framed Drawer Full 1x1

        if (!config.cache.enableFramedDrawers) return;

        // Framed Drawer Full 1x1 Workbench recipe
        final ItemStack fullCustom1 = new ItemStack(ModBlocks.fullCustom1, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(fullCustom1, "bbb", "bcb", "bbb", 'b', carpentersBlock1, 'c', ORE_chestWood));

        // Framed Drawer Full 1x1 Gregtech Assembler recipe
        GT_Values.RA.stdBuilder().itemInputs(carpentersBlock8, chest1, integratedCircuit1).itemOutputs(fullCustom1)
                .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);
    }

    private static void fullDrawers2Recipes() {
        if (!config.isBlockEnabled("fulldrawers2")) return;

        // Oak Drawer Full 1x2

        final int recipeOutput = config.getBlockRecipeOutput("fulldrawers2");

        // Oak Drawer Full 1x2 Workbench recipe
        final ItemStack fullDrawers2 = new ItemStack(ModBlocks.fullDrawers2, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(fullDrawers2, "scs", "sss", "scs", 's', ORE_slabWood, 'c', ORE_chestWood));

        // Oak Drawer Full 1x2 Gregtech Assembler recipes
        for (ItemStack slabWood7W : slabWood7WList)
            GT_Values.RA.stdBuilder().itemInputs(slabWood7W, chest2, integratedCircuit2).itemOutputs(fullDrawers2)
                    .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);

        // Framed Drawer Full 1x2

        if (!config.cache.enableFramedDrawers) return;

        // Framed Drawer Full 1x2 Workbench recipe
        final ItemStack fullCustom2 = new ItemStack(ModBlocks.fullCustom2, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(fullCustom2, "bcb", "bbb", "bcb", 'b', carpentersBlock1, 'c', ORE_chestWood));

        // Framed Drawer Full 1x2 Gregtech Assembler recipe
        GT_Values.RA.stdBuilder().itemInputs(carpentersBlock7, chest2, integratedCircuit2).itemOutputs(fullCustom2)
                .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);
    }

    private static void fullDrawers4Recipes() {
        if (!config.isBlockEnabled("fulldrawers4")) return;

        // Oak Drawer Full 2x2

        final int recipeOutput = config.getBlockRecipeOutput("fulldrawers4");

        // Oak Drawer Full 2x2 Workbench recipe
        final ItemStack fullDrawers4 = new ItemStack(ModBlocks.fullDrawers4, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(fullDrawers4, "csc", "sss", "csc", 's', ORE_slabWood, 'c', ORE_chestWood));

        // Oak Drawer Full 2x2 Gregtech Assembler recipes
        for (ItemStack slabWood5W : slabWood5WList)
            GT_Values.RA.stdBuilder().itemInputs(slabWood5W, chest4, integratedCircuit4).itemOutputs(fullDrawers4)
                    .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);

        // Framed Drawer Full 2x2

        if (!config.cache.enableFramedDrawers) return;

        // Framed Drawer Full 2x2 Workbench recipe
        final ItemStack fullCustom4 = new ItemStack(ModBlocks.fullCustom4, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(fullCustom4, "cbc", "bbb", "cbc", 'b', carpentersBlock1, 'c', ORE_chestWood));

        // Framed Drawer Full 2x2 Gregtech Assembler recipe
        GT_Values.RA.stdBuilder().itemInputs(carpentersBlock5, chest4, integratedCircuit4).itemOutputs(fullCustom4)
                .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);
    }

    private static void halfDrawers2Recipes() {
        if (!config.isBlockEnabled("halfdrawers2")) return;

        // Oak Drawer Half 1x2

        final int recipeOutput = config.getBlockRecipeOutput("halfdrawers2");

        // Oak Drawer Half 1x2 Workbench recipe
        final ItemStack halfDrawers2 = new ItemStack(ModBlocks.halfDrawers2, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(halfDrawers2, "sss", "csc", "sss", 's', ORE_slabWood, 'c', ORE_chestWood));

        // Oak Drawer Half 1x2 Gregtech Assembler recipes
        for (ItemStack slabWood7W : slabWood7WList)
            GT_Values.RA.stdBuilder().itemInputs(slabWood7W, chest2, integratedCircuit12).itemOutputs(halfDrawers2)
                    .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);

        // Framed Drawer Half 1x2

        if (!config.cache.enableFramedDrawers) return;

        // Framed Drawer Half 1x2 Workbench recipe
        final ItemStack halfCustom2 = new ItemStack(ModBlocks.halfCustom2, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(halfCustom2, "bbb", "cbc", "bbb", 'b', carpentersBlock1, 'c', ORE_chestWood));

        // Framed Drawer Half 1x2 Gregtech Assembler recipe
        GT_Values.RA.stdBuilder().itemInputs(carpentersBlock7, chest2, integratedCircuit12).itemOutputs(halfCustom2)
                .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);
    }

    private static void halfDrawers4Recipes() {
        if (!config.isBlockEnabled("halfdrawers4")) return;

        // Oak Drawer Half 2x2

        final int recipeOutput = config.getBlockRecipeOutput("halfdrawers4");

        // Oak Drawer Half 2x2 Workbench recipe
        final ItemStack halfDrawers4 = new ItemStack(ModBlocks.halfDrawers4, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(halfDrawers4, "csc", "scs", "csc", 's', ORE_slabWood, 'c', ORE_chestWood));

        for (ItemStack slabWood4W : slabWood4WList)
            GT_Values.RA.stdBuilder().itemInputs(slabWood4W, chest5, integratedCircuit14).itemOutputs(halfDrawers4)
                    .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);

        // Framed Drawer Half 2x2

        if (!config.cache.enableFramedDrawers) return;

        // Framed Drawer Half 2x2 Workbench recipe
        final ItemStack halfCustom4 = new ItemStack(ModBlocks.halfCustom4, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(halfCustom4, "cbc", "bcb", "cbc", 'b', carpentersBlock1, 'c', ORE_chestWood));

        // Framed Drawer Half 2x2 Gregtech Assembler recipe
        GT_Values.RA.stdBuilder().itemInputs(carpentersBlock4, chest5, integratedCircuit14).itemOutputs(halfCustom4)
                .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);
    }

    private static void trimRecipes() {
        if (!config.isBlockEnabled("trim")) return;

        // Oak Trim

        final int recipeOutput = config.getBlockRecipeOutput("trim");

        // Oak Trim Workbench recipe
        final ItemStack trim = new ItemStack(ModBlocks.trim, recipeOutput, 0);
        GameRegistry.addRecipe(new ShapedOreRecipe(trim, "tst", "sss", "tst", 't', ORE_stickWood, 's', ORE_slabWood));

        // Oak Trim Gregtech Assembler recipe
        for (ItemStack slabWood5W : slabWood5WList)
            GT_Values.RA.stdBuilder().itemInputs(slabWood5W, stick4, integratedCircuit1).itemOutputs(trim)
                    .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);

        // Framed Trim

        if (!config.cache.enableFramedDrawers) return;

        // Framed Trim Workbench recipe
        final ItemStack trimCustom = new ItemStack(ModBlocks.trimCustom, recipeOutput, 0);
        GameRegistry.addRecipe(
                new ShapedOreRecipe(trimCustom, "tbt", "bbb", "tbt", 'b', carpentersBlock1, 't', ORE_stickWood));

        // Framed Trim Gregtech Assembler recipe
        GT_Values.RA.stdBuilder().itemInputs(carpentersBlock5, stick4, integratedCircuit1).itemOutputs(trimCustom)
                .duration(10 * SECONDS).eut(16).addTo(assemblerRecipes);
    }

    private static void compDrawersRecipes() {
        if (!config.isBlockEnabled("compdrawers")) return;
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModBlocks.compDrawers, 1),
                        new String[] { "sss", "pdp", "oio" },
                        's',
                        ORE_stoneConcrete,
                        'p',
                        piston,
                        'd',
                        ORE_drawerBasic,
                        'o',
                        ORE_plateObsidian,
                        'i',
                        ORE_plateIron));
    }

    private static void controllerRecipes() {
        if (!config.isBlockEnabled("controller")) return;
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModBlocks.controller),
                        new String[] { "scs", "gbg", "odo" },
                        's',
                        ORE_plateStainlessSteel,
                        'c',
                        ORE_circuitGood,
                        'g',
                        comparatorGate,
                        'b',
                        ORE_drawerBasic,
                        'o',
                        ORE_plateObsidian,
                        'd',
                        ORE_plateDiamond));
    }

    private static void controllerSlaveRecipes() {
        if (!config.isBlockEnabled("controllerSlave")) return;
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModBlocks.controllerSlave),
                        "scs",
                        "gbg",
                        "oeo",
                        's',
                        ORE_plateStainlessSteel,
                        'c',
                        ORE_circuitGood,
                        'g',
                        comparatorGate,
                        'b',
                        ORE_drawerBasic,
                        'o',
                        ORE_plateObsidian,
                        'e',
                        ORE_plateEnderEye));
    }

    private static void framingTableRecipes() {
        if (!config.cache.enableFramedDrawers) return;
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModBlocks.framingTable),
                        "ttt",
                        "fsf",
                        "fdf",
                        't',
                        ModBlocks.trim,
                        'f',
                        ORE_fenceWood,
                        's',
                        ORE_stickWood,
                        'd',
                        ORE_craftingToolScrewdriver));
    }

    private static void upgradeTemplateRecipes() {
        if (!(config.cache.enableIndicatorUpgrades || config.cache.enableLockUpgrades
                || config.cache.enablePersonalUpgrades
                || config.cache.enableRedstoneUpgrades
                || config.cache.enableShroudUpgrades
                || config.cache.enableSortingUpgrades
                || config.cache.enableStorageUpgrades
                || config.cache.enableVoidUpgrades))
            return;

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        upgradeTemplate,
                        new String[] { "sps", "sds", " t " },
                        's',
                        ORE_screwSteel,
                        'p',
                        piston,
                        'd',
                        ORE_drawerBasic,
                        't',
                        ORE_craftingToolScrewdriver));

        for (ItemStack drawer : drawerBasicW) GT_Values.RA.stdBuilder().itemInputs(drawer, piston)
                .itemOutputs(upgradeTemplate).duration(1 * MINUTES).eut(16).addTo(assemblerRecipes);
    }

    private static void storageUpgradeRecipes() {
        if (!config.cache.enableStorageUpgrades) return;

        // Upgarde Iron
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgrade, 1, 2),
                        new String[] { "pup", "upu", "psp" },
                        'p',
                        ORE_plateIron,
                        's',
                        ORE_stickIron,
                        'u',
                        upgradeTemplate));

        // Upgarde Gold
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgrade, 1, 3),
                        new String[] { "pup", "utu", "psp" },
                        'p',
                        ORE_plateGold,
                        's',
                        ORE_stickGold,
                        'u',
                        upgradeTemplate,
                        't',
                        ORE_plateBronze));

        // Upgarde Obsidian
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgrade, 1, 4),
                        new String[] { "pup", "utu", "psp" },
                        'p',
                        ORE_plateObsidian,
                        's',
                        ORE_stickObsidian,
                        'u',
                        upgradeTemplate,
                        't',
                        ORE_plateSteel));

        // Upgarde Diamond
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgrade, 1, 5),
                        new String[] { "pup", "utu", "psp" },
                        'p',
                        ORE_plateDiamond,
                        's',
                        ORE_stickDiamond,
                        'u',
                        upgradeTemplate,
                        't',
                        ORE_plateAluminium));

        // Upgarde Emerald
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgrade, 1, 6),
                        new String[] { "pup", "utu", "psp" },
                        'p',
                        ORE_plateEmerald,
                        's',
                        ORE_stickEmerald,
                        'u',
                        upgradeTemplate,
                        't',
                        ORE_plateTantalum));

        // Upgarde Ruby
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgrade, 1, 7),
                        new String[] { "pup", "utu", "psp" },
                        'p',
                        ORE_plateRuby,
                        's',
                        ORE_stickRuby,
                        'u',
                        upgradeTemplate,
                        't',
                        ORE_plateStainlessSteel));

        // Upgarde Tanzanite
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgrade, 1, 8),
                        new String[] { "pup", "utu", "psp" },
                        'p',
                        ORE_plateTanzanite,
                        's',
                        ORE_stickTanzanite,
                        'u',
                        upgradeTemplate,
                        't',
                        ORE_plateTitanium));

        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgradeDowngrade, 1, 0),
                        new String[] { "pup", "utu", "psp" },
                        'p',
                        ORE_plateRubber,
                        's',
                        ORE_stickRubber,
                        'u',
                        upgradeTemplate,
                        't',
                        new ItemStack(Blocks.soul_sand)));
    }

    private static void statusUpgradeRecipes() {
        if (!config.cache.enableIndicatorUpgrades) return;

        // Status Upgrade I recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgradeStatus, 1, 1),
                        "twt",
                        "wuw",
                        "rwr",
                        't',
                        new ItemStack(Blocks.redstone_torch),
                        'w',
                        redAlloyWire,
                        'u',
                        upgradeTemplate,
                        'r',
                        ORE_plateRedstone));

        // Status Upgrade II recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgradeStatus, 1, 2),
                        "gwg",
                        "wuw",
                        "rwr",
                        'g',
                        comparatorGate,
                        'w',
                        redAlloyWire,
                        'u',
                        upgradeTemplate,
                        'r',
                        ORE_plateRedstone));
    }

    private static void redstoneUpgradeRecipes() {
        if (!config.cache.enableRedstoneUpgrades) return;

        // Redstone Upgrade recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgradeRedstone, 1, 0),
                        "rsr",
                        "sus",
                        "rwr",
                        'r',
                        ORE_plateRedstone,
                        's',
                        ORE_stickWood,
                        'u',
                        upgradeTemplate,
                        'w',
                        redAlloyWire));

        // Redstone Max Upgrade recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgradeRedstone, 1, 1),
                        "rrr",
                        "sus",
                        "sws",
                        'r',
                        ORE_plateRedstone,
                        's',
                        ORE_stickWood,
                        'u',
                        upgradeTemplate,
                        'w',
                        redAlloyWire));

        // Redstone Min recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgradeRedstone, 1, 2),
                        "sws",
                        "sus",
                        "rrr",
                        'r',
                        ORE_plateRedstone,
                        's',
                        ORE_stickWood,
                        'u',
                        upgradeTemplate,
                        'w',
                        redAlloyWire));
    }

    private static void voidUpgradeRecipes() {
        if (!config.cache.enableVoidUpgrades) return;

        // Upgrade Void recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgradeVoid),
                        "sws",
                        "ouo",
                        "sws",
                        's',
                        ORE_stickWood,
                        'w',
                        redAlloyWire,
                        'o',
                        ORE_plateDenseObsidian,
                        'u',
                        upgradeTemplate));
    }

    private static void lockUpgradeRecipes() {
        if (!config.cache.enableLockUpgrades) return;

        // Drawer Key recipe
        GameRegistry.addRecipe(
                new ShapedOreRecipe(
                        new ItemStack(ModItems.upgradeLock),
                        " bs",
                        "uso",
                        "gt ",
                        'b',
                        ORE_boltGold,
                        's',
                        ORE_plateSteel,
                        'u',
                        upgradeTemplate,
                        'o',
                        ORE_stickGold,
                        'g',
                        ORE_plateGold,
                        't',
                        ORE_craftingToolSaw));
    }

    private static void shroudKeyUpgradeRecipes() {
        if (!config.cache.enableShroudUpgrades) return;

        // Concealment Key
        GT_Values.RA.stdBuilder().itemInputs(upgradeLock, new ItemStack(Items.ender_eye))
                .itemOutputs(new ItemStack(ModItems.shroudKey)).duration(5 * SECONDS).eut(TierEU.RECIPE_LV)
                .addTo(assemblerRecipes);
    }

    private static void personalKeyUpgradeRecipes() {
        if (!config.cache.enablePersonalUpgrades) return;

        // Storage Personal Key
        GT_Values.RA.stdBuilder().itemInputs(upgradeLock, new ItemStack(Items.name_tag))
                .itemOutputs(new ItemStack(ModItems.personalKey)).duration(5 * SECONDS).eut(TierEU.RECIPE_LV)
                .addTo(assemblerRecipes);
    }

    private static void packingTapeRecipes() {
        if (!config.cache.enableTape) return;

        // Packing Tape
        GT_Values.RA.stdBuilder().itemInputs(new ItemStack(Items.paper), integratedCircuit1)
                .fluidInputs(getFluidStack("glue", 144)).itemOutputs(new ItemStack(ModItems.tape))
                .duration(10 * SECONDS).eut(TierEU.RECIPE_LV).addTo(assemblerRecipes);

        GT_Values.RA.stdBuilder().itemInputs(new ItemStack(Items.paper), integratedCircuit1)
                .fluidInputs(Materials.Glue.getFluid(144)).itemOutputs(new ItemStack(ModItems.tape))
                .duration(10 * SECONDS).eut(TierEU.RECIPE_LV).addTo(assemblerRecipes);
    }

    /*
     * Same as cpw.mods.fml.common.registry.GameRegistry.makeItemStack But with working stackSize
     */
    @SuppressWarnings("SameParameterValue")
    private static ItemStack makeItemStack(String itemName, int meta, int stackSize, String nbtString) {
        final ItemStack rStack = GameRegistry.makeItemStack(itemName, meta, stackSize, nbtString);
        rStack.stackSize = stackSize;
        return rStack;
    }
}
