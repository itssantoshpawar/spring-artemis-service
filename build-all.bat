@echo off
echo ========================================
echo Building All Spring Boot Components
echo ========================================

echo.
echo Building Parent Project...
call mvn clean install -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Build failed!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ========================================
echo Build Completed Successfully!
echo ========================================
echo.
echo JAR files created:
echo - component-1/target/component-1-1.0.0-SNAPSHOT.jar
echo - component-2/target/component-2-1.0.0-SNAPSHOT.jar
echo - component-4/target/component-4-1.0.0-SNAPSHOT.jar
echo.
pause
