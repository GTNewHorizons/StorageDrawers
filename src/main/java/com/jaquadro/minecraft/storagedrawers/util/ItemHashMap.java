package com.jaquadro.minecraft.storagedrawers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemHashMap {

    // Takes a key (based on item) and returns list of drawers with this item
    private final Map<Integer, List<SlotRecord>> registry;

    // Takes same type of key as registry and maps to a set registry keys
    private final Map<Integer, Set<Integer>> oreDictRegistryMatch;

    public ItemHashMap() {
        registry = new HashMap<>();
        oreDictRegistryMatch = new HashMap<>();
    }

    public void clear() {
        for (List<SlotRecord> list : registry.values()) {
            list.clear();
        }
        registry.clear();
        for (Set<Integer> list : oreDictRegistryMatch.values()) {
            list.clear();
        }
        oreDictRegistryMatch.clear();
    }

    /**
     * Register an ItemStack -> slotRecord for lookup. oreDictItems allows access to this entry if the requested item is
     * present in this list when oreDict is enabled.
     *
     */
    public void register(Item item, int meta, SlotRecord entry, List<ItemStack> oreDictItems) {

        // Convert ItemStack into an integer key to use for the map
        int key = (Item.getIdFromItem(item) << 16) | meta;

        List<SlotRecord> registryList = registry.get(key);

        if (registryList == null) {
            registryList = new ArrayList<>();
            registry.put(key, registryList);
        }
        registryList.add(entry);

        if (oreDictItems != null) {

            for (ItemStack oreItem : oreDictItems) {

                // Convert ItemStack into an integer key for lookup
                int oreDictKey = (Item.getIdFromItem(oreItem.getItem()) << 16) | oreItem.getItemDamage();

                // Prevent key in registry from also appearing in the oreDictRegistryMatch
                if (oreDictKey == key) continue;

                // Update oreDictList
                Set<Integer> oreDictList = oreDictRegistryMatch.get(oreDictKey);
                if (oreDictList == null) {
                    oreDictList = new HashSet<>();
                    oreDictRegistryMatch.put(oreDictKey, oreDictList);
                }
                oreDictList.add(key);
            }
        }
    }

    /**
     * Remove ItemStack -> SlotRecord entry and associated oreDict matches if they are not being used. oreDictItems
     * should always be the same as the one used for registering the item.
     **/
    public void remove(Item item, int meta, SlotRecord entry, List<ItemStack> oreDictItems) {

        int key = (Item.getIdFromItem(item) << 16) | meta;

        List<SlotRecord> registryList = registry.get(key);

        if (registryList == null || !registryList.remove(entry) || !registryList.isEmpty()) return;

        registry.remove(key);

        if (oreDictItems != null) {

            for (ItemStack stack : oreDictItems) {

                int oreDictKey = (Item.getIdFromItem(stack.getItem()) << 16) | stack.getItemDamage();

                Set<Integer> oreDictList = oreDictRegistryMatch.get(oreDictKey);

                if (oreDictList == null) continue;

                oreDictList.remove(key);

                if (oreDictList.isEmpty()) oreDictRegistryMatch.remove(oreDictKey);
            }
        }
    }

    /**
     * Iterates though SlotRecords for a given ItemStack starting with exact registry matches before fetching records
     * with oreDict matches if present. next() returns nextRecord variable gets updated in advance().
     **/
    public Iterator<SlotRecord> candidateIterator(Item item, int meta) {
        return new ItemHashMapIterator(item, meta);
    }

    private class ItemHashMapIterator implements Iterator<SlotRecord> {

        private Iterator<SlotRecord> primaryMatches;
        private Iterator<Integer> oreDictIterator;

        private Iterator<SlotRecord> oreDictMatches = null;
        private SlotRecord nextRecord = null;

        public ItemHashMapIterator(Item item, int meta) {

            Integer itemKey = (Item.getIdFromItem(item) << 16) | meta;

            List<SlotRecord> primary = registry.get(itemKey);
            primaryMatches = (primary != null) ? primary.iterator() : null;

            Set<Integer> oreKeys = oreDictRegistryMatch.get(itemKey);
            oreDictIterator = (oreKeys != null) ? oreKeys.iterator() : null;
        }

        private void advance() {

            // Direct matches first
            if (primaryMatches != null && primaryMatches.hasNext()) {

                nextRecord = primaryMatches.next();
                return;
            }

            // Find next key in the oreDictIterator of Set<Integer> if needed
            while ((oreDictMatches == null || !oreDictMatches.hasNext()) && oreDictIterator != null
                    && oreDictIterator.hasNext()) {

                Integer key = oreDictIterator.next();

                List<SlotRecord> matches = registry.get(key);

                if (matches != null && !matches.isEmpty()) {

                    oreDictMatches = matches.iterator();
                }
            }

            // Oredict matches second
            if (oreDictMatches != null && oreDictMatches.hasNext()) {

                nextRecord = oreDictMatches.next();
            }
        }

        @Override
        public boolean hasNext() {

            if (nextRecord == null) advance();

            return nextRecord != null;
        }

        @Override
        public SlotRecord next() {

            if (!hasNext()) throw new NoSuchElementException();

            SlotRecord slot = nextRecord;
            nextRecord = null;

            return slot;
        }
    }
}
