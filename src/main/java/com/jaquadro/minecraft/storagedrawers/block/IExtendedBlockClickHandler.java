package com.jaquadro.minecraft.storagedrawers.block;

import net.minecraft.entity.player.EntityPlayer;

public interface IExtendedBlockClickHandler {

    public void onBlockClicked(EntityPlayer player, int side, float hitX, float hitY, float hitZ, boolean invertShift,
            boolean isHolding);
}
