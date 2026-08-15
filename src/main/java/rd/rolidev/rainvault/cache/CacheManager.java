package rd.rolidev.rainvault.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CacheManager<K, V> {
    private final Map<K, CacheEntry<V>> cache;
    private final long expirationTime;

    public CacheManager(long expirationTime, TimeUnit timeUnit) {
        this.cache = new ConcurrentHashMap<>();
        this.expirationTime = timeUnit.toMillis(expirationTime);
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + expirationTime));
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }

        return entry.getValue();
    }

    public void remove(K key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    public boolean contains(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return false;
        }

        if (entry.isExpired()) {
            cache.remove(key);
            return false;
        }

        return true;
    }

    public int size() {
        cleanExpired();
        return cache.size();
    }

    public void cleanExpired() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private static class CacheEntry<V> {
        private final V value;
        private final long expirationTime;

        public CacheEntry(V value, long expirationTime) {
            this.value = value;
            this.expirationTime = expirationTime;
        }

        public V getValue() {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }
}
