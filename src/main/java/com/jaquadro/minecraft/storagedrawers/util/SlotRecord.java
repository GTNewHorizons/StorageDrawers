package com.jaquadro.minecraft.storagedrawers.util;

import com.jaquadro.minecraft.storagedrawers.block.tile.BlockCoord;

public class SlotRecord {

    public final BlockCoord coord;

    /** Slot inside Drawer */
    public final int slot;

    /** Index inside controller drawerSlotList variable */
    public int listIndex;

    /** Index inside controller drawerSlots variable */
    public int priorityIndex;

    public SlotRecord(BlockCoord coord, int slot, int listIndex) {
        this.coord = coord;
        this.slot = slot;
        this.listIndex = listIndex;
        this.priorityIndex = -1;
    }
}
