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
    public void init() throws Throwable {
        // [17:09:38] [Server thread/DEBUG] [LaunchWrapper/StorageDrawers]: Transformer error
        // java.lang.ClassNotFoundException: Exception caught while transforming class
        // net.minecraft.client.gui.GuiScreen
        // at System//net.minecraft.launchwrapper.LaunchClassLoader.findClass(LaunchClassLoader.java:295)
        // [LaunchClassLoader.class:?]
        // at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:557) [?:?]
        // at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:490) [?:?]
        // at java.base/java.lang.Class.forName0(Native Method) [?:?]
        // at java.base/java.lang.Class.forName(Class.java:467) [?:?]
        // at java.base/java.lang.Class.forName(Class.java:458) [?:?]
        // at Launch//com.jaquadro.minecraft.storagedrawers.integration.NotEnoughItems.init(NotEnoughItems.java:23)
        // [NotEnoughItems.class:?]
        // at
        // Launch//com.jaquadro.minecraft.storagedrawers.integration.IntegrationRegistry.init(IntegrationRegistry.java:63)
        // [IntegrationRegistry.class:?]
        // at Launch//com.jaquadro.minecraft.storagedrawers.StorageDrawers.init(StorageDrawers.java:108)
        // [StorageDrawers.class:?]
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
