package com.jaquadro.minecraft.storagedrawers.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;

public class ItemCompDrawers extends ItemDrawers {

    public ItemCompDrawers(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(x, y, z);
        if (tile != null) {
            int initCapacity = StorageDrawers.config.getBlockBaseStorage("compdrawers");
            tile.setDrawerCapacity(initCapacity);

            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tile"))
                tile.readFromPortableNBT(stack.getTagCompound().getCompoundTag("tile"));

            if (side > 1) tile.setDirection(side);

            tile.setIsSealed(false);
        }

        return true;
    }

    @Override
    protected void addDrawersInformation(NBTTagCompound tag, List list) {
        NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
        int totalCount = tag.getInteger("Count");

        list.add(
                EnumChatFormatting.GRAY
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.compDrawerList"));

        for (int i = 0; i < slots.tagCount(); i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);
            ItemStack stack = getItemStackFromDrawer(slot);
            String slotCounter = EnumChatFormatting.YELLOW + " #" + (i + 1) + ": ";
            if (stack != null && tag.hasKey("Conv" + i)) {
                // "Convi" is the conversion rate, for example in vanilla nugget = 1, ingot = 9, block = 81
                int itemCount = totalCount / tag.getByte("Conv" + i);
                // Example of show in tooltips (if we have vanilla minecraft):
                // #1: Gold Block [1] - totalCount / 81
                // #2: Gold Ingot [9] - totalCount / 9
                // #3: Gold Nugget [1x64 + 17] - totalCount / 1
                list.add(
                        slotCounter + getGoodDisplayName(stack)
                                + " "
                                + getItemCountDisplay(stack.getMaxStackSize(), itemCount));
            } else {
                list.add(
                        slotCounter + EnumChatFormatting.DARK_GRAY
                                + StatCollector.translateToLocal("storageDrawers.drawers.sealed.drawerEmpty"));
            }
        }
    }
}
