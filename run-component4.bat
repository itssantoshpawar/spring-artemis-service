@echo off
echo ========================================
echo Running Component 4 - Consumer Service
echo ========================================
echo.
echo Component 4 will start on port 8084
echo Listening to queue: component4.queue
echo.

cd component-4
java -jar target/component-4-1.0.0-SNAPSHOT.jar

pause
