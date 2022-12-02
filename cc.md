# 编程题-题目

## 标题备注

编程题

## 题目内容

请完善TestMap类，要求只实现get、put、remove、size四个方法

- 要求不能使用第三方包，不能使用JDK中Map实现类
- 请对完成的方法进行测试，在main方法中调用验证

## 代码片段

```Java
import java.util.Collection;
import java.util.Map;
import java.util.Set;
​
public class TestMap<K, V> implements Map<K, V> {
​
    @Override
    public int size() {
        return 0;
    }
​
    @Override
    public V get(Object key) {
        return null;
    }
​
    @Override
    public V put(K key, V value) {
        return null;
    }
​
    @Override
    public V remove(Object key) {
        return null;
    }
​
    @Override
    public boolean isEmpty() {
        return false;
    }
​
    @Override
    public boolean containsKey(Object key) {
        return false;
    }
​
    @Override
    public boolean containsValue(Object value) {
        return false;
    }
​
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
​
    }
​
    @Override
    public void clear() {
​
    }
​
    @Override
    public Set<K> keySet() {
        return null;
    }
​
    @Override
    public Collection<V> values() {
        return null;
    }
​
    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
```