package org.lqc.jxc.tests;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ParserTest extends TestCase {
	
	private File f;
	private boolean positive;

	public ParserTest(String name, File f, boolean positive) {
		super(name);
		this.f = f;
		this.positive = positive;		
	}
	
	public void runTest() throws Exception {		
		ByteArrayOutputStream bs;
		PrintStream log = new PrintStream( bs = new ByteArrayOutputStream());
		
		System.out.printf("*** START OF TEST: %s ***\n", this.getName());
		
		int retcode = 0;
		
		try {
			ProcessBuilder pb = new ProcessBuilder();
			String name = f.getName();
			name = name.substring(0, name.indexOf("."));
			
			File outdir = new File("temp/" + name);
			outdir.delete();			
			outdir.mkdirs();			
			
			/* compile to Jasmin*/
			File[] fx = org.lqc.jxc.JxCompiler.compile(
					f.getAbsolutePath(), outdir);					
						
			if(fx != null) {				
				System.out.println("Assembling...");				
						
				pb.directory(outdir);
				pb.command("java", "-jar", "E:\\workspaces\\mimuw\\jxc\\rtlib\\jasmin.jar", "*.j");
				
				Process jasmin = pb.start();								
			}			

			/*
			if(retcode == 0) {
				System.out.println("Runnning... ");
				pb.command("java", "-classpath", ".;e:\\workspaces\\mimuw\\jxlib\\bin", name);
				Process prog = pb.start();
				InputStream is = prog.getInputStream();
							
				retcode = prog.waitFor();
				
				if(retcode == 0) {
					int x;
					FileOutputStream fs = new FileOutputStream(
						new File(name + ".myout") );
								
					while( (x = is.read()) != -1)
						fs.write(x);
				}	
				else {
					int x;
					
					System.out.println("Error:");
					while( (x = is.read()) != -1)
						System.out.write(x);					
				}
			} */
						
		} catch (FileNotFoundException e) {			
			Assert.fail(e.getLocalizedMessage());
		} catch (Exception e) {
			if(positive)
				throw e;			
			
			log.printf("Exception: " + e.getMessage());			
			System.out.println(bs.toString());		
			System.out.printf("*** END OF TEST: %s ***\n", this.getName());
			return;
		}
		
		if( !positive && (retcode == 0)) {
			System.out.println(bs.toString());
			System.out.printf("*** TEST: %s FAILED ***\n", this.getName());
			Assert.fail("Malformed input accepted.");
			return;
		}
		
		if( positive & (retcode != 0)) 
		{
			System.out.println(bs.toString());
			System.out.printf("*** TEST: %s FAILED ***\n", this.getName());
			Assert.fail("Input not accepted.");
			return;
		}
			
		System.out.printf("*** END OF TEST: %s ***\n", this.getName());
	}
	
	

}
