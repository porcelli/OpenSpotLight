export OSL_HOME=`pwd`
export MAVEN_OPTS="-Xmx500m -Xms500m" 
mvn clean install -fn -o ; for (( i=0; i < 10; i++ )); do for (( i=0; i < 5; i++ )); do printf '\a'; done; sleep 5; done;