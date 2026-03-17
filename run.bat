@echo off
setlocal

if not exist out (
    echo No compiled classes found. Run build.bat first.
    exit /b 1
)

java -cp out JGraphEdFrame
endlocal