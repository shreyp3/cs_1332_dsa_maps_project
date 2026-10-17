package implement;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Your implementation of a Linear Probing HashMap. Must implement {@link Iterable}.
 */
public class HashMap<K, V> implements Iterable<K> {


    /**
     * The initial capacity of the LinearProbingHashMap when created with the
     * default constructor.
     *
     * DO NOT MODIFY THIS VARIABLE!
     */
    public static final int INITIAL_CAPACITY = 13;

    /**
     * The max load factor of the LinearProbingHashMap
     *
     * DO NOT MODIFY THIS VARIABLE!
     */
    public static final double MAX_LOAD_FACTOR = 0.67;

    private MapEntry<K, V>[] table;
    private int size;

    /**
     * Constructs a new Linear Probing HashMap.
     *
     * The backing array should have an initial capacity of {@code INITIAL_CAPACITY}.
     *
     * Use constructor chaining.
     */
    public HashMap() {
        table = new MapEntry[INITIAL_CAPACITY];
        size = 0;
    }

    /**
     * Constructs a new LinearProbingHashMap.
     *
     * The backing array should have an initial capacity of initialCapacity.
     *
     * You may assume initialCapacity will always be positive.
     *
     * @param initialCapacity the initial capacity of the backing array
     */
    public HashMap(int initialCapacity) {
        table = (MapEntry<K, V>[]) new MapEntry[initialCapacity];
        size = 0;
    }

