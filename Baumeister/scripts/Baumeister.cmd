@echo off
rem
rem Usage:
rem baumeister [args]
rem Runs the script passing it arguments
rem

set BAUMEISTER_HOME=%~dp0
if %BAUMEISTER_HOME:~-1%==\ set BAUMEISTER_HOME=%BAUMEISTER_HOME:~0,-1%

rem Java 6 style class path
set BAUMEISTER_CLASSPATH="%BAUMEISTER_HOME%\lib\*"
set BAUMEISTER_ARGS=
:setupArgs
if "%1"=="" goto doneStart
set BAUMEISTER_ARGS=%BAUMEISTER_ARGS% %1
shift
goto setupArgs
:doneStart
if "%BAUMEISTER_ARGS%"=="" (
  java -server -cp %BAUMEISTER_CLASSPATH% baumeister.process
) else (
  java -server -cp %BAUMEISTER_CLASSPATH% baumeister.process %BAUMEISTER_ARGS%
)
