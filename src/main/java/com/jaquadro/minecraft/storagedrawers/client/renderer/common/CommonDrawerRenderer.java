package com.jaquadro.minecraft.storagedrawers.client.renderer.common;

import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.jaquadro.minecraft.storagedrawers.block.BlockDrawersCustom;
import com.jaquadro.minecraft.storagedrawers.client.renderer.ModularBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.PanelBoxRenderer;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelper;
import com.jaquadro.minecraft.storagedrawers.util.RenderHelperState;

public class CommonDrawerRenderer {

    private static final double UNIT_7_16 = 0.4375;
    private static final double UNIT_9_16 = 0.5625;

    private PanelBoxRenderer panelRenderer = new PanelBoxRenderer();

    private double depth;
    private double trimWidth;
    private double trimDepth;

    private RenderHelper start(IBlockAccess world, int x, int y, int z, BlockDrawersCustom block, int direction,
            int rotation) {
        depth = block.halfDepth ? .5 : 0;
        trimWidth = block.getTrimWidth();
        trimDepth = block.getTrimDepth();

        panelRenderer.setTrimWidth(trimWidth);
        panelRenderer.setTrimDepth(0);
        panelRenderer.setTrimColor(ModularBoxRenderer.COLOR_WHITE);
        panelRenderer.setPanelColor(ModularBoxRenderer.COLOR_WHITE);

        RenderHelper renderHelper = RenderHelper.instances.get();
        if (world != null) renderHelper.setColorAndBrightness(world, block, x, y, z);

        renderHelper.state.setOrientation(RenderHelper.ZNEG, direction, rotation);
        if (direction > 1) {
            renderHelper.state.setUVRotation(
                    RenderHelper.YPOS,
                    RenderHelperState.ROTATION_BY_FACE_FACE[RenderHelper.ZNEG][direction]);
        } else if (direction == RenderHelper.YPOS) {
            renderHelper.state.setUVRotation(RenderHelper.YPOS, (4 - rotation) % 4);
        } else {
            renderHelper.state.setUVRotation(RenderHelper.YNEG, (rotation + 2) % 4);
        }

        return renderHelper;
    }

    private void end() {
        RenderHelper renderHelper = RenderHelper.instances.get();

        renderHelper.state.clearRotateTransform();
        renderHelper.state.clearUVRotation(RenderHelper.YPOS);
        renderHelper.state.clearUVRotation(RenderHelper.YNEG);
    }

    private void renderFrontFace(RenderHelper renderHelper, IBlockAccess world, int x, int y, int z,
            BlockDrawersCustom block, double xMin, double yMin, double xMax, double yMax, IIcon icon) {
        renderHelper.setRenderBounds(xMin, yMin, depth + trimDepth, xMax, yMax, 1);
        renderHelper.renderFace(RenderHelper.ZNEG, world, block, x, y, z, icon);
    }

    private void renderFrontOverlay(RenderHelper renderHelper, IBlockAccess world, int x, int y, int z,
            BlockDrawersCustom block, double xMin, double yMin, double xMax, double yMax) {
        renderFrontFace(renderHelper, world, x, y, z, block, xMin, yMin, xMax, yMax, block.getHandleOverlay());
        renderFrontFace(renderHelper, world, x, y, z, block, xMin, yMin, xMax, yMax, block.getFaceShadowOverlay());
    }

    public void renderBasePass(IBlockAccess world, int x, int y, int z, BlockDrawersCustom block, int direction,
            IIcon iconSide, IIcon iconTrim, IIcon iconFront) {
        renderBasePass(world, x, y, z, block, direction, 0, iconSide, iconTrim, iconFront);
    }

