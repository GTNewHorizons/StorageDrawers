package com.jaquadro.minecraft.storagedrawers.util;

import net.minecraft.client.gui.FontRenderer;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;

public class CountFormatter {

    public static String format(FontRenderer font, IDrawer drawer) {
        return formatApprox(font, drawer);
    }

    public static String formatStackNotation(IDrawer drawer) {
        if (drawer == null || drawer.isEmpty()) return "";

        String text;
        int stacks = drawer.getStoredItemCount() / drawer.getStoredItemStackSize();
        int remainder = drawer.getStoredItemCount() - (stacks * drawer.getStoredItemStackSize());

        if (stacks > 0 && remainder > 0) text = stacks + "x" + drawer.getStoredItemStackSize() + "+" + remainder;
        else if (stacks > 0) text = stacks + "x" + drawer.getStoredItemStackSize();
        else text = String.valueOf(remainder);

        return text;
    }

    public static String formatExact(IDrawer drawer) {
        if (drawer == null || drawer.isEmpty()) return "";

        return String.valueOf(drawer.getStoredItemCount());
    }

    public static String formatApprox(FontRenderer font, IDrawer drawer) {
        if (drawer == null || drawer.isEmpty()) return "";

        return formatApprox(font, drawer.getStoredItemCount());
    }

    public static String formatApprox(FontRenderer font, int count) {
        String text;

        if (count >= 1000000000) text = String.format("%.1fG", count / 1000000000f);
        else if (count >= 100000000) text = String.format("%.0fM", count / 1000000f);
        else if (count >= 1000000) text = String.format("%.1fM", count / 1000000f);
        else if (count >= 100000) text = String.format("%.0fK", count / 1000f);
        else if (count >= 10000) text = String.format("%.1fK", count / 1000f);
        else text = String.valueOf(count);

        return text;
    }
}
