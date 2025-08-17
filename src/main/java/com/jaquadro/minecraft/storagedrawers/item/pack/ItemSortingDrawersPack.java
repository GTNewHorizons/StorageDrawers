package com.jaquadro.minecraft.storagedrawers.item.pack;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import com.jaquadro.minecraft.storagedrawers.block.pack.BlockSortingDrawersPack;

public class ItemSortingDrawersPack extends ItemDrawersPack {

    public ItemSortingDrawersPack(Block block) {
        super(block, getUnlocalizedNames(block));
    }

    private static String[] getUnlocalizedNames(Block block) {
        if (block instanceof BlockSortingDrawersPack) return ((BlockSortingDrawersPack) block).getUnlocalizedNames();
        else return new String[16];
    }

    @Override
    protected void addSubDescriptionInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocalFormatted("storageDrawers.waila.sorting"));
    }
}
