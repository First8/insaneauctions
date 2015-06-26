@echo off

:: Maven needs the JAVA_HOME set
set JAVA_HOME=c:\Program Files\Java\jdk1.8.0_45\

:: Set path to maven, including a trailing ''
set PATH_TO_MAVEN="C:\Users\Piet\bin\apache-maven-3.3.3\bin\"

echo Let us build
call %PATH_TO_MAVEN%mvn clean install

echo We have error %ERRORLEVEL%

IF %ERRORLEVEL% GTR 0 (
	echo Build failed
) else (
	echo Let us deploy to nodeOne
	%PATH_TO_MAVEN%mvn wildfly:undeploy -Dwildfly.port=10190
	%PATH_TO_MAVEN%mvn wildfly:deploy -Dwildfly.port=10190
)
