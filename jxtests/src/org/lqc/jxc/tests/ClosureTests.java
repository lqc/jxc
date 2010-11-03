package org.lqc.jxc.tests;


import java.io.File;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class ClosureTests {

		
	@Before
	public void setUp() {
		System.setErr( System.out );
	}
	
	@Test
	public void simpleClosure() {
		ParserTest test = new ParserTest("closureOne", 
			new File("examples/extensions/closures/closureOne.jl"),	true);
		
		test.run();		
	}
	
	@Test
	public void printerClosure() {
		ParserTest test = new ParserTest("closureTwo", 
			new File("examples/extensions/closures/closureTwo.jl"),	true);
		
		
		Assert.assertEquals(test.run().failureCount(), 0);		
	}
	
	@Test
	public void yielderClosure() {
		ParserTest test = new ParserTest("closure3", 
			new File("examples/extensions/closures/closure3.jl"),	true);
		
		
		Assert.assertEquals(test.run().failureCount(), 0);		
	}
	
	@Test
	public void lists() {
		ParserTest test = new ParserTest("closure4", 
			new File("examples/extensions/closures/closure4.jl"),	true);
				
		Assert.assertEquals(test.run().failureCount(), 0);		
	}
	
	@Test
	public void curring() {
		ParserTest test = new ParserTest("closure5", 
			new File("examples/extensions/closures/closure5.jl"),	true);
				
		Assert.assertEquals(test.run().failureCount(), 0);		
	}
	
	@Test
	public void recursive() {
		ParserTest test = new ParserTest("Recursive Closure", 
			new File("examples/extensions/closures/closureRecursive.jl"), true);
				
		Assert.assertEquals(test.run().failureCount(), 0);		
	}

}
