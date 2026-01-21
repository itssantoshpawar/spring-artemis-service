@echo off
echo ========================================
echo Running Component 1 - Adapter Service
echo ========================================
echo.
echo Component 1 will start on port 8081
echo REST API available at http://localhost:8081/api/messages/send
echo Health check at http://localhost:8081/api/messages/health
echo.

cd component-1
java -jar target/component-1-1.0.0-SNAPSHOT.jar --spring.profiles.active=artemis-only

pause
