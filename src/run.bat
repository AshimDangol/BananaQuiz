@echo off
cd /d %~dp0\..
if not exist bin mkdir bin
javac -d bin src/module-info.java src/com/perisic/tomato/engine/*.java src/com/perisic/tomato/peripherals/*.java src/com/weatherquiz/engine/*.java src/com/weatherquiz/peripherals/*.java
if errorlevel 1 pause
copy /Y src\tomato1.png bin\ >nul
copy /Y src\tomato2.png bin\ >nul
copy /Y src\banana1.png bin\ >nul
java -cp bin com.weatherquiz.peripherals.QuizLoginGUI
pause
