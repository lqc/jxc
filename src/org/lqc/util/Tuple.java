package org.lqc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class Tuple<T extends PartiallyOrdered<T>> 
	implements PartiallyOrdered<Tuple<T>>,
				Iterable<T>

{
	
	private ArrayList<T> tuple;
	
	public Tuple(T... elements) {
		tuple = new ArrayList<T>(elements.length);
		for(T e : elements)
			tuple.add(e);			
	}		
	
	public Tuple(Collection<T> col) {
		// tuple = (T[]) new NoncomparableObject[0];
		tuple = new ArrayList<T>();
		tuple.addAll(col);
	}

	public Relation compareTo(Tuple<T> other) {
		Relation c = Relation.EQUAL;
		
		if(other.tuple.size() != this.tuple.size())
			return Relation.NONCOMPARABLE;
		
		for(int i=0; i < tuple.size(); i++) {
			Relation r = tuple.get(i).compareTo(other.tuple.get(i));
			
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
		return tuple.size();
	}	
	
	public T get(int index) {
		return tuple.get(index);
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
			return (lastIndex < tuple.size());			
		}

		public T next() {
			return tuple.get(lastIndex++);
		}

		public void remove() {
			throw new UnsupportedOperationException();			
		}		
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("(");
		for(int i=0; i < tuple.size()-1; i++)
		{
			buf.append(tuple.get(i).toString());
			buf.append(", ");
		}
		
		buf.append(tuple.get(tuple.size()-1).toString());
		buf.append(")");
		return buf.toString();			
	}
	
}
