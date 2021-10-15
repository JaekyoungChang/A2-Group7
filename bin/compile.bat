@REM This script will compile the Cinco Ticket program to class files 
@REM and then build a runnable JAR file from those class files.

FOR /F "tokens=* USEBACKQ" %%g IN (`echo %~dp0`) do (SET "SCRIPT_DIR=%%g")

@REM compile class files
dir /s /b "%SCRIPT_DIR%\..\src\cinco\ticket" > "%SCRIPT_DIR%\files.txt"
javac -d "%SCRIPT_DIR%\..\build" @"%SCRIPT_DIR%\files.txt"
del /f "%SCRIPT_DIR%\files.txt"

@REM build runnable jar file
jar -cvfe "%SCRIPT_DIR%\..\build\ticket.jar" "cinco.ticket.Main" -C "%SCRIPT_DIR%\..\build" .

@REM clean up class files
@REM rd /s /q "%SCRIPT_DIR%\..\build\cinco"
