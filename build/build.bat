echo BUILD_NUMBER=%BUILD_NUMBER%>MAround\assets\build_number
echo MAROUND_CHANNEL=MAROUND_APP>MAround\assets\channel
echo BUILD_TIME=%date:~0,10% %time:~0,5%>>MAround\assets\channel
call ant -buildfile MAround\build.xml release
if %ERRORLEVEL% NEQ 0 GOTO Error

md MAround\release
copy MAround\bin\MAround-release.apk MAround\release\MAround_%BUILD_NUMBER%_Release.apk
copy MAround\bin\proguard\mapping.txt MAround\release\mapping_%BUILD_NUMBER%.txt

:Succeed
set RES=0
goto Final

:Error
set RES=1
goto Final

:Final
svn revert -R .
exit /b %RES%
