package data_structures;

/* Name: Asislo Alfiler IV
 * cssc0901
 * CS310
 * Professor Riggins
 * 05/01/17
 */
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;

public class BinarySearchTree<K extends Comparable<K>,V> implements DictionaryADT<K,V>{
	private int currentSize, tableSize, maxSize;
	long modCount;
	private DictionaryNode<K,V> root;
	K keyFound;
	
	public BinarySearchTree(int n){
		currentSize = 0;
		root = null;
		modCount = 0;
		K keyFound = null;
	}
	
	private class DictionaryNode<K,V> implements Comparable<DictionaryNode<K,V>>{
		K key;
		V value;
		DictionaryNode<K,V> leftChild;
		DictionaryNode<K,V> rightChild;
		public DictionaryNode (K key, V value){
			this.key = key;
			this.value = value;
			leftChild = rightChild = null;
		}
		public int compareTo(DictionaryNode<K,V> node){
			return((Comparable<K>)key).compareTo((K)node.key);
		}
	}
	
	public boolean contains(K key) {
		if(has(key, root) != null)
			return true;
		else
			return false;
	}
	//contains helper method that checks a node and its children
	private V has(K key, DictionaryNode<K,V> n){
		if(n == null) return null;
		if(((Comparable<K>)key).compareTo(n.key) < 0)
			return has(key, n.leftChild);
		if(((Comparable<K>)key).compareTo(n.key) > 0)
			return has(key, n.rightChild);
		if(((Comparable<K>)key).compareTo(n.key) == 0)
			return n.value;
		else
			return null;
			
	}
	
	public boolean add(K key, V value) {
		if(root == null)
			root = new DictionaryNode<K,V>(key,value);
		else if(contains(key))
			return false;
		else
			insert(key,value,root,null,false);
		modCount++;
		currentSize++;
		return true;
	}
	// recursive insertion helper method for add
	private void insert(K key, V value, DictionaryNode<K,V> n, DictionaryNode<K,V> parent, boolean wasLeft){
		if(n == null){
			if(wasLeft) parent.leftChild = new DictionaryNode<K,V>(key,value);
			else parent.rightChild = new DictionaryNode<K,V>(key,value);
		}
		else if(((Comparable<K>)key).compareTo((K)n.key) < 0)
				insert(key,value,n.leftChild,n,true);
		else
				insert(key,value,n.rightChild,n,false);
	}
	
	public boolean delete(K key) {
		if(isEmpty()) return false;
		if(!remove(key,root,null,false)) return false;
		modCount++;
		currentSize--;
		return true;
	}
	private boolean remove(K key, DictionaryNode<K,V> n, DictionaryNode<K,V> parent, boolean wasLeft){
		if(n == null)return false;
		if(key.compareTo(n.key) < 0) 
			remove(key,n.leftChild,n,true);
		else if(key.compareTo(n.key) > 0) 
			remove(key,n.rightChild,n,false);
		else
			//0 children
			if(n.leftChild == null && n.rightChild == null){
				if(n == root)
					root = null;
				else if(wasLeft)
					parent.leftChild = null;
				else parent.rightChild = null;
			}
			//one child on the left
			else if(n.rightChild == null){
				if(n == root)
					root = n.leftChild;
				else if(wasLeft)
					parent.leftChild = n.leftChild;
				else
					parent.rightChild = n.rightChild;
			}
			//one child on the right
			else if(n.leftChild == null){
				if(n == root)
					root = n.rightChild;
				else if(wasLeft)
					parent.leftChild = n.rightChild;
				else
					parent.rightChild = n.rightChild;
			}
			//two children
			else{
				DictionaryNode<K,V> successor = getSuccessor(n);
				if(n == root)
					root = successor;
				else if(wasLeft)
					parent.leftChild = successor;
				else
					parent.rightChild = successor;
				successor.leftChild = n.leftChild;
			}
		return true;
	}
	//find the successor of the node to be removed with two children
	private DictionaryNode<K,V> getSuccessor(DictionaryNode<K,V> currNode){
		DictionaryNode<K,V> successorParent = currNode;
		DictionaryNode<K,V> successor = currNode;
		DictionaryNode<K,V> n = currNode.rightChild;
		//get left most child of the right child of the node
		while(n != null){
			successorParent = successor;
			successor = n;
			n = n.leftChild;
		}
		//get the left most successor
		if(successor != currNode.rightChild){
			successorParent.leftChild = successor.rightChild;
			successor.rightChild = currNode.rightChild;
		}
		return successor;
	}

	public V getValue(K key) {
		return findValue(key, root);
	}
	private V findValue(K key, DictionaryNode<K,V> n){
		if(n == null) return null;
		if(((Comparable<K>)key).compareTo(n.key) < 0)
			return findValue(key, n.leftChild);
		if(((Comparable<K>)key).compareTo(n.key) > 0)
			return findValue(key, n.rightChild);
		return (V) n.value;
	}

	
	public K getKey(V value) {
		keyFound = null;
		findKey(value, root);
		return keyFound;
	}
	private void findKey(V value, DictionaryNode<K,V> n){
		if(n == null)
			return;
		findKey(value, n.leftChild);
		if(((Comparable<V>) n.value).compareTo(value) == 0)
			keyFound = n.key;
		findKey(value, n.rightChild);
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
		root = null;
		currentSize = 0;
		modCount = 0;
	}

	public Iterator<K> keys() {
		return new KeyIteratorHelper<K>();
	}
	
	public Iterator<V> values() {
		return new ValueIteratorHelper<V>();
	}
	
	abstract class IteratorHelper<E> implements Iterator<E> {
		protected DictionaryNode<K,V>[] array;
		protected int idx;
		protected long modCheck;
		
		public IteratorHelper(){
		array = new DictionaryNode[currentSize];
		idx = 0;
		modCheck = modCount;
		iterate(root);
		idx = 0;
		}
		//in order traversal
		private void iterate(DictionaryNode<K,V> n){
			if(n == null)
				return;
			iterate(n.leftChild);
			array[idx++] = n;
			iterate(n.rightChild);
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
			if(!hasNext())
				throw new NoSuchElementException();
			return (K) array[idx++].key;
		}
	}
	class ValueIteratorHelper<V> extends IteratorHelper{
		public ValueIteratorHelper(){
			super();
		}
		public V next(){
			if(!hasNext())
				throw new NoSuchElementException();
			return (V) array[idx++].value;
		}
	}
}
