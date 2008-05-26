@echo off
java -cp rtlib\java-cup-11a-runtime.jar;bin\;..\jxlib\bin\;. org.lqc.jxc.JxCompiler %1.jl && java -jar rtlib\jasmin.jar %1.j