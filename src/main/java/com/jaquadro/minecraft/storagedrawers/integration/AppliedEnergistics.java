package com.jaquadro.minecraft.storagedrawers.integration;

import java.lang.reflect.Constructor;

import javax.annotation.Nonnull;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.integration.ae2.DrawerExternalStorageHandler;
import com.jaquadro.minecraft.storagedrawers.integration.ae2.IStorageBusMonitorFactory;
import com.jaquadro.minecraft.storagedrawers.integration.ae2.IngredientHandler;
import com.jaquadro.minecraft.storagedrawers.integration.ae2.ShapedRecipeHandler;
import com.jaquadro.minecraft.storagedrawers.integration.ae2.ShapelessRecipeHandler;

import appeng.api.AEApi;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.recipes.IIngredient;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;

public final class AppliedEnergistics extends IntegrationModule {

    private IStorageBusMonitorFactory factory;

    @Nonnull
    @Override
    public String getModID() {
        return "appliedenergistics2";
    }

    @Override
    protected boolean moduleConfig() {
        return StorageDrawers.config.cache.enableAE2Integration;
    }

    @Override
    public void init() throws Throwable {
        ShapedRecipeHandler shapedHandler = new ShapedRecipeHandler();
        if (shapedHandler.isValid()) {
            StorageDrawers.recipeHandlerRegistry.registerRecipeHandler(shapedHandler.getRecipeClass(), shapedHandler);
        }

        ShapelessRecipeHandler shapelessHandler = new ShapelessRecipeHandler();
        if (shapelessHandler.isValid()) {
            StorageDrawers.recipeHandlerRegistry
                    .registerRecipeHandler(shapelessHandler.getRecipeClass(), shapelessHandler);
        }

        StorageDrawers.recipeHandlerRegistry.registerIngredientHandler(IIngredient.class, new IngredientHandler());

        ReflectionFactory rfactory = new ReflectionFactory();
        if (!rfactory.init()) throw new Exception("No valid Storage Bus Monitor factory");

        factory = rfactory;
    }

    @Override
    public void postInit() {
        AEApi.instance().registries().externalStorage()
                .addExternalStorageInterface(new DrawerExternalStorageHandler(factory));
    }

    private static class ReflectionFactory implements IStorageBusMonitorFactory {

        private Constructor<?> constMEAdaptor;
        private Constructor<?> constMonitor;

        public boolean init() {
            try {
                Class<?> classInventoryAdaptor = Class.forName("appeng.util.InventoryAdaptor");
                Class<?> classMEAdaptor = Class.forName("appeng.util.inv.IMEAdaptor");
                Class<?> classMonitor = Class.forName("appeng.me.storage.MEMonitorIInventory");
                constMEAdaptor = classMEAdaptor.getConstructor(IMEInventory.class, BaseActionSource.class);
                constMonitor = classMonitor.getConstructor(classInventoryAdaptor);
                return true;
            } catch (Throwable t) {
                return false;
            }
        }

        @Override
        public IMEMonitor<IAEItemStack> createStorageBusMonitor(IMEInventory<IAEItemStack> inventory,
                BaseActionSource src) {
            try {
                Object adaptor = constMEAdaptor.newInstance(inventory, src);
                Object monitor = constMonitor.newInstance(adaptor);

                return (IMEMonitor<IAEItemStack>) monitor;
            } catch (Throwable t) {
                return null;
            }
        }
    }
}
