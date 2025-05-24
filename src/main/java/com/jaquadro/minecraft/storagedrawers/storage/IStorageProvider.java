package com.jaquadro.minecraft.storagedrawers.storage;

import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;

public interface IStorageProvider {

    public boolean isCentrallyManaged();

    public int getSlotCount(int slot);

    public void setSlotCount(int slot, int amount);

    public int getSlotStackCapacity(int slot);

    public boolean isLocked(int slot, LockAttribute attr);

    public boolean isVoid(int slot);

    public boolean isShrouded(int slot);

    public boolean isDowngraded(int slot);

    public boolean setIsShrouded(int slot, boolean state);

    public boolean isQuantified(int slot);

    public boolean setIsQuantified(int slot, boolean state);

    public boolean isStorageUnlimited(int slot);

    public boolean isVendingUnlimited(int slot);

    public boolean isRedstone(int slot);

    public void markAmountDirty(int slot);

    public void markDirty(int slot);
}
