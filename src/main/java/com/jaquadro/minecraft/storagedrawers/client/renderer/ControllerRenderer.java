package com.jaquadro.minecraft.storagedrawers.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.block.BlockController;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityController;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperState;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@ThreadSafeISBRH(perThread = true)
public class ControllerRenderer implements ISimpleBlockRenderingHandler {

    private static final double UNIT = .0625f;
    private static final int INVENTORY_SIDE = 4;

    private ModularBoxRenderer boxRenderer = new ModularBoxRenderer();

    private RenderHelper renderHelper = RenderHelper.instances.get();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        if (!(block instanceof BlockController)) return;

        renderInventoryBlock((BlockController) block, metadata, modelId, renderer);
    }

    private void renderInventoryBlock(BlockController block, int metadata, int modelId, RenderBlocks renderer) {
        initInventoryBoxRenderer(block, metadata);

        GL11.glRotatef(90, 0, 1, 0);
        GL11.glTranslatef(-.5f, -.5f, -.5f);

        applyTopUvRotation(INVENTORY_SIDE, 0);

        renderExterior(block, 0, 0, 0, INVENTORY_SIDE, renderer);

        boxRenderer.setUnit(0);
        boxRenderer.setInteriorIcon(block.getIcon(INVENTORY_SIDE, metadata), ForgeDirection.OPPOSITES[INVENTORY_SIDE]);

        renderInterior(block, 0, 0, 0, INVENTORY_SIDE, renderer);

        renderHelper.state.clearUVRotation(RenderHelper.YPOS);

        GL11.glTranslatef(.5f, .5f, .5f);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {
        if (!(block instanceof BlockController)) return false;

        return renderWorldBlock(world, x, y, z, (BlockController) block, modelId, renderer);
    }

    private boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockController block, int modelId,
            RenderBlocks renderer) {
        TileEntityController tile = block.getTileEntity(world, x, y, z);
        if (tile == null) return false;

        int side = tile.getDirection();
        int rotation = tile.getRotation();

        applyTopUvRotation(side, rotation);

        initWorldBoxRenderer(world, x, y, z, block);

        boxRenderer.setCutIcon(block.getIconTrim(0));
        boxRenderer.setInteriorIcon(block.getIconTrim(0));

        renderExterior(block, x, y, z, side, renderer);

        boxRenderer.setUnit(0);
        boxRenderer.setInteriorIcon(block.getIcon(world, x, y, z, side), ForgeDirection.OPPOSITES[side]);

        renderInterior(block, x, y, z, side, renderer);

        renderHelper.state.clearUVRotation(RenderHelper.YPOS);

        return true;
    }

    private void initInventoryBoxRenderer(BlockController block, int metadata) {
        boxRenderer.setUnit(UNIT);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        for (int i = 0; i < 6; i++) boxRenderer.setIcon(block.getIcon(i, metadata), i);
    }

    private void initWorldBoxRenderer(IBlockAccess world, int x, int y, int z, BlockController block) {
        boxRenderer.setUnit(UNIT);
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        for (int i = 0; i < 6; i++) boxRenderer.setExteriorIcon(block.getIcon(world, x, y, z, i), i);
    }

    private void applyTopUvRotation(int side, int rotation) {
        int uvRotation = side <= 1 ? rotation : RenderHelperState.ROTATION_BY_FACE_FACE[RenderHelper.ZNEG][side];
        renderHelper.state.setUVRotation(RenderHelper.YPOS, uvRotation);
    }

    private void renderExterior(BlockController block, int x, int y, int z, int side, RenderBlocks renderer) {
        boxRenderer.renderExterior(
                renderer.blockAccess,
                block,
                x,
                y,
                z,
                0,
                0,
                0,
                1,
                1,
                1,
                0,
                ModularBoxRenderer.sideCut[side]);
    }

    private void renderInterior(BlockController block, int x, int y, int z, int side, RenderBlocks renderer) {
        double unit = UNIT;
        double xMin = unit, xMax = 1 - unit, yMin = unit, yMax = 1 - unit, zMin = unit, zMax = 1 - unit;

        switch (side) {
            case 0:
                yMin = 0;
                yMax = unit;
                break;
            case 1:
                yMin = 1 - unit;
                yMax = 1;
                break;
            case 2:
                zMin = 0;
                zMax = unit;
                break;
            case 3:
                zMin = 1 - unit;
                zMax = 1;
                break;
            case 4:
                xMin = 0;
                xMax = unit;
                break;
            case 5:
                xMin = 1 - unit;
                xMax = 1;
                break;
        }

        boxRenderer.renderInterior(
                renderer.blockAccess,
                block,
                x,
                y,
                z,
                xMin,
                yMin,
                zMin,
                xMax,
                yMax,
                zMax,
                0,
                ModularBoxRenderer.sideCut[side]);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return StorageDrawers.proxy.controllerRenderID;
    }
}
