@echo off
setlocal

if exist out rmdir /s /q out
mkdir out

if exist JGraphEd.jar del /q JGraphEd.jar
if exist manifest.txt del /q manifest.txt
if exist sources.txt del /q sources.txt

(
  for %%F in (*.java) do @echo %%F
  for /r dataStructure %%F in (*.java) do @echo %%F
  for /r graphException %%F in (*.java) do @echo %%F
  for /r graphStructure %%F in (*.java) do @echo %%F
  for /r operation %%F in (*.java) do @echo %%F
  for /r userInterface %%F in (*.java) do @echo %%F
) > sources.txt

javac -Xmaxerrs 500 -encoding UTF-8 -d out @sources.txt
if errorlevel 1 (
    echo.
    echo BUILD FAILED
    exit /b 1
)

if exist images xcopy images out\images /E /I /Y >nul
if exist help xcopy help out\help /E /I /Y >nul

echo Main-Class: JGraphEdFrame > manifest.txt
echo.>>manifest.txt

jar cfm JGraphEd.jar manifest.txt -C out .
if errorlevel 1 (
    echo.
    echo JAR BUILD FAILED
    exit /b 1
)

echo.
echo BUILD OK
echo Classes and resources are in out\
echo JAR created: JGraphEd.jar

echo.
echo Note: integration\\ contains reference files only and is excluded from compilation.

endlocal
