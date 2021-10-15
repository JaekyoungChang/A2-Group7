@REM This script will launch the Cinco Ticket program.

FOR /F "tokens=* USEBACKQ" %%g IN (`echo %~dp0`) do (SET "SCRIPT_DIR=%%g")

@REM launch from compiled class files
@REM java -classpath "%SCRIPT_DIR%\..\build" cinco.ticket.Main

@REM launch from jar file (specifying the entrypoint)
@REM java -classpath "%SCRIPT_DIR%\..\build\ticket.jar" cinco.ticket.Main

@REM launch from runnable jar file
java -jar "%SCRIPT_DIR%\..\build\ticket.jar"
