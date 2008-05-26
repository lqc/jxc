package org.lqc.util;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.lqc.jxc.Pair;

public final class PathID {	
	
	private Vector<String> path;
	private String base;
	
	private PathID(List<String> p) {
		path = new Vector<String>(p);				
		base = p.get(p.size()-1);
	}
	public PathID(String s) {
		StringTokenizer st = new StringTokenizer(s, ".");
		path = new Vector<String>(st.countTokens());		
		while(st.hasMoreTokens()) {
			path.add(st.nextToken());
		}
		
		base = path.lastElement();
	}
	
	public void append(String p) {
		StringTokenizer st = new StringTokenizer(p, ".");
		path.ensureCapacity(path.size() + st.countTokens());
		while(st.hasMoreTokens()) {
			path.add(st.nextToken());
		}		
		
		base = path.lastElement();
	}
	
	public void prepend(String p) {
		StringTokenizer st = new StringTokenizer(p, ".");
		path.ensureCapacity(path.size() + st.countTokens());
		while(st.hasMoreTokens()) {
			path.add(0, st.nextToken());
		}		
	}
	
	public String basename() {
		return base;
	}
	
	public String absoluteName() {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i < path.size()-1; i++) {
			buf.append(path.get(i));
			buf.append('.');
		}
		buf.append(this.basename());
		
		return buf.toString();		
	}
		
	public String toString() {
		return this.absoluteName();		
	}	
	
	public boolean isRelative() {
		return path.size() == 1;
	}
	
	public Pair<PathID, String> tailSplit() {
		if(path.size() == 1)
			return new Pair<PathID, String>(null, base);
		
		PathID np = new PathID(path.subList(0, path.size()-1) );		
		return new Pair<PathID, String>(np, base);		
	}
	
	public Pair<String, PathID> headSplit() {
		if(path.size() == 1)
			return new Pair<String, PathID>(base, null);
		
		PathID np = new PathID(path.subList(1, path.size()-1) );		
		return new Pair<String, PathID>(path.get(0), np);		
	}
}
