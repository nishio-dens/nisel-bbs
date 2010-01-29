package bbs.util;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class MultiSortedMap<T, V> {

	private Map<T,SortedSet<V>> mmap = new ConcurrentHashMap<T,SortedSet<V>>();
	
	/**
	 *
	 * @param key
	 * @param value
	 */
	public void add(T key, V value){
		SortedSet<V> mapValue = null;

		if(mmap.containsKey(key)) {
			mapValue = mmap.get(key);
		} else {
			mapValue = Collections.synchronizedSortedSet( new TreeSet<V>() );
		}

		mapValue.add(value);
		mmap.put(key, mapValue);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	public SortedSet<V> get(T key){
		if( mmap == null ) {
			return null;
		}
		SortedSet<V> s = new TreeSet<V>();
		s = mmap.get(key);

		return s;
	}

	/**
	 *
	 * @return
	 */
	public Set<T> getKeys() {
		if( mmap == null ) {
			return null;
		}
		return mmap.keySet();
	}
	
	/**
	 * 
	 * @return
	 */
	public Collection<SortedSet<V>> getValues() {
		if( mmap == null ) {
			return null;
		}
		return mmap.values();
	}
	
	/**
	 * 登録されているキーの数を返す
	 * @return
	 */
	public int size() {
		return mmap.size();
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean contains(T key, V value) {
		Set<V> t = get( key );
		if( t != null ) {
			return t.contains(value);
		}
		return false;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(T key) {
		return mmap.containsKey(key);
	}

	/**
	 * 
	 * @param key
	 */
	public SortedSet<V> remove(T key) {
		return mmap.remove(key);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean removeValue(T key, V value) {
		Set<V> t = get( key );
		if( t != null ) {
			return t.remove(value);
		}
		return false;
	}
}

