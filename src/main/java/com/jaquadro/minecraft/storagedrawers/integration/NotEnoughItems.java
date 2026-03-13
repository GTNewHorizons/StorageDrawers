package com.jaquadro.minecraft.storagedrawers.integration;

import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.entity.RenderItem;

public final class NotEnoughItems extends IntegrationModule {

    private static Field fdDrawItems;

    @Nonnull
    @Override
    public String getModID() {
        return "NotEnoughItems";
    }

    @Override
    protected boolean moduleConfig() {
        return true;
    }

    @Override
    protected LoadingSide loadingSide() {
        return LoadingSide.CLIENT;
    }

    @Override
    public void init() throws Throwable {
        Class<?> clGuiContainerManager = Class.forName("codechicken.nei.guihook.GuiContainerManager");
        fdDrawItems = clGuiContainerManager.getDeclaredField("drawItems");
    }

    public static RenderItem setItemRender(RenderItem itemRender) {
        if (fdDrawItems == null) return null;
        try {
            RenderItem prev = (RenderItem) fdDrawItems.get(null);
            fdDrawItems.set(null, itemRender);
            return prev;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
