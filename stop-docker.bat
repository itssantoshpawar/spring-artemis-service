@echo off
echo ========================================
echo Stopping Docker Containers
echo ========================================

docker-compose down

echo.
echo Containers stopped successfully!
pause
