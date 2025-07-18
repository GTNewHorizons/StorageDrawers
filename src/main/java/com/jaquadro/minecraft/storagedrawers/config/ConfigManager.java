package com.jaquadro.minecraft.storagedrawers.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import com.jaquadro.minecraft.storagedrawers.api.config.IAddonConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IBlockConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IIntegrationConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IPacksConfig;
import com.jaquadro.minecraft.storagedrawers.api.config.IUserConfig;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;

public class ConfigManager {

    public class ConfigSection {

        public final ConfigSection parent;
        public final String name;
        public final String lang;

        private ConfigCategory category;

        public ConfigSection(List<ConfigSection> list, ConfigSection parent, String name, String lang) {
            this.parent = parent;
            this.name = name;
            this.lang = lang;

            list.add(this);
        }

        public ConfigSection(List<ConfigSection> list, String name, String lang) {
            this(list, null, name, lang);
        }

        public ConfigCategory getCategory() {
            if (category != null) return category;

            if (parent != null)
                category = config.getCategory(parent.getCategory().getQualifiedName() + "." + name.toLowerCase());
            else category = config.getCategory(name.toLowerCase());

            category.setLanguageKey(LANG_PREFIX + lang);
            return category;
        }

        public String getQualifiedName() {
            return getCategory().getQualifiedName();
        }
    }

    public static class ConfigCache {

        public boolean enableIndicatorUpgrades;
        public boolean enableStorageUpgrades;
        public boolean enableLockUpgrades;
        public boolean enableVoidUpgrades;
        public boolean enableCreativeUpgrades;
        public boolean enableShroudUpgrades;
        public boolean enableQuantifyUpgrades;
        public boolean enablePersonalUpgrades;
        public boolean enableSortingUpgrades;
        public boolean enableRedstoneUpgrades;
        public boolean renderStorageUpgrades;
        public boolean enableDrawerUI;
        public String itemRenderType;
        public String breakDrawerDropMode;
        public boolean creativeTabVanillaWoods;
        public boolean enableSidedInput;
        public boolean enableSidedOutput;
        public boolean enableItemConversion;
        public boolean enableWailaIntegration;
        public boolean enableAE2Integration;
        public boolean enableThaumcraftIntegration;
        public boolean enableMineTweakerIntegration;
        public boolean enableRefinedRelocationIntegration;
        public boolean enableThermalExpansionIntegration;
        public boolean enableThermalFoundationIntegration;
        public boolean enableChiselIntegration;
        public boolean enableGTNHIntegration;
        public boolean autoEnablePacks;
        public boolean enableNaturaPack;
        public boolean enableBopPack;
        public boolean enableForestryPack;
        public boolean enableErebusPack;
        public boolean enableMiscPack;
        public boolean enableTape;
        public boolean enableFallbackRecipes;
        public boolean enableFramedDrawers;
        public boolean invertShift;
        public boolean debugTrace;
        public boolean stackRemainderWaila;

        public int level2Mult;
        public int level3Mult;
        public int level4Mult;
        public int level5Mult;
        public int level6Mult;
        public int level7Mult;
        public int level8Mult;

        public boolean addonSeparateVanilla;
        public boolean addonShowNEI;
        public boolean addonShowVanilla;
    }

    private class AddonConfig implements IAddonConfig {

        @Override
        public boolean showAddonItemsNEI() {
            return cache.addonShowNEI;
        }

        @Override
        public boolean showAddonItemsVanilla() {
            return cache.addonShowVanilla;
        }

        @Override
        public boolean addonItemsUseSeparateTab() {
            return cache.addonSeparateVanilla;
        }
    }

    private class BlockConfig implements IBlockConfig {

