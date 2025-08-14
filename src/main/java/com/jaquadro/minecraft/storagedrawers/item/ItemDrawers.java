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
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("tile")) {
            NBTTagCompound nbtDrawers = itemStack.getTagCompound().getCompoundTag("tile");
            TileEntityDrawers tileDrawers = (TileEntityDrawers) TileEntityDrawers.createAndLoadEntity(nbtDrawers);

            // Shows description
            list.add(
                    StatCollector.translateToLocalFormatted(
                            "storageDrawers.drawers.description",
                            getCapacityForBlock(block) * tileDrawers.getEffectiveStorageMultiplier()));

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                AddStatsInformation(tileDrawers, player, list);
                AddDrawersInformation(tileDrawers, list);
                AddUpgradesInformation(tileDrawers, list);
            } else {
                list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("storageDrawers.drawers.sealed"));
                list.add(
                        EnumChatFormatting.DARK_GRAY
                                + StatCollector.translateToLocal("storageDrawers.drawers.sealed.descriptionShift"));
            }
        } else {
            list.add(
                    StatCollector.translateToLocalFormatted(
                            "storageDrawers.drawers.description",
                            getCapacityForBlock(block)));
        }
    }

    private void AddStatsInformation(TileEntityDrawers tileDrawers, EntityPlayer player, List list) {
        StringBuilder infoStatesBuilder = new StringBuilder();

        // Show locked drawer status
        if (tileDrawers.isLocked(LockAttribute.LOCK_POPULATED)) {
            infoStatesBuilder.append(EnumChatFormatting.YELLOW)
                    .append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.locked"));
        } else {
            infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY)
                    .append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.unlocked"));
        }

        // Add comma separator
        infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY).append(", ");

        // Show if drawer is Public or Protected
        if (tileDrawers.getOwner() != null) {
            // Check if the current player holding drawer is the owner
            if (player.getUniqueID().equals(tileDrawers.getOwner())) {
                infoStatesBuilder.append(EnumChatFormatting.GREEN);
            } else {
                infoStatesBuilder.append(EnumChatFormatting.RED);
            }
            infoStatesBuilder.append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.access_owner"));
        } else {
            infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY)
                    .append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.access_public"));
        }

        // Show two previous status in single line
        list.add(infoStatesBuilder.toString());

        // In the next line show if drawer hide item label
        if (tileDrawers.isShrouded()) {
            list.add(
                    EnumChatFormatting.WHITE
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.hideItemLabel"));
        } else {
            list.add(
                    EnumChatFormatting.DARK_GRAY
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.showItemLabel"));
        }

        // In the next line show if drawer show item quantity
        if (!tileDrawers.isQuantified()) {
            list.add(
                    EnumChatFormatting.DARK_GRAY
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.hideItemQuantity"));
        } else {
            list.add(
                    EnumChatFormatting.WHITE
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.showItemQuantity"));
        }
    }

    private void AddDrawersInformation(TileEntityDrawers tileDrawers, List list) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.drawerList"));
        for (int i = 0; i < tileDrawers.getDrawerCount(); i++) {
            IDrawer drawerInventory = tileDrawers.getDrawer(i);
            ItemStack storedItem = drawerInventory.getStoredItemCopy();

            // Create builder and add number of slot
            StringBuilder infoItemBuilder = new StringBuilder(EnumChatFormatting.YELLOW + " #" + (i + 1) + ": ");

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
                    infoItemBuilder.append(quantityNumStack).append("x").append(storedItem.getMaxStackSize());
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
                        .append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.empty"));
                list.add(infoItemBuilder.toString());
            }
        }
    }

    private void AddUpgradesInformation(TileEntityDrawers tileDrawers, List list) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.upgradeList"));
        boolean hasUpgrades = false;

        for (int i = 0; i < tileDrawers.getUpgradeSlotCount(); i++) {
            ItemStack drawerUpgrade = tileDrawers.getUpgrade(i);
            if (drawerUpgrade != null) {
                StringBuilder infoUpgradeBuilder = new StringBuilder(EnumChatFormatting.YELLOW + "  - ");
                infoUpgradeBuilder.append(drawerUpgrade.getRarity().rarityColor);
                if (drawerUpgrade.hasDisplayName()) {
                    infoUpgradeBuilder.append(EnumChatFormatting.ITALIC).append(drawerUpgrade.getDisplayName())
                            .append(EnumChatFormatting.RESET);
                } else {
                    infoUpgradeBuilder.append(drawerUpgrade.getDisplayName());
                }
                hasUpgrades = true;
                list.add(infoUpgradeBuilder.toString());
            }
        }

        if (!hasUpgrades) {
            list.add(
                    "  " + EnumChatFormatting.DARK_GRAY
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.none"));
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
