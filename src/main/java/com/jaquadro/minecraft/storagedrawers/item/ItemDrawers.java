package com.jaquadro.minecraft.storagedrawers.item;

import java.util.ArrayList;
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
                StringBuilder infoStatesBuilder = new StringBuilder();

                // Show locked drawer status
                if (drawerTile.isLocked(LockAttribute.LOCK_POPULATED)) {
                    infoStatesBuilder.append(EnumChatFormatting.YELLOW)
                            .append(StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.locked"));
                } else {
                    infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY)
                            .append(StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.unlocked"));
                }

                infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY).append(", ");

                // Show if drawer is Public or Protected
                if (drawerTile.getOwner() != null) {
                    // Check if the current player holding drawer is the owner
                    if (player.getUniqueID().equals(drawerTile.getOwner())) {
                        infoStatesBuilder.append(EnumChatFormatting.GREEN);
                    } else {
                        infoStatesBuilder.append(EnumChatFormatting.RED);
                    }
                    infoStatesBuilder.append(
                            StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.access_owner"));
                } else {
                    infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY).append(
                            StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.access_public"));
                }

                // Show two previous status in single line
                list.add(infoStatesBuilder.toString());

                // Show if drawer hide item label
                if (drawerTile.isShrouded()) {
                    list.add(
                            EnumChatFormatting.WHITE + StatCollector
                                    .translateToLocalFormatted("storageDrawers.drawers.sealed.hideItemLabel"));
                } else {
                    list.add(
                            EnumChatFormatting.DARK_GRAY + StatCollector
                                    .translateToLocalFormatted("storageDrawers.drawers.sealed.showItemLabel"));
                }

                // Show if drawer show item quantity
                if (!drawerTile.isQuantified()) {
                    list.add(
                            EnumChatFormatting.DARK_GRAY + StatCollector
                                    .translateToLocalFormatted("storageDrawers.drawers.sealed.hideItemQuantity"));
                } else {
                    list.add(
                            EnumChatFormatting.WHITE + StatCollector
                                    .translateToLocalFormatted("storageDrawers.drawers.sealed.showItemQuantity"));
                }

                // Show items inside.
                list.add(
                        EnumChatFormatting.GRAY
                                + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.itemList"));
                for (int i = 0; i < drawerTile.getDrawerCount(); i++) {
                    IDrawer drawerInventory = drawerTile.getDrawer(i);
                    ItemStack storedItem = drawerInventory.getStoredItemCopy();

                    // Create builder and add number of slot
                    StringBuilder infoItemBuilder = new StringBuilder(
                            EnumChatFormatting.YELLOW + "  #" + (i + 1) + ": ");

                    // If item is null just add "<Empty" else quantity and display name.
                    if (storedItem != null) {
                        // Calculates quantities
                        int quantityNumStack = drawerInventory.getStoredItemCount() / storedItem.getMaxStackSize();
                        int quantityRemainer = drawerInventory.getStoredItemCount()
                                - quantityNumStack * storedItem.getMaxStackSize();

                        // Add rarity color and display name
                        infoItemBuilder.append(storedItem.getRarity().rarityColor);
                        if (storedItem.hasDisplayName()) {
                            infoItemBuilder.append(EnumChatFormatting.ITALIC).append(storedItem.getDisplayName())
                                    .append(EnumChatFormatting.RESET);
                        } else {
                            infoItemBuilder.append(storedItem.getDisplayName());
                        }

                        // Add space between display name and quantity and then add quantity of items.
                        infoItemBuilder.append(" ").append(EnumChatFormatting.BLUE).append("[");
                        if (quantityNumStack > 0) {
                            infoItemBuilder.append(storedItem.getMaxStackSize()).append("x").append(quantityNumStack);
                            if (quantityRemainer > 0) {
                                infoItemBuilder.append(" + ").append(quantityRemainer);
                            }
                        } else {
                            infoItemBuilder.append(quantityRemainer);
                        }
                        infoItemBuilder.append("]");

                        // add to tooltip drawer item info in certain slot.
                        list.add(infoItemBuilder.toString());
                    } else {
                        infoItemBuilder.append(EnumChatFormatting.DARK_GRAY)
                                .append(StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.empty"));
                        list.add(infoItemBuilder.toString());
                    }
                }

                // Create a list of " #(slot_id): upgrade name"
                List<String> upgradeInfoList = new ArrayList<>();
                for (int i = 0; i < drawerTile.getUpgradeSlotCount(); i++) {
                    ItemStack drawerUpgrade = drawerTile.getUpgrade(i);
                    if (drawerUpgrade != null) {
                        StringBuilder infoUpgradeBuilder = new StringBuilder(
                                EnumChatFormatting.YELLOW + "  #" + (i + 1) + ": ");
                        infoUpgradeBuilder.append(drawerUpgrade.getRarity().rarityColor);
                        if (drawerUpgrade.hasDisplayName()) {
                            infoUpgradeBuilder.append(EnumChatFormatting.ITALIC).append(drawerUpgrade.getDisplayName())
                                    .append(EnumChatFormatting.RESET);
                        } else {
                            infoUpgradeBuilder.append(drawerUpgrade.getDisplayName());
                        }
                        upgradeInfoList.add(infoUpgradeBuilder.toString());
                    }
                }

                list.add(
                        EnumChatFormatting.GRAY
                                + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.upgradeList"));
                if (upgradeInfoList.isEmpty()) {
                    list.add(
                            "  " + EnumChatFormatting.DARK_GRAY
                                    + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed.none"));
                } else {
                    list.addAll(upgradeInfoList);
                }
            } else {
                list.add(
                        EnumChatFormatting.YELLOW
                                + StatCollector.translateToLocalFormatted("storageDrawers.drawers.sealed"));
                list.add(
                        EnumChatFormatting.DARK_GRAY + StatCollector
                                .translateToLocalFormatted("storageDrawers.drawers.sealed.descriptionShift"));
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