        @Override
        public String getBlockConfigName(BlockConfiguration blockConfig) {
            switch (blockConfig) {
                case BasicFull1:
                case SortingFull1:
                    return "fulldrawers1";
                case BasicFull2:
                case SortingFull2:
                    return "fulldrawers2";
                case BasicFull4:
                case SortingFull4:
                    return "fulldrawers4";
                case BasicHalf2:
                case SortingHalf2:
                    return "halfdrawers2";
                case BasicHalf4:
                case SortingHalf4:
                    return "halfdrawers4";
                case Trim:
                case TrimSorting:
                    return "trim";
                default:
                    return null;
            }
        }

        @Override
        public boolean isBlockEnabled(String blockConfigName) {
            return ConfigManager.this.isBlockEnabled(blockConfigName);
        }

        @Override
        public int getBlockRecipeOutput(String blockConfigName) {
            return ConfigManager.this.getBlockRecipeOutput(blockConfigName);
        }

        @Override
        public int getBaseCapacity(String blockConfigName) {
            return ConfigManager.this.getBlockBaseStorage(blockConfigName);
        }
    }

    private class IntegrationConfig implements IIntegrationConfig {

        @Override
        public boolean isRefinedRelocationEnabled() {
            return cache.enableRefinedRelocationIntegration;
        }

        @Override
        public boolean isChiselEnabled() {
            return cache.enableChiselIntegration;
        }

        @Override
        public boolean isGTNHEnabled() {
            return cache.enableGTNHIntegration;
        }
    }

    private class PacksConfig implements IPacksConfig {

        @Override
        public boolean autoEnablePacks() {
            return cache.autoEnablePacks;
        }

        @Override
        public boolean isNaturaPackEnabled() {
            return cache.enableNaturaPack;
        }

        @Override
        public boolean isBopPackEnabled() {
            return cache.enableBopPack;
        }

        @Override
        public boolean isForestryPackEnabled() {
            return cache.enableForestryPack;
        }

        @Override
        public boolean isErebusPackEnabled() {
            return cache.enableErebusPack;
        }

        @Override
        public boolean isMiscPackEnabled() {
            return cache.enableMiscPack;
        }
    }

    private class UserConfig implements IUserConfig {

        @Override
        public IAddonConfig addonConfig() {
            return addonConfig;
        }

        @Override
        public IBlockConfig blockConfig() {
            return blockConfig;
        }

        @Override
        public IIntegrationConfig integrationConfig() {
            return integrationConfig;
        }

        @Override
        public IPacksConfig packsConfig() {
            return packsConfig;
        }
    }

    private static final String LANG_PREFIX = "storageDrawers.config.";

    private final Configuration config;
    public final ConfigCache cache;

    public final List<ConfigSection> sections = new ArrayList<ConfigSection>();
    public final ConfigSection sectionGeneral = new ConfigSection(sections, "general", "general");
    public final ConfigSection sectionIntegration = new ConfigSection(sections, "integration", "integration");
    public final ConfigSection sectionPacks = new ConfigSection(sections, "packs", "packs");
    public final ConfigSection sectionBlocks = new ConfigSection(sections, "blocks", "blocks");
    public final ConfigSection sectionUpgrades = new ConfigSection(sections, "upgrades", "upgrades");
    public final ConfigSection sectionAddons = new ConfigSection(sections, "addons", "addons");

    public final List<ConfigSection> blockSections = new ArrayList<ConfigSection>();
    public final ConfigSection sectionBlocksFullDrawers1x1 = new ConfigSection(
            blockSections,
            sectionBlocks,
            "fulldrawers1",
            "blocks.fullDrawers1");
    public final ConfigSection sectionBlocksFullDrawers1x2 = new ConfigSection(
            blockSections,
            sectionBlocks,
            "fulldrawers2",
            "blocks.fullDrawers2");
    public final ConfigSection sectionBlocksFullDrawers2x2 = new ConfigSection(
            blockSections,
            sectionBlocks,
            "fulldrawers4",
            "blocks.fullDrawers4");
    public final ConfigSection sectionBlocksHalfDrawers1x2 = new ConfigSection(
            blockSections,
            sectionBlocks,
            "halfdrawers2",
            "blocks.halfDrawers2");
    public final ConfigSection sectionBlocksHalfDrawers2x2 = new ConfigSection(
            blockSections,
            sectionBlocks,
            "halfdrawers4",
            "blocks.halfDrawers4");
    public final ConfigSection sectionBlocksCompDrawers = new ConfigSection(
            blockSections,
            sectionBlocks,
            "compdrawers",
            "blocks.compDrawers");
    public final ConfigSection sectionBlocksController = new ConfigSection(
            blockSections,
            sectionBlocks,
            "controller",
            "blocks.controller");
    public final ConfigSection sectionBlocksTrim = new ConfigSection(
            blockSections,
            sectionBlocks,
            "trim",
            "blocks.trim");
    public final ConfigSection sectionBlocksSlave = new ConfigSection(
            blockSections,
            sectionBlocks,
            "controllerSlave",
            "blocks.controllerSlave");

    public Map<String, ConfigSection> blockSectionsMap = new HashMap<String, ConfigSection>();

    public IAddonConfig addonConfig = new AddonConfig();
    public IBlockConfig blockConfig = new BlockConfig();
    public IIntegrationConfig integrationConfig = new IntegrationConfig();
    public IPacksConfig packsConfig = new PacksConfig();
    public IUserConfig userConfig = new UserConfig();

    // private Property itemRenderType;

    public ConfigManager(File file) {
        config = new Configuration(file);
        cache = new ConfigCache();

        for (ConfigSection section : sections) section.getCategory();

        for (ConfigSection section : blockSections) {
            section.getCategory();
            blockSectionsMap.put(section.name, section);
        }

        syncConfig();
    }

