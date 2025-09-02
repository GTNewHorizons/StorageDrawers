package com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.jaquadro.minecraft.storagedrawers.item.ItemCompDrawers;

public class ItemSortingCompDrawers extends ItemCompDrawers {

    public ItemSortingCompDrawers(Block block) {
        super(block);
    }

    @Override
    protected void addDescriptionInformation(int drawerCapacity, List list) {
        super.addDescriptionInformation(drawerCapacity, list);
        list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted("storageDrawers.waila.sorting"));
    }
}
