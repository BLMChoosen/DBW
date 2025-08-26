@echo off
title DBW Build - Discord But Worse
color 0E

echo ==========================================
echo                  DBW                   
echo           Discord But Worse           
echo                                        
echo  Uma bosta e assumimos sem vergonha   
echo ==========================================
echo.
echo Compilando servidor e cliente...
echo.

cd /d "%~dp0"

echo [1/3] Limpando builds antigos...
gradle clean

echo.
echo [2/3] Compilando codigo...
gradle build

echo.
echo [3/3] Gerando JARs do servidor e cliente...
gradle serverJar clientJar

echo.
echo ==========================================
echo            BUILD COMPLETO!
echo ==========================================
echo.
echo JARs criados em build\libs\:
echo   ? Servidor: DBW-Server-Standalone-1.0-server.jar
echo   ? Cliente:  DBW-Client-Standalone-1.0-client.jar
echo.
echo Como usar:
echo   Servidor: start-server.bat
echo   Cliente:  start-client.bat
echo.
echo Ou manual:
echo   java -jar DBW-Server-Standalone-1.0-server.jar server
echo   java -jar DBW-Client-Standalone-1.0-client.jar client
echo.
pause
