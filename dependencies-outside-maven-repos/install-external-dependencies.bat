echo "Installing OSL external dependencies"

call mvn install:install-file -DgroupId=cajo -DartifactId=cajo -Dversion=1.127 -Dpackaging=jar -Dfile=cajo.jar

call mvn install:install-file -DgroupId=org.clapper -DartifactId=ocutil -Dversion=2.4.4b -Dpackaging=jar -Dfile=ocutil-2.4.4b.jar

call mvn install:install-file -DgroupId=dynamo -DartifactId=dynamo-file-gen -Dversion=1.0.1 -Dpackaging=jar -Dfile=dynamo-file-gen-1.0.1.jar

call mvn install:install-file -DgroupId=org.jboss.identity.idm -DartifactId=idm-api -Dversion=1.0.0-Beta4-SNAPSHOT -Dpackaging=jar -Dfile=idm-api-1.0.0-Beta4-SNAPSHOT.jar

call mvn install:install-file -DgroupId=org.jboss.identity.idm -DartifactId=idm-common -Dversion=1.0.0-Beta4-SNAPSHOT -Dpackaging=jar -Dfile=idm-common-1.0.0-Beta4-SNAPSHOT.jar

call mvn install:install-file -DgroupId=org.jboss.identity.idm -DartifactId=idm-testsuite -Dversion=1.0.0-Beta4-SNAPSHOT -Dpackaging=jar -Dfile=idm-testsuite-1.0.0-Beta4-SNAPSHOT.jar

call mvn install:install-file -DgroupId=org.jboss.identity.idm -DartifactId=idm-spi -Dversion=1.0.0-Beta4-SNAPSHOT -Dpackaging=jar -Dfile=idm-spi-1.0.0-Beta4-SNAPSHOT.jar

call mvn install:install-file -DgroupId=org.jboss.identity.idm -DartifactId=idm-ldap -Dversion=1.0.0-Beta4-SNAPSHOT -Dpackaging=jar -Dfile=idm-ldap-1.0.0-Beta4-SNAPSHOT.jar

call mvn install:install-file -DgroupId=org.jboss.identity.idm -DartifactId=idm-hibernate -Dversion=1.0.0-Beta4-SNAPSHOT -Dpackaging=jar -Dfile=idm-hibernate-1.0.0-Beta4-SNAPSHOT.jar

call mvn install:install-file -DgroupId=org.jboss.identity.idm -DartifactId=idm-core -Dversion=1.0.0-Beta4-SNAPSHOT -Dpackaging=jar -Dfile=idm-core-1.0.0-Beta4-SNAPSHOT.jar

call mvn install:install-file -DgroupId=org.jboss.identity.idm -DartifactId=idm-core -Dversion=1.0.0-Beta4-SNAPSHOT -Dclassifier=tests -Dpackaging=jar -Dfile=idm-core-1.0.0-Beta4-SNAPSHOT-tests.jar

call mvn install:install-file -DgroupId=jredis -DartifactId=jredis -Dversion=1.0-rc1-java5 -Dpackaging=jar -Dfile=jredis-core-all-a.0-SNAPSHOT-jar-with-dependencies.jar
