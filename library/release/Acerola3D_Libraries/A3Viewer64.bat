@echo off
set CLASSPATH=.;%~dp0lib\*;%~dp0a3files;%CLASSPATH%
set PATH=%~dp0Java\jdk\bin;%PATH%
set PATH=%~dp0natives\windows-amd64;%PATH%
if "%1" == "" (java jp.sourceforge.acerola3d.a3viewer.A3Viewer) else java jp.sourceforge.acerola3d.a3viewer.A3Viewer -open %1
pause
