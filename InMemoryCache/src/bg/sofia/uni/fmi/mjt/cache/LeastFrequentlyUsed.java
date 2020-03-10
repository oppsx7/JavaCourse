package bg.sofia.uni.fmi.mjt.cache;
import java.lang.Integer;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LeastFrequentlyUsed<K,V> implements Cache<K,V> {

    private Map<Integer, Integer> values;
    private Map<Integer, Integer> countKeyTimes;
    private Map<Integer, LinkedHashSet<Integer>> lists;
    private int minimum = -1;
    private int capacity;

    public LeastFrequentlyUsed(int capacity) {
        this.capacity = capacity;
        values = new HashMap<>();
        countKeyTimes = new HashMap<>();
        lists = new HashMap<>();
    }

    @Override
    public V get(K key) {
        if (key!=null || !values.containsKey(key)) {
            return ;
        }
        int count = countKeyTimes.get(key);
        countKeyTimes.put(key, count + 1);
        lists.get(count).remove(key);
    }

    @Override
    public void set(Object key, Object value) {

    }

    @Override
    public boolean remove(Object key) {
        return false;
    }

    @Override
    public long size() {
        return capacity;
    }

    @Override
    public void clear() {

    }

    @Override
    public double getHitRate() {
        return 0;
    }

    @Override
    public int getUsesCount(Object key) {
        return 0;
    }

}
