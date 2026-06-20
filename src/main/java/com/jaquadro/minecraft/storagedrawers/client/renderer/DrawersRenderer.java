package com.jaquadro.minecraft.storagedrawers.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;
import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawers;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperState;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

@ThreadSafeISBRH(perThread = true)
public class DrawersRenderer implements ISimpleBlockRenderingHandler {

    private static final double unit = .0625f;
    private ModularBoxRenderer boxRenderer = new ModularBoxRenderer();

    private RenderHelper renderHelper = RenderHelper.instances.get();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        return;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
            RenderBlocks renderer) {
        if (!(block instanceof BlockDrawers)) return false;

        return renderWorldBlock(world, x, y, z, (BlockDrawers) block, modelId, renderer);
    }

    private boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, BlockDrawers block, int modelId,
            RenderBlocks renderer) {
        TileEntityDrawers tile = block.getTileEntity(world, x, y, z);
        if (tile == null) return false;

        renderBaseBlock(world, tile, x, y, z, block, renderer);

        if (renderer.overrideBlockTexture != null
                && renderer.overrideBlockTexture.getIconName().startsWith("destroy_stage"))
            return true;

        int side = tile.getDirection();
        int rotation = tile.getRotation();
        if (StorageDrawers.config.cache.enableIndicatorUpgrades)
            renderIndicator(block, x, y, z, side, rotation, renderer, tile.getEffectiveStatusLevel());
        if (StorageDrawers.config.cache.enableLockUpgrades) renderLock(
                block,
                x,
                y,
                z,
                side,
                rotation,
                renderer,
                tile.isLocked(LockAttribute.LOCK_POPULATED),
                tile.getOwner() != null);
        if (StorageDrawers.config.cache.enableVoidUpgrades)
            renderVoid(block, x, y, z, side, rotation, renderer, tile.isVoid());
        if (StorageDrawers.config.cache.enableTape)
            renderTape(block, x, y, z, side, rotation, renderer, tile.isSealed());

        renderShroud(block, x, y, z, side, rotation, renderer, tile.isShrouded());

        return true;
    }

    protected void renderBaseBlock(IBlockAccess world, TileEntityDrawers tile, int x, int y, int z, BlockDrawers block,
            RenderBlocks renderer) {
        int side = tile.getDirection();
        int rotation = tile.getRotation();
        int meta = world.getBlockMetadata(x, y, z);

        if (side <= 1) {
            // Front points up or down; the front/back Y faces carry the front grain spun to match the rigid tilt that
            // the item and decoration renderers use (see RenderHelperState orientation matrix).
            if (side == 1) renderHelper.state.setUVRotation(RenderHelper.YPOS, (4 - rotation) % 4);
            else renderHelper.state.setUVRotation(RenderHelper.YNEG, (rotation + 2) % 4);
        } else {
            renderHelper.state
                    .setUVRotation(RenderHelper.YPOS, RenderHelperState.ROTATION_BY_FACE_FACE[RenderHelper.YPOS][side]);
            renderHelper.state
                    .setUVRotation(RenderHelper.YNEG, RenderHelperState.ROTATION_BY_FACE_FACE[RenderHelper.YNEG][side]);
        }

        boxRenderer.setUnit(block.getTrimWidth());
        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);
        for (int i = 0; i < 6; i++) boxRenderer.setExteriorIcon(block.getIcon(world, x, y, z, i), i);

        boxRenderer.setCutIcon(block.getIconTrim(meta));
        boxRenderer.setInteriorIcon(block.getIconTrim(meta));

        renderExterior(block, x, y, z, side, renderer);

        int maxStorageLevel = tile.getMaxStorageLevel();
        if (maxStorageLevel > 1 && StorageDrawers.config.cache.renderStorageUpgrades && !tile.shouldHideUpgrades()) {
            for (int i = 0; i < 6; i++)
                boxRenderer.setExteriorIcon(block.getOverlayIcon(world, x, y, z, i, maxStorageLevel), i);

            boxRenderer.setCutIcon(block.getOverlayIconTrim(maxStorageLevel));
            boxRenderer.setInteriorIcon(block.getOverlayIconTrim(maxStorageLevel));

            renderExterior(block, x, y, z, side, renderer);
        }

        boxRenderer.setUnit(0);
        boxRenderer.setInteriorIcon(block.getIcon(world, x, y, z, side), ForgeDirection.OPPOSITES[side]);

        renderInterior(block, x, y, z, side, renderer);
    }

    private void renderLock(BlockDrawers block, int x, int y, int z, int side, int rotation, RenderBlocks renderer,
            boolean locked, boolean owned) {
        if (!locked && !owned) return;

        IIcon iconLock = block.getLockIcon(locked, owned);
        renderFrontDecoration(block, x, y, z, side, rotation, renderer, iconLock, 0.46875, 0.9375, 0.53125, 1);
    }

    private void renderVoid(BlockDrawers block, int x, int y, int z, int side, int rotation, RenderBlocks renderer,
            boolean voided) {
        if (!voided) return;

        IIcon iconVoid = block.getVoidIcon();
        renderFrontDecoration(block, x, y, z, side, rotation, renderer, iconVoid, 1 - .0625, 0.9375, 1, 1);
    }

    private void renderTape(BlockDrawers block, int x, int y, int z, int side, int rotation, RenderBlocks renderer,
            boolean taped) {
        if (!taped) return;

        double depth = block.halfDepth ? .5 : 1;
        IIcon iconTape = block.getTapeIcon();

        renderHelper.setRenderBounds(0, 0, 0, 1, 1, depth + .005);
        renderHelper.state.setOrientation(RenderHelper.ZPOS, side, rotation);
        renderHelper.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconTape);
        renderHelper.state.clearRotateTransform();
    }

    private void renderFrontDecoration(BlockDrawers block, int x, int y, int z, int side, int rotation,
            RenderBlocks renderer, IIcon icon, double uMin, double vMin, double uMax, double vMax) {
        double depth = block.halfDepth ? .5 : 1;

        if (side > 1) {
            renderHelper.setRenderBounds(uMin, vMin, 0, uMax, vMax, depth + .005);
            renderHelper.state.setOrientation(RenderHelper.ZPOS, side, rotation);
            renderHelper.renderPartialFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, icon, 0, 0, 1, 1);
            renderHelper.state.clearRotateTransform();
            return;
        }

        setVerticalFrontDecorationBounds(depth, side, rotation, uMin, vMin, uMax, vMax);
        applyVerticalFrontDecorationRotation(side, rotation);
        renderHelper.renderPartialFace(side, renderer.blockAccess, block, x, y, z, icon, 0, 0, 1, 1);
        clearVerticalFrontDecorationRotation(side);
    }

    private void setVerticalFrontDecorationBounds(double depth, int side, int rotation, double uMin, double vMin,
            double uMax, double vMax) {
        double[] min = new double[2];
        double[] max = new double[2];

        mapVerticalFrontPoint(side, rotation, uMin, vMin, min);
        mapVerticalFrontPoint(side, rotation, uMax, vMax, max);

        double minX = Math.min(min[0], max[0]);
        double maxX = Math.max(min[0], max[0]);
        double minZ = Math.min(min[1], max[1]);
        double maxZ = Math.max(min[1], max[1]);

        if (side == RenderHelper.YPOS) {
            renderHelper.setRenderBounds(minX, 0, minZ, maxX, depth + .005, maxZ);
        } else {
            renderHelper.setRenderBounds(minX, 1 - depth - .005, minZ, maxX, 1, maxZ);
        }
    }

    private void mapVerticalFrontPoint(int side, int rotation, double u, double v, double[] out) {
        int rot = ((rotation % 4) + 4) % 4;

        if (side == RenderHelper.YPOS) {
            switch (rot) {
                case 1:
                    out[0] = 1 - v;
                    out[1] = 1 - u;
                    return;
                case 2:
                    out[0] = 1 - u;
                    out[1] = v;
                    return;
                case 3:
                    out[0] = v;
                    out[1] = u;
                    return;
                default:
                    out[0] = u;
                    out[1] = 1 - v;
                    return;
            }
        }

        switch (rot) {
            case 1:
                out[0] = v;
                out[1] = 1 - u;
                return;
            case 2:
                out[0] = 1 - u;
                out[1] = 1 - v;
                return;
            case 3:
                out[0] = 1 - v;
                out[1] = u;
                return;
            default:
                out[0] = u;
                out[1] = v;
                return;
        }
    }

    private void applyVerticalFrontDecorationRotation(int side, int rotation) {
        int uvRotation;
        if (side == RenderHelper.YPOS) {
            uvRotation = (4 - rotation) % 4;
            if ((rotation & 1) == 1) uvRotation = (uvRotation + 2) % 4;
            renderHelper.state.setUVRotation(RenderHelper.YPOS, uvRotation);
        } else {
            uvRotation = (rotation + 2) % 4;
            if ((rotation & 1) == 1) uvRotation = (uvRotation + 2) % 4;
            renderHelper.state.setUVRotation(RenderHelper.YNEG, uvRotation);
        }
    }

    private void clearVerticalFrontDecorationRotation(int side) {
        renderHelper.state.clearRotateTransform();
        renderHelper.state.clearUVRotation(side);
    }

    private static final int[] cut = new int[] {
            ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG
                    | ModularBoxRenderer.CUT_XPOS
                    | ModularBoxRenderer.CUT_XNEG
                    | ModularBoxRenderer.CUT_ZPOS,
            ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG
                    | ModularBoxRenderer.CUT_XPOS
                    | ModularBoxRenderer.CUT_XNEG
                    | ModularBoxRenderer.CUT_ZNEG,
            ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG
                    | ModularBoxRenderer.CUT_XPOS
                    | ModularBoxRenderer.CUT_ZNEG
                    | ModularBoxRenderer.CUT_ZPOS,
            ModularBoxRenderer.CUT_YPOS | ModularBoxRenderer.CUT_YNEG
                    | ModularBoxRenderer.CUT_XNEG
                    | ModularBoxRenderer.CUT_ZNEG
                    | ModularBoxRenderer.CUT_ZPOS, };

    private static final float[][] drawerXYWH1 = new float[][] { { 0, 0, 16, 16 }, };

    private static final float[][] drawerXYWH2 = new float[][] { { 0, 8, 16, 8 }, { 0, 0, 16, 8 }, };

    private static final float[][] drawerXYWH4 = new float[][] { { 0, 8, 8, 8 }, { 0, 0, 8, 8 }, { 8, 8, 8, 8 },
            { 8, 0, 8, 8 }, };

    private static final float[][] drawerXYWH3 = new float[][] { { 0, 8, 16, 8 }, { 0, 0, 8, 8 }, { 8, 0, 8, 8 }, };

    private void renderShroud(BlockDrawers block, int x, int y, int z, int side, int rotation, RenderBlocks renderer,
            boolean shrouded) {
        if (!shrouded) return;

        TileEntityDrawers tile = block.getTileEntity(renderer.blockAccess, x, y, z);

        double depth = block.halfDepth ? 8 : 16;
        double depthAdj = block.getTrimDepth() * 16;

        int count = 0;
        float w = 2;
        float h = 2;

        float[][] xywhSet = null;
        if (block.drawerCount == 1) {
            count = 1;
            w = 4;
            h = 4;
            xywhSet = drawerXYWH1;
        } else if (block.drawerCount == 2) {
            count = 2;
            xywhSet = drawerXYWH2;
        } else if (block.drawerCount == 3) {
            count = 3;
            xywhSet = drawerXYWH3;
        } else if (block.drawerCount == 4) {
            count = 4;
            xywhSet = drawerXYWH4;
        }

        IIcon icon = block.getIconTrim(renderer.blockAccess.getBlockMetadata(x, y, z));

        for (int i = 0; i < count; i++) {
            IDrawer drawer = tile.getDrawer(i);
            if (drawer == null || drawer.isEmpty()) continue;

            float[] xywh = xywhSet[i];
            float subX = xywh[0] + (xywh[2] - w) / 2;
            float subY = xywh[1] + (xywh[3] - h) / 2;

            renderHelper.setRenderBounds(
                    subX * unit,
                    subY * unit,
                    0,
                    (subX + w) * unit,
                    (subY + h) * unit,
                    (depth - depthAdj + .05) * unit);
            renderHelper.state.setOrientation(RenderHelper.ZPOS, side, rotation);
            renderHelper.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, icon);
            renderHelper.state.clearRotateTransform();
        }
    }

    private void renderIndicator(BlockDrawers block, int x, int y, int z, int side, int rotation, RenderBlocks renderer,
            int level) {
        if (level <= 0) return;

        TileEntityDrawers tile = block.getTileEntity(renderer.blockAccess, x, y, z);

        double depth = block.halfDepth ? 8 : 16;
        double depthAdj = block.getTrimDepth() * 16;

        int count = 0;
        float[][] xywhSet = null;
        if (block.drawerCount == 1) {
            count = 1;
            xywhSet = drawerXYWH1;
        } else if (block.drawerCount == 2) {
            count = 2;
            xywhSet = drawerXYWH2;
        } else if (block.drawerCount == 4) {
            count = 4;
            xywhSet = drawerXYWH4;
        }

        IIcon iconOff = block.getIndicatorIcon(count, false);
        IIcon iconOn = block.getIndicatorIcon(count, true);

        boxRenderer.setColor(ModularBoxRenderer.COLOR_WHITE);

        for (int i = 0; i < count; i++) {
            IDrawer drawer = tile.getDrawer(i);
            if (drawer == null) continue;

            float[] xywh = xywhSet[i];

            renderHelper.setRenderBounds(
                    xywh[0] * unit,
                    xywh[1] * unit,
                    0,
                    (xywh[0] + xywh[2]) * unit,
                    (xywh[1] + xywh[3]) * unit,
                    (depth - depthAdj + .05) * unit);
            renderHelper.state.setOrientation(RenderHelper.ZPOS, side, rotation);
            renderHelper.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconOff);
            renderHelper.state.clearRotateTransform();

            if (level == 1 && drawer.getMaxCapacity() > 0 && drawer.getRemainingCapacity() == 0) {
                renderHelper.state.setColorMult(1, 1, .9f, 1);
                renderHelper.setRenderBounds(
                        xywh[0] * unit,
                        xywh[1] * unit,
                        0,
                        (xywh[0] + xywh[2]) * unit,
                        (xywh[1] + xywh[3]) * unit,
                        (depth - depthAdj + .06) * unit);
                renderHelper.state.setOrientation(RenderHelper.ZPOS, side, rotation);
                renderHelper.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconOn);
                renderHelper.state.clearRotateTransform();
                renderHelper.state.resetColorMult();
            } else if (level >= 2) {
                double indXStart = xywh[0] + block.getIndStart() / unit;
                double indXEnd = xywh[0] + block.getIndEnd() / unit;
                double indXCur = (block.getIndSteps() == 0) ? indXEnd
                        : getIndEnd(block, tile, i, indXStart, (block.getIndEnd() - block.getIndStart()) / unit);

                double indYStart = xywh[1];
                double indYEnd = xywh[1] + xywh[3];
                double indYCur = indYEnd;

                if (indXCur > indXStart) {
                    renderHelper.state.setColorMult(1, 1, .9f, 1);
                    renderHelper.setRenderBounds(
                            indXStart * unit,
                            indYStart * unit,
                            0,
                            indXCur * unit,
                            indYCur * unit,
                            (depth - depthAdj + .06) * unit);
                    renderHelper.state.setOrientation(RenderHelper.ZPOS, side, rotation);
                    renderHelper.renderFace(RenderHelper.ZPOS, renderer.blockAccess, block, x, y, z, iconOn);
                    renderHelper.state.clearRotateTransform();
                    renderHelper.state.resetColorMult();
                }
            }
        }
    }

    private double getIndEnd(BlockDrawers block, TileEntityDrawers tile, int slot, double x, double w) {
        IDrawer drawer = tile.getDrawer(slot);
        if (drawer == null) return x;

        int cap = drawer.getMaxCapacity();
        int count = drawer.getStoredItemCount();
        if (cap == 0 || count == 0) return x;

        int step = block.getIndSteps() > 0 ? block.getIndSteps() : 1000;
        float fillAmt = (float) ((double) step * count / cap) / step;

        return x + (w * fillAmt);
    }

    private void renderExterior(BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer) {
        double depth = block.halfDepth ? .5 : 1;
        double xMin = 0, xMax = 1, yMin = 0, yMax = 1, zMin = 0, zMax = 1;

        switch (side) {
            case 0:
                yMin = 1 - depth;
                yMax = 1;
                break;
            case 1:
                yMin = 0;
                yMax = depth;
                break;
            case 2:
                zMin = 1 - depth;
                zMax = 1;
                break;
            case 3:
                zMin = 0;
                zMax = depth;
                break;
            case 4:
                xMin = 1 - depth;
                xMax = 1;
                break;
            case 5:
                xMin = 0;
                xMax = depth;
                break;
        }

        boxRenderer.renderExterior(
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

    private void renderInterior(BlockDrawers block, int x, int y, int z, int side, RenderBlocks renderer) {
        double unit = block.getTrimDepth();
        double depth = block.halfDepth ? .5 : 1;
        double xMin = unit, xMax = 1 - unit, yMin = unit, yMax = 1 - unit, zMin = unit, zMax = 1 - unit;

        switch (side) {
            case 0:
                yMin = 1 - depth;
                yMax = 1 - depth + unit;
                break;
            case 1:
                yMin = depth - unit;
                yMax = depth;
                break;
            case 2:
                zMin = 1 - depth;
                zMax = 1 - depth + unit;
                break;
            case 3:
                zMin = depth - unit;
                zMax = depth;
                break;
            case 4:
                xMin = 1 - depth;
                xMax = 1 - depth + unit;
                break;
            case 5:
                xMin = depth - unit;
                xMax = depth;
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
        return StorageDrawers.proxy.drawersRenderID;
    }
}
