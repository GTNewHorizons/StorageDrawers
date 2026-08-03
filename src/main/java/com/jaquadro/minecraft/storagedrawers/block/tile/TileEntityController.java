package com.jaquadro.minecraft.storagedrawers.block.tile;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.jaquadro.minecraft.storagedrawers.StorageDrawers;
import com.jaquadro.minecraft.storagedrawers.api.inventory.IDrawerInventory;
import com.jaquadro.minecraft.storagedrawers.api.security.ISecurityProvider;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.IFractionalDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.INetworked;
import com.jaquadro.minecraft.storagedrawers.api.storage.IPriorityGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.ISmartGroup;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.ILockable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IProtectable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IQuantifiable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IShroudable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.IVoidable;
import com.jaquadro.minecraft.storagedrawers.api.storage.attribute.LockAttribute;
import com.jaquadro.minecraft.storagedrawers.block.BlockDrawers;
import com.jaquadro.minecraft.storagedrawers.security.SecurityManager;
import com.jaquadro.minecraft.storagedrawers.util.ItemHashMap;
import com.jaquadro.minecraft.storagedrawers.util.SlotRecord;
import com.mojang.authlib.GameProfile;

public class TileEntityController extends TileEntity
        implements IDrawerGroup, IPriorityGroup, ISmartGroup, ISidedInventory {

    /**
     * Each controller tick one of 3 updates can occur. FULL is queued when placing/loading controller block or removing
     * a drawer from network. PRIORITY when removeing/adding new items or adding upgrade to a drawer. Priority included
     * in FULL updates. NONE when no update needed (default state when no changes made since last update)
     */
    enum controllerUpdateType {
        NONE,
        PRIORITY,
        FULL
    }

    /**
     * 6 proirity states a drawer slot can have as defined in getSlotPriority().
     */
    private enum priorityState {
        PRI_VOID,
        PRI_LOCKED,
        PRI_NORMAL,
        PRI_EMPTY,
        PRI_LOCKED_EMPTY,
        PRI_DISABLED
    }

    /** Full update always needed when entity first loaded */
    private controllerUpdateType nextUpdate = controllerUpdateType.FULL;

    /** 0 = insertion 1 = extraction used for IInvenotry interfacing blocks */
    private final int[] virtualSlots = new int[] { 0, 1 };
    private final int[] emptySlots = new int[] { 0 };

    /** Item that controller Entity can extract with IInventory interface (Vanilla hoppers and Pipes) */
    private ItemStack IInventoryExtract;

    /** IDrawerGroup objects in network (drawers, controllers, slaves) are found here */
    private final Map<BlockCoord, IDrawerGroup> storage = new HashMap<BlockCoord, IDrawerGroup>();

    /** SlotRecords with coordinate referencing storage along with access indexes */
    private final List<SlotRecord> drawerSlotList = new ArrayList<SlotRecord>();

    /** Containers for ordering drawerSlots during priority update */
    private final List<List<Integer>> drawerSlotPriorityBins = new ArrayList<>();

    /** drawerSlotList indices in Priority order */
    private int[] drawerSlots = new int[0];

    /** int[] drawerSlots Index of first empty slot since last priority update or when setting slot to null */
    private int emptyDrawerSlot = 0;

    /** Lookup a drawerSlotList for a given itemstack request (including oredictionary if no direct match) */
    private final ItemHashMap drawerPrimaryLookup = new ItemHashMap();

    /**
     * to prevent multiple controllers they are cleared when encountered during full update either directly or from
     * another drawer linked to a different controller. This variable prevents multiple clears of the same controller
     */
    private final Set<BlockCoord> clearedControllers = new HashSet<BlockCoord>();

    /**
     * if disableAutoSync is true drawers that need syncing to client will go into syncSet and sync only when loading,
     * doing inventory dump, toggling autosync or breaking controller
     */
    private boolean enableClientAutoSync = true;
    private final Set<BlockCoord> syncSet = new HashSet<BlockCoord>();

    private int direction;
    private final int range;
    private final int maxDrawers;

    private int drawersCount;

    private long lastClickTime;
    private UUID lastClickUUID;

    private String customName;

    public TileEntityController() {

        range = StorageDrawers.config.getControllerRange();
        maxDrawers = StorageDrawers.config.getControllerMaxDrawers();

        for (int i = 0; i < priorityState.values().length; i++) {
            drawerSlotPriorityBins.add(new ArrayList<>());
        }
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction % 6;
    }

    public void interactPutItemsIntoInventory(EntityPlayer player) {

        boolean dumpInventory = worldObj.getTotalWorldTime() - lastClickTime < 10
                && player.getPersistentID().equals(lastClickUUID);
        int count = 0;

        if (!dumpInventory) {
            ItemStack currentStack = player.inventory.getCurrentItem();
            if (currentStack != null) {
                count = insertItems(currentStack, player.getGameProfile());
                if (currentStack.stackSize == 0)
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        } else {
            for (int i = 0, n = player.inventory.getSizeInventory(); i < n; i++) {
                ItemStack subStack = player.inventory.getStackInSlot(i);
                if (subStack != null) {
                    count += insertItems(subStack, player.getGameProfile());
                    if (subStack.stackSize == 0) player.inventory.setInventorySlotContents(i, null);
                }
            }

            syncClient();

            if (count > 0) StorageDrawers.proxy.updatePlayerInventory(player);
        }

        lastClickTime = worldObj.getTotalWorldTime();
        lastClickUUID = player.getPersistentID();
    }

    private int insertItems(@NotNull ItemStack stack, GameProfile profile) {
        int itemsLeft = stack.stackSize;

        for (int slot : enumerateDrawersForInsertion(stack, false)) {

            IDrawerGroup group = getGroupForDrawerSlot(slot);
            if (group instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable) group)) continue;
            }

            IDrawer drawer = getDrawer(slot);
            ItemStack itemProto = drawer.getStoredItemPrototype();
            if (itemProto == null) break;

            itemsLeft = insertItemsIntoDrawer(drawer, itemsLeft, false);

            if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) itemsLeft = 0;
            if (itemsLeft == 0) break;
        }

        int count = stack.stackSize - itemsLeft;
        stack.stackSize = itemsLeft;

        return count;
    }

    public int insertItemsIntoDrawer(@NotNull IDrawer drawer, int itemCount, boolean testOnly) {

        int capacity = drawer.getMaxCapacity();
        int storedItems = drawer.getStoredItemCount();

        int storableItems = capacity - storedItems;
        if (drawer instanceof IFractionalDrawer) {
            IFractionalDrawer fracDrawer = (IFractionalDrawer) drawer;
            if (!fracDrawer.isSmallestUnit() && fracDrawer.getStoredItemRemainder() > 0) storableItems--;
        }

        if (storableItems == 0) return itemCount;

        int remainder = Math.max(itemCount - storableItems, 0);

        if (testOnly) return remainder;

        storedItems += Math.min(itemCount, storableItems);
        drawer.setStoredItemCount(storedItems);

        return remainder;
    }

    public void toggleProtection(GameProfile profile, ISecurityProvider provider) {
        IProtectable template = null;
        UUID state = null;

        for (IDrawerGroup drawerGroup : storage.values()) {
            if (drawerGroup == null) continue;

            if (drawerGroup instanceof IProtectable) {
                IProtectable protectable = (IProtectable) drawerGroup;
                if (!SecurityManager.hasOwnership(profile, protectable)) continue;

                if (template == null) {
                    template = protectable;

                    if (template.getOwner() == null) state = profile.getId();
                    else {
                        state = null;
                        provider = null;
                    }
                }

                protectable.setOwner(state);
                protectable.setSecurityProvider(provider);
            }
        }
    }

    public void toggleShroud(GameProfile profile) {
        IShroudable template = null;
        boolean state = false;

        for (IDrawerGroup drawerGroup : storage.values()) {
            if (drawerGroup == null) continue;

            if (drawerGroup instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable) drawerGroup)) continue;
            }

            for (int i = 0, n = drawerGroup.getDrawerCount(); i < n; i++) {
                if (!drawerGroup.isDrawerEnabled(i)) continue;

                IDrawer drawer = drawerGroup.getDrawer(i);
                if (!(drawer instanceof IShroudable)) continue;

                IShroudable shroudableStorage = (IShroudable) drawer;
                if (template == null) {
                    template = shroudableStorage;
                    state = !template.isShrouded();
                }

                shroudableStorage.setIsShrouded(state);
            }
        }
    }

    public void toggleQuantify(GameProfile profile) {
        IQuantifiable template = null;
        boolean state = false;

        for (IDrawerGroup drawerGroup : storage.values()) {
            if (drawerGroup == null) continue;

            if (drawerGroup instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable) drawerGroup)) continue;
            }

            for (int i = 0, n = drawerGroup.getDrawerCount(); i < n; i++) {
                if (!drawerGroup.isDrawerEnabled(i)) continue;

                IDrawer drawer = drawerGroup.getDrawer(i);
                if (!(drawer instanceof IQuantifiable)) continue;

                IQuantifiable quantifiableStorage = (IQuantifiable) drawer;
                if (template == null) {
                    template = quantifiableStorage;
                    state = !template.isQuantified();
                }

                quantifiableStorage.setIsQuantified(state);
            }
        }
    }

    public void toggleLock(EnumSet<LockAttribute> attributes, LockAttribute key, GameProfile profile) {
        ILockable template = null;
        boolean state = false;

        for (IDrawerGroup drawerGroup : storage.values()) {
            if (drawerGroup == null) continue;

            if (drawerGroup instanceof IProtectable) {
                if (!SecurityManager.hasAccess(profile, (IProtectable) drawerGroup)) continue;
            }

            if (drawerGroup instanceof ILockable) {
                ILockable lockableStorage = (ILockable) drawerGroup;
                if (template == null) {
                    template = lockableStorage;
                    state = !template.isLocked(key);
                }

                for (LockAttribute attr : attributes) lockableStorage.setLocked(attr, state);
            } else {
                for (int i = 0, n = drawerGroup.getDrawerCount(); i < n; i++) {
                    if (!drawerGroup.isDrawerEnabled(i)) continue;

                    IDrawer drawer = drawerGroup.getDrawer(i);
                    if (!(drawer instanceof IShroudable)) continue;

                    ILockable lockableStorage = (ILockable) drawer;
                    if (template == null) {
                        template = lockableStorage;
                        state = !template.isLocked(key);
                    }

                    for (LockAttribute attr : attributes) lockableStorage.setLocked(attr, state);
                }
            }
        }
    }

    private void addSlotRecordMap(ItemHashMap lookup, SlotRecord record, @NotNull IDrawerGroup group) {

        IDrawer drawer = group.getDrawer(record.slot);
        if (drawer.isEmpty()) return;

        ItemStack item = drawer.getStoredItemPrototype();
        lookup.register(item.getItem(), item.getItemDamage(), record, drawer.getOreDictMatches());
    }

    private priorityState getSlotPriority(@NotNull SlotRecord record) {
        IDrawerGroup group = getGroupForCoord(record.coord);
        if (group == null) {
            return priorityState.PRI_DISABLED;
        }

        int drawerSlot = record.slot;
        if (!group.isDrawerEnabled(drawerSlot)) {
            return priorityState.PRI_DISABLED;
        }

        IDrawer drawer = group.getDrawer(drawerSlot);
        if (drawer.isEmpty()) {
            if ((drawer instanceof ILockable && ((ILockable) drawer).isLocked(LockAttribute.LOCK_EMPTY))
                    || (group instanceof ILockable && ((ILockable) group).isLocked(LockAttribute.LOCK_EMPTY))) {
                return priorityState.PRI_LOCKED_EMPTY;
            } else return priorityState.PRI_EMPTY;
        }

        if ((drawer instanceof IVoidable && ((IVoidable) drawer).isVoid())
                || (group instanceof IVoidable && ((IVoidable) group).isVoid())) {
            return priorityState.PRI_VOID;
        }

        if ((drawer instanceof ILockable && ((ILockable) drawer).isLocked(LockAttribute.LOCK_POPULATED))
                || (group instanceof ILockable && ((ILockable) group).isLocked(LockAttribute.LOCK_POPULATED))) {
            return priorityState.PRI_LOCKED;
        }

        return priorityState.PRI_NORMAL;
    }

    public void drawerUpdatePriority() {

        // drawerSlotPriorityBins already cleared at end of this function and only used here
        for (int i = 0; i < drawerSlotList.size(); i++) {
            priorityState priority = getSlotPriority(drawerSlotList.get(i));
            drawerSlotPriorityBins.get(priority.ordinal()).add(i);
        }

        // Resize drawerSlots if needed
        int size = drawerSlotList.size();
        if (size != drawerSlots.length) {
            drawerSlots = new int[size];
        }

        emptyDrawerSlot = size;
        int index = 0;
        for (int i = 0; i < drawerSlotPriorityBins.size(); i++) {

            for (int drawerSlotListSlot : drawerSlotPriorityBins.get(i)) {

                if (i >= priorityState.PRI_EMPTY.ordinal() && emptyDrawerSlot == size) {
                    emptyDrawerSlot = index;
                }
                drawerSlotList.get(drawerSlotListSlot).priorityIndex = index;
                drawerSlots[index++] = drawerSlotListSlot;
            }
            drawerSlotPriorityBins.get(i).clear();
        }
    }

    public void DrawerSearchUpdateRecordInfo(TileEntity te) {

        if (te == null) {
            return;
        }

        if (te instanceof TileEntityController) {
            storage.put(new BlockCoord(te.xCoord, te.yCoord, te.zCoord), null);
        }

        else if (te instanceof TileEntitySlave) {

            storage.put(new BlockCoord(te.xCoord, te.yCoord, te.zCoord), null);

            if (((TileEntitySlave) te).getController() != this)
                ((TileEntitySlave) te).setControllerCoord(xCoord, yCoord, zCoord);

        } else if (te instanceof IDrawerGroup) {

            IDrawerGroup group = (IDrawerGroup) te;
            group.setControllerCoord(xCoord, yCoord, zCoord);

            IDrawerInventory inventory = group.getDrawerInventory();
            if (inventory == null) return;

            storage.put(new BlockCoord(te.xCoord, te.yCoord, te.zCoord), group);

            for (int i = 0, n = group.getDrawerCount(); i < n; i++) {

                int size = drawerSlotList.size();
                SlotRecord slotRecord = new SlotRecord(new BlockCoord(te.xCoord, te.yCoord, te.zCoord), i, size);
                drawerSlotList.add(slotRecord);

                IDrawer drawer = getDrawer(size);
                drawer.setTileEntityController(this, size);

                addSlotRecordMap(drawerPrimaryLookup, slotRecord, group);
            }
        }
    }

    /**
     * Controller can be set such that any drawers registered do not sync as soon as an item is inserted/extracted
     * (unless manually inserting) as these calls are expensive when inserting a large quantity of ItemStacks but result
     * in displayed item count and texture not automatically updating. This function adds to the set of coordinates that
     * need updating.
     */
    public void addClientSyncList(int xcoord, int ycoord, int zcoord) {

        BlockCoord teCoord = new BlockCoord(xcoord, ycoord, zcoord);
        syncSet.add(teCoord);
    }

    /** When drawer is interacted with sync with client */
    public void syncClient() {

        for (BlockCoord element : syncSet) {
            getWorldObj().markBlockForUpdate(element.x(), element.y(), element.z());
        }
        syncSet.clear();
    }

    /** Set the controller not sync unless interacted with */
    public void toggleSync() {
        syncClient();
        enableClientAutoSync = !enableClientAutoSync;
    }

    /** Get autosync configuration */
    public boolean getClientAutoSync() {
        return enableClientAutoSync;
    }

    /**
     * Clear storage and drawer variables referring to this controller if nullControllerCoord is set (prevents markdirty
     * being set upon loading)
     */
    public void clearStorage(boolean nullControllerCoord) {

        for (IDrawerGroup drawer : storage.values()) {
            if (drawer != null) {
                drawer.clearControllerVariables(xCoord, yCoord, zCoord, nullControllerCoord);
            }
        }
        storage.clear();
    }

    public void clearDrawerVariables(boolean nullControllerCoord) {

        nextUpdate = controllerUpdateType.NONE;

        drawersCount = 0;
        emptyDrawerSlot = 0;

        clearStorage(nullControllerCoord);

        drawerSlotList.clear();
        drawerPrimaryLookup.clear();

        syncSet.clear();
    }

    /** Fully erase and update the controller with all the drawers that it can access */
    public void fullDrawerUpdate() {

        clearDrawerVariables(false);

        Set<BlockCoord> clearedControllers = new HashSet<BlockCoord>();
        Set<BlockCoord> searchDiscovered = new HashSet<BlockCoord>();
        Queue<BlockCoord> searchQueue = new LinkedList<BlockCoord>();

        searchQueue.add(new BlockCoord(xCoord, yCoord, zCoord));

        while (!searchQueue.isEmpty()) {

            BlockCoord coord = searchQueue.poll();

            int depth = Math.max(
                    Math.max(Math.abs(coord.x() - xCoord), Math.abs(coord.y() - yCoord)),
                    Math.abs(coord.z() - zCoord));
            if (depth > range) continue;

            Block block = worldObj.getBlock(coord.x(), coord.y(), coord.z());

            if (!(block instanceof INetworked)) continue;

            if (block instanceof BlockDrawers && ++drawersCount > maxDrawers) {
                break;
            }

            // If any of the drawers are connected to another controller the controller variables should be cleared (one
            // controller only)
            TileEntity te = worldObj.getTileEntity(coord.x(), coord.y(), coord.z());
            if (te instanceof IDrawerGroup) {

                // Controllers
                if (te instanceof TileEntityController && te != this
                        && clearedControllers.add(new BlockCoord(coord.x(), coord.y(), coord.z()))) {
                    ((TileEntityController) te).clearDrawerVariables(true);
                }

                // Drawers and slaves
                else {

                    BlockCoord connectedControllerCoord = ((IDrawerGroup) te).getControllerCoord();

                    if (connectedControllerCoord != null && clearedControllers.add(
                            new BlockCoord(
                                    connectedControllerCoord.x(),
                                    connectedControllerCoord.y(),
                                    connectedControllerCoord.z()))) {

                        TileEntity otherController = worldObj.getTileEntity(
                                connectedControllerCoord.x(),
                                connectedControllerCoord.y(),
                                connectedControllerCoord.z());

                        if (otherController instanceof TileEntityController && otherController != this) {
                            ((TileEntityController) otherController).clearDrawerVariables(true);
                        }
                    }
                    DrawerSearchUpdateRecordInfo(te);
                }
            }

            BlockCoord[] neighbors = new BlockCoord[] { new BlockCoord(coord.x() + 1, coord.y(), coord.z()),
                    new BlockCoord(coord.x() - 1, coord.y(), coord.z()),
                    new BlockCoord(coord.x(), coord.y(), coord.z() + 1),
                    new BlockCoord(coord.x(), coord.y(), coord.z() - 1),
                    new BlockCoord(coord.x(), coord.y() + 1, coord.z()),
                    new BlockCoord(coord.x(), coord.y() - 1, coord.z()), };

            for (BlockCoord n : neighbors) {
                if (searchDiscovered.add(n)) {
                    searchQueue.add(n);
                }
            }
        }

        drawerUpdatePriority();
    }

    /** Run each block tick */
    public void updateOnTick() {

        if (nextUpdate == controllerUpdateType.PRIORITY) {
            this.drawerUpdatePriority();
        } else if (nextUpdate == controllerUpdateType.FULL) {
            this.fullDrawerUpdate();
        }
        nextUpdate = controllerUpdateType.NONE;
    }

    /** On next updateOnTick() will run drawerUpdatePriority() */
    public void schedulePriorityUpdate() {
        if (nextUpdate.ordinal() < controllerUpdateType.PRIORITY.ordinal()) {
            nextUpdate = controllerUpdateType.PRIORITY;
        }
    }

    /** On next updateOnTick() will run fullDrawerUpdate() */
    public void scheduleFullUpdate() {

        if (nextUpdate.ordinal() < controllerUpdateType.FULL.ordinal()) {
            nextUpdate = controllerUpdateType.FULL;
        }
    }

    @Override
    public void controllerFullUpdate() {
        scheduleFullUpdate();
    }

    /** Should be called when registering a new item with a drawer registered with this controller */
    public void setLookup(@NotNull ItemStack itemPrototype, int drawerSlot, List<ItemStack> oreDictMatches) {

        drawerPrimaryLookup.register(
                itemPrototype.getItem(),
                itemPrototype.getItemDamage(),
                drawerSlotList.get(drawerSlot),
                oreDictMatches);

        schedulePriorityUpdate();
    }

    /** Should be called when setting a stored item in a drawer registered with this controller to null */
    public void removeLookup(@NotNull ItemStack itemPrototype, int drawerSlot, List<ItemStack> oreDictMatches) {

        drawerPrimaryLookup.remove(
                itemPrototype.getItem(),
                itemPrototype.getItemDamage(),
                drawerSlotList.get(drawerSlot),
                oreDictMatches);

        // Ensure emptyDrawerSlot is set to the lowest priority empty priorityIndex (Should usually have lower index as
        // previously had item so had a higher priority)
        int emptyPriorityIndex = drawerSlotList.get(drawerSlot).priorityIndex;
        if (emptyPriorityIndex != -1 && emptyPriorityIndex < emptyDrawerSlot) {
            emptyDrawerSlot = emptyPriorityIndex;
        }

        schedulePriorityUpdate();
    }

    private @Nullable IDrawerGroup getGroupForDrawerSlot(int drawerSlot) {
        if (drawerSlot >= drawerSlotList.size()) return null;

        SlotRecord record = drawerSlotList.get(drawerSlot);
        if (record == null) return null;

        return getGroupForCoord(record.coord);
    }

    private IDrawerGroup getGroupForCoord(BlockCoord coord) {

        if (coord == null) return null;

        IDrawerGroup drawerGroup = storage.get(coord);
        if (drawerGroup == null) return null;

        if (drawerGroup instanceof TileEntity) {
            TileEntity tile = (TileEntity) drawerGroup;
            if (tile.isInvalid() && tile != worldObj.getTileEntity(coord.x(), coord.y(), coord.z())) {
                storage.remove(coord);
                return null;
            }
        }

        return drawerGroup;
    }

    private int getLocalDrawerSlot(int drawerSlot) {
        if (drawerSlot >= drawerSlotList.size()) return 0;

        SlotRecord record = drawerSlotList.get(drawerSlot);
        if (record == null) return 0;

        return record.slot;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {

        super.readFromNBT(tag);

        setDirection(tag.getByte("Dir"));

        if (tag.hasKey("CustomName", Constants.NBT.TAG_STRING)) customName = tag.getString("CustomName");

        if (tag.hasKey("clientAutoSync")) enableClientAutoSync = tag.getBoolean("clientAutoSync");

        if (tag.hasKey("IInventoryExtract")) {
            IInventoryExtract = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("IInventoryExtract"));
        } else {
            IInventoryExtract = null;
        }

        if (worldObj != null && !worldObj.isRemote) scheduleFullUpdate();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {

        super.writeToNBT(tag);

        tag.setByte("Dir", (byte) direction);

        if (hasCustomInventoryName()) tag.setString("CustomName", customName);

        tag.setBoolean("clientAutoSync", enableClientAutoSync);

        if (IInventoryExtract != null) {
            tag.setTag("IInventoryExtract", IInventoryExtract.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);

        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, @NotNull S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
        getWorldObj().func_147479_m(xCoord, yCoord, zCoord); // markBlockForRenderUpdate
    }

    @Override
    public IDrawerInventory getDrawerInventory() {
        return null;
    }

    @Override
    public BlockCoord getControllerCoord() {
        return new BlockCoord(xCoord, yCoord, zCoord);
    }

    @Override
    public void setControllerCoord(int sourceControllerX, int sourceControllerY, int sourceControllerZ) {}

    @Override
    public void clearControllerVariables(int sourceControllerX, int sourceControllerY, int sourceControllerZ,
            boolean nullController) {}

    @Override
    public int getDrawerCount() {
        return drawerSlotList.size();
    }

    @Override
    public IDrawer getDrawer(int slot) {
        IDrawerGroup group = getGroupForDrawerSlot(slot);
        if (group == null) return null;

        return group.getDrawer(getLocalDrawerSlot(slot));
    }

    @Override
    public boolean isDrawerEnabled(int slot) {
        IDrawerGroup group = getGroupForDrawerSlot(slot);
        if (group == null) return false;

        return group.isDrawerEnabled(getLocalDrawerSlot(slot));
    }

    @Override
    public int[] getAccessibleDrawerSlots() {
        return drawerSlots;
    }

    @Override
    public void markDirty() {

        for (IDrawerGroup drawerGroup : storage.values()) {
            if (drawerGroup != null && drawerGroup.getDrawerInventory() != null) drawerGroup.markDirtyIfNeeded();
        }

        super.markDirty();
    }

    @Override
    public boolean markDirtyIfNeeded() {
        boolean synced = false;

        for (IDrawerGroup drawerGroup : storage.values()) {
            if (drawerGroup != null && drawerGroup.getDrawerInventory() != null)
                synced |= drawerGroup.markDirtyIfNeeded();
        }

        if (synced) super.markDirty();

        return synced;
    }

    // Inserting/Extracting using IInventory/ISidedInventory interfaces

    public ItemStack getExtractionItem() {
        return IInventoryExtract;
    }

    public void setExtractionItem(ItemStack stack) {
        if (stack == null) {
            IInventoryExtract = null;
        } else {
            IInventoryExtract = stack.copy();
            IInventoryExtract.stackSize = 1;
        }
        markDirty();
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {

        if (side >= 0 && side <= 5) {
            return virtualSlots;
        }
        return emptySlots;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {

        // Insertion
        if (slot == 0 || IInventoryExtract == null) return null;

        // Extraction
        int amount = 0;
        for (int drawerSlot : enumerateDrawersForExtraction(IInventoryExtract, true)) {
            IDrawer drawer = getDrawer(drawerSlot);
            amount += drawer.getStoredItemCount();
            if (amount > this.getInventoryStackLimit()) {
                amount = this.getInventoryStackLimit();
                break;
            }
        }

        if (amount == 0) {
            return null;
        }

        ItemStack itemsLeft = IInventoryExtract.copy();
        itemsLeft.stackSize = amount;
        return itemsLeft;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {

        if (slot == 1) return false;

        int itemsLeft = stack.stackSize;

        for (int drawerSlot : enumerateDrawersForInsertion(stack, false)) {

            IDrawer drawer = getDrawer(drawerSlot);

            ItemStack itemProto = drawer.getStoredItemPrototype();
            if (itemProto == null) {
                return true;
            }

            itemsLeft = insertItemsIntoDrawer(drawer, itemsLeft, true);

            if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) itemsLeft = 0;
            if (itemsLeft == 0) return true;
        }

        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {

        if (slot == 0 || IInventoryExtract == null
                || stack == null
                || !stack.isItemEqual(IInventoryExtract)
                || !ItemStack.areItemStackTagsEqual(stack, IInventoryExtract))
            return false;

        int itemsLeft = stack.stackSize;

        for (int drawerSlot : enumerateDrawersForExtraction(stack, true)) {

            IDrawer drawer = getDrawer(drawerSlot);
            int itemCount = drawer.getStoredItemCount();

            if (itemsLeft > itemCount) {
                itemsLeft -= itemCount;
            } else {
                itemsLeft = 0;
            }

            if (itemsLeft == 0) return true;
        }
        return false;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {

        // Insertion
        if (slot == 0) {

            if (stack == null) return;

            int itemsLeft = stack.stackSize;

            for (int drawerSlot : enumerateDrawersForInsertion(stack, false)) {

                IDrawer drawer = getDrawer(drawerSlot);

                ItemStack itemProto = drawer.getStoredItemPrototype();
                if (itemProto == null) {
                    drawer = drawer.setStoredItemRedir(stack, 0);
                }

                itemsLeft = insertItemsIntoDrawer(drawer, itemsLeft, false);

                if (drawer instanceof IVoidable && ((IVoidable) drawer).isVoid()) itemsLeft = 0;
                if (itemsLeft == 0) break;
            }
        }

        // Extraction
        else if (slot == 1 && IInventoryExtract != null) {

            if (stack == null || (stack.isItemEqual(IInventoryExtract)
                    && ItemStack.areItemStackTagsEqual(stack, IInventoryExtract))) {

                // Get avaliable items in slot up to 64
                int avaliableItems = 0;
                for (int drawerSlot : enumerateDrawersForExtraction(IInventoryExtract, true)) {
                    IDrawer drawer = getDrawer(drawerSlot);
                    avaliableItems += drawer.getStoredItemCount();
                    if (avaliableItems > this.getInventoryStackLimit()) {
                        avaliableItems = this.getInventoryStackLimit();
                        break;
                    }
                }

                // Remove enough to make equal to stack.stackSize or 0 if stack is null
                int toRemove = avaliableItems - ((stack == null) ? 0 : stack.stackSize);
                for (int drawerSlot : enumerateDrawersForExtraction(IInventoryExtract, true)) {

                    IDrawer drawer = getDrawer(drawerSlot);
                    int itemCount = drawer.getStoredItemCount();

                    if (toRemove > itemCount) {
                        drawer.setStoredItemCount(0);
                        toRemove -= itemCount;
                    } else {
                        drawer.setStoredItemCount(itemCount - toRemove);
                        toRemove = 0;
                    }
                    if (toRemove == 0) break;
                }
            }
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {

        if (slot == 0) return null;

        int amountRemoved = 0;
        for (int drawerSlot : enumerateDrawersForExtraction(IInventoryExtract, true)) {

            IDrawer drawer = getDrawer(drawerSlot);
            int itemCount = drawer.getStoredItemCount();

            if (count > itemCount) {
                drawer.setStoredItemCount(0);
                amountRemoved += itemCount;
                count -= itemCount;
            } else {
                drawer.setStoredItemCount(itemCount - count);
                amountRemoved += (itemCount - count);
                count = 0;
            }

            if (count == 0) return IInventoryExtract;
        }

        ItemStack itemsLeft = IInventoryExtract.copy();
        itemsLeft.stackSize = amountRemoved;
        return itemsLeft;
    }

    // Inserting/Extracting iterators

    private class DrawerStackIteratorInsert implements Iterable<Integer> {

        private final ItemStack stack;
        private final boolean strict;

        public DrawerStackIteratorInsert(ItemStack stack, boolean strict) {
            this.stack = stack;
            this.strict = strict;
        }

        @Override
        public Iterator<Integer> iterator() {

            if (this.stack == null) return new ArrayList<Integer>(0).iterator();

            return new Iterator<Integer>() {

                Iterator<SlotRecord> slotRecordIter = drawerPrimaryLookup
                        .candidateIterator(stack.getItem(), stack.getItemDamage());
                Integer nextSlot = null;

                @Override
                public boolean hasNext() {

                    if (nextSlot == null) advance();

                    return nextSlot != null;
                }

                @Override
                public Integer next() {
                    if (!hasNext()) throw new NoSuchElementException();

                    Integer slot = nextSlot;
                    nextSlot = null;
                    return slot;
                }

                private void advance() {

                    // For drawers that have an item (first check direct matches then oredict matches)
                    if (slotRecordIter != null) {

                        while (slotRecordIter.hasNext()) {

                            SlotRecord candidate = slotRecordIter.next();
                            IDrawerGroup candidateGroup = getGroupForCoord(candidate.coord);
                            if (candidateGroup == null) continue;

                            IDrawer drawer = candidateGroup.getDrawer(candidate.slot);

                            if (strict) {
                                ItemStack proto = drawer.getStoredItemPrototype();
                                if (!proto.isItemEqual(stack)) continue;
                            }

                            boolean voiding = drawer instanceof IVoidable && ((IVoidable) drawer).isVoid();
                            if (!drawer.canItemBeStored(stack) || (drawer.getRemainingCapacity() == 0 && !voiding))
                                continue;

                            nextSlot = candidate.listIndex;
                            return;
                        }
                        slotRecordIter = null;
                    }

                    // For inserting a new item
                    for (; emptyDrawerSlot < drawerSlots.length; emptyDrawerSlot++) {

                        int slot = drawerSlots[emptyDrawerSlot];
                        if (!isDrawerEnabled(slot)) continue;

                        IDrawer drawer = getDrawer(slot);

                        if (!(drawer.isEmpty() && drawer.canItemBeStored(stack))) continue;

                        nextSlot = slot;
                        return;
                    }
                }
            };
        }
    };

    private class DrawerStackIteratorExtract implements Iterable<Integer> {

        private final ItemStack stack;
        private final boolean strict;

        public DrawerStackIteratorExtract(ItemStack stack, boolean strict) {
            this.stack = stack;
            this.strict = strict;
        }

        @Override
        public Iterator<Integer> iterator() {

            if (this.stack == null) return new ArrayList<Integer>(0).iterator();

            return new Iterator<Integer>() {

                final Iterator<SlotRecord> slotRecordIter = drawerPrimaryLookup
                        .candidateIterator(stack.getItem(), stack.getItemDamage());

                Integer nextSlot = null;

                @Override
                public boolean hasNext() {

                    if (nextSlot == null) advance();

                    return nextSlot != null;
                }

                @Override
                public Integer next() {
                    if (!hasNext()) throw new NoSuchElementException();

                    Integer slot = nextSlot;
                    nextSlot = null;
                    return slot;
                }

                private void advance() {

                    if (slotRecordIter != null) {

                        while (slotRecordIter.hasNext()) {

                            SlotRecord candidate = slotRecordIter.next();

                            IDrawerGroup candidateGroup = getGroupForCoord(candidate.coord);
                            if (candidateGroup == null) continue;

                            IDrawer drawer = candidateGroup.getDrawer(candidate.slot);

                            if (strict) {
                                ItemStack proto = drawer.getStoredItemPrototype();
                                if (!proto.isItemEqual(stack)) continue;
                            }

                            if (!drawer.canItemBeExtracted(stack) || drawer.getStoredItemCount() == 0) continue;

                            nextSlot = candidate.listIndex;
                            return;
                        }
                    }
                }
            };
        }
    };

    public Iterable<Integer> enumerateDrawersForInsertion(ItemStack stack, boolean strict) {
        return new DrawerStackIteratorInsert(stack, strict);
    }

    public Iterable<Integer> enumerateDrawersForExtraction(ItemStack stack, boolean strict) {
        return new DrawerStackIteratorExtract(stack, strict);
    }

    // Other stuff

    public void setInventoryName(String name) {
        customName = name;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? customName : "storageDrawers.container.controller";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return customName != null && !customName.isEmpty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}
}
