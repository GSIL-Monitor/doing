echo BUILD_NUMBER=%BUILD_NUMBER%>MAround\assets\build_number
echo MAROUND_CHANNEL=MAROUND_APP>MAround\assets\channel
echo BUILD_TIME=%date:~0,10% %time:~0,5%>>MAround\assets\channel
rd MAround\\bin /s /q 

call ant -buildfile MAround\build.xml securitycheck
if %ERRORLEVEL% NEQ 0 GOTO Error

md MAround\securitycheck
copy MAround\bin\MAround-debug.apk MAround\securitycheck\360around_securitycheck_%BUILD_NUMBER%_%date:~0,4%_%date:~5,2%_%date:~8,2%__%time:~0,2%_%time:~3,2%.apk

:Succeed
set RES=0
goto Final

:Error
set RES=1
goto Final

:Final
::svn revert -R .
exit /b %RES%
