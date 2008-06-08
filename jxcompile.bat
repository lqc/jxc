@echo off
set JXDIR=e:\workspaces\mimuw\jxc
set JXLIB=e:\workspaces\mimuw\jxlib\bin
set RLCP=%JXDIR%\rtlib\java-cup-11a-runtime.jar;%JXDIR%\rtlib\jasmin.jar;%JXDIR%\bin\;%JXLIB%\;.

java -cp %RLCP% org.lqc.jxc.JxCompiler %1