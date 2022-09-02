REM
REM compile and package the project without running the tests
REM

chcp 65001
rem call mvn clean verify spring-boot:repackage -DskipTests -X
call mvn clean package -DskipTests
pause