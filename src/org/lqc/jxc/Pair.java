package org.lqc.jxc;

public class Pair<K,V> {
	
	private K first;
	private V second;
	
	public Pair(K f, V s) {
		this.first = f;
		this.second = s;
	}

	/**
	 * @return the first
	 */
	public K first() {
		return first;
	}

	/**
	 * @param first the first to set
	 */
	public void first(K first) {
		this.first = first;
	}

	/**
	 * @return the second
	 */
	public V second() {
		return second;
	}

	/**
	 * @param second the second to set
	 */
	public void second(V second) {
		this.second = second;
	}

}
