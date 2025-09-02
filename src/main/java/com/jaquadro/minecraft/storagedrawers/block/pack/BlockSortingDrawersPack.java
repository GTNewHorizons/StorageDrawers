package com.jaquadro.minecraft.storagedrawers.block.pack;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockConfiguration;
import com.jaquadro.minecraft.storagedrawers.api.pack.BlockType;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackBlock;
import com.jaquadro.minecraft.storagedrawers.api.pack.IPackDataResolver;
import com.jaquadro.minecraft.storagedrawers.api.pack.TextureType;
import com.jaquadro.minecraft.storagedrawers.core.ModCreativeTabs;
import com.jaquadro.minecraft.storagedrawers.integration.RefinedRelocation;
import com.jaquadro.minecraft.storagedrawers.integration.refinedrelocation.BlockSortingDrawers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSortingDrawersPack extends BlockSortingDrawers implements IPackBlock {

    private IPackDataResolver resolver;

    public BlockSortingDrawersPack(IPackDataResolver resolver, int drawerCount, boolean halfDepth) {
        super(
                resolver.getBlockName(BlockConfiguration.by(BlockType.DrawersSorting, drawerCount, halfDepth)),
                drawerCount,
                halfDepth);

        this.resolver = resolver;

        CreativeTabs tabs = resolver.getCreativeTabs(BlockType.DrawersSorting);
        if (StorageDrawers.config.cache.addonSeparateVanilla && tabs != null) setCreativeTab(tabs);
        else setCreativeTab(ModCreativeTabs.tabStorageDrawers);
    }

    public String[] getUnlocalizedNames() {
        String[] names = new String[16];
        for (int i = 0; i < 16; i++) {
            if (resolver.isValidMetaValue(i)) names[i] = resolver.getUnlocalizedName(i);
        }

        return names;
    }

    @Override
    public IPackDataResolver getDataResolver() {
        return resolver;
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list) {
        if (!StorageDrawers.config.cache.addonShowVanilla) return;

        for (int i = 0; i < 16; i++) {
            if (resolver.isValidMetaValue(i)) list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        meta = (meta < 0 || meta >= iconSide.length) ? 0 : meta;
        if (!resolver.isValidMetaValue(meta)) getSimilarBlock().getIcon(side, meta);

        return super.getIcon(side, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected IIcon getIcon(IBlockAccess blockAccess, int x, int y, int z, int side, int level) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        meta = (meta < 0 || meta >= iconSide.length) ? 0 : meta;
        if (!resolver.isValidMetaValue(meta)) getSimilarBlock().getIcon(side, meta);

        return super.getIcon(blockAccess, x, y, z, side, level);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconTrim(int meta) {
        meta = (meta < 0 || meta >= iconSide.length) ? 0 : meta;
        if (!resolver.isValidMetaValue(meta)) getSimilarBlock().getIconTrim(meta);

        return super.getIconTrim(meta);
    }

    private BlockSortingDrawers getSimilarBlock() {
        if (drawerCount == 1 && !halfDepth) return RefinedRelocation.fullDrawers1;
        if (drawerCount == 2 && !halfDepth) return RefinedRelocation.fullDrawers2;
        if (drawerCount == 4 && !halfDepth) return RefinedRelocation.fullDrawers4;
        if (drawerCount == 2 && halfDepth) return RefinedRelocation.halfDrawers2;
        if (drawerCount == 4 && halfDepth) return RefinedRelocation.halfDrawers4;
        return RefinedRelocation.fullDrawers1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        super.registerBlockIcons(register);

        iconSide = new IIcon[16];
        iconSideH = new IIcon[16];
        iconSideV = new IIcon[16];
        iconFront1 = new IIcon[16];
        iconFront2 = new IIcon[16];
        iconFront4 = new IIcon[16];
        iconTrim = new IIcon[16];
        iconSort = new IIcon[16];

        for (int i = 0; i < 16; i++) {
            if (!resolver.isValidMetaValue(i)) continue;

            iconFront1[i] = register.registerIcon(resolver.getTexturePath(TextureType.Front1, i));
            iconFront2[i] = register.registerIcon(resolver.getTexturePath(TextureType.Front2, i));
            iconFront4[i] = register.registerIcon(resolver.getTexturePath(TextureType.Front4, i));
            iconSide[i] = register.registerIcon(resolver.getTexturePath(TextureType.Side, i));
            iconSideV[i] = register.registerIcon(resolver.getTexturePath(TextureType.SideVSplit, i));
            iconSideH[i] = register.registerIcon(resolver.getTexturePath(TextureType.SideHSplit, i));
            iconTrim[i] = register.registerIcon(resolver.getTexturePath(TextureType.TrimBorder, i));
            iconSort[i] = register.registerIcon(resolver.getTexturePath(TextureType.SideSort, i));
        }
    }
}
