package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility to convert from the nbt storage format for ItemStacks to the new
 */
public final class ItemStackConversion {

    private ItemStackConversion() {}

    public static void writeToNBT(@NotNull NBTTagCompound tag, @Nullable ItemStack stack, int stacksize) {
        if (stack != null && stack.getItem() != null) {
            tag.setTag("ItemStack", stack.writeToNBT(new NBTTagCompound()));
            tag.setInteger("Count", stacksize);
        }
    }

    @Nullable
    public static ItemStack readFromNBT(@NotNull NBTTagCompound tag) {
        if (tag.hasKey("ItemStack", NBT.TAG_COMPOUND)) {
            final ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("ItemStack"));
            if (stack != null && tag.hasKey("Count", NBT.TAG_INT)) {
                stack.stackSize = tag.getInteger("Count");
            }
            return stack;
        }
        return readFromLegacyNBT(tag);
    }

    @Nullable
    private static ItemStack readFromLegacyNBT(@NotNull NBTTagCompound tag) {
        if (tag.hasKey("Item") && tag.hasKey("Count")) {
            final NBTTagCompound stackNBT = new NBTTagCompound();
            stackNBT.setShort("id", tag.getShort("Item"));
            stackNBT.setByte("Count", (byte) 1);
            stackNBT.setShort("Damage", tag.getShort("Meta"));
            if (tag.hasKey("Tags", NBT.TAG_COMPOUND)) {
                stackNBT.setTag("tag", tag.getCompoundTag("Tags"));
            }
            final ItemStack stack = ItemStack.loadItemStackFromNBT(stackNBT);
            if (stack != null) stack.stackSize = tag.getInteger("Count");
            return stack;
        }
        return null;
    }
}
