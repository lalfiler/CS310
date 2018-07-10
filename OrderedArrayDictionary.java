/* Name: Asislo Alfiler IV
 * cssc0901
 */
package data_structures;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class OrderedArrayDictionary<K extends Comparable<K>, V> implements DictionaryADT<K,V>{
	private DictionaryNode<K,V>[] array;
	private int currentSize, maxSize;
	private long modCount;
	
	public OrderedArrayDictionary(int size){
		currentSize = 0;
		maxSize = size;
		array = new DictionaryNode[maxSize];
	}

	//wrapper class
	private class DictionaryNode<K,V> implements Comparable<DictionaryNode<K,V>>{
		K key;
		V value;
		public DictionaryNode (K key, V value){
			this.key = key;
			this.value = value;
		}
		public int compareTo(DictionaryNode<K,V> node){
			return((Comparable<K>)key).compareTo((K)node.key);
		}
	}
	
	public boolean contains(K key) {	
		return binSearch(key,0,currentSize-1) != -1;
	}
	
	public boolean add(K key, V value) {
		if(contains(key)){
		return false;
		}
		if(isFull()){
			return false;
		}
		int find = findInsertionIndex(key, 0 , currentSize-1);
		//shift right
		for(int i = currentSize-1; i >= find; i--){
			array[i+1] = array[i];
		}
		array[find] = new DictionaryNode<K,V>(key, value);	
		currentSize++;
		modCount++;
		return true;
	}

	private int findInsertionIndex(K key, int low, int high){
		if(high < low) return low;
		int mid = (low + high) >> 1;
		int comp = key.compareTo(array[mid].key);
		if(comp >= 0)
			return findInsertionIndex(key,mid+1,high);
		return findInsertionIndex(key,low,mid-1);
	}
	//find if the value is there or not
	private int binSearch(K key, int first, int last){
	if(last < first)
		return -1;
	int mid = (first + last)/2;
	if(((Comparable<K>)key).compareTo(array[mid].key) == 0)
		return mid;
	if(((Comparable<K>)key).compareTo(array[mid].key) < 0)
		return binSearch(key,first,mid-1);
	return binSearch(key,mid+1,last);
	}
	
	public boolean delete(K key) {
		if(isEmpty())
			return false;
		if(!contains(key))
			return false;
		int find = binSearch(key,0,currentSize-1);
		for(int i = find; i < currentSize-1; i++)
			array[i] = array[i+1];
		modCount++;
		currentSize--;
		return true;
	}

	public V getValue(K key) {
		if(isEmpty())
			return null;
		int find = binSearch(key,0,currentSize-1);
		if(find == -1)
			return null;
		return array[find].value;

	}
	
	public K getKey(V value) {
		if(isEmpty())
			return null;
		for(int i = 0; i < currentSize; i++)
				if(((Comparable<V>)value).compareTo((V) array[i].value) == 0)
					return array[i].key;
		return null;
	}
	
	public int size() {
		return currentSize;
	}
	
	public boolean isFull() {
		return currentSize == maxSize;
	}
	
	public boolean isEmpty() {
		return currentSize == 0;
	}
	
	public void clear() {
		modCount = 0;
		currentSize = 0;
	}
	
	public Iterator<K> keys() {
		return new KeyIteratorHelper<K>();
	}

	public Iterator<V> values() {
		return new ValueIteratorHelper<V>();
	}
	
	abstract class IteratorHelper<E> implements Iterator<E> {
		protected int idx;
		protected long modCheck;
		
		public IteratorHelper(){
		idx = 0;
		modCheck = modCount;
		}
		public boolean hasNext(){
			if(modCheck != modCount)
				throw new ConcurrentModificationException();
			return idx < currentSize;
		}	
		public abstract E next();
		public void remove(){
			throw new UnsupportedOperationException();
		}
	} // End IteratorHelper
	
	class KeyIteratorHelper<K> extends IteratorHelper{
		public KeyIteratorHelper(){
			super();
		}
		public K next(){
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			return (K) array[idx++].key;
		}
	}
	class ValueIteratorHelper<V> extends IteratorHelper{
		public ValueIteratorHelper(){
			super();
		}
		public V next(){
			if(!hasNext()){
				throw new NoSuchElementException();
			}
			return (V) array[idx++].value;
		}
	}
}