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
        if (itemStack.hasTagCompound()) {
            NBTTagCompound tag = itemStack.getTagCompound();
            if (tag.hasKey("tile")) {
                NBTTagCompound tileTag = tag.getCompoundTag("tile");

                // 5 - magic number used by TileEntityDrawers class representing number of upgrade slots.
                ItemStack[] upgrades = new ItemStack[5];
                int drawerCapacity = getUpgradesAndDrawerCapacity(tileTag, upgrades);

                // Add to tooltip description + true stack storage space (if storage upgrades are applied).
                this.addDescriptionInformation(drawerCapacity, list);

                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    this.addStatsInformation(tileTag, player, list);
                    this.addDrawersInformation(tileTag, list);
                    this.addUpgradesInformation(upgrades, list);
                    // Possible alternative drawer information from tag.
                    this.addSealedContentsInformation(tileTag, list);
                } else {
                    list.add(
                            EnumChatFormatting.YELLOW
                                    + StatCollector.translateToLocal("storageDrawers.drawers.sealed"));
                    list.add(
                            EnumChatFormatting.DARK_GRAY
                                    + StatCollector.translateToLocal("storageDrawers.drawers.sealed.descriptionShift"));
                }
            } else {
                this.addAlternativeTagInformation(itemStack, player, list, par4);
            }
        } else {
            Block block = Block.getBlockFromItem(itemStack.getItem());
            this.addNoneTagDescriptionInformation(getCapacityForBlock(block), list);
        }
    }

    /** Returns default drawers storage capacity of drawer block. If the block is not drawer returns 0. */
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

    // Add description information in different places

    /** Add common description for all types of drawers, like sealed drawers and framed drawers with materials. */
    protected void addDescriptionInformation(int drawerCapacity, List list) {
        list.add(StatCollector.translateToLocalFormatted("storageDrawers.drawers.description", drawerCapacity));
    }

    /** Add bonus description information for drawers without NBTTagCompound. */
    protected void addNoneTagDescriptionInformation(int drawerCapacity, List list) {
        this.addDescriptionInformation(drawerCapacity, list);
    }

    /**
     * Add information in the case if they are others tags to check. In this case itemStack has always an
     * NBTTagCompound.
     */
    protected void addAlternativeTagInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {}

    // Add sealed contents information.

    /** Add to tooltip some drawers stats. */
    protected void addStatsInformation(NBTTagCompound tag, EntityPlayer player, List list) {
        ConfigManager config = StorageDrawers.config;

        EnumSet<LockAttribute> lockAttributes = null; // Unlocked or Locked drawer.
        UUID owner = null; // Owner of drawer.
        boolean shrouded = false; // Hide or show item label.
        boolean quantified = false; // Hide or show item quantities.

        // Logic for reading NBT are copied from "readFromPortableNBT" method from "TileEntityDrawers" class.

        if (config.cache.enableLockUpgrades && tag.hasKey("Lock"))
            lockAttributes = LockAttribute.getEnumSet(tag.getByte("Lock"));
        if (config.cache.enablePersonalUpgrades && tag.hasKey("Own")) owner = UUID.fromString(tag.getString("Own"));
        if (config.cache.enableShroudUpgrades && tag.hasKey("Shr")) shrouded = tag.getBoolean("Shr");
        if (config.cache.enableQuantifyUpgrades && tag.hasKey("Qua")) quantified = tag.getBoolean("Qua");

        String lockInfo; // Stat of the locked drawer.
        String accessInfo; // Stat of the owner.

        // Show locked drawer status
        if (lockAttributes != null && lockAttributes.contains(LockAttribute.LOCK_POPULATED)) {
            lockInfo = EnumChatFormatting.YELLOW
                    + StatCollector.translateToLocal("storageDrawers.drawers.sealed.locked");
        } else {
            lockInfo = EnumChatFormatting.DARK_GRAY
                    + StatCollector.translateToLocal("storageDrawers.drawers.sealed.unlocked");
        }

        // Show if drawer is Public or Protected
        if (owner != null) {
            if (player.getUniqueID().equals(owner)) {
                accessInfo = EnumChatFormatting.GREEN
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.access_owner");
            } else {
                accessInfo = EnumChatFormatting.RED
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.access_owner");
            }
        } else {
            accessInfo = EnumChatFormatting.DARK_GRAY
                    + StatCollector.translateToLocal("storageDrawers.drawers.sealed.access_public");
        }

        list.add(lockInfo + EnumChatFormatting.DARK_GRAY + ", " + accessInfo);

        // In the next line show if drawer hide item label
        if (shrouded) {
            list.add(
                    EnumChatFormatting.WHITE
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.hideItemLabel"));
        } else {
            list.add(
                    EnumChatFormatting.DARK_GRAY
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.showItemLabel"));
        }

        // In the next line show if drawer show item quantity
        if (quantified) {
            list.add(
                    EnumChatFormatting.WHITE
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.showItemQuantity"));
        } else {
            list.add(
                    EnumChatFormatting.DARK_GRAY
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.hideItemQuantity"));
        }
    }

    /** Add to tooltip information about drawers content. */
    protected void addDrawersInformation(NBTTagCompound tag, List list) {
        NBTTagList slots = tag.getTagList("Slots", Constants.NBT.TAG_COMPOUND);
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.drawerList"));
        for (int i = 0; i < slots.tagCount(); i++) {
            NBTTagCompound slot = slots.getCompoundTagAt(i);
            ItemStack stack = getItemStackFromDrawer(slot);
            String slotCounter = EnumChatFormatting.YELLOW + " #" + (i + 1) + ": ";
            if (stack != null) {
                list.add(
                        slotCounter + getGoodDisplayName(stack)
                                + " "
                                + getItemCountDisplay(stack.getMaxStackSize(), slot.getInteger("Count")));
            } else {
                list.add(
                        slotCounter + EnumChatFormatting.DARK_GRAY
                                + StatCollector.translateToLocal("storageDrawers.drawers.sealed.drawerEmpty"));
            }
        }
    }

    /** Add to tooltip information about upgrades. */
    protected void addUpgradesInformation(ItemStack[] upgrades, List list) {
        list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.upgradeList"));
        boolean hasUpgrades = false;

        for (int i = 0; i < upgrades.length; i++) { // 5 - upgrade count
            ItemStack upgrade = upgrades[i];
            if (upgrade != null) {
                list.add(EnumChatFormatting.YELLOW + "  - " + getGoodDisplayName(upgrade));
                hasUpgrades = true;
            }
        }

        if (!hasUpgrades) {
            list.add(
                    "  " + EnumChatFormatting.DARK_GRAY
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.upgradeNone"));
        }
    }

    /** Add information after Stats, Drawers and Upgrades information. */
    protected void addSealedContentsInformation(NBTTagCompound tag, List list) {}

    // Functions for extracting data from NBT

    /** Read ItemStack from NBT when representing drawer slot. */
    protected ItemStack getItemStackFromDrawer(NBTTagCompound slot) {
        // Logic copied from readNBT method of DrawerData class.
        ItemStack stack = null;
        if (slot.hasKey("Item") && slot.hasKey("Count")) {
            Item item = Item.getItemById(slot.getShort("Item"));
            if (item != null) { // p_i1881_2_ - stackSize
                stack = new ItemStack(item, 1, slot.getShort("Meta"));
                if (slot.hasKey("Tags")) stack.setTagCompound(slot.getCompoundTag("Tags"));
            }
        }
        return stack;
    }

    /** Read upgrades and drawer capacity from NBT when representing drawers. */
    protected int getUpgradesAndDrawerCapacity(NBTTagCompound tag, ItemStack[] upgrades) {
        ConfigManager config = StorageDrawers.config;

        int multiplier = 0; // effective storage multiplier for calculating true storage space.
        boolean isDowngrade = false; // if downgrade upgrade is applied.

        // Read upgrades in legacy format. Copied like in `readLegacyUpgradeNBT` method from "TileEntityDrawers" class.
        if (!tag.hasKey("Upgrades")) {
            int i = 0; // idk how it's worked in legacy, so maybe like this ?
            if (tag.hasKey("Lev") && tag.getByte("Lev") > 1) {
                upgrades[i] = new ItemStack(ModItems.upgrade, 1, tag.getByte("Lev"));
                multiplier += config.getStorageUpgradeMultiplier(upgrades[i++].getItemDamage());
            }
            if (tag.hasKey("Stat")) upgrades[i++] = new ItemStack(ModItems.upgradeStatus, 1, tag.getByte("Stat"));
            if (tag.hasKey("Void")) upgrades[i++] = new ItemStack(ModItems.upgradeVoid);
            if (tag.hasKey("Down")) {
                upgrades[i] = new ItemStack(ModItems.upgradeDowngrade);
                isDowngrade = true;
            }
        }
        // Read upgrades in new format. Copied like in `readFromPortableNBT` method from "TileEntityDrawers" class.
        else {
            NBTTagList upgradeList = tag.getTagList("Upgrades", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < upgradeList.tagCount(); i++) {
                NBTTagCompound upgradeTag = upgradeList.getCompoundTagAt(i);
                ItemStack stack = ItemStack.loadItemStackFromNBT(upgradeTag);
                if (stack != null) {
                    if (stack.getItem() == ModItems.upgrade)
                        multiplier += StorageDrawers.config.getStorageUpgradeMultiplier(stack.getItemDamage());
                    // TODO: Add not implemented config check.
                    if (stack.getItem() == ModItems.upgradeDowngrade) isDowngrade = true;
                }
                upgrades[i] = stack;
            }
        }

        // Later when we calculate total storage it will not multiply by 0
        if (multiplier == 0) multiplier = 1;

        // "Cap" tag is drawer capacity
        return isDowngrade ? multiplier : tag.getShort("Cap") * multiplier;
    }

    // String functions for good displays.

    /** Returns a colored display name of stack != null. Can be in italic format if stack has display tag. */
    protected String getGoodDisplayName(ItemStack stack) {
        if (stack.hasDisplayName()) {
            return EnumChatFormatting.ITALIC.toString() + stack.getRarity().rarityColor + stack.getDisplayName();
        } else {
            return stack.getRarity().rarityColor.toString() + stack.getDisplayName();
        }
    }

    /** Returns blue colored item count display with format like in WAILA. */
    protected String getItemCountDisplay(int maxStackSize, int itemCount) {
        int numStack = itemCount / maxStackSize;
        int remainder = itemCount - numStack * maxStackSize;

        String itemCountDisplay;

        if (numStack > 0) {
            if (remainder > 0) itemCountDisplay = "[" + numStack + "x" + maxStackSize + " + " + remainder + "]";
            else itemCountDisplay = "[" + numStack + "x" + maxStackSize + "]";
        } else {
            itemCountDisplay = "[" + remainder + "]";
        }

        return EnumChatFormatting.BLUE + itemCountDisplay;
    }
}
