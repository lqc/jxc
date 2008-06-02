package org.lqc.util;

public final class POUtil {
	
	public static <T extends PartiallyOrdered<? super T>> T min(T a, T b) 
	{
		switch(a.compareTo(b)) {
			case LESSER:
			case EQUAL:
				return a;
			case GREATER:
				return b;
			default:
				return null;				
		}				
	}
	
	public static <T extends PartiallyOrdered<? super T>> T max(T a, T b) 
	{
		switch(a.compareTo(b)) {
			case GREATER:
			case EQUAL:
				return a;			
			case LESSER:
				return b;
			default:
				return null;				
		}				
	}

}
