package com.jaquadro.minecraft.storagedrawers.core;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.apache.logging.log4j.Level;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ForgeEventHandler {

    @SubscribeEvent
    public void playerInteracts(PlayerInteractEvent event) {
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
            TileEntity mystery_tile = event.world.getTileEntity(event.x, event.y, event.z);
            if (mystery_tile instanceof TileEntityDrawers tile
                    && event.world.getBlock(event.x, event.y, event.z) instanceof BlockDrawers block) {
                tile.onClick(event, block);
                event.setCanceled(true);
            }
        }
    }
}
