@echo on

echo %JAVA_HOME%

REM set mypath=%cd%

set mypath=%~dp0

REM echo %mypath%

chdir /d %mypath%

javac -cp lib\* -d bin src\bhfUtility\*.java
javac -cp lib\*;bin -d bin src\eAppBHFAutomation\*.java

java -cp lib\*;bin eAppBHFAutomation.AutomationDriver

pause