@echo off	

rem ----------------------------------------
rem TODO: Modify this constants before using
rem ----------------------------------------
set strFile=%1
set strSignCode=c:\CodeSigning\signcode.exe
set strSignCodePwd=C:\CodeSigning\signcode-pwd.exe
set strSpc=c:\CodeSigning\myCredentials.spc
set strPvk=c:\CodeSigning\myPrivateKey.pvk
set strPwd=c:\CodeSigning\myPassword.pwd
set strTimeStampUrl=http://timestamp.verisign.com/scripts/timstamp.dll

echo Start signcode-pwd.exe
%strSignCodePwd% -f %strPwd%

echo Execute signcode.exe
%strSignCode% %strFile% -spc %strSpc% -v %strPvk% -t %strTimeStampUrl%

if errorlevel 0 goto end_success

:end_error
echo Error occured while signing
%strSignCodePwd% -t
exit 1

:end_success
echo Singing was successfully
%strSignCodePwd% -t