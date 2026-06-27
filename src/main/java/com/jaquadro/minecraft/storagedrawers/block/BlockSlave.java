package com.jaquadro.minecraft.storagedrawers.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntitySlave;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSlave extends BlockContainer implements INetworked {

    @SideOnly(Side.CLIENT)
    private IIcon iconSide;

    @SideOnly(Side.CLIENT)
    private IIcon iconSideEtched;

    public BlockSlave(String blockName) {
        super(Material.rock);

        setCreativeTab(ModCreativeTabs.tabStorageDrawers);
        setHardness(5f);
        setBlockName(blockName);
        setStepSound(Block.soundTypeStone);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (side) {
            case 0:
            case 1:
                return iconSide;
            default:
                return iconSideEtched;
        }
    }

    @Override
    public TileEntitySlave createNewTileEntity(World world, int meta) {
        return new TileEntitySlave();
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        if (world.isRemote) return;

        TileEntitySlave te = getTileEntitySafe(world, x, y, z);
        if (te == null) return;

        te.refreshController(world, x, y, z);
    }

    @Override
    public void onBlockPreDestroy(World worldIn, int x, int y, int z, int meta) {
        if (worldIn.isRemote) return;

        TileEntitySlave te = getTileEntitySafe(worldIn, x, y, z);
        if (te == null) return;

        te.controllerFullUpdate();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
            float hitY, float hitZ) {

        TileEntitySlave te = getTileEntitySafe(world, x, y, z);
        boolean isSneaking = player.isSneaking();
        ItemStack heldItem = player.inventory.getCurrentItem();

        if (!isSneaking) {

            if (heldItem == null || heldItem.getItem() == null) {
                if (!world.isRemote) {
                    player.addChatMessage(
                            new ChatComponentText(
                                    "Extracting " + ((te.getExtractionItem() == null) ? "Nothing"
                                            : te.getExtractionItem().getDisplayName())));
                }
            } else {
                if (!world.isRemote) {
                    if (te.getExtractionItem() == null) te.setExtractionItem(heldItem);
                    player.addChatMessage(
                            new ChatComponentText(
                                    "Extracting " + ((te.getExtractionItem() == null) ? "Nothing"
                                            : te.getExtractionItem().getDisplayName())));
                }
            }
            return true;
        } else if (heldItem == null || heldItem.getItem() == null) {
            if (!world.isRemote) {
                te.setExtractionItem(null);
                player.addChatMessage(new ChatComponentText("Extracting Nothing"));
            }
            return true;
        }
        return false;
    }

    public TileEntitySlave getTileEntity(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tile = blockAccess.getTileEntity(x, y, z);
        return (tile instanceof TileEntitySlave) ? (TileEntitySlave) tile : null;
    }

    public TileEntitySlave getTileEntitySafe(World world, int x, int y, int z) {
        TileEntitySlave tile = getTileEntity(world, x, y, z);
        if (tile == null) {
            tile = createNewTileEntity(world, world.getBlockMetadata(x, y, z));
            world.setTileEntity(x, y, z, tile);
        }

        return tile;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        iconSide = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_side");
        iconSideEtched = register.registerIcon(StorageDrawers.MOD_ID + ":drawers_comp_side_2");
    }
}
