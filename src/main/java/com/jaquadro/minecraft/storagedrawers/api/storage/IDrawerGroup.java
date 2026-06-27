package com.jaquadro.minecraft.storagedrawers.api.storage;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.block.tile.BlockCoord;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;

public interface IDrawerGroup {

    /**
     * Search for a neighbour that has a controller coord for full controller update when this drawer group when a
     * drawer/slave is placed.
     */
    default void refreshController(World world, int x, int y, int z) {

        BlockCoord start = new BlockCoord(x, y, z);

        Queue<BlockCoord> searchCoordQueue = new LinkedList<BlockCoord>();
        Set<BlockCoord> visited = new HashSet<BlockCoord>();

        searchCoordQueue.add(start);
        visited.add(start);

        BlockCoord discoveredController = null;

        // Test direct surrounding neighbors (or multiple INetworked non drawer blocks (trims)) for a valid drawer Coord
        while (!searchCoordQueue.isEmpty()) {

            BlockCoord current = searchCoordQueue.poll();

            BlockCoord[] neighbors = new BlockCoord[] { new BlockCoord(current.x() + 1, current.y(), current.z()),
                    new BlockCoord(current.x() - 1, current.y(), current.z()),
                    new BlockCoord(current.x(), current.y(), current.z() + 1),
                    new BlockCoord(current.x(), current.y(), current.z() - 1),
                    new BlockCoord(current.x(), current.y() + 1, current.z()),
                    new BlockCoord(current.x(), current.y() - 1, current.z()), };

            for (BlockCoord n : neighbors) {

                // Must not have been visited before
                if (visited.contains(n)) continue;
                visited.add(n);

                // Must be an INetworked (Drawer, Controller, Slave, Trim)
                Block block = world.getBlock(n.x(), n.y(), n.z());
                if (!(block instanceof INetworked)) continue;

                // Drawer or slave
                TileEntity te = world.getTileEntity(n.x(), n.y(), n.z());
                if (te instanceof IDrawerGroup) {

                    IDrawerGroup drawer = (IDrawerGroup) te;

                    if (drawer.getControllerCoord() != null) {
                        discoveredController = drawer.getControllerCoord();
                    }
                }

                // Only trims added to queue
                else {
                    searchCoordQueue.add(n);
                }
            }
            if (discoveredController != null) {
                break;
            }
        }

        // No Controller found
        if (discoveredController == null) return;

        TileEntity controllerTe = world
                .getTileEntity(discoveredController.x(), discoveredController.y(), discoveredController.z());

        if (controllerTe instanceof TileEntityController) {
            ((TileEntityController) controllerTe).scheduleFullUpdate();
        }
    }

    /**
     * Schedule controller update if IDrawerGroup was linked when block broken.
     */
    void controllerFullUpdate();

    /**
     * Gets controller coordinate for this group.
     */
    BlockCoord getControllerCoord();

    /**
     * Sets controller coordinate for this group.
     */
    void setControllerCoord(int sourceControllerX, int sourceControllerY, int sourceControllrZ);

    /**
     * Clear Variables used for controller access if it is linked to that controllers coords
     */
    void clearControllerVariables(int sourceControllerX, int sourceControllerY, int sourceControllerZ,
            boolean nullController);

    /**
     * Gets the number of drawers contained within this group.
     */
    int getDrawerCount();

    /**
     * Gets the drawer at the given slot within this group.
     */
    IDrawer getDrawer(int slot);

    /**
     * Gets whether the drawer in the given slot is usable.
     */
    boolean isDrawerEnabled(int slot);

    IDrawerInventory getDrawerInventory();

    boolean markDirtyIfNeeded();
}
