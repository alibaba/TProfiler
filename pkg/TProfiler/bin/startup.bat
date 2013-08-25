@REM TProfiler command line script
@REM Author: manlge
@REM Mail: manlge168@gmail.com
@REM Create Time: 2013-08-24

@echo off

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto chkTP_HOME

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:chkTP_HOME
if not "%TP_HOME%"=="" goto vaTP_HOME

if "%OS%"=="Windows_NT" SET "TP_HOME=%~dp0.."
if "%OS%"=="WINNT" SET "TP_HOME=%~dp0.."
if not "%TP_HOME%"=="" goto vaTP_HOME

echo.
echo ERROR: TP_HOME not found in your environment.
echo Please set the TP_HOME variable in your environment to match the
echo location of the tprofiler installation
echo.
goto error

:vaTP_HOME

:stripTP_HOME
if not "_%TP_HOME:~-1%"=="_\" goto checkTP_HOME
set "TP_HOME=%TP_HOME:~0,-1%"
goto stripTP_HOME

:checkTP_HOME
if exist "%TP_HOME%\bin\startup.bat" goto init

echo.
echo ERROR: TP_HOME is set to an invalid directory.
echo TP_HOME = "%TP_HOME%"
echo Please set the TP_HOME variable in your environment to match the
echo location of the tprofiler installation
echo.
goto error
@REM ==== END VALIDATION ====

:init
@REM Decide how to startup depending on the version of windows

@REM -- Windows NT with Novell Login
if "%OS%"=="WINNT" goto WinNTNovell

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

:WinNTNovell

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto endInit

@REM The 4NT Shell
:4NTArgs
set CMD_LINE_ARGS=%$
goto endInit

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto endInit
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

@REM Reaching here means variables are defined and arguments have been captured
:endInit
SET JAVA_EXE="%JAVA_HOME%\bin\java.exe"

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NT_TP_Jars

@REM -- Regular WinNT shell
for %%i in ("%TP_HOME%"\lib\tprofiler-*) do set TPROFILER_JAR="%%i"
goto run_tprofiler

@REM The 4NT Shell
:4NT_TP_Jars
for %%i in ("%TP_HOME%\lib\tprofiler-*") do set TPROFILER_JAR="%%i"
goto run_tprofiler

@REM Start TProfiler
:run_tprofiler
set MAIN_CLASS=%1
%JAVA_EXE% -classpath %TPROFILER_JAR% %MAIN_CLASS% %CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
if "%OS%"=="WINNT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set JAVA_EXE=
set CMD_LINE_ARGS=
goto postExec

:endNT
@endlocal & set ERROR_CODE=%ERROR_CODE%

:postExec

cmd /C exit /B %ERROR_CODE%

