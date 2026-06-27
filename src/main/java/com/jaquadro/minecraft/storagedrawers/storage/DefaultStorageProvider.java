package com.jaquadro.minecraft.storagedrawers.storage;

import net.minecraft.tileentity.TileEntity;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class DefaultStorageProvider implements IStorageProvider {

    TileEntity tile;
    IDrawerGroup group;

    /**
     * If the DrawerData that this is in has a drawer controller, markDirty/markAmountDirty schedules client sync
     */
    public DefaultStorageProvider(TileEntity tileEntity, IDrawerGroup drawerGroup) {
        tile = tileEntity;
        group = drawerGroup;
    }

    @Override
    public boolean isCentrallyManaged() {
        return false;
    }

    @Override
    public int getSlotCount(int slot) {
        return 0;
    }

    @Override
    public void setSlotCount(int slot, int amount) {}

    @Override
    public int getSlotStackCapacity(int slot) {
        return 0;
    }

    @Override
    public boolean isLocked(int slot, LockAttribute attr) {
        return false;
    }

    @Override
    public boolean isVoid(int slot) {
        return false;
    }

    @Override
    public boolean isShrouded(int slot) {
        return false;
    }

    @Override
    public boolean isDowngraded(int slot) {
        return false;
    }

    @Override
    public boolean setIsShrouded(int slot, boolean state) {
        return false;
    }

    @Override
    public boolean isQuantified(int slot) {
        return false;
    }

    @Override
    public boolean setIsQuantified(int slot, boolean state) {
        return false;
    }

    @Override
    public boolean isStorageUnlimited(int slot) {
        return false;
    }

    @Override
    public boolean isVendingUnlimited(int slot) {
        return false;
    }

    @Override
    public boolean isRedstone(int slot) {
        return false;
    }

    @Override
    public void markAmountDirty(int slot, TileEntityController controller) {
        if (tile.getWorldObj().isRemote) return;

        if (controller != null && !controller.isInvalid() && !controller.getClientAutoSync()) {
            controller.addClientSyncList(tile.xCoord, tile.yCoord, tile.zCoord);
        } else {

            tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);

            int count = group.getDrawer(slot).getStoredItemCount();
            IMessage message = new CountUpdateMessage(tile.xCoord, tile.yCoord, tile.zCoord, slot, count);
            NetworkRegistry.TargetPoint targetPoint = new NetworkRegistry.TargetPoint(
                    tile.getWorldObj().provider.dimensionId,
                    tile.xCoord,
                    tile.yCoord,
                    tile.zCoord,
                    500);
            StorageDrawers.network.sendToAllAround(message, targetPoint);
        }

        if (isRedstone(slot)) {
            tile.getWorldObj().notifyBlocksOfNeighborChange(tile.xCoord, tile.yCoord, tile.zCoord, tile.getBlockType());
            tile.getWorldObj()
                    .notifyBlocksOfNeighborChange(tile.xCoord, tile.yCoord - 1, tile.zCoord, tile.getBlockType());

        }
        tile.getWorldObj().markTileEntityChunkModified(tile.xCoord, tile.yCoord, tile.zCoord, tile);
    }

    @Override
    public void markDirty(TileEntityController controller) {
        if (tile.getWorldObj().isRemote) return;

        if (controller != null && !controller.isInvalid() && !controller.getClientAutoSync()) {
            controller.addClientSyncList(tile.xCoord, tile.yCoord, tile.zCoord);
        } else {
            tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
        }
        tile.getWorldObj().markTileEntityChunkModified(tile.xCoord, tile.yCoord, tile.zCoord, tile);
    }
}
