echo BUILD_NUMBER=%BUILD_NUMBER%>MAround\assets\build_number
echo MAROUND_CHANNEL=MAROUND_APP>MAround\assets\channel
::echo BUILD_TIME=%date:~0,10% %time:~0,5%>>MAround\assets\channel
call ant -buildfile MAround\build.xml production
if %ERRORLEVEL% NEQ 0 GOTO Error

md MAround\production
copy MAround\bin\MAround-release.apk MAround\production\360around_production_%BUILD_NUMBER%_%date:~0,4%_%date:~5,2%_%date:~8,2%__%time:~0,2%_%time:~3,2%.apk
copy MAround\bin\proguard\mapping.txt MAround\production\mapping_%BUILD_NUMBER%.txt

:Succeed
set RES=0
goto Final

:Error
set RES=1
goto Final

:Final
svn revert -R .
exit /b %RES%
