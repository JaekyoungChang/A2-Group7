@REM This script will launch the Cinco Ticket program.

@REM move to base directory
cd "%cd%\.."

@REM launch from compiled class files
@REM java -classpath "%cd%\build" cinco.ticket.Main

@REM launch from jar file (specifying the entrypoint)
@REM java -classpath "%cd%\build\ticket.jar" cinco.ticket.Main

@REM launch from runnable jar file
java -jar "%cd%\build\ticket.jar"
