package com.jaquadro.minecraft.storagedrawers.core;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandler {

    @SubscribeEvent
    public void playerInteracts(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
            if (tile instanceof TileEntityDrawers) {
                event.setCanceled(true);
            }
        }
    }
}
