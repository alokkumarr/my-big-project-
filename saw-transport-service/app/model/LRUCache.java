package model;

import java.util.LinkedHashMap;

public class LRUCache <K, V> extends LinkedHashMap<K, V> {

    private static final long serialVersionUID = 1L;

    private final int capacity;

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        // Remove the eldest element whenever size of cache exceeds the capacity
        return (size() > this.capacity);
    }

    public LRUCache(int capacity) {
        // Call constructor of LinkedHashMap with accessOrder set to true to
        // achieve LRU Cache behavior
        super(capacity + 1, 1.0f, true);
        this.capacity = capacity;
    }

    /**
     * Returns the value corresponding to input key from Cache Map.
     *
     * @param key
     *            Key for the element whose value needs to be returned
     * @return Value of the element with input key or null if no such element
     *         exists
     */
    public V find(K key) {
        return super.get(key);
    }

    /**
     * Set the element with input key and value in the cache. If the element
     * already exits, it updates its value.
     *
     * @param key
     *            Key of the element
     * @param value
     *            Value of the element
     */
    public void set(K key, V value) {
        super.put(key, value);
    }
}
