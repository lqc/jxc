package org.lqc.util;

import java.util.Set;

/**
 * 
 * Definition of partially ordered set. Every element
 * of type K has an associated value of type V.  
 *
 * @param <K> - key type that implements Parital Order 
 * @param <V> - type of associated values
 */
public interface POSet<K extends PartiallyOrdered<? super K>, V> 
{
	
	public void insert(K key, V value)
		throws NonUniqueElementException;
		
	public boolean contains(K key);	
	
	public V find(K key)
		throws ElementNotFoundException;
	
	public V remove(K key);
	
	public Set<V> values();
}
