@REM This script will compile the Cinco Ticket program to class files 
@REM and then build a runnable JAR file from those class files.

@REM move to base directory
cd "%cd%\.."

@REM compile class files
dir /s /b "%cd%\src\cinco\ticket" > "%cd%\files.txt"
javac -d "%cd%\build" @"%cd%\files.txt"
del /f "%cd%\files.txt"

@REM build runnable jar file
jar -cvfe "%cd%\build\ticket.jar" "cinco.ticket.Main" -C "%cd%\build" .

@REM clean up class files
@REM rd /s /q "%cd%\build\cinco"
