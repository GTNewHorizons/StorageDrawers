package com.jaquadro.minecraft.storagedrawers.core;

import net.minecraft.item.Item;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.item.ItemPersonalKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemQuantifyKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemShroudKey;
import com.jaquadro.minecraft.storagedrawers.item.ItemTape;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeCreative;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeDowngrade;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeLock;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeRedstone;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeStatus;
import com.jaquadro.minecraft.storagedrawers.item.ItemUpgradeVoid;

import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems {

    public static Item upgradeTemplate;
    public static ItemUpgrade upgrade;
    public static ItemUpgradeStatus upgradeStatus;
    public static ItemUpgradeLock upgradeLock;
    public static ItemUpgradeVoid upgradeVoid;
    public static ItemUpgradeCreative upgradeCreative;
    public static ItemUpgradeRedstone upgradeRedstone;
    public static ItemShroudKey shroudKey;
    public static ItemPersonalKey personalKey;
    public static ItemQuantifyKey quantifyKey;
    public static ItemTape tape;
    public static ItemUpgradeDowngrade upgradeDowngrade;

    public void init() {
        upgradeTemplate = new Item().setUnlocalizedName(makeName("upgradeTemplate"))
                .setTextureName(StorageDrawers.MOD_ID + ":upgrade_template")
                .setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        upgrade = new ItemUpgrade(makeName("upgrade"));
        upgradeStatus = new ItemUpgradeStatus(makeName("upgradeStatus"));
        upgradeLock = new ItemUpgradeLock(makeName("upgradeLock"));
        upgradeVoid = new ItemUpgradeVoid(makeName("upgradeVoid"));
        upgradeCreative = new ItemUpgradeCreative(makeName("upgradeCreative"));
        upgradeRedstone = new ItemUpgradeRedstone(makeName("upgradeRedstone"));
        upgradeDowngrade = new ItemUpgradeDowngrade(makeName("upgradeDowngrade"));

        shroudKey = new ItemShroudKey(makeName("shroudKey"));
        personalKey = new ItemPersonalKey(makeName("personalKey"));
        quantifyKey = new ItemQuantifyKey(makeName("quantifyKey"));
        tape = new ItemTape(makeName("tape"));

        GameRegistry.registerItem(upgradeTemplate, "upgradeTemplate");

        if (StorageDrawers.config.cache.enableStorageUpgrades) GameRegistry.registerItem(upgrade, "upgrade");
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            GameRegistry.registerItem(upgradeStatus, "upgradeStatus");
        if (StorageDrawers.config.cache.enableVoidUpgrades) GameRegistry.registerItem(upgradeVoid, "upgradeVoid");
        if (StorageDrawers.config.cache.enableCreativeUpgrades)
            GameRegistry.registerItem(upgradeCreative, "upgradeCreative");
        if (StorageDrawers.config.cache.enableRedstoneUpgrades)
            GameRegistry.registerItem(upgradeRedstone, "upgradeRedstone");
        if (StorageDrawers.config.cache.enableLockUpgrades) GameRegistry.registerItem(upgradeLock, "upgradeLock");
        if (StorageDrawers.config.cache.enableShroudUpgrades) GameRegistry.registerItem(shroudKey, "shroudKey");
        if (StorageDrawers.config.cache.enableQuantifyUpgrades) GameRegistry.registerItem(quantifyKey, "quantifyKey");
        if (StorageDrawers.config.cache.enablePersonalUpgrades) GameRegistry.registerItem(personalKey, "personalKey");
        if (StorageDrawers.config.cache.enableTape) GameRegistry.registerItem(tape, "tape");
        GameRegistry.registerItem(upgradeDowngrade, "upgradeDowngrade");
    }

    public static String makeName(String name) {
        return StorageDrawers.MOD_ID.toLowerCase() + "." + name;
    }
}
