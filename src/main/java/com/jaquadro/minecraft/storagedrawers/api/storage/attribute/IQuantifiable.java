package com.jaquadro.minecraft.storagedrawers.api.storage.attribute;

public interface IQuantifiable {

    /**
     * Gets whether the drawer has the quantifiable attribute. The quantifiable attribute instructs the drawer to render
     * the quantity of the item contained in the drawer.
     */
    boolean isQuantified();

    boolean setIsQuantified(boolean state);
}
