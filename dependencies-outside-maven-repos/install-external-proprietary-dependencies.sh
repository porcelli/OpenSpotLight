mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc5 -Dversion=11.1.0 -Dpackaging=jar -Dfile=ojdbc5.jar
mvn install:install-file -DgroupId=com.ibm -DartifactId=db2jcc -Dversion=9.7 -Dpackaging=jar -Dfile=db2jcc.jar
mvn install:install-file -DgroupId=com.ibm -DartifactId=db2jcc_license_cu -Dversion=9.7 -Dpackaging=jar -Dfile=db2jcc_license_cu.jar
