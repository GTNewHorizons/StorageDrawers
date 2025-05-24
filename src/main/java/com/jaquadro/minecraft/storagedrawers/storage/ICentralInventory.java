package com.jaquadro.minecraft.storagedrawers.storage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;

public interface ICentralInventory {

    public ItemStack getStoredItemPrototype(int slot);

    public IDrawer setStoredItem(int slot, ItemStack itemPrototype, int amount);

    public int getStoredItemCount(int slot);

    public void setStoredItemCount(int slot, int amount);

    public int getMaxCapacity(int slot);

    public int getMaxCapacity(int slot, ItemStack itemPrototype);

    public int getRemainingCapacity(int slot);

    public int getStoredItemStackSize(int slot);

    public int getItemCapacityForInventoryStack(int slot);

    public int getConversionRate(int slot);

    public int getStoredItemRemainder(int slot);

    public boolean isSmallestUnit(int slot);

    public boolean isVoidSlot(int slot);

    public boolean isShroudedSlot(int slot);

    public boolean setIsSlotShrouded(int slot, boolean state);

    public boolean isQuantifiedSlot(int slot);

    public boolean setIsSlotQuantifiable(int slot, boolean state);

    public boolean isLocked(int slot, LockAttribute attr);

    public void readFromNBT(int slot, NBTTagCompound tag);

    public void writeToNBT(int slot, NBTTagCompound tag);
}
