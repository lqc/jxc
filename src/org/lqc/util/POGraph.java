package org.lqc.util;

import java.util.List;
import java.util.Vector;

public class POGraph<V extends PartialyComparable<V>>
{		
	private GraphNode root;	
	
	/** Creates new empty Partial Order DAG. */
	public POGraph() {
		root = new GraphNode();		
	}
	
	public void insert(V e) 
		throws NonUniqueElementException 
	{
		/* create a new node for the value */
		GraphNode x = new GraphNode(e);
		
		this.insert(root, x);
	}	
	
	private void insert(GraphNode root, GraphNode x) 
		throws NonUniqueElementException {	
		
		/* STUDY. let x < n 
		 * 1. x !~ child(n) => 
		 * 	  child(n) = child(n) + {x}
		 * 	  parent(x) = parent(x) + {n} 
		 * 2. E(V sub child(n)) x >= V => 
		 * 		child(n) = (child(n) \ V) + {x}
		 * 		parent(x) = parent(x) + {n}
		 * 		for e in V 
		 * 			parent(e) = parent(e) + {x} \ {n} 
		 * 		child(x) = V
		 * 3. E(V sub child(n)) x < V =>
		 * 		for n_k in V 
		 * 			insert(n_k, x).
		 */				
		
		Vector<GraphNode> pg = new Vector<GraphNode>(); 
		Vector<GraphNode> cg = new Vector<GraphNode>();
				
		for(GraphNode n : root.children) {
			if( n.value.isComparable(x.value))
			{
				/* x >= n => n !parentOf x */ 
				if( x.value.isGreaterEqual(n.value))
					pg.add(n);
				else 
					cg.add(n);
			}
		}
		
		/* if not related to any, add to root */
		if( pg.size() + cg.size() == 0) {
			root.children.add(x);
			return;
		}
		
		if ( pg.size() > 0) 
		{
			/* we are parent of some types */			
			for(GraphNode n : pg) {
				if(n.value.isGreaterEqual(x.value))
					throw new NonUniqueElementException();
			
				root.children.remove(n);
				x.children.add(n);			
			}
		
			root.children.add(x);
		}		
		else {
			/* cg > 0 */
			/* this type belongs to one or more graphs */					
			for(GraphNode n : cg) this.insert(n, x);			
		}
		
	}
	
	public GraphNode root() {
		return root;
	}	
		
	public class GraphNode {		
		protected Vector<GraphNode> parents;		
		protected Vector<GraphNode> children;
		protected V value;
		
		public Vector<GraphNode> parents() {
			return parents;
		}
		
		public List<GraphNode> children() {
			return this.children;
		}
		
		public V value() {
			return this.value;
		}		
		
		public GraphNode() {
			this(null);
		}
		
		public GraphNode(V value) {
			this.children = new Vector<GraphNode>();
			this.parents = new Vector<GraphNode>();			
			this.value = value;
		}		
	}

}
