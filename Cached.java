import java.util.function.Function;
import java.util.HashMap;
import java.util.Map;

/** Compute a value based on a function. Repeated calls to `get` with the same key will just return the same value. */
class Cached<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final Function<K, V> fn;

    Cached(Function<K, V> fn) {
        this.fn = fn;
    }

    V get(K k) {
        if (cache.containsKey(k))
            return cache.get(k);
        else {
            V v = fn.apply(k);
            cache.put(k, v);
            return v;
        }

    }
}
