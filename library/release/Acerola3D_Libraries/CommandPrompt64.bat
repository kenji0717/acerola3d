@echo off
set CLASSPATH=.;%~dp0lib\*;%~dp0a3files;%CLASSPATH%
set PATH=%~dp0Java\jdk\bin;%PATH%
set PATH=%~dp0natives\windows-amd64;%PATH%
%windir%\system32\cmd.exe
