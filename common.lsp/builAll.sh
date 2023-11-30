# build the common JDT extension
cd common.jdt && mvn clean; mvn install && cd ..

# build the common LS
cd common.ls && mvn clean; mvn install && cd ..