    public void syncConfig() {
        cache.enableIndicatorUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableIndicatorUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableIndicatorUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableStorageUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableStorageUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableStorageUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableLockUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableLockUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableLockUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableVoidUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableVoidUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableVoidUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableCreativeUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableCreativeUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableCreativeUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableShroudUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableShroudUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableShroudUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableQuantifyUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableQuantifyUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableQuantifyUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enablePersonalUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enablePersonalUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enablePersonalUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableSortingUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableSortingUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableSortingUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableRedstoneUpgrades = config.get(Configuration.CATEGORY_GENERAL, "enableRedstoneUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableRedstoneUpgrades").setRequiresMcRestart(true).getBoolean();
        cache.enableTape = config.get(Configuration.CATEGORY_GENERAL, "enableTape", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableTape").setRequiresMcRestart(true).getBoolean();
        cache.itemRenderType = config
                .get(Configuration.CATEGORY_GENERAL, "itemRenderType", "fast", null, new String[] { "fancy", "fast" })
                .setLanguageKey(LANG_PREFIX + "prop.itemRenderType").getString();
        cache.renderStorageUpgrades = config.get(Configuration.CATEGORY_GENERAL, "renderStorageUpgrades", true)
                .setLanguageKey(LANG_PREFIX + "prop.renderStorageUpgrades").getBoolean();
        cache.creativeTabVanillaWoods = config.get(Configuration.CATEGORY_GENERAL, "creativeTabVanillaWoods", true)
                .setLanguageKey(LANG_PREFIX + "prop.creativeTabVanillaWoods").getBoolean();
        cache.enableDrawerUI = config.get(Configuration.CATEGORY_GENERAL, "enableDrawerUI", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableDrawerUI").getBoolean();
        cache.enableSidedInput = config.get(Configuration.CATEGORY_GENERAL, "enableSidedInput", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableSidedInput").getBoolean();
        cache.enableSidedOutput = config.get(Configuration.CATEGORY_GENERAL, "enableSidedOutput", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableSidedOutput").getBoolean();
        cache.enableItemConversion = config.get(Configuration.CATEGORY_GENERAL, "enableItemConversion", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableItemConversion").getBoolean();
        cache.enableFallbackRecipes = config.get(Configuration.CATEGORY_GENERAL, "enableFallbackRecipes", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableFallbackRecipes").setRequiresMcRestart(true).getBoolean();
        cache.enableFramedDrawers = config.get(Configuration.CATEGORY_GENERAL, "enableFramedDrawers", true)
                .setLanguageKey(LANG_PREFIX + "prop.enableFramedDrawers").setRequiresMcRestart(true).getBoolean();
        cache.stackRemainderWaila = !config
                .get(
                        Configuration.CATEGORY_GENERAL,
                        "wailaStackRemainder",
                        "stack + remainder",
                        null,
                        new String[] { "exact", "stack + remainder" })
                .setLanguageKey(LANG_PREFIX + "prop.wailaStackRemainder").getString().equals("exact");
        cache.invertShift = config.get(
                Configuration.CATEGORY_GENERAL,
                "invertShift",
                false,
                "Inverts how shift works with drawers. If this is true, shifting will only give one item, where regular clicks will give a full stack. Leave false for default behavior.")
                .setLanguageKey(LANG_PREFIX + "prop.invertShift").getBoolean();
        cache.debugTrace = config.get(
                Configuration.CATEGORY_GENERAL,
                "enableDebugLogging",
                false,
                "Writes additional log messages while using the mod.  Mainly for debug purposes.  Should be kept disabled unless instructed otherwise.")
                .setLanguageKey(LANG_PREFIX + "prop.enableDebugLogging").getBoolean();
        cache.breakDrawerDropMode = config
                .get(
                        Configuration.CATEGORY_GENERAL,
                        "breakDrawerDropMode",
                        "mixed",
                        "Select: default, mixed, merge, destroy and cluster. ",
                        new String[] { "default", "mixed", "merge", "destroy", "cluster" })
                .setLanguageKey(LANG_PREFIX + "prop.breakDrawerDropMode").getString();

        cache.enableAE2Integration = config.get(sectionIntegration.getQualifiedName(), "enableAE2", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableAE2").setRequiresMcRestart(true).getBoolean();
        cache.enableWailaIntegration = config.get(sectionIntegration.getQualifiedName(), "enableWaila", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableWaila").setRequiresMcRestart(true).getBoolean();
        cache.enableThaumcraftIntegration = config.get(sectionIntegration.getQualifiedName(), "enableThaumcraft", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableThaumcraft").setRequiresMcRestart(true).getBoolean();
        cache.enableMineTweakerIntegration = config
                .get(sectionIntegration.getQualifiedName(), "enableMineTweaker", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableMineTweaker").setRequiresMcRestart(true).getBoolean();
        cache.enableRefinedRelocationIntegration = config
                .get(sectionIntegration.getQualifiedName(), "enableRefinedRelocation", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableRefinedRelocation").setRequiresMcRestart(true)
                .getBoolean();
        cache.enableThermalExpansionIntegration = config
                .get(sectionIntegration.getQualifiedName(), "enableThermalExpansion", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableThermalExpansion").setRequiresMcRestart(true)
                .getBoolean();
        cache.enableThermalFoundationIntegration = config
                .get(sectionIntegration.getQualifiedName(), "enableThermalFoundation", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableThermalFoundation").setRequiresMcRestart(true)
                .getBoolean();
        cache.enableChiselIntegration = config.get(sectionIntegration.getQualifiedName(), "enableChisel", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableChisel").setRequiresMcRestart(true).getBoolean();
        cache.enableGTNHIntegration = config.get(sectionIntegration.getQualifiedName(), "enableGTNH", true)
                .setLanguageKey(LANG_PREFIX + "integration.enableGTNH").setRequiresMcRestart(true).getBoolean();

        cache.autoEnablePacks = config.get(
                sectionPacks.getQualifiedName(),
                "autoEnablePacks",
                true,
                "If enabled, all explicit pack options will be ignored. Packs will be enabled if their corresponding mod is present (Not including misc pack)")
                .setLanguageKey(LANG_PREFIX + "packs.autoEnable").setRequiresMcRestart(true).getBoolean();
        cache.enableNaturaPack = config.get(sectionPacks.getQualifiedName(), "enableNatura", false)
                .setLanguageKey(LANG_PREFIX + "packs.enableNatura").setRequiresMcRestart(true).getBoolean();
        cache.enableBopPack = config.get(sectionPacks.getQualifiedName(), "enableBiomesOPlenty", false)
                .setLanguageKey(LANG_PREFIX + "packs.enableBop").setRequiresMcRestart(true).getBoolean();
        cache.enableForestryPack = config.get(sectionPacks.getQualifiedName(), "enableForestry", false)
                .setLanguageKey(LANG_PREFIX + "packs.enableForestry").setRequiresMcRestart(true).getBoolean();
        cache.enableErebusPack = config.get(sectionPacks.getQualifiedName(), "enableErebus", false)
                .setLanguageKey(LANG_PREFIX + "packs.enableErebus").setRequiresMcRestart(true).getBoolean();
        cache.enableMiscPack = config.get(sectionPacks.getQualifiedName(), "enableMisc", false)
                .setLanguageKey(LANG_PREFIX + "packs.enableMisc").setRequiresMcRestart(true).getBoolean();

        config.get(sectionBlocksFullDrawers1x1.getQualifiedName(), "enabled", true)
                .setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers1x1.getQualifiedName(), "baseStorage", 32)
                .setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksFullDrawers1x1.getQualifiedName(), "recipeOutput", 1)
                .setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "enabled", true)
                .setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "baseStorage", 16)
                .setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksFullDrawers1x2.getQualifiedName(), "recipeOutput", 2)
                .setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "enabled", true)
                .setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "baseStorage", 8)
                .setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksFullDrawers2x2.getQualifiedName(), "recipeOutput", 4)
                .setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "enabled", true)
                .setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "baseStorage", 8)
                .setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksHalfDrawers1x2.getQualifiedName(), "recipeOutput", 2)
                .setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "enabled", true)
                .setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "baseStorage", 4)
                .setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksHalfDrawers2x2.getQualifiedName(), "recipeOutput", 4)
                .setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksCompDrawers.getQualifiedName(), "enabled", true)
                .setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksCompDrawers.getQualifiedName(), "baseStorage", 16)
                .setLanguageKey(LANG_PREFIX + "prop.baseStorage").setRequiresWorldRestart(true);
        config.get(sectionBlocksCompDrawers.getQualifiedName(), "recipeOutput", 1)
                .setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksController.getQualifiedName(), "enabled", true)
                .setLanguageKey(LANG_PREFIX + "prop.enabled").setRequiresMcRestart(true);
        config.get(sectionBlocksController.getQualifiedName(), "range", 12)
                .setLanguageKey(LANG_PREFIX + "prop.controllerRange");
        config.get(sectionBlocksController.getQualifiedName(), "maxDrawers", 2048)
                .setLanguageKey(LANG_PREFIX + "prop.controllerMaxDrawers");

        config.get(sectionBlocksTrim.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled")
                .setRequiresMcRestart(true);
        config.get(sectionBlocksTrim.getQualifiedName(), "recipeOutput", 4)
                .setLanguageKey(LANG_PREFIX + "prop.recipeOutput").setRequiresMcRestart(true);

        config.get(sectionBlocksSlave.getQualifiedName(), "enabled", true).setLanguageKey(LANG_PREFIX + "prop.enabled")
                .setRequiresMcRestart(true);

        cache.level2Mult = config.get(sectionUpgrades.getQualifiedName(), "level2Mult", 2)
                .setLanguageKey(LANG_PREFIX + "upgrades.level2Mult").setRequiresWorldRestart(true).getInt();
        cache.level3Mult = config.get(sectionUpgrades.getQualifiedName(), "level3Mult", 3)
                .setLanguageKey(LANG_PREFIX + "upgrades.level3Mult").setRequiresWorldRestart(true).getInt();
        cache.level4Mult = config.get(sectionUpgrades.getQualifiedName(), "level4Mult", 5)
                .setLanguageKey(LANG_PREFIX + "upgrades.level4Mult").setRequiresWorldRestart(true).getInt();
        cache.level5Mult = config.get(sectionUpgrades.getQualifiedName(), "level5Mult", 8)
                .setLanguageKey(LANG_PREFIX + "upgrades.level5Mult").setRequiresWorldRestart(true).getInt();
        cache.level6Mult = config.get(sectionUpgrades.getQualifiedName(), "level6Mult", 13)
                .setLanguageKey(LANG_PREFIX + "upgrades.level6Mult").setRequiresWorldRestart(true).getInt();
        cache.level7Mult = config.get(sectionUpgrades.getQualifiedName(), "level7Mult", 21)
                .setLanguageKey(LANG_PREFIX + "upgrades.level7Mult").setRequiresWorldRestart(true).getInt();
        cache.level8Mult = config.get(sectionUpgrades.getQualifiedName(), "level8Mult", 34)
                .setLanguageKey(LANG_PREFIX + "upgrades.level8Mult").setRequiresWorldRestart(true).getInt();

        cache.addonShowNEI = config.get(sectionAddons.getQualifiedName(), "showBlocksInNEI", true)
                .setLanguageKey(LANG_PREFIX + "addons.showNEI").setRequiresWorldRestart(true).getBoolean();
        cache.addonShowVanilla = config.get(sectionAddons.getQualifiedName(), "showBlocksInCreative", true)
                .setLanguageKey(LANG_PREFIX + "addons.showCreative").setRequiresWorldRestart(true).getBoolean();
        cache.addonSeparateVanilla = config.get(sectionAddons.getQualifiedName(), "useSeparateCreativeTabs", true)
                .setLanguageKey(LANG_PREFIX + "addons.separateTabs").setRequiresMcRestart(true).getBoolean();

        getControllerRange();
        getControllerMaxDrawers();

        if (config.hasChanged()) config.save();
    }

    public boolean isFancyItemRenderEnabled() {
        return cache.itemRenderType.equals("fancy");
    }

    public String getPath() {
        return config.toString();
    }

    public boolean isBlockEnabled(String blockName) {
        if (!blockSectionsMap.containsKey(blockName)) return false;

        ConfigSection section = blockSectionsMap.get(blockName);
        return section.getCategory().get("enabled").getBoolean();
    }

    public int getBlockBaseStorage(String blockName) {
        if (!blockSectionsMap.containsKey(blockName)) return 0;

        ConfigSection section = blockSectionsMap.get(blockName);
        return section.getCategory().get("baseStorage").getInt();
    }

    public int getBlockRecipeOutput(String blockName) {
        if (!blockSectionsMap.containsKey(blockName)) return 0;

        ConfigSection section = blockSectionsMap.get(blockName);
        return section.getCategory().get("recipeOutput").getInt();
    }

    public int getControllerRange() {
        ConfigSection section = blockSectionsMap.get("controller");
        return section.getCategory().get("range").getInt();
    }

    public int getControllerMaxDrawers() {
        ConfigSection section = blockSectionsMap.get("controller");
        return section.getCategory().get("maxDrawers").getInt();
    }

    public int getStorageUpgradeMultiplier(int level) {
        switch (level) {
            case 2:
                return cache.level2Mult;
            case 3:
                return cache.level3Mult;
            case 4:
                return cache.level4Mult;
            case 5:
                return cache.level5Mult;
            case 6:
                return cache.level6Mult;
            case 7:
                return cache.level7Mult;
            case 8:
                return cache.level8Mult;
            default:
                return 1;
        }
    }
}
