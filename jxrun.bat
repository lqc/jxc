@echo off
set LIB=e:\workspaces\mimuw\jxlib\bin
cd jxc_build
java -cp .;%LIB%\ %1
cd ..