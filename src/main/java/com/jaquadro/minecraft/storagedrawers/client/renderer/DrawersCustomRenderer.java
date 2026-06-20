package com.jaquadro.minecraft.storagedrawers.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.ForgeHooksClient;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.client.renderer.common.CommonDrawerRenderer;

@ThreadSafeISBRH(perThread = true)
public class DrawersCustomRenderer extends DrawersRenderer {

    private final CommonDrawerRenderer commonRender = new CommonDrawerRenderer();

    @Override
    protected void renderBaseBlock(IBlockAccess world, TileEntityDrawers tile, int x, int y, int z, BlockDrawers block,
            RenderBlocks renderer) {
        BlockDrawersCustom custom = (BlockDrawersCustom) block;

        ItemStack materialSide = getMaterialOrDefault(tile.getMaterialSide(), new ItemStack(block));
        ItemStack materialFront = getMaterialOrDefault(tile.getMaterialFront(), materialSide);
        ItemStack materialTrim = getMaterialOrDefault(tile.getMaterialTrim(), materialSide);

        IIcon trimIcon = getIconOrDefault(materialTrim, custom.getDefaultTrimIcon());
        IIcon panelIcon = getIconOrDefault(materialSide, custom.getDefaultFaceIcon());
        IIcon frontIcon = getIconOrDefault(materialFront, custom.getDefaultFaceIcon());

        if (ForgeHooksClient.getWorldRenderPass() == 0) commonRender.renderBasePass(
                world,
                x,
                y,
                z,
                custom,
                tile.getDirection(),
                tile.getRotation(),
                panelIcon,
                trimIcon,
                frontIcon);
        else if (ForgeHooksClient.getWorldRenderPass() == 1) commonRender.renderOverlayPass(
                world,
                x,
                y,
                z,
                custom,
                tile.getDirection(),
                tile.getRotation(),
                trimIcon,
                frontIcon);
    }

    @Override
    public int getRenderId() {
        return StorageDrawers.proxy.drawersCustomRenderID;
    }

    private ItemStack getMaterialOrDefault(ItemStack material, ItemStack fallback) {
        return material != null ? material : fallback;
    }

    private IIcon getIconOrDefault(ItemStack material, IIcon fallback) {
        IIcon icon = Block.getBlockFromItem(material.getItem()).getIcon(4, material.getItemDamage());
        return icon != null ? icon : fallback;
    }
}
