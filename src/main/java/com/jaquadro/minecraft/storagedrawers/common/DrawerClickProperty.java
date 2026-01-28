package com.jaquadro.minecraft.storagedrawers.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class DrawerClickProperty implements IExtendedEntityProperties {

    public static final String PROP_KEY = "drawer-click";

    public long lastLeftClickTime;

    @SubscribeEvent
    public static void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer player) {
            player.registerExtendedProperties(PROP_KEY, new DrawerClickProperty());
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = new NBTTagCompound();
        compound.setTag(PROP_KEY, tag);

        tag.setLong("lastLeftClickTime", lastLeftClickTime);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound tag = compound.getCompoundTag(PROP_KEY);

        if (tag != null) {
            lastLeftClickTime = tag.getLong("lastLeftClickTime");
        }
    }

    @Override
    public void init(Entity entity, World world) {

    }
}
