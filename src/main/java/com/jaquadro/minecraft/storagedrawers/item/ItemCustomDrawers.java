package com.jaquadro.minecraft.storagedrawers.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;

public class ItemCustomDrawers extends ItemDrawers {

    public ItemCustomDrawers(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false;

        TileEntityDrawers tile = (TileEntityDrawers) world.getTileEntity(x, y, z);
        if (tile != null && stack.hasTagCompound() && !stack.getTagCompound().hasKey("tile")) {
            if (stack.getTagCompound().hasKey("MatS"))
                tile.setMaterialSide(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatS")));
            if (stack.getTagCompound().hasKey("MatT"))
                tile.setMaterialTrim(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatT")));
            if (stack.getTagCompound().hasKey("MatF"))
                tile.setMaterialFront(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatF")));
        }

        return true;
    }

    public static ItemStack makeItemStack(Block block, int count, ItemStack matSide, ItemStack matTrim,
            ItemStack matFront) {
        Item item = Item.getItemFromBlock(block);
        if (!(item instanceof ItemCustomDrawers)) return null;

        NBTTagCompound tag = new NBTTagCompound();

        if (matSide != null) tag.setTag("MatS", getMaterialTag(matSide));

        if (matTrim != null) tag.setTag("MatT", getMaterialTag(matTrim));

        if (matFront != null) tag.setTag("MatF", getMaterialTag(matFront));

        ItemStack stack = new ItemStack(item, count, 0);
        if (!tag.hasNoTags()) stack.setTagCompound(tag);

        return stack;
    }

    @Override
    protected void addAlternativeTagInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        // Add materials information for not sealed framed drawers.
        Block block = Block.getBlockFromItem(itemStack.getItem());
        this.addDescriptionInformation(getCapacityForBlock(block), list);
        this.addMaterialsInformation(itemStack.getTagCompound(), list); // safe to use without tags.
    }

    @Override
    protected void addNoneTagDescriptionInformation(int drawerCapacity, List list) {
        super.addNoneTagDescriptionInformation(drawerCapacity, list);
        list.add(
                EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialList"));
        list.add(
                "  " + EnumChatFormatting.DARK_GRAY
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialNone"));
    }

    @Override
    protected void addSealedContentsInformation(NBTTagCompound tag, List list) {
        this.addMaterialsInformation(tag, list);
    }

    /** Add to tooltip information about materials used in framed drawers */
    private void addMaterialsInformation(NBTTagCompound tag, List list) {
        ItemStack materialSide = null;
        ItemStack materialFront = null;
        ItemStack materialTrim = null;
        boolean hasMaterials = false;

        // Logic copied from "readFromPortableNBT" method from "TileEntityDrawers".
        if (tag.hasKey("MatS")) {
            materialSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));
            hasMaterials = true;
        }
        if (tag.hasKey("MatF")) {
            materialFront = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatF"));
            hasMaterials = true;
        }
        if (tag.hasKey("MatT")) {
            materialTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));
            hasMaterials = true;
        }

        list.add(
                EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialList"));

        if (hasMaterials) {
            // Display side material
            list.add(
                    "  " + EnumChatFormatting.YELLOW
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialSide")
                            + " "
                            + getMaterialDisplayName(materialSide));
            // Display trim material
            list.add(
                    "  " + EnumChatFormatting.YELLOW
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialTrim")
                            + " "
                            + getMaterialDisplayName(materialTrim));
            // Display front material
            list.add(
                    "  " + EnumChatFormatting.YELLOW
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialFront")
                            + " "
                            + getMaterialDisplayName(materialFront));
        } else {
            // Display <None> ...
            list.add(
                    "  " + EnumChatFormatting.DARK_GRAY
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialNone"));
        }
    }

    /** Returns good display name or in gray localised "sealed.materialNone". */
    private String getMaterialDisplayName(ItemStack stack) {
        if (stack != null) {
            return getGoodDisplayName(stack);
        } else {
            return EnumChatFormatting.DARK_GRAY
                    + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialNone");
        }
    }

    private static NBTTagCompound getMaterialTag(ItemStack mat) {
        mat = mat.copy();
        mat.stackSize = 1;

        NBTTagCompound itag = new NBTTagCompound();
        mat.writeToNBT(itag);

        return itag;
    }
}
