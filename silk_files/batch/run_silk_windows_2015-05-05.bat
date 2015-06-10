@Echo OFF
CD "%~dp0
ECHO Launch dir: "%~dp0"
SET TEST=km4c-InfoDoc_M-wgs84_COORD_B1
ECHO TEST: "%TEST%"
SET SILKPATH="%~dp0silk_2.6.0
ECHO TEST: "%SILKPATH%"
java -Xmx6G -Dlog4j.configuration=file:C:\Users\Marco\Documents\GitHub\EAT\silk_files\batch\silk_2.6.0\log4j.properties -DconfigFile="%CD%\%TEST%\SLS_"%TEST%".xml" -DlinkSpec="interlink_location" -DlogQueries=true -Dreload=true -jar %SILKPATH%\commandline\silk.jar
PAUSE&EXIT