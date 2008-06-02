@echo off
set JXDIR=e:\workspaces\mimuw\jxc
set JXLIB=e:\workspaces\mimuw\jxlib\bin
set RLCP=%JXDIR%\rtlib\java-cup-11a-runtime.jar;%JXDIR%\bin\;%JXLIB%\;.

FOR %%G IN (%*) DO java -cp %RLCP% org.lqc.jxc.JxCompiler %%G.jl && java -jar %JXDIR%\rtlib\jasmin.jar %%G.j