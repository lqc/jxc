package org.lqc.jxc.tests;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.lqc.jxc.tests");
		//$JUnit-BEGIN$
		File dir;
		File[] tests;		
		
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				if(!pathname.isFile()) return false;
				
				return Pattern.matches("[a-zA-Z0-9\\-]+\\.jl", 
						pathname.getName());				
			}
			
		};
		
		
		dir  = new File("examples/good");		
		tests = dir.listFiles(filter);		
		for(File file : tests) {
			suite.addTest( new ParserTest("test-"+file.getName(), file, true));
		}
		
		dir  = new File("examples/bad");		
		tests = dir.listFiles(filter);
		
		for(File file : tests) {
			suite.addTest( new ParserTest("test-"+file.getName(), file, false));
		}		
		
		dir  = new File("examples/extensions/overloading");		
		tests = dir.listFiles(filter);		
		for(File file : tests) {
			suite.addTest( new ParserTest("xtest-"+file.getName(), file, true));
		}

		//$JUnit-END$
		return suite;
	}

}
