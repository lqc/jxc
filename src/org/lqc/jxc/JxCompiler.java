package org.lqc.jxc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Map;

import java_cup.runtime.Symbol;

import org.lqc.jxc.il.Klass;
import org.lqc.jxc.javavm.IL2Jasmin;
import org.lqc.jxc.tokens.CompileUnit;
import org.lqc.jxc.transform.AST2IL;
import org.lqc.jxc.transform.ExternalContext;
import org.lqc.jxc.transform.ILFlowAnalyzer;
import org.lqc.jxc.transform.ScopeAnalyzer;


public class JxCompiler {
	
	public static final int MAJOR_VERSION = 0;
	public static final int MINOR_VERSION = 2;
	
	/* TODO refactor error messages */
	/* TODO IEEE real constants */
	
	public static void main(String[] args) 
		throws IOException 
	{
		int ret;
		File temp;
				
		temp = new File( new File("."), "jxc_build");
		temp.delete();
		temp.mkdirs();		
		
		File[] files = compile(args[0].trim(), temp);
		
		if(files == null)
			System.exit(1);
		
//		StringBuilder paths = new StringBuilder();		
//		int i=0;		
//		for(File f : files)
//		{
//			paths.append(f.getAbsolutePath());
//			paths.append(" ");
//		}
		
		System.out.println("Assembling...");				
		
		ProcessBuilder pb = new ProcessBuilder();
		pb.directory(temp);
		pb.command("java", "-jar", "E:\\workspaces\\mimuw\\jxc\\rtlib\\jasmin.jar", "*.j");
			
		Process jasmin = pb.start();
		
		System.out.println("Done.");		
		System.exit(0);
	}
	
	
	public static File[] compile(String path, File outdir) 
		throws IOException		
	{		
		File[] outs = null;		
		int current;
		File in;
		
		String fname;
		
		System.out.printf("jxCompiler version %d.%d\n",
				MAJOR_VERSION, MINOR_VERSION);
		
		in = new File(path);
		fname = in.getName();
		fname = fname.substring(0, fname.indexOf("."));		
		
		try {
			/* files opended, go to parse phase */			
			InputStream is = new FileInputStream(in);
			Lexer scanner = new Lexer(is);
			Parser parser = new Parser(scanner);
		
			Symbol root = parser.parse();
			
			/* Extract the program node */
			CompileUnit cu = (CompileUnit)root.value;			
			cu.setName(fname);						
												
			/* Visibility and type checking */
			System.out.println("Type checking...");		
			ScopeAnalyzer scope = new ScopeAnalyzer(); 
			cu.visitNode(scope);
			
			Map<String, ExternalContext> exts = scope.externals(); 
			
			/* Transform to high-level IL. Single CU yields a module. */
			System.out.println("Converting to IL...");
			Collection<Klass> klist = AST2IL.convert(cu, exts.values());
			
			outs = new File[klist.size()];
									
			/* TODO: flow analysis */
			int i = 0;
			for(Klass klass : klist) {				
				ILFlowAnalyzer ilfa = new ILFlowAnalyzer();
				
				ilfa.analyze(klass);				
				/* generate Jasmin from IL Tree */
				
				FileOutputStream s;
				outs[i] = new File(outdir, klass.getKlassName() + ".j");
			
				System.out.println("Generating Jasmin for klass: " + klass);
				IL2Jasmin conv = new IL2Jasmin(
					new PrintStream(
					s = new FileOutputStream(outs[i])) );
			
				conv.emmit(klass);
				s.close();
				i++;
			}					
		} catch(FileNotFoundException e) {
			System.out.printf("[Error] " + e.getMessage());
			outs = null;
		} catch(CompilerException e) {
			System.out.println(e.getMessage());
			outs = null;						
		} catch(Exception e) {
			e.printStackTrace();
			outs = null;			
		}	
		
		return outs;
	}
}