    /**
     * Adds the given key-value pair to the map. If an entry in the map
     * already has this key, replace the entry's value with the new one
     * passed in.
     *
     * In the case of a collision, use linear probing as your resolution
     * strategy. See the PDF for more instructions on edge cases and resizing.
     *
     * If a value was updated, return the old value; otherwise, return null.
     *
     * @param key   the key to add
     * @param value the value to add
     * @return null if the key was not already in the map. If it was in the
     * map, return the old value associated with it
     * @throws IllegalArgumentException if key or value is null
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        //might need to ensure num is a double
        if ((double) (size + 1) / table.length > MAX_LOAD_FACTOR) {
            resizeBackingTable(2 * table.length + 1);
        }
        int i = Math.abs(key.hashCode() % table.length);
        if (table[i] == null) {
            table[i] = new MapEntry<>(key, value, false);
            size++;
            return null;
        }
        boolean del = false;
        int firstDel = 0;
        for (int j = 0; j < table.length; j++) {
            if (table[i] != null) {
                if (table[i].getKey().equals(key) && !table[i].isRemoved()) {
                    V oldValue = table[i].getValue();
                    table[i].setValue(value);
                    return oldValue;
                }
                if (table[i].isRemoved()) {
                    if (!del) {
                        firstDel = i;
                        del = true;
                    }
                }
            } else {
                break;
            }
            i++;
            if (i >= table.length) {
                i = 0;
            }
        }
        if (!del) {
            table[i] = new MapEntry<>(key, value);
        } else {
            table[firstDel] = new MapEntry<>(key, value);
        }
        size++;
        return null;
    }

    /**
     * Removes the entry with a matching key from map by marking the entry as
     * removed.
     *
     * @param key the key to remove
     * @return the value previously associated with the key
     * @throws IllegalArgumentException if key is null
     * @throws NoSuchElementException   if the key is not in the map
     */
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        int i = Math.abs(key.hashCode() % table.length);
        for (int j = 0; j < table.length; j++) {
            if (table[i] != null) {
                if (table[i].getKey().equals(key) && !table[i].isRemoved()) {
                    table[i].setRemoved(true);
                    size--;
                    return table[i].getValue();
                }
                i++;
                if (i >= table.length) {
                    i = 0;
                }
            } else {
                throw new NoSuchElementException("Key is not in the map");
            }
        }
        throw new NoSuchElementException("Key is not in the map");
    }

    /**
     * Gets the value associated with the given key.
     *
     * @param key the key to search for in the map
     * @return the value associated with the given key
     * @throws IllegalArgumentException if key is null
     * @throws NoSuchElementException   if the key is not in the map
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        int i = Math.abs(key.hashCode() % table.length);
        for (int j = 0; j < table.length; j++) {
            if (table[i] != null) {
                if (table[i].getKey().equals(key) && !table[i].isRemoved()) {
                    return table[i].getValue();
                }
                i++;
                if (i >= table.length) {
                    i = 0;
                }
            } else {
                throw new NoSuchElementException("Key is not in the map");
            }
        }
        throw new NoSuchElementException("Key is not in the map");
    }

    /**
     * Gets the value associated with the given key, or returns a provided default value.
     *
     * @param key the key to search for in the map
     * @param defaultValue the value to return if key not found
     * @return the value associated with the given key if found,
     * {@code defaultValue} otherwise
     * @throws IllegalArgumentException if key is null
     */
    public V getOrDefault(K key, V defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        int i = Math.abs(key.hashCode() % table.length);
        for (int j = 0; j < table.length; j++) {
            if (table[i] != null) {
                if (table[i].getKey().equals(key) && !table[i].isRemoved()) {
                    return table[i].getValue();
                }
                i++;
                if (i >= table.length) {
                    i = 0;
                }
            } else {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * Returns whether or not the key is in the map.
     *
     * @param key the key to search for in the map
     * @return true if the key is contained within the map, false
     * otherwise
     * @throws IllegalArgumentException if key is null
     */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        int i = Math.abs(key.hashCode() % table.length);
        for (int j = 0; j < table.length; j++) {
            if (table[i] != null) {
                if (table[i].getKey().equals(key) && !table[i].isRemoved()) {
                    return true;
                }
                i++;
                if (i >= table.length) {
                    i = 0;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * Returns a Set view of the keys contained in this map.
     *
     * Use java.util.HashSet.
     *
     * @return the set of keys in this map
     */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>(size);
        for (MapEntry<K, V> entry : table) {
            if (entry != null) {
                if (!entry.isRemoved()) {
                    keys.add(entry.getKey());
                }
            }
        }
        return keys;
    }

    /**
     * Returns a List view of the values contained in this map.
     *
     * Use java.util.ArrayList or java.util.LinkedList.
     *
     * You should iterate over the table in order of increasing index and add
     * entries to the List in the order in which they are traversed.
     *
     * @return list of values in this map
     */
    public List<V> values() {
        List<V> values = new LinkedList<>();
        for (MapEntry<K, V> entry : table) {
            if (entry != null) {
                if (!entry.isRemoved()) {
                    values.add(entry.getValue());
                }
            }
        }
        return values;
    }

    /**
     * Resize the backing table to length.
     *
     * Disregard the load factor for this method. So, if the passed in length is
     * smaller than the current capacity, and this new length causes the table's
     * load factor to exceed MAX_LOAD_FACTOR, you should still resize the table
     * to the specified length.
     *
     * See the PDF for more details.
     *
     * @param length new length of the backing table
     * @throws IllegalArgumentException if length is less than the
     *                                            number of items in the hash
     *                                            map
     */
    public void resizeBackingTable(int length) {
        if (length < size) {
            throw new IllegalArgumentException("New length is less than the number of items in the hash map");
        }
        MapEntry<K, V>[] temp = new MapEntry[length];
        for (MapEntry<K, V> entry : table) {
            if (entry != null) {
                if (!entry.isRemoved()) {
                    int j = Math.abs(entry.getKey().hashCode() % temp.length);
                    while (temp[j] != null) {
                        j++;
                        if (j >= temp.length) {
                            j = 0;
                        }
                    }
                    temp[j] = entry;
                }
            }
        }
        table = temp;
    }

    /**
     * Clears the map.
     *
     * Resets the table to a new array of the INITIAL_CAPACITY and resets the
     * size.
     *
     * Must be O(1).
     */
    public void clear() {
        table = new MapEntry[INITIAL_CAPACITY];
        size = 0;
    }

    /**
     * Returns the table of the map.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the table of the map
     */
    public MapEntry<K, V>[] getTable() {
        // DO NOT MODIFY THIS METHOD!
        return table;
    }

    /**
     * Returns the size of the map.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the size of the map
     */
    public int size() {
        // DO NOT MODIFY THIS METHOD!
        return size;
    }

    /**
     * Returns an iterator of all keys in the HashMap. The order should be the
     * order they appear in the array from left to right.
     * <p>
     * Must be done in O(1) auxiliary space.
     *
     * @return an iterator of all keys in the HashMap
     * @implNote you may create a private inner class to implement {@link Iterator}
     */
    public Iterator<K> iterator() {
        return new HashMapIterator<>(table);
    }

    private static class HashMapIterator<K> implements Iterator<K> {
        private final MapEntry[] table;
        private int i;
        private int j;
        /**
         * Instantiates a HashMapIterator Object.
         *
         * @param table the HashMap passed in as an array of MapEntries
         */
        public HashMapIterator(MapEntry[] table) {
            this.table = table;
            i = 0;
        }

        @Override
        public boolean hasNext() {
            j = i;
            while (j < table.length) {
                if (table[j] != null) {
                    if (!table[j].isRemoved()) {
                        return true;
                    }
                }
                j++;
            }
            return false;
        }
        @Override
        public K next() {
            if (hasNext()) {
                i = j;
                K returnKey = (K) table[i].getKey();
                i++;
                return returnKey;
            }
            throw new NoSuchElementException("No more Keys in the HashMap");
        }
    }
}