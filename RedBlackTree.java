package data_structures;
/* Name: Asislo Alfiler IV
 * cssc0901
 * CS310
 * Professor Riggins
 * 05/01/17
 */
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RedBlackTree<K extends Comparable<K>,V> implements DictionaryADT<K,V> {
	private TreeMap<K,V> rbTree;
	
	public RedBlackTree() {rbTree = new TreeMap<K,V>();}
	
	public boolean contains(K key) {return rbTree.containsKey(key);}
	
	public boolean add(K key, V value) {
		if(contains(key))
			return false;
		rbTree.put(key, value);
		return true;
	}
	
	public boolean delete(K key) {
		if(!contains(key))
			return false;
		if(isEmpty())
			return false;
		rbTree.remove(key);
		return true;
	}
	
	public V getValue(K key) {return rbTree.get(key);}
	//iterate through the set of values until found
	public K getKey(V value) {
		Iterator<Map.Entry<K, V>> iter = rbTree.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<K,V> tmp = iter.next();
			if(((Comparable<V>) tmp.getValue()).compareTo(value) == 0)
				return tmp.getKey();
		}
		return null;
	}

	public int size() {return rbTree.size();}

	public boolean isFull() {return false;}

	public boolean isEmpty() {return rbTree.isEmpty();}

	public void clear() {rbTree.clear();}
	
	public Iterator<K> keys() {return rbTree.navigableKeySet().iterator();}

	public Iterator<V> values() {return rbTree.values().iterator();}
}
