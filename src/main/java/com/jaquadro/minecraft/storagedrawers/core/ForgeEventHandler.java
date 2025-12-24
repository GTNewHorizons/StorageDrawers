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
                int dir = tile.getDirection();
                if (dir == event.face) {
                    final int reach = 5;
                    double eyeX = event.entityPlayer.posX;
                    double eyeY = event.entityPlayer.posY + event.entityPlayer.getEyeHeight();
                    double eyeZ = event.entityPlayer.posZ;

                    Vec3 look = event.entityPlayer.getLookVec();

                    Vec3 end = Vec3.createVectorHelper(
                            eyeX + look.xCoord * reach,
                            eyeY + look.yCoord * reach,
                            eyeZ + look.zCoord * reach);

                    MovingObjectPosition mop = event.world
                            .rayTraceBlocks(Vec3.createVectorHelper(eyeX, eyeY, eyeZ), end);
                    float hitX = (float) (mop.hitVec.xCoord - mop.blockX);
                    float hitY = (float) (mop.hitVec.yCoord - mop.blockY);
                    float hitZ = (float) (mop.hitVec.zCoord - mop.blockZ);
                    boolean invertShift = StorageDrawers.config.cache.invertShift;

                    int slot = block.getDrawerSlot(event.face, hitX, hitY, hitZ);
                    IDrawer drawer = tile.getDrawer(slot);
                    if (drawer == null) return;
                    final ItemStack item;
                    // if invertSHift is true this will happen when the player is not shifting
                    if (event.entityPlayer.isSneaking() != invertShift)
                        item = tile.takeItemsFromSlot(slot, drawer.getStoredItemStackSize());
                    else item = tile.takeItemsFromSlot(slot, 1);

                    if (StorageDrawers.config.cache.debugTrace)
                        FMLLog.log(StorageDrawers.MOD_ID, Level.INFO, (item == null) ? "  null item" : "  " + item);

                    if (item != null && item.stackSize > 0) {
                        if (!event.entityPlayer.inventory.addItemStackToInventory(item)) {
                            ForgeDirection dir2 = ForgeDirection.getOrientation(event.face);
                            block.dropItemStack(
                                    event.world,
                                    event.x + dir2.offsetX,
                                    event.y,
                                    event.z + dir2.offsetZ,
                                    item);
                            event.world.markBlockForUpdate(event.x, event.y, event.z);
                        } else event.world.playSoundEffect(
                                event.x + .5f,
                                event.y + .5f,
                                event.z + .5f,
                                "random.pop",
                                .2f,
                                ((event.world.rand.nextFloat() - event.world.rand.nextFloat()) * .7f + 1) * 2);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
