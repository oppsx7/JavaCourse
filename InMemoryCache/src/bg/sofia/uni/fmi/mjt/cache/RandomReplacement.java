package bg.sofia.uni.fmi.mjt.cache;

public class RandomReplacement<K,V> implements Cache<K,V> {


    @Override
    public Object get(Object key) {
        return null;
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
        return 0;
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
