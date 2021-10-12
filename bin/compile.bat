@REM compile class files
dir /s /b "%cd%\src\cinco\ticket" > "%cd%\files.txt"
javac -d "%cd%\build" @"%cd%\files.txt"
del /f "%cd%\files.txt"

@REM build jar
jar -cvfe "ticket.jar" "cinco.ticket.Main" -C "%cd%\build" .
rd /s /q "%cd%\build"
