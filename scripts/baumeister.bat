#
# Usage:
#
# baumeister [args]	# Runs the script passing it arguments
#

set BAUMEISTER_HOME=%~dp0
if %BAUMEISTER_HOME:~-1%==\ set BAUMEISTER_HOME=%BAUMEISTER_HOME:~0,-1% 

# Java 6 style class path
BAUMEISTER_CLASSPATH="%BAUMEISTER_HOME%\lib\*"

set BAUMEISTER_ARGS=
:setupArgs
if ""%1""=="""" goto doneStart
set BAUMEISTER_ARGS=%BAUMEISTER_ARGS% %1
shift
goto setupArgs

:doneStart

if %BAUMEISTER_ARGS%==""
  (java -server -cp "%BAUMEISTER_CLASSPATH%" org.soulspace.build.baumeister.process)
else
  (java -server -cp "%BAUMEISTER_CLASSPATH%" org.soulspace.build.baumeister.process %BAUMEISTER_ARGS%)
