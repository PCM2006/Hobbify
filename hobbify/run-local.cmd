@echo off
setlocal
pushd "%~dp0" || exit /b 1

call ".\mvnw.cmd" spring-boot:run
set "HOBBIFY_RUN_EXIT_CODE=%ERRORLEVEL%"
popd
exit /b %HOBBIFY_RUN_EXIT_CODE%
