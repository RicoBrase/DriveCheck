
::pushd ..

set current=%time%

mkdir %date:~-4,4%-%date:~-7,2%-%date:~-10,2%_%current:~0,2%-%current:~3,2%-%current:~6,2%

For /F %%i IN (SCRIPT\checkdrives.txt) DO (

    SCRIPT\psexec \\%%i | wmic diskdrive get status >> %date:~-4,4%-%date:~-7,2%-%date:~-10,2%_%current:~0,2%-%current:~3,2%-%current:~6,2%\%%i.txt

)

exit