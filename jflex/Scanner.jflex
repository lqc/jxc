package org.lqc.jxc;

import java_cup.runtime.*;
import org.lqc.jxc.tokens.*;
import org.lqc.jxc.types.*;

%%
%public
%class Lexer
%implements java_cup.runtime.Scanner
%function next_token
%type org.lqc.jxc.Lexem
%unicode
%line
%column

%{
	public Lexem newSymbol(String name, int id) {
		return new Lexem(name, id, yyline, yycolumn);
	}	
	
	public Lexem newSymbol(String name, int id, Object v) {
		return new Lexem(name, id, yyline, yycolumn, v);
	}			
	
	private StringBuffer string = new StringBuffer();
	private int comment_level = 0;
%}

%init{
	yybegin(YYINITIAL);
%init}

%eofval{
    return newSymbol("EndOfFile", sym.EOF);
%eofval}

%{
	public String srcFilename;
%}

Eol 		= \r|\n|\r\n
InputChar 	= [^\r\n]
WhiteSpace 	= {Eol} | [ \t\f]

LineComment = ("#"|"//") {InputChar}* {Eol}

Digit = [0-9]
Letter = [:jletter:]
AlphaNum = [:jletterdigit:]
Integer = [1-9][0-9]*|0
Real = [1-9][0-9]*\.[0-9]+ | 0\.[0-9]+

Id = {Letter}({AlphaNum}|_)*

%state STRING
%state COMMENT

%%

<YYINITIAL> {

";" { return newSymbol(yytext(), sym.SEMI); }
"(" { return newSymbol(yytext(), sym.LPAR); }
")" { return newSymbol(yytext(), sym.RPAR); }
"{" { return newSymbol(yytext(), sym.LCURLY); }
"}" { return newSymbol(yytext(), sym.RCURLY); }
"," { return newSymbol(yytext(), sym.COLON); }
"." { return newSymbol(yytext(), sym.DOT); }

"=" {return newSymbol(yytext(), sym.EQUAL); }
"==" { return newSymbol(yytext(), sym.EQEQ); }
"!=" { return newSymbol(yytext(), sym.NEQEQ); }
"<" { return newSymbol(yytext(), sym.LT); }
"<=" { return newSymbol(yytext(), sym.LTEQ); }
">" { return newSymbol(yytext(), sym.GT); }
">=" { return newSymbol(yytext(), sym.GTEQ); }

"+" { return newSymbol(yytext(), sym.PLUS); }
"-" { return newSymbol(yytext(), sym.MINUS); }
"*" { return newSymbol(yytext(), sym.TIMES); }
"/" { return newSymbol(yytext(), sym.DIVIDE); }
"%" { return newSymbol(yytext(), sym.MODULO); }

"!" { return newSymbol(yytext(), sym.NEG); }

"||" { return newSymbol(yytext(), sym.LOGOR); }
"&&" { return newSymbol(yytext(), sym.LOGAND); }

"++" { return newSymbol(yytext(), sym.PLUSPLUS); }
"--" { return newSymbol(yytext(), sym.MINUSMINUS); }

"--" { return newSymbol(yytext(), sym.MINUSMINUS); } 

"int" { return newSymbol(yytext(), sym.TYPE, PrimitiveType.INT); }
"double" { return newSymbol(yytext(), sym.TYPE, PrimitiveType.REAL); }
"boolean" { return newSymbol(yytext(), sym.TYPE, PrimitiveType.BOOLEAN); }
"string" { return newSymbol(yytext(), sym.TYPE, PrimitiveType.STRING); }
"void" { return newSymbol(yytext(), sym.TYPE, Type.VOID); }

"true" { return newSymbol("Boolean", sym.BOOLEAN, new Boolean(true)); }
"false" { return newSymbol("Boolean", sym.BOOLEAN, new Boolean(false)); }

"if" { return newSymbol("Keyword", sym.IF); }
"else" { return newSymbol("Keyword", sym.ELSE); }
"for" { return newSymbol("Keyword", sym.FOR); }
"while" { return newSymbol("Keyword", sym.WHILE); }
"return" { return newSymbol("Keyword", sym.RETURN); }
"import" { return newSymbol("Keyword", sym.IMPORT); }
"defines" { return newSymbol("Keyword", sym.DEFINES); }
"implements" { return newSymbol("Keyword", sym.DEFINES); }

{Real} 		{ return newSymbol("Real", sym.REAL, new Double(yytext()) ); }
{Integer}	{ return newSymbol("Integer", sym.INT, new Integer(yytext()) ); }
{Id} 		{ return newSymbol("Identifier", sym.ID, new String(yytext()) ); }

\" { yybegin(STRING); string = new StringBuffer(); }
"/*" { yybegin(COMMENT); comment_level = 1; }

{LineComment} { /* comment */ }
{WhiteSpace} { /* ignore white space. */ }

. { 
    System.err.println("Illegal character: "+yytext()); 
}

}

<COMMENT> {
	"/*"			{ comment_level++; } 
					  
	"*/"			{ comment_level--;					    
					  if(comment_level == 0) yybegin(YYINITIAL); }
		
	[^\*]			{  }
	"*"[^/]			{  }	
}

<STRING> {
  \"			   { yybegin(YYINITIAL); 
					 return newSymbol("String", sym.STRING, string.toString()); }			
  [^\n\r\"\\]+     { string.append( yytext() ); }
  \\t              { string.append('\t'); }
  \\n              { string.append('\n'); }
  \\r              { string.append('\r'); }
  \\\"             { string.append('\"'); }
  \\               { string.append('\\'); }  
}
