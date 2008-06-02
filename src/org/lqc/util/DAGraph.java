package org.lqc.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class DAGraph<K extends PartiallyOrdered<? super K> , V> implements POSet<K, V> {
	
	public class Node {
		protected Vector<Node> children;
		protected K key;
		protected Vector<Node> parents;
		protected V value;

		private Node() {
			this(null, null);
		}

		private Node(K key, V value) {
			this.children = new Vector<Node>();
			this.parents = new Vector<Node>();
			this.value = value;
			this.key = key;
		}
		
		public K key() {
			return key;
		}
		
		public V value() {
			return value;
		}

		public List<Node> children() {
			return this.children;
		}

		public Vector<Node> parents() {
			return parents;
		}
	}

	private Node root;

	/** Creates new empty Partial Order DAG. */
	public DAGraph() {
		root = new Node();
	}

	public boolean contains(K key)		
	{
		try {
			return (this.find(root, key) != null);
		} catch (MultiplyMatchException e) {
			return false;
		}
	}

	public V find(K key) 
		throws ElementNotFoundException, MultiplyMatchException
	{
		Node ret = null;
		
		Vector<Node> matches = new Vector<Node>();
		
		for(Node n : root.children) {
			Node x = this.find(n, key);
			if(x != null) 
				matches.add(x);
		}
		
		switch(matches.size()) {
			case 1:
				return matches.firstElement().value;
			case 0:
				throw new ElementNotFoundException();
			default:
				throw new MultiplyMatchException(matches);
		}			
		
	}
	
	private Node find(Node root, K key) 
		throws MultiplyMatchException
	{		
		switch(key.compareTo(root.key)) {			 
			case EQUAL:
				return root;
			case LESSER:
				Vector<Node> matches = new Vector<Node>();
				for(Node n : root.children) {
					Node x = this.find(n, key);
					if(x != null) 
						matches.add(x);
				}
				switch(matches.size()) {
					case 1:
						return matches.firstElement();
					case 0:
						return root;
					default:
						throw new MultiplyMatchException(matches);
				}
			default:
				return null;				
		}		
	}

	public void insert(K key, V value) 
		throws NonUniqueElementException 
	{
		Node node = new Node(key, value);
		this.insert(root, node);
	}

	private void insert(Node root, Node node) 
		throws NonUniqueElementException 
	{

		/*
		 * STUDY. let x < n 1. x !~ child(n) => child(n) = child(n) + {x}
		 * parent(x) = parent(x) + {n} 2. E(V sub child(n)) x >= V => child(n) =
		 * (child(n) \ V) + {x} parent(x) = parent(x) + {n} for e in V parent(e) =
		 * parent(e) + {x} \ {n} child(x) = V 3. E(V sub child(n)) x < V => for
		 * n_k in V insert(n_k, x).
		 */

		Vector<Node> pg = new Vector<Node>();
		Vector<Node> cg = new Vector<Node>();

		for (Node n : root.children) {
			switch (node.key.compareTo(n.key)) {
			case EQUAL:
				throw new NonUniqueElementException();
			case GREATER:
				pg.add(n);
				break;
			case LESSER:
				cg.add(n);
				break;
			}
		}

		if (pg.size() + cg.size() == 0) {
			root.children.add(node);
			return;
		}

		if (pg.size() > 0) {
			/* we are parent of some types */
			for (Node n : pg) {
				root.children.remove(n);
				node.children.add(n);
			}
			root.children.add(node);
		} else {
			/* cg > 0 */
			/* this type belongs to one or more graphs */
			for (Node n : cg)
				this.insert(n, node);
		}

	}

	public V remove(K key) {
		throw new UnsupportedOperationException();
	}
	
	public Node getRoot() {
		return root;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("<DAG>");
		
		return buf.toString();
	}
	
	private void putAllChildren(Node x, Set<V> set) {
		for(Node n : x.children) {
			set.add(n.value);
			putAllChildren(n, set);
		}
	}

	public Set<V> values() {
		HashSet<V> set = new HashSet<V>();
		
		for(Node n : root.children) {
			set.add(n.value);
			putAllChildren(n, set);
		}
		
		return set;
	}
}
