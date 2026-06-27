package com.jaquadro.minecraft.storagedrawers.block.tile;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IPriorityGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.ISmartGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;

public class TileEntitySlave extends TileEntity implements IDrawerGroup, IPriorityGroup, ISmartGroup, ISidedInventory {

    private int[] inventorySlots = new int[] { 0 };
    private int[] drawerSlots = new int[] { 0 };

    private BlockCoord controllerCoord;
    private TileEntityController controller = null;
    private boolean isFirstTick = true;

    // Item that this Entity can extract with IInventory interface (Vanilla hoppers and Pipes. Only 1 item to reduce
    // overhead of otherwise having to cycling though many slots)
    private ItemStack IInventoryExtract;

    @Override
    public void readFromNBT(NBTTagCompound tag) {

        super.readFromNBT(tag);

        if (tag.hasKey("Controller", Constants.NBT.TAG_COMPOUND)) {
            NBTTagCompound ctag = tag.getCompoundTag("Controller");
            controllerCoord = new BlockCoord(ctag.getInteger("x"), ctag.getInteger("y"), ctag.getInteger("z"));
        }
        if (tag.hasKey("IInventoryExtract")) {
            IInventoryExtract = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("IInventoryExtract"));
        } else {
            IInventoryExtract = null;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        if (controllerCoord != null) {
            NBTTagCompound ctag = new NBTTagCompound();
            ctag.setInteger("x", controllerCoord.x());
            ctag.setInteger("y", controllerCoord.y());
            ctag.setInteger("z", controllerCoord.z());
            tag.setTag("Controller", ctag);
        }
        if (IInventoryExtract != null) {
            tag.setTag("IInventoryExtract", IInventoryExtract.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        getWorldObj().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
    }

    @Override
    public int[] getAccessibleDrawerSlots() {
        if (controller == null || controller.isInvalid()) return drawerSlots;
        return controller.getAccessibleDrawerSlots();
    }

    // =================================================================================================================

    public ItemStack getExtractionItem() {
        return IInventoryExtract;
    }

    public void setExtractionItem(ItemStack stack) {
        if (stack == null) {
            IInventoryExtract = null;
        } else {
            IInventoryExtract = stack.copy();
            IInventoryExtract.stackSize = 1;
        }
        markDirty();
    }

    @Override
    public int getSizeInventory() {
        if (controller == null || controller.isInvalid()) return 0;
        return controller.getSizeInventory();
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (controller == null || controller.isInvalid()) return inventorySlots;
        return controller.getAccessibleSlotsFromSide(0);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {

        // Insertion (slot 0)
        if (slot == 0 || IInventoryExtract == null) return null;
        if (controller == null || controller.isInvalid()) return null;

        // Extraction (slot 1)
        int amount = 0;
        for (int drawerSlot : controller.enumerateDrawersForExtraction(IInventoryExtract, true)) {
            IDrawer drawer = controller.getDrawer(drawerSlot);
            amount += drawer.getStoredItemCount();
            if (amount > this.getInventoryStackLimit()) {
                amount = this.getInventoryStackLimit();
                break;
            }
        }

        if (amount == 0) {
            return null;
        }

        ItemStack stackView = IInventoryExtract.copy();
        stackView.stackSize = amount;
        return stackView;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {

        if (slot == 1) return false;
        if (controller == null || controller.isInvalid()) return false;

        int itemsLeft = stack.stackSize;

        for (int drawerSlot : controller.enumerateDrawersForInsertion(stack, false)) {

            IDrawer drawer = controller.getDrawer(drawerSlot);

            ItemStack itemProto = drawer.getStoredItemPrototype();
            if (itemProto == null) {
                return true;
            }

            itemsLeft = controller.insertItemsIntoDrawer(drawer, itemsLeft, true);

            if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) itemsLeft = 0;
            if (itemsLeft == 0) return true;
        }

        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {

        if (slot == 0 || IInventoryExtract == null
                || stack == null
                || !stack.isItemEqual(IInventoryExtract)
                || !ItemStack.areItemStackTagsEqual(stack, IInventoryExtract))
            return false;

        if (controller == null || controller.isInvalid()) return false;

        int itemsLeft = stack.stackSize;

        for (int drawerSlot : controller.enumerateDrawersForExtraction(stack, true)) {

            IDrawer drawer = controller.getDrawer(drawerSlot);
            int itemCount = drawer.getStoredItemCount();

            if (itemsLeft > itemCount) {
                itemsLeft -= itemCount;
            } else {
                itemsLeft = 0;
            }

            if (itemsLeft == 0) return true;
        }
        return false;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {

        if (controller == null || controller.isInvalid()) return;

        // Insertion
        if (slot == 0) {

            if (stack == null) return;

            int itemsLeft = stack.stackSize;

            for (int drawerSlot : controller.enumerateDrawersForInsertion(stack, false)) {

                IDrawer drawer = controller.getDrawer(drawerSlot);

                ItemStack itemProto = drawer.getStoredItemPrototype();
                if (itemProto == null) {
                    drawer = drawer.setStoredItemRedir(stack, 0);
                }

                itemsLeft = controller.insertItemsIntoDrawer(drawer, itemsLeft, false);

                if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) itemsLeft = 0;
                if (itemsLeft == 0) break;
            }

        }

        // Extraction
        else if (slot == 1 && IInventoryExtract != null) {

            if (stack == null || (stack.isItemEqual(IInventoryExtract)
                    && ItemStack.areItemStackTagsEqual(stack, IInventoryExtract))) {

                // Get avaliable items in slot up to 64
                int avaliableItems = 0;
                for (int drawerSlot : controller.enumerateDrawersForExtraction(IInventoryExtract, true)) {
                    IDrawer drawer = controller.getDrawer(drawerSlot);
                    avaliableItems += drawer.getStoredItemCount();
                    if (avaliableItems > this.getInventoryStackLimit()) {
                        avaliableItems = this.getInventoryStackLimit();
                        break;
                    }
                }

                // Remove enough to make equal to stack.stackSize or 0 if stack is null
                int toRemove = avaliableItems - ((stack == null) ? 0 : stack.stackSize);
                for (int drawerSlot : controller.enumerateDrawersForExtraction(IInventoryExtract, true)) {

                    IDrawer drawer = controller.getDrawer(drawerSlot);
                    int itemCount = drawer.getStoredItemCount();

                    if (toRemove > itemCount) {
                        drawer.setStoredItemCount(0);
                        toRemove -= itemCount;
                    } else {
                        drawer.setStoredItemCount(itemCount - toRemove);
                        toRemove = 0;
                    }
                    if (toRemove == 0) break;
                }
            }
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {

        if (slot == 0 || IInventoryExtract == null) return null;
        if (controller == null || controller.isInvalid()) return null;

        int amountRemoved = 0;
        for (int drawerSlot : controller.enumerateDrawersForExtraction(IInventoryExtract, true)) {

            IDrawer drawer = getDrawer(drawerSlot);
            int itemCount = drawer.getStoredItemCount();

            if (count > itemCount) {
                drawer.setStoredItemCount(0);
                amountRemoved += itemCount;
                count -= itemCount;
            } else {
                drawer.setStoredItemCount(itemCount - count);
                amountRemoved += (itemCount - count);
                count = 0;
            }

            if (count == 0) return IInventoryExtract;
        }

        ItemStack itemsLeft = IInventoryExtract.copy();
        itemsLeft.stackSize = amountRemoved;
        return itemsLeft;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        if (controller == null || controller.isInvalid()) return null;
        return controller.getStackInSlotOnClosing(slot);
    }

    @Override
    public String getInventoryName() {
        if (controller == null || controller.isInvalid()) return "storageDrawers.container.unboundSlave";
        return controller.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        if (controller == null || controller.isInvalid()) return false;
        return controller.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public void controllerFullUpdate() {
        if (controller == null || controller.isInvalid()) return;
        controller.scheduleFullUpdate();
    }

    @Override
    public BlockCoord getControllerCoord() {
        return controllerCoord;
    }

    @Override
    public void setControllerCoord(int sourceControllerX, int sourceControllerY, int sourceControllerZ) {

        if (controllerCoord == null || controllerCoord.x() != sourceControllerX
                || controllerCoord.y() != sourceControllerY
                || controllerCoord.z() != sourceControllerZ) {
            controllerCoord = new BlockCoord(sourceControllerX, sourceControllerY, sourceControllerZ);
            markDirty();
        }

        if (worldObj == null) return;

        TileEntity te = worldObj.getTileEntity(controllerCoord.x(), controllerCoord.y(), controllerCoord.z());
        if (te instanceof TileEntityController) {
            controller = (TileEntityController) te;
        }
    }

    @Override
    public void clearControllerVariables(int sourceControllerX, int sourceControllerY, int sourceControllerZ,
            boolean nullController) {

        if (controllerCoord == null || controllerCoord.x() != sourceControllerX
                || controllerCoord.y() != sourceControllerY
                || controllerCoord.z() != sourceControllerZ) {

        }
        if (nullController) {
            controllerCoord = null;
            controller = null;
            markDirty();
        }
    }

    public TileEntityController getController() {
        return controller;
    }

    @Override
    public void updateEntity() {

        if (isFirstTick && worldObj != null && !worldObj.isRemote && controllerCoord != null) {

            isFirstTick = false;

            TileEntity te = worldObj.getTileEntity(controllerCoord.x(), controllerCoord.y(), controllerCoord.z());

            if (te instanceof TileEntityController) {
                controller = ((TileEntityController) te);
                ((TileEntityController) te).scheduleFullUpdate();
            }
        }
    }

    @Override
    public int getDrawerCount() {
        if (controller == null || controller.isInvalid()) return 0;
        return controller.getDrawerCount();
    }

    @Override
    public IDrawer getDrawer(int slot) {
        if (controller == null || controller.isInvalid()) return null;
        return controller.getDrawer(slot);
    }

    @Override
    public boolean isDrawerEnabled(int slot) {
        if (controller == null || controller.isInvalid()) return false;
        return controller.isDrawerEnabled(slot);
    }

    @Override
    public IDrawerInventory getDrawerInventory() {
        if (controller == null || controller.isInvalid()) return null;
        return controller.getDrawerInventory();
    }

    @Override
    public Iterable<Integer> enumerateDrawersForInsertion(ItemStack stack, boolean strict) {
        if (controller == null || controller.isInvalid()) return new ArrayList<Integer>();
        return controller.enumerateDrawersForInsertion(stack, strict);
    }

    @Override
    public Iterable<Integer> enumerateDrawersForExtraction(ItemStack stack, boolean strict) {
        if (controller == null || controller.isInvalid()) return new ArrayList<Integer>();
        return controller.enumerateDrawersForExtraction(stack, strict);
    }

    @Override
    public void markDirty() {

        TileEntityController controller = getController();
        if (controller != null && !controller.isInvalid()) controller.markDirty();

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded() {
        if (controller != null && !controller.isInvalid()) return controller.markDirtyIfNeeded();
        return false;
    }
}