    public void renderBasePass(IBlockAccess world, int x, int y, int z, BlockDrawersCustom block, int direction,
            int rotation, IIcon iconSide, IIcon iconTrim, IIcon iconFront) {
        RenderHelper renderHelper = start(world, x, y, z, block, direction, rotation);

        panelRenderer.setTrimIcon(iconTrim);
        panelRenderer.setPanelIcon(iconSide);

        for (int i = 0; i < 6; i++) {
            if (i != RenderHelper.ZNEG) panelRenderer.renderFacePanel(i, world, block, x, y, z, 0, 0, depth, 1, 1, 1);
            panelRenderer.renderFaceTrim(i, world, block, x, y, z, 0, 0, depth, 1, 1, 1);
        }

        panelRenderer.setTrimDepth(trimDepth);
        panelRenderer.renderInteriorTrim(RenderHelper.ZNEG, world, block, x, y, z, 0, 0, depth, 1, 1, 1);

        if (block.drawerCount == 1) {
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    trimWidth,
                    trimWidth,
                    1 - trimWidth,
                    1 - trimWidth,
                    iconFront);
        } else if (block.drawerCount == 2) {
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    trimWidth,
                    trimWidth,
                    1 - trimWidth,
                    UNIT_7_16,
                    iconFront);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    trimWidth,
                    UNIT_9_16,
                    1 - trimWidth,
                    1 - trimWidth,
                    iconFront);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    trimWidth,
                    UNIT_7_16,
                    1 - trimWidth,
                    UNIT_9_16,
                    iconTrim);
        } else if (block.drawerCount == 4) {
            renderHelper.state.flipTexture = true;
            renderFrontFace(renderHelper, world, x, y, z, block, trimWidth, trimWidth, UNIT_7_16, UNIT_7_16, iconFront);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    trimWidth,
                    UNIT_9_16,
                    UNIT_7_16,
                    1 - trimWidth,
                    iconFront);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    UNIT_9_16,
                    trimWidth,
                    1 - trimWidth,
                    UNIT_7_16,
                    iconFront);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    UNIT_9_16,
                    UNIT_9_16,
                    1 - trimWidth,
                    1 - trimWidth,
                    iconFront);

            renderFrontFace(renderHelper, world, x, y, z, block, trimWidth, UNIT_7_16, UNIT_7_16, UNIT_9_16, iconTrim);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    UNIT_9_16,
                    UNIT_7_16,
                    1 - trimWidth,
                    UNIT_9_16,
                    iconTrim);
            renderFrontFace(renderHelper, world, x, y, z, block, UNIT_7_16, trimWidth, UNIT_9_16, UNIT_7_16, iconTrim);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    UNIT_7_16,
                    UNIT_9_16,
                    UNIT_9_16,
                    1 - trimWidth,
                    iconTrim);
            renderFrontFace(renderHelper, world, x, y, z, block, UNIT_7_16, UNIT_7_16, UNIT_9_16, UNIT_9_16, iconTrim);
            renderHelper.state.flipTexture = false;
        }

        end();
    }

    public void renderOverlayPass(IBlockAccess world, int x, int y, int z, BlockDrawersCustom block, int direction,
            IIcon iconTrim, IIcon iconFront) {
        renderOverlayPass(world, x, y, z, block, direction, 0, iconTrim, iconFront);
    }

    public void renderOverlayPass(IBlockAccess world, int x, int y, int z, BlockDrawersCustom block, int direction,
            int rotation, IIcon iconTrim, IIcon iconFront) {
        RenderHelper renderHelper = start(world, x, y, z, block, direction, rotation);

        IIcon trimShadow = block.getTrimShadowOverlay(iconTrim == iconFront);

        panelRenderer.setTrimIcon(trimShadow);
        panelRenderer.renderFaceTrim(RenderHelper.ZNEG, world, block, x, y, z, 0, 0, depth, 1, 1, 1);

        if (block.drawerCount == 1) {
            renderFrontOverlay(renderHelper, world, x, y, z, block, trimWidth, trimWidth, 1 - trimWidth, 1 - trimWidth);
        } else if (block.drawerCount == 2) {
            renderFrontOverlay(renderHelper, world, x, y, z, block, trimWidth, trimWidth, 1 - trimWidth, UNIT_7_16);
            renderFrontOverlay(renderHelper, world, x, y, z, block, trimWidth, UNIT_9_16, 1 - trimWidth, 1 - trimWidth);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    trimWidth,
                    UNIT_7_16,
                    1 - trimWidth,
                    UNIT_9_16,
                    trimShadow);
        } else if (block.drawerCount == 4) {
            renderFrontOverlay(renderHelper, world, x, y, z, block, trimWidth, trimWidth, UNIT_7_16, UNIT_7_16);
            renderFrontOverlay(renderHelper, world, x, y, z, block, trimWidth, UNIT_9_16, UNIT_7_16, 1 - trimWidth);
            renderFrontOverlay(renderHelper, world, x, y, z, block, UNIT_9_16, trimWidth, 1 - trimWidth, UNIT_7_16);
            renderFrontOverlay(renderHelper, world, x, y, z, block, UNIT_9_16, UNIT_9_16, 1 - trimWidth, 1 - trimWidth);

            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    trimWidth,
                    UNIT_7_16,
                    UNIT_7_16,
                    UNIT_9_16,
                    trimShadow);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    UNIT_9_16,
                    UNIT_7_16,
                    1 - trimWidth,
                    UNIT_9_16,
                    trimShadow);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    UNIT_7_16,
                    trimWidth,
                    UNIT_9_16,
                    UNIT_7_16,
                    trimShadow);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    UNIT_7_16,
                    UNIT_9_16,
                    UNIT_9_16,
                    1 - trimWidth,
                    trimShadow);
            renderFrontFace(
                    renderHelper,
                    world,
                    x,
                    y,
                    z,
                    block,
                    UNIT_7_16,
                    UNIT_7_16,
                    UNIT_9_16,
                    UNIT_9_16,
                    trimShadow);
        } else RenderHelper.instances.get().renderEmptyPlane(x, y, z);

        end();
    }
}
