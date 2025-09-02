package com.jaquadro.minecraft.storagedrawers.core.recipe;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import com.jaquadro.minecraft.storagedrawers.item.ItemDrawers;

public class TemplateRecipe implements IRecipe {

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return getCraftingResult(inventory) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        List<ItemStack> sticks = OreDictionary.getOres("stickWood");
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (row == 1 && col == 1) continue;

                ItemStack stack = inventory.getStackInRowAndColumn(col, row);
                if (stack == null) return null;

                boolean match = false;
                for (ItemStack comp : sticks) {
                    if (comp != null && comp.isItemEqual(stack)) match = true;
                }

                if (!match) return null;
            }
        }

        ItemStack center = inventory.getStackInRowAndColumn(1, 1);
        if (center == null || !(center.getItem() instanceof ItemDrawers)) return null;

        if (center.getTagCompound() != null && center.getTagCompound().hasKey("tile")) return null;

        return getRecipeOutput();
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ModItems.upgradeTemplate, 1);
    }
}
