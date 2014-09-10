@echo off
echo -------------
if (%1)==(0) goto err
call D:/dev/android/sdk/build-tools/18.1.1/dx --dex --output="d:/download/%1.jar" d:/download/t%1.jar
echo -----jar built.
java -jar j2d.jar %1
echo -----dat built.
if errorlevel 1 goto err
goto :end
:err
echo -----error!
:end
echo -----end.
