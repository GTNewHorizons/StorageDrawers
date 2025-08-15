package com.jaquadro.minecraft.storagedrawers.item;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import org.lwjgl.input.Keyboard;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.config.ConfigManager;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;

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
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("tile")) {
            NBTTagCompound tag = itemStack.getTagCompound().getCompoundTag("tile");
            ItemStack[] upgrades = new ItemStack[5]; // 5 - magic number defined in TileEntityDrawers.
            int[] upgradesStats = readUpgradesFromNBT(tag, upgrades); // - 0 effectiveStorageMultiplier, 1 - isDownGrade

            int drawerCapacity = tag.getInteger("Cap") * upgradesStats[0];
            if (upgradesStats[1] == 1) drawerCapacity = upgradesStats[0];

            // Add to tooltip description + max stored stacks per drawer.
            list.add(StatCollector.translateToLocalFormatted("storageDrawers.drawers.description", drawerCapacity));

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                addStatsInformation(tag, player, list);
                addDrawersInformation(tag, list);
                addUpgradesInformation(upgrades, list);
            } else {
                list.add(EnumChatFormatting.YELLOW + StatCollector.translateToLocal("storageDrawers.drawers.sealed"));
                list.add(
                        EnumChatFormatting.DARK_GRAY
                                + StatCollector.translateToLocal("storageDrawers.drawers.sealed.descriptionShift"));
            }

        } else {
            Block block = Block.getBlockFromItem(itemStack.getItem());
            list.add(
                    StatCollector.translateToLocalFormatted(
                            "storageDrawers.drawers.description",
                            getCapacityForBlock(block)));
        }
    }

    /** Add to tooltip some drawers stats. */
    private void addStatsInformation(NBTTagCompound tag, EntityPlayer player, List list) {
        ConfigManager config = StorageDrawers.config;
        StringBuilder infoStatesBuilder = new StringBuilder();

        EnumSet<LockAttribute> lockAttributes = null; // Unlocked or Locked drawer.
        UUID owner = null; // Owner of driver.
        boolean shrouded = false; // Hide or show item label.
        boolean quantified = false; // Hide or show item quantities.

        if (config.cache.enableLockUpgrades && tag.hasKey("Lock"))
            lockAttributes = LockAttribute.getEnumSet(tag.getByte("Lock"));
        if (config.cache.enablePersonalUpgrades && tag.hasKey("Own")) owner = UUID.fromString(tag.getString("Own"));
        if (config.cache.enableShroudUpgrades && tag.hasKey("Shr")) shrouded = tag.getBoolean("Shr");
        if (config.cache.enableQuantifyUpgrades && tag.hasKey("Qua")) quantified = tag.getBoolean("Qua");

        // Show locked drawer status
        if (lockAttributes != null && lockAttributes.contains(LockAttribute.LOCK_POPULATED))
            infoStatesBuilder.append(EnumChatFormatting.YELLOW)
                    .append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.locked"));
        else infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY)
                .append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.unlocked"));

        // Add comma separator
        infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY).append(", ");

        // Show if drawer is Public or Protected
        if (owner != null) {
            // Check if the current player holding drawer is the owner
            if (player.getUniqueID().equals(owner)) infoStatesBuilder.append(EnumChatFormatting.GREEN);
            else infoStatesBuilder.append(EnumChatFormatting.RED);
            infoStatesBuilder.append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.access_owner"));
        } else infoStatesBuilder.append(EnumChatFormatting.DARK_GRAY)
                .append(StatCollector.translateToLocal("storageDrawers.drawers.sealed.access_public"));

        // Show two previous status in single line
        list.add(infoStatesBuilder.toString());

        // In the next line show if drawer hide item label
        if (shrouded) list.add(
                EnumChatFormatting.WHITE
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.hideItemLabel"));
        else list.add(
                EnumChatFormatting.DARK_GRAY
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.showItemLabel"));

        // In the next line show if drawer show item quantity
        if (quantified) list.add(
                EnumChatFormatting.WHITE
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.showItemQuantity"));
        else list.add(
                EnumChatFormatting.DARK_GRAY
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.hideItemQuantity"));
    }

    /** Add to tooltip information about drawers content. */
    private void addDrawersInformation(NBTTagCompound tag, List list) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.drawerList"));

        NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);

        for (int i = 0; i < slots.tagCount(); i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);

            ItemStack stack = readItemStackFromDrawerSlotNBT(slot);
            int itemCount = slot.getInteger("Count");

            // Create builder and add number of slot
            StringBuilder infoItemBuilder = new StringBuilder(EnumChatFormatting.YELLOW + " #" + (i + 1) + ": ");

            // If item is null just add "<Empty" else quantity and display name.
            if (stack != null) {
                // Calculates quantities
                int quantityNumStack = itemCount / stack.getMaxStackSize();
                int quantityRemainer = itemCount - quantityNumStack * stack.getMaxStackSize();

                // Add rarity color and display name
                infoItemBuilder.append(stack.getRarity().rarityColor);
                if (stack.hasDisplayName()) {
                    infoItemBuilder.append(EnumChatFormatting.ITALIC).append(stack.getDisplayName())
                            .append(EnumChatFormatting.RESET);
                } else {
                    infoItemBuilder.append(stack.getDisplayName());
                }

                // Add space between display name and quantity and then add quantity of items.
                infoItemBuilder.append(" ").append(EnumChatFormatting.BLUE).append("[");
                if (quantityNumStack > 0) {
                    infoItemBuilder.append(quantityNumStack).append("x").append(stack.getMaxStackSize());
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

    /** Add to tooltip information about upgrades. */
    private void addUpgradesInformation(ItemStack[] upgrades, List list) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.upgradeList"));
        boolean hasUpgrades = false;

        for (int i = 0; i < upgrades.length; i++) { // 5 - upgrade count
            if (upgrades[i] != null) {
                StringBuilder infoUpgradeBuilder = new StringBuilder(EnumChatFormatting.YELLOW + "  - ");
                infoUpgradeBuilder.append(upgrades[i].getRarity().rarityColor);
                if (upgrades[i].hasDisplayName()) {
                    infoUpgradeBuilder.append(EnumChatFormatting.ITALIC).append(upgrades[i].getDisplayName())
                            .append(EnumChatFormatting.RESET);
                } else {
                    infoUpgradeBuilder.append(upgrades[i].getDisplayName());
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

    /** Read from NBT drawer upgrades and also returns { effectiveStorageMultiplier, isDownGrade } */
    private int[] readUpgradesFromNBT(NBTTagCompound tag, ItemStack[] upgrades) {
        ConfigManager config = StorageDrawers.config;

        int effectiveStorageMultiplier = 0;
        int isDownGrade = 0;
        int slotId = 0;

        // Read upgrades in legacy format.
        if (!tag.hasKey("Upgrades")) {
            if (tag.hasKey("Lev") && tag.getByte("Lev") > 1) {
                upgrades[slotId] = new ItemStack(ModItems.upgrade, 1, tag.getByte("Lev"));
                effectiveStorageMultiplier += StorageDrawers.config
                        .getStorageUpgradeMultiplier(upgrades[slotId++].getItemDamage());
            }
            if (tag.hasKey("Stat")) upgrades[slotId++] = new ItemStack(ModItems.upgradeStatus, 1, tag.getByte("Stat"));
            if (tag.hasKey("Void")) upgrades[slotId++] = new ItemStack(ModItems.upgradeVoid);
            if (tag.hasKey("Down")) {
                upgrades[slotId] = new ItemStack(ModItems.upgradeDowngrade);
                isDownGrade = 1;
            }
        }
        // Read upgrades in new format.
        else {
            NBTTagList upgradeList = tag.getTagList("Upgrades", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < upgradeList.tagCount(); i++) {
                NBTTagCompound upgradeTag = upgradeList.getCompoundTagAt(i);
                upgrades[i] = ItemStack.loadItemStackFromNBT(upgradeTag);
                if (upgrades[i] != null) {
                    if (upgrades[i].getItem() == ModItems.upgrade)
                        effectiveStorageMultiplier += StorageDrawers.config
                                .getStorageUpgradeMultiplier(upgrades[i].getItemDamage());
                    // TODO: Config check (see TileEntityDrawers)
                    if (upgrades[i].getItem() == ModItems.upgradeDowngrade) isDownGrade = 1;
                }
            }
        }

        // Later when we calculate total storage it will not multiply by 0
        if (effectiveStorageMultiplier == 0) effectiveStorageMultiplier = 1;

        return new int[] { effectiveStorageMultiplier, isDownGrade };
    }

    /** Read from NBT slot of drawers ItemStack */
    private ItemStack readItemStackFromDrawerSlotNBT(NBTTagCompound tag) {
        ItemStack stack = null;

        // Logic copied from one of IDrawer implementations.
        if (tag.hasKey("Item") && tag.hasKey("Count")) {
            Item item = Item.getItemById(tag.getShort("Item"));
            if (item != null) {
                stack = new ItemStack(item);
                stack.setItemDamage(tag.getShort("Meta"));
                if (tag.hasKey("Tags")) stack.setTagCompound(tag.getCompoundTag("Tags"));
            }
        }

        return stack;
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
