@echo off
echo =======================================================
echo          Inicializador - Farm Food (Spring Boot)
echo =======================================================
echo.

REM Verifica se o Java está instalado
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERRO] Java (JDK 17) nao encontrado no PATH!
    echo Por favor, instale o Java 17 e tente novamente.
    pause
    exit /b
)

REM Verifica se o Maven está instalado
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo [AVISO] O comando 'mvn' (Maven) nao foi encontrado no seu PATH.
    echo Como voce nao tem o Maven configurado globalmente no Windows,
    echo recomendamos iniciar o projeto diretamente pela sua IDE 
    echo (IntelliJ, Eclipse ou VS Code).
    echo.
    echo Para iniciar pela IDE:
    echo 1. Abra a pasta deste projeto na sua IDE.
    echo 2. Execute a classe: FoodfarmerApplication.java
    echo.
    pause
    exit /b
)

echo Iniciando o projeto via Maven...
echo.
mvn spring-boot:run

pause
