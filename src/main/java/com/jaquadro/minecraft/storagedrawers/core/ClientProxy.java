package com.jaquadro.minecraft.storagedrawers.core;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersComp;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityDrawersStandard;
import com.jaquadro.minecraft.storagedrawers.block.tile.TileEntityFramingTable;
import com.jaquadro.minecraft.storagedrawers.client.renderer.ControllerRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.DrawersCustomRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.DrawersItemRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.DrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.FramingTableRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityDrawersRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TileEntityFramingRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TrimCustomRenderer;
import com.jaquadro.minecraft.storagedrawers.client.renderer.TrimItemRender;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

    private DrawersItemRenderer itemRenderer = new DrawersItemRenderer();
    private TrimItemRender trimItemRenderer = new TrimItemRender();

    @Override
    public void registerRenderers() {
        drawersRenderID = RenderingRegistry.getNextAvailableRenderId();
        controllerRenderID = RenderingRegistry.getNextAvailableRenderId();
        drawersCustomRenderID = RenderingRegistry.getNextAvailableRenderId();
        framingTableRenderID = RenderingRegistry.getNextAvailableRenderId();
        trimCustomRenderID = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(drawersRenderID, new DrawersRenderer());
        RenderingRegistry.registerBlockHandler(controllerRenderID, new ControllerRenderer());
        RenderingRegistry.registerBlockHandler(drawersCustomRenderID, new DrawersCustomRenderer());
        RenderingRegistry.registerBlockHandler(framingTableRenderID, new FramingTableRenderer());
        RenderingRegistry.registerBlockHandler(trimCustomRenderID, new TrimCustomRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersStandard.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDrawersComp.class, new TileEntityDrawersRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFramingTable.class, new TileEntityFramingRenderer());

        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.trimCustom), trimItemRenderer);
    }

    @Override
    public void registerDrawer(Block block) {
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), itemRenderer);
    }
}
