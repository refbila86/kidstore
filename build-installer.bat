@echo off
echo ========================================
echo Gerando instalador MSI - Farmacy Management
echo ========================================

REM Limpa e compila o projeto
echo [1/4] Compilando o projeto...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo Erro na compilacao!
    pause
    exit /b %errorlevel%
)

REM Cria diretório para o instalador
echo [2/4] Preparando estrutura...
if not exist "installer" mkdir installer
if not exist "installer\input" mkdir installer\input
copy target\farmacy-management-0.0.1-SNAPSHOT.jar installer\input\app.jar

REM Gera o instalador com jpackage
echo [3/4] Gerando instalador MSI com jpackage...
jpackage ^
    --input installer\input ^
    --name "FarmacyManagement" ^
    --main-jar app.jar ^
    --main-class mz.co.crud.FarmacyManagementApplication ^
    --type msi ^
    --app-version 0.0.1 ^
    --vendor "Farmacy Management" ^
    --description "Sistema de vendas e controlo de stock" ^
    --win-dir-chooser ^
    --win-menu ^
    --win-menu-group "Farmacy Management" ^
    --win-shortcut ^
    --dest installer\output ^
    --java-options "-Xms256m" ^
    --java-options "-Xmx1024m"

if %errorlevel% neq 0 (
    echo Erro ao gerar instalador!
    echo.
    echo Certifique-se de ter o WiX Toolset instalado:
    echo https://wixtoolset.org/releases/
    echo.
    echo Adicione ao PATH: C:\Program Files (x86)\WiX Toolset v3.14\bin
    pause
    exit /b %errorlevel%
)

echo [4/4] Instalador MSI gerado com sucesso!
echo.
echo O instalador esta em: installer\output\FarmacyManagement-0.0.1.msi
echo.
echo Tamanho aproximado: 50-70 MB
echo.
pause