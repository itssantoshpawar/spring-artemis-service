@echo off
echo ========================================
echo Running Component 2 - Consumer Service
echo ========================================
echo.
echo Component 2 will start on port 8082
echo Listening to queue: component2.queue
echo.

cd component-2
java -jar target/component-2-1.0.0-SNAPSHOT.jar

pause
