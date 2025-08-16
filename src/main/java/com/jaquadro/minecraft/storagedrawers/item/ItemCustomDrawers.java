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

    @Override
    public void addLeftShiftInformation(NBTTagCompound tag, EntityPlayer player, List list) {
        addMaterialsInformation(tag, list);
    }

    private void addMaterialsInformation(NBTTagCompound tag, List list) {
        ItemStack materialSide = null;
        ItemStack materialFront = null;
        ItemStack materialTrim = null;

        // Logic copied from "readFromPortableNBT" method from "TileEntityDrawers".
        if (tag.hasKey("MatS")) materialSide = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatS"));
        if (tag.hasKey("MatF")) materialFront = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatF"));
        if (tag.hasKey("MatT")) materialTrim = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("MatT"));

        list.add(
                EnumChatFormatting.GRAY + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialList"));

        // Display side material
        list.add(
                "  " + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialSide")
                        + " "
                        + getGoodMaterialDisplayName(materialSide));
        // Display trim material
        list.add(
                "  " + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialTrim")
                        + " "
                        + getGoodMaterialDisplayName(materialTrim));
        // Display front material
        list.add(
                "  " + EnumChatFormatting.YELLOW
                        + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialFront")
                        + " "
                        + getGoodMaterialDisplayName(materialFront));
    }

    private String getGoodMaterialDisplayName(ItemStack stack) {
        if (stack != null) {
            return getGoodItemStackDisplayName(stack);
        } else {
            return EnumChatFormatting.DARK_GRAY
                    + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialNone");
        }
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

    private static NBTTagCompound getMaterialTag(ItemStack mat) {
        mat = mat.copy();
        mat.stackSize = 1;

        NBTTagCompound itag = new NBTTagCompound();
        mat.writeToNBT(itag);

        return itag;
    }
}
