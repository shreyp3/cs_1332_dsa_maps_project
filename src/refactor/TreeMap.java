package refactor;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class TreeMap<K extends Comparable<? super K>, V> implements StaticTreeMap<K, V> {

    private TreeMapEntry root;
    private int size;
    private V oldValue;

    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value is null");
        }
        oldValue = null;
        root = putH(root, key, value);

        return oldValue;
    }

    /**
     * Recursive helper method for add method.
     *
     * @param entry the root of the current subtree
     * @param key   the key to add to the tree
     * @param value the value to add to the tree accompanying the key
     * @return the new root of the current subtree
     */
    private TreeMapEntry putH(TreeMapEntry entry, K key, V value) {
        if (entry == null) {
            size++;
            oldValue = null;
            return new TreeMapEntry(key, value);
        }
        int comp = key.compareTo(entry.key);
        if (comp > 0) {
            entry.right = putH(entry.right, key, value);
        } else if (comp < 0) {
            entry.left = putH(entry.left, key, value);
        } else {
            oldValue = entry.value;
            entry.value = value;
            return entry;
        }
        update(entry);
        return balance(entry);
    }

    @Override
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }

        TreeMapEntry dummy = new TreeMapEntry(null, null);
        root = removeH(root, dummy, key);
        return dummy.value;
    }

    /**
     * Recursive helper method for remove method.
     *
     * @param entry the root of the current subtree
     * @param dummy extra TreeMapEntry to store removed data
     * @param key   the key to remove
     * @return the new root of the subtree
     */
    private TreeMapEntry removeH(TreeMapEntry entry, TreeMapEntry dummy, K key) {
        if (entry == null) {
            throw new NoSuchElementException("Key is not in the map");
        }
        int comp = key.compareTo(entry.key);
        if (comp > 0) {
            entry.right = removeH(entry.right, dummy, key);
        } else if (comp < 0) {
            entry.left = removeH(entry.left, dummy, key);
        } else {
            dummy.key = entry.key;
            dummy.value = entry.value;
            --size;
            if (entry.right == null) {
                return entry.left;
            } else if (entry.left == null) {
                return entry.right;
            } else {
                TreeMapEntry predecessor = new TreeMapEntry(null, null);
                entry.left = removePredecessor(entry.left, predecessor);
                entry.key = predecessor.key;
                entry.value = predecessor.value;
            }
        }
        update(entry);
        return balance(entry);
    }

    /**
     * Recursive helper method for removing the predecessor.
     *
     * @param entry the root of the current subtree
     * @param dummy extra node to store the predecessor
     * @return the new root of the subtree
     */
    private TreeMapEntry removePredecessor(TreeMapEntry entry, TreeMapEntry dummy) {
        if (entry.right == null) {
            dummy.key = entry.key;
            dummy.value = entry.value;
            return entry.left;
        } else {
            entry.right = removePredecessor(entry.right, dummy);
            update(entry);
            return balance(entry);
        }
    }

    @Override
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        TreeMapEntry entry = search(root, key);
        if (entry == null) {
            throw new NoSuchElementException("Key is not in the map");
        }
        return entry.value;
    }

    @Override
    public List<V> getRange(K lower, K upper) {
        if (lower == null || upper == null) {
            throw new IllegalArgumentException("Lower and/or upper bound(s) is null");
        }
        List<V> values = new LinkedList<>();
        int boundComparison = lower.compareTo(upper);
        if (boundComparison < 0) {
            inOrder(root, values, lower, upper);
        } else if (boundComparison == 0) {
            try {
                values.addLast(get(lower));
            } catch (NoSuchElementException _) { }
        }
        return values;
    }

    /**
     * A recursive method that will traverse through the TreeMap in order of increasing key.
     *
     * @param entry  passed in to be recursed through
     * @param values the list that holds the values from lower to upper key bounds
     * @param lower  the lower key bound (inclusive)
     * @param upper  the upper key bound (inclusive)
     */
    private void inOrder(TreeMapEntry entry, List<V> values, K lower, K upper) {
        if (entry != null) {
            if (lower != null && entry.key.compareTo(lower) < 0) {
                inOrder(entry.right, values, lower, upper);
                return;
            }
            if (upper != null && entry.key.compareTo(upper) > 0) {
                inOrder(entry.left, values, lower, upper);
                return;
            }
            inOrder(entry.left, values, lower, null);
            values.addLast(entry.value);
            inOrder(entry.right, values, null, upper);
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        return search(root, key) != null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>(size);
        inOrder(root, keys);
        return keys;
    }

    /**
     * A recursive method that will traverse through the TreeMap in order of increasing key.
     *
     * @param entry passed in to be recursed through
     * @param keys  the list that holds the keys in increasing order.
     */
    private void inOrder(TreeMapEntry entry, Set<K> keys) {
        if (entry != null) {
            inOrder(entry.left, keys);
            keys.add(entry.key);
            inOrder(entry.right, keys);
        }
    }

    @Override
    public List<V> values() {
        List<V> values = new LinkedList<>();
        inOrder(root, values);
        return values;
    }

    /**
     * A recursive method that will traverse through the TreeMap in order of increasing key.
     *
     * @param entry  passed in to be recursed through
     * @param values the list that holds the values in increasing order of its associated keys.
     */
    private void inOrder(TreeMapEntry entry, List<V> values) {
        if (entry != null) {
            inOrder(entry.left, values);
            values.add(entry.value);
            inOrder(entry.right, values);
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int height() {
        if (root == null) {
            return -1;
        }
        return root.height;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<V> iterator() {
        return new TreeMapIterator(root);
    }

    /**
     * A recursive method that will search for the key-value pair associated with the key being searched for.
     *
     * @param entry passed in to be recursed through
     * @param key   key that is being searched for
     * @return the key-value pair of the searched key
     */
    private TreeMapEntry search(TreeMapEntry entry, K key) {
        if (entry == null) {
            return null;
        }
        int comp = key.compareTo(entry.key);
        if (comp > 0) {
            return search(entry.right, key);
        } else if (comp < 0) {
            return search(entry.left, key);
        } else {
            return entry;
        }
    }

    /**
     * Updates an entry's height and balance factor, assuming that the entry's
     * children were both already properly updated.
     *
     * @param entry the entry to update (assume non-null)
     */
    private void update(TreeMapEntry entry) {
        int leftHeight;
        int rightHeight;

        if (entry.left == null) {
            leftHeight = -1;
        } else {
            leftHeight = entry.left.height;
        }
        if (entry.right == null) {
            rightHeight = -1;
        } else {
            rightHeight = entry.right.height;
        }

        entry.height = 1 + Math.max(leftHeight, rightHeight);
        entry.balanceFactor = leftHeight - rightHeight;
    }

    /**
     * @param entry the root of the current subtree (assumed to be unbalanced)
     * @return the new root of the balanced subtree
     * @implNote <b>Fill in this method with your code.</b>
     */
    private TreeMapEntry leftRotate(TreeMapEntry entry) {
        TreeMapEntry rightChild = entry.right;
        entry.right = rightChild.left;
        rightChild.left = entry;
        update(entry);
        update(rightChild);
        return rightChild;
    }

    /**
     * @param entry the root of the current subtree (assumed to be unbalanced)
     * @return the new root of the balanced subtree
     * @implNote <b>Fill in this method with your code.</b>
     */
    private TreeMapEntry rightRotate(TreeMapEntry entry) {
        TreeMapEntry leftChild = entry.left;
        entry.left = leftChild.right;
        leftChild.right = entry;
        update(entry);
        update(leftChild);
        return leftChild;
    }

    /**
     * Checks if the passed in node needs to be balanced and calls
     * the relevant rotation operations, if necessary.
     *
     * @param entry the root of the current subtree
     * @return the new root of the balanced subtree
     * @implNote <b>Fill in this method with your code.</b>
     */
    private TreeMapEntry balance(TreeMapEntry entry) {
        if (entry.balanceFactor > 1) {
            if (entry.left.balanceFactor < 0) {
                entry.left = leftRotate(entry.left);
            }
            entry = rightRotate(entry);
        } else if (entry.balanceFactor < -1) {
            if (entry.right.balanceFactor > 0) {
                entry.right = rightRotate(entry.right);
            }
            entry = leftRotate(entry);
        }
        return entry;
    }

    private class TreeMapEntry {
        private K key;
        private V value;
        private TreeMapEntry left;
        private TreeMapEntry right;
        private int height;
        private int balanceFactor;

        /**
         * A constructor for the TreeMapEntry private inner class
         *
         * @param key   the key of the key-value pair passed in
         * @param value the value of the key-value pair passed in
         */
        TreeMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private class TreeMapIterator implements Iterator<V> {
        private final TreeMapEntry root;
        private TreeMapEntry entry;
        private final ArrayDeque<TreeMapEntry> deque = new ArrayDeque<>(size);

        /**
         * Instantiates a TreeMapIterator object
         * @param root the root of the TreeMap passed in
         */
        public TreeMapIterator(TreeMapEntry root) {
            this.root = root;
            this.entry = root;
            while (entry != null) {
                deque.addFirst(entry);
                entry = entry.left;
            }
            entry = root;
        }

        @Override
        public boolean hasNext() {
            return !(deque.isEmpty());
        }

        @Override
        public V next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more entries in the TreeMap");
            }
            TreeMapEntry returnEntry = deque.removeFirst();
            if (returnEntry.right != null) {
                entry = returnEntry.right;
                while (entry != null) {
                    deque.addFirst(entry);
                    entry = entry.left;
                }
            }
            return returnEntry.value;
        }
    }
}