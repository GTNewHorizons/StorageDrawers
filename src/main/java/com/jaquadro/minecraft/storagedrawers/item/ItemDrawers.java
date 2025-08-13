package com.jaquadro.minecraft.storagedrawers.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDrawers extends ItemBlock {

    public ItemDrawers(Block block) {
        super(block);
        setMaxDamage(0);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(x, y, z);
        if (tile != null) {
            BlockDrawers block = (BlockDrawers) field_150939_a;
            if (tile instanceof TileEntityDrawersStandard)
                ((TileEntityDrawersStandard) tile).setDrawerCount(block.drawerCount);

            tile.setDrawerCapacity(getCapacityForBlock(block));

            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tile"))
                tile.readFromPortableNBT(stack.getTagCompound().getCompoundTag("tile"));

            if (side > 1) tile.setDirection(side);

            tile.setIsSealed(false);
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        list.add(
                StatCollector
                        .translateToLocalFormatted("storageDrawers.drawers.description", getCapacityForBlock(block)));

        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("tile")) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                NBTTagCompound drawerNBT = itemStack.getTagCompound().getCompoundTag("tile");
                TileEntityDrawers drawerTile = (TileEntityDrawers) TileEntityDrawers.createAndLoadEntity(drawerNBT);

                // Check if the drawer is owned by player.
                if (drawerTile.getOwner() == null) {
                    list.add(
                            EnumChatFormatting.DARK_GRAY + StatCollector
                                    .translateToLocalFormatted("storageDrawers.drawers.sealed.no_owned"));
                } else {
                    if (drawerTile.getOwner().equals(player.getUniqueID())) list.add(
                            EnumChatFormatting.GREEN + StatCollector
                                    .translateToLocalFormatted("storageDrawers.drawers.sealed.access_yes"));
                    else list.add(
                            EnumChatFormatting.RED + StatCollector
                                    .translateToLocalFormatted("storageDrawers.drawers.sealed.access_no"));
                }

                // Check if the drawer is locked
                if (drawerTile.isLocked(LockAttribute.LOCK_POPULATED)) list.add(
                        EnumChatFormatting.YELLOW
                                + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.locked"));
                else list.add(
                        EnumChatFormatting.DARK_GRAY
                                + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.unlocked"));

                // Check if the drawer show or hide items
                if (drawerTile.isShrouded()) list.add(
                        EnumChatFormatting.YELLOW + StatCollector
                                .translateToLocalFormatted("storageDrawers.drawers.sealed.hideItemLabel"));
                else list.add(
                        EnumChatFormatting.DARK_GRAY + StatCollector
                                .translateToLocalFormatted("storageDrawers.drawers.sealed.showItemLabel"));

                // Check if the drawer show or hide quantify of items
                if (drawerTile.isQuantified()) list.add(
                        EnumChatFormatting.YELLOW + StatCollector
                                .translateToLocalFormatted("storageDrawers.drawers.sealed.showItemQuantity"));
                else list.add(
                        EnumChatFormatting.DARK_GRAY + StatCollector
                                .translateToLocalFormatted("storageDrawers.drawers.sealed.hideItemQuantity"));

                // Show upgrades of drawer
                list.add(
                        EnumChatFormatting.YELLOW
                                + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.upgrades"));
                for (int i = 0; i < drawerTile.getUpgradeSlotCount(); i++) {
                    ItemStack drawerUpgrade = drawerTile.getUpgrade(i);
                    if (drawerUpgrade != null)
                        list.add("    " + drawerUpgrade.getRarity().rarityColor + drawerUpgrade.getDisplayName());
                    else list.add(
                            "    " + EnumChatFormatting.DARK_GRAY
                                    + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.empty"));
                }

                // Show items of drawer
                list.add(
                        EnumChatFormatting.YELLOW
                                + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.items"));
                for (int i = 0; i < drawerTile.getDrawerCount(); i++) {
                    IDrawer drawer = drawerTile.getDrawer(i);
                    ItemStack storedItem = drawer.getStoredItemCopy();
                    if (storedItem != null) {
                        if (storedItem.hasDisplayName()) {
                            list.add(
                                    "    " + EnumChatFormatting.BLUE
                                            + drawer.getStoredItemCount()
                                            + "x "
                                            + storedItem.getRarity().rarityColor
                                            + EnumChatFormatting.ITALIC
                                            + storedItem.getDisplayName());
                        } else {
                            list.add(
                                    "    " + EnumChatFormatting.BLUE
                                            + drawer.getStoredItemCount()
                                            + "x "
                                            + storedItem.getRarity().rarityColor
                                            + storedItem.getDisplayName());
                        }
                    } else list.add(
                            "    " + EnumChatFormatting.DARK_GRAY
                                    + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.empty"));
                }
            } else {
                list.add(
                        EnumChatFormatting.YELLOW
                                + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed"));
                list.add(
                        EnumChatFormatting.DARK_GRAY + StatCollector
                                .translateToLocalFormatted("storageDrawers.drawers.sealed.description_shift"));
            }
        }
    }

    protected int getCapacityForBlock(Block block) {
        ConfigManager config = StorageDrawers.config;
        int count = 0;

        if (!(block instanceof BlockDrawers)) return 0;

        BlockDrawers drawer = (BlockDrawers) block;

        if (drawer.drawerCount == 1) count = config.getBlockBaseStorage("fulldrawers1");
        else if (drawer.drawerCount == 2 && !drawer.halfDepth) count = config.getBlockBaseStorage("fulldrawers2");
        else if (drawer.drawerCount == 4 && !drawer.halfDepth) count = config.getBlockBaseStorage("fulldrawers4");
        else if (drawer.drawerCount == 2 && drawer.halfDepth) count = config.getBlockBaseStorage("halfdrawers2");
        else if (drawer.drawerCount == 4 && drawer.halfDepth) count = config.getBlockBaseStorage("halfdrawers4");
        else if (drawer.drawerCount == 3) count = config.getBlockBaseStorage("compDrawers");

        return count;
    }
}
