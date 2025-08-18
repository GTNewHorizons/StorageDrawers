package com.jaquadro.minecraft.storagedrawers.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityTrim;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCustomTrim extends ItemBlock {

    public ItemCustomTrim(Block block) {
        super(block);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ, int metadata) {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false;

        TileEntityTrim tile = (TileEntityTrim) world.getTileEntity(x, y, z);
        if (tile != null && stack.hasTagCompound() && !stack.getTagCompound().hasKey("tile")) {
            if (stack.getTagCompound().hasKey("MatS"))
                tile.setMaterialSide(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatS")));
            if (stack.getTagCompound().hasKey("MatT"))
                tile.setMaterialTrim(ItemStack.loadItemStackFromNBT(stack.getTagCompound().getCompoundTag("MatT")));
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        if (itemStack.hasTagCompound()) {
            NBTTagCompound tag = itemStack.getTagCompound();
            this.addMaterialsInformation(tag, list);
        } else {
            list.add(StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialList"));
            list.add(
                    "  " + EnumChatFormatting.DARK_GRAY
                            + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialNone"));
        }
    }

    /**
     * Add to tooltip information about materials used in framed trim. Copied from ItemCustomDrawers method.
     */
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

    /**
     * Returns good display name or in gray localised "sealed.materialNone". Copied from ItemDrawers and
     * ItemCustomDrawers methods.
     */
    private String getMaterialDisplayName(ItemStack stack) {
        if (stack != null) {
            if (stack.hasDisplayName()) {
                return EnumChatFormatting.ITALIC.toString() + stack.getRarity().rarityColor + stack.getDisplayName();
            } else {
                return stack.getRarity().rarityColor.toString() + stack.getDisplayName();
            }
        } else {
            return EnumChatFormatting.DARK_GRAY
                    + StatCollector.translateToLocal("storageDrawers.drawers.sealed.materialNone");
        }
    }

    public static ItemStack makeItemStack(Block block, int count, ItemStack matSide, ItemStack matTrim) {
        Item item = Item.getItemFromBlock(block);
        if (!(item instanceof ItemCustomTrim)) return null;

        NBTTagCompound tag = new NBTTagCompound();

        if (matSide != null) tag.setTag("MatS", getMaterialTag(matSide));

        if (matTrim != null) tag.setTag("MatT", getMaterialTag(matTrim));

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
