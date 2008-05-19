package org.lqc.jxc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import java_cup.runtime.Symbol;

import org.lqc.jxc.il.Module;
import org.lqc.jxc.javavm.IL2Jasmin;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.transform.AST2IL;
import org.lqc.jxc.transform.ExternalContext;
import org.lqc.jxc.transform.ScopeAnalyzer;


public class JxCompiler {
	
	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 2;
	
	/* TODO refactor error messages */
	/* TODO IEEE real constants */
	
	public static void main(String[] args) 
		throws IOException 
	{
		compile(args[0].trim());
	}
	
	
	public static int compile(String path) throws IOException		
	{		
		File in;
		File out;
		String fname;
		int retcode = 0;
		
		System.out.printf("jxCompiler version %d.%d\n",
				MAJOR_VERSION, MINOR_VERSION);
		
		in = new File(path);
		fname = in.getName();
		fname = fname.substring(0, fname.indexOf("."));
		out = new File(fname + ".j", ".");		
		
		try {
			/* files opended, go to parse phase */			
			InputStream is = new FileInputStream(in);
			Lexer scanner = new Lexer(is);
			Parser parser = new Parser(scanner);
		
			Symbol root = parser.parse();
			
			/* Extract the program node */
			CompileUnit cu = (CompileUnit)root.value;			
			cu.setName(fname);						
			
			System.out.println("Loading libraries...");
			//Vector<URL> paths = new Vector<URL>();
			//paths.add( new URL("file://./") );
			//paths.add( new URL("file://e:\\workspaces\\mimuw\\jxlib\\bin\\") );
			//URLClassLoader cls = new URLClassLoader(paths.toArray(new URL[0]));
			ClassLoader cls = ClassLoader.getSystemClassLoader();
			
			Module sysm = new Module(cls.loadClass("lang.jx.System"));
			ExternalContext sysctx = new ExternalContext(null, sysm);
			/* TODO: Resolve imports here */
						
			/* Visibility and type checking */
			System.out.println("Type checking...");			
			cu.visitNode(new ScopeAnalyzer(sysctx));
			
			/* Transform to high-level IL. Single CU yields a module. */
			System.out.println("Converting to IL...");
			Module m = AST2IL.convert(cu);
						
			/* TODO: flow analysis */
			//ControlFlowAnalyzer cfa = new ControlFlowAnalyzer();
			//p.visitNode(cfa);
						 
			//for(CompilerWarning w : cfa.getWarnings()) {
			//	System.out.println(w.getMessage());
			//}
						
			/* generate Jasmin from IL Tree */
			FileOutputStream s;
			
			System.out.println("Generating Jasmin...");
			IL2Jasmin conv = new IL2Jasmin(
					new PrintStream(
					s = new FileOutputStream(out)) );
			
			conv.emmit(m);
			s.close();							
			
		} catch(FileNotFoundException e) {
			System.out.printf("[Error] Input file '%s' not found.", in.getPath());
			retcode = 1;
		} catch(CompilerException e) {
			System.out.println(e.getMessage());
			retcode = 1;						
		} catch(Exception e) {
			e.printStackTrace();
			retcode = 1;			
		}	
		
		return retcode;		
	}
}
