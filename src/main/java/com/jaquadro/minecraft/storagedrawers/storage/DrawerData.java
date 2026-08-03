package com.jaquadro.minecraft.storagedrawers.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.event.DrawerPopulatedEvent;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ILockable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IQuantifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IShroudable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.util.ItemStackConversion;

public class DrawerData extends BaseDrawerData implements IVoidable, IShroudable, ILockable, IQuantifiable {

    private static final ItemStack nullStack = new ItemStack((Item) null);

    // Syncs Client with Server and marks chunk for saving when protostack/count changes
    private final IStorageProvider storageProvider;

    // Drawer Slot within IDrawerGroup
    private final int slot;

    // Item that is held in this slot
    private ItemStack protoStack;
    private int count;

    public DrawerData(IStorageProvider provider, int slot) {

        storageProvider = provider;
        protoStack = nullStack;
        this.slot = slot;

        postInit();
    }

    public void setLookupController(ItemStack itemPrototype) {

        if (controller == null || controller.isInvalid() || controllerDrawerSlot == -1) {
            clearTileEntityController();
        } else if (itemPrototype != nullStack) {
            controller.setLookup(
                    itemPrototype,
                    controllerDrawerSlot,
                    StorageDrawers.config.cache.enableItemConversion ? oreDictMatches : null);
        }

    }

    public void removeLookupController() {

        if (controller == null || controller.isInvalid() || controllerDrawerSlot == -1) {
            clearTileEntityController();
        }

        else if (protoStack != nullStack) {
            controller.removeLookup(
                    protoStack,
                    controllerDrawerSlot,
                    StorageDrawers.config.cache.enableItemConversion ? oreDictMatches : null);
        }
    }

    @Override
    public boolean isVendingUnlimited() {
        return storageProvider.isVendingUnlimited(slot);
    }

    @Override
    public ItemStack getStoredItemPrototype() {
        if (protoStack == nullStack) return null;

        return protoStack;
    }

    @Override
    public void setStoredItem(ItemStack itemPrototype, int amount) {
        setStoredItem(itemPrototype, amount, true);
    }

    @Override
    public IDrawer setStoredItemRedir(ItemStack itemPrototype, int amount) {
        setStoredItem(itemPrototype, amount, true);
        return this;
    }

    private void setStoredItem(ItemStack itemPrototype, int amount, boolean mark) {

        if (itemPrototype == null) {

            removeLookupController();

            boolean amountDirty = protoStack == null;

            protoStack = nullStack;
            inventoryStack.reset();

            setStoredItemCount(0, false, true, amountDirty);

            DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
            MinecraftForge.EVENT_BUS.post(event);

            return;
        }

        boolean amountDirty = (protoStack == null || !itemPrototype.isItemEqual(protoStack)
                || !ItemStack.areItemStackTagsEqual(itemPrototype, protoStack));

        protoStack = itemPrototype.copy();
        protoStack.stackSize = 1;
        inventoryStack.reset();
        refreshOreDictMatches();

        setLookupController(protoStack);

        setStoredItemCount(amount, mark, false, amountDirty);

        DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Override
    public int getStoredItemCount() {
        if (protoStack != nullStack && isVendingUnlimited()) return Integer.MAX_VALUE;

        return count;
    }

    @Override
    public void setStoredItemCount(int amount) {
        setStoredItemCount(amount, true, true, amount != 0);
    }

    public void setStoredItemCount(int amount, boolean mark, boolean clearOnEmpty, boolean amountDirty) {

        if (isVendingUnlimited()) return;

        count = amount;
        if (count > getMaxCapacity()) count = getMaxCapacity();

        if (amount == 0 && clearOnEmpty) {
            if (!storageProvider.isLocked(slot, LockAttribute.LOCK_POPULATED)) {
                reset();
            }
        }
        if (mark) {
            if (amountDirty) {
                storageProvider.markAmountDirty(slot, controller);
            } else {
                storageProvider.markDirty(controller);
            }
        }
    }

    @Override
    public int getMaxCapacity() {
        return getMaxCapacity(protoStack);
    }

    @Override
    public int getMaxCapacity(ItemStack itemPrototype) {
        if (itemPrototype == null || itemPrototype.getItem() == null) return 0;

        if (storageProvider.isStorageUnlimited(slot) || storageProvider.isVendingUnlimited(slot))
            return Integer.MAX_VALUE;

        return itemPrototype.getItem().getItemStackLimit(itemPrototype) * storageProvider.getSlotStackCapacity(slot);
    }

    @Override
    public int getRemainingCapacity() {
        if (protoStack.getItem() == null) return 0;

        if (storageProvider.isVendingUnlimited(slot)) return Integer.MAX_VALUE;

        return getMaxCapacity() - getStoredItemCount();
    }

    @Override
    public int getStoredItemStackSize() {
        if (protoStack.getItem() == null) return 0;

        return protoStack.getItem().getItemStackLimit(protoStack);
    }

    @Override
    protected int getItemCapacityForInventoryStack() {
        if (storageProvider.isVoid(slot)) return Integer.MAX_VALUE;
        else return getMaxCapacity();
    }

    @Override
    public boolean canItemBeStored(ItemStack itemPrototype) {
        if (protoStack == nullStack && !isLocked(LockAttribute.LOCK_EMPTY)) return true;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean canItemBeExtracted(ItemStack itemPrototype) {
        if (protoStack == nullStack) return false;

        return areItemsEqual(itemPrototype);
    }

    @Override
    public boolean isEmpty() {
        return protoStack == nullStack;
    }

    public void writeToNBT(NBTTagCompound tag) {
        ItemStackConversion.writeToNBT(tag, protoStack, count);
    }

    public void readFromNBT(NBTTagCompound tag) {
        final ItemStack stack = ItemStackConversion.readFromNBT(tag);
        if (stack == null) {
            reset();
        } else {
            final int count = stack.stackSize;
            stack.stackSize = 1;
            setStoredItem(stack, count, false);
        }
    }

    @Override
    protected void reset() {

        removeLookupController();
        protoStack = nullStack;
        super.reset();

        DrawerPopulatedEvent event = new DrawerPopulatedEvent(this);
        MinecraftForge.EVENT_BUS.post(event);
    }

    @Override
    public boolean isVoid() {
        return storageProvider.isVoid(slot);
    }

    @Override
    public boolean isShrouded() {
        return storageProvider.isShrouded(slot);
    }

    @Override
    public boolean setIsShrouded(boolean state) {
        return storageProvider.setIsShrouded(slot, state);
    }

    @Override
    public boolean isQuantified() {
        return storageProvider.isQuantified(slot);
    }

    @Override
    public boolean setIsQuantified(boolean state) {
        return storageProvider.setIsQuantified(slot, state);
    }

    @Override
    public boolean isLocked(LockAttribute attr) {
        return storageProvider.isLocked(slot, attr);
    }

    @Override
    public boolean canLock(LockAttribute attr) {
        return false;
    }

    @Override
    public void setLocked(LockAttribute attr, boolean isLocked) {}
}
