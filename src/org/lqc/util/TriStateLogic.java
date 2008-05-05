package org.lqc.util;

public enum TriStateLogic {
	TRUE,
	FALSE,
	UNKNOWN;
	
	private TriStateLogic and(TriStateLogic a, TriStateLogic b) {
		if(a.equals(TRUE))
			return b;
		
		if(a.equals(FALSE) || b.equals(FALSE))
			return FALSE;
		
		return UNKNOWN;			
	}
	
	private TriStateLogic or(TriStateLogic a, TriStateLogic b) {
		if(a.equals(TRUE) || b.equals(TRUE))
			return TRUE;
		
		if(a.equals(FALSE))
			return b;
		
		return UNKNOWN;			
	}
	
	
	public TriStateLogic and(TriStateLogic x) {
		return and(this, x);
	}
	
	public TriStateLogic or(TriStateLogic x) {
		return or(this, x);
	}
	
	public TriStateLogic neg() {
		switch(this) {
			case TRUE:
				return FALSE;
			case FALSE:
				return TRUE;			
		}		
		return UNKNOWN;
	}	

}
