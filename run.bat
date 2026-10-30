@echo off
cd /d %~dp0
if not exist bin mkdir bin
javac -d bin src/module-info.java src/com/weatherquiz/engine/*.java src/com/weatherquiz/peripherals/*.java
if errorlevel 1 pause
copy /Y src\banana1.png bin\ >nul
java -cp bin com.weatherquiz.peripherals.QuizLoginGUI
pause
