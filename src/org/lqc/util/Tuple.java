package org.lqc.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

public class Tuple<T extends PartiallyOrdered<T>> 
	implements PartiallyOrdered<Tuple<T>>,
				Iterable<T>

{
	
	private T[] tuple;
	
	public Tuple(T... elements) {
		tuple = elements;	
	}		
	
	public Tuple(Collection<T> col) {
		// tuple = (T[]) new NoncomparableObject[0];
		tuple = (T[]) col.toArray();
		
		int i=0;
		for(T elem : col) {
			tuple[i] = elem;
			i++;
		}		
	}

	public Relation compareTo(Tuple<T> other) {
		Relation c = Relation.EQUAL;
		
		if(other.tuple.length != this.tuple.length)
			return Relation.NONCOMPARABLE;
		
		for(int i=0; i < tuple.length; i++) {
			Relation r = tuple[i].compareTo(other.tuple[i]);
			
			if(r.equals(Relation.NONCOMPARABLE))
				return Relation.NONCOMPARABLE;
			
			if(r.equals(Relation.EQUAL))
				continue;
			
			if(c.equals(Relation.EQUAL)) {
				c = r;
				continue;
			}			
			
			if(!r.equals(c))
				return Relation.NONCOMPARABLE;				
		}	
		
		return c;		
	}
	
	public int size() {
		return tuple.length;
	}	
	
	public T get(int index) {
		return tuple[index];
	}

	public Iterator<T> iterator() {
		return new TupleIterator();		
	}
	
	private class TupleIterator implements Iterator<T> {		
		private int lastIndex;	
				
		private TupleIterator() {
			lastIndex = 0;
		}

		public boolean hasNext() {
			return (lastIndex < tuple.length);			
		}

		public T next() {
			return tuple[lastIndex++];
		}

		public void remove() {
			throw new UnsupportedOperationException();			
		}		
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		for(int i=0; i < tuple.length-1; i++)
		{
			buf.append(tuple[i].toString());
			buf.append(", ");
		}
		
		buf.append(tuple[tuple.length-1].toString());
		buf.append(")");
		return buf.toString();			
	}
	
}
