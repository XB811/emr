package top.xblog1.emr.framework.starter.cache.core;

/**
 * 缓存加载器
 */
@FunctionalInterface
public interface CacheLoader<T> {

    /**
     * 加载缓存
     */
    T load();
}
