package org.lqc.util;

public interface PartiallyOrdered<T extends PartiallyOrdered<? super T>> 
{
	public Relation compareTo(T object);	

};
