package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.integration.RefinedRelocation;
import com.jaquadro.minecraft.storagedrawers.item.ItemBasicDrawers;

public class ItemSortingDrawers extends ItemBasicDrawers {

    public ItemSortingDrawers(Block block) {
        super(block);
    }

    @Override
    protected void addDescriptionInformation(int drawerCapacity, List list) {
        super.addDescriptionInformation(drawerCapacity, list);
        list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted("storageDrawers.waila.sorting"));
    }

    @Override
    protected int getCapacityForBlock(Block block) {
        ConfigManager config = StorageDrawers.config;
        int count = 0;

        if (block == RefinedRelocation.fullDrawers1) count = config.getBlockBaseStorage("fulldrawers1");
        else if (block == RefinedRelocation.fullDrawers2) count = config.getBlockBaseStorage("fulldrawers2");
        else if (block == RefinedRelocation.fullDrawers4) count = config.getBlockBaseStorage("fulldrawers4");
        else if (block == RefinedRelocation.halfDrawers2) count = config.getBlockBaseStorage("halfdrawers2");
        else if (block == RefinedRelocation.halfDrawers4) count = config.getBlockBaseStorage("halfdrawers4");
        else if (block == RefinedRelocation.compDrawers) count = config.getBlockBaseStorage("compDrawers");

        return count;
    }
}
