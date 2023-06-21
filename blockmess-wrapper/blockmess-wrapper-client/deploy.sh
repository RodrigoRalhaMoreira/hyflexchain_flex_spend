#! /bin/bash

./build.sh

mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file  \
    -Dfile=./target/Blockmess-Wrapper-Client-1.0-SNAPSHOT-jar-with-dependencies.jar \
    -DgroupId=pt.unl.fct.di.blockmess.wrapper -DartifactId=Blockmess-Wrapper-Client \
    -Dversion=1.0-SNAPSHOT -Dpackaging=jar \
    -DlocalRepositoryPath=../../dependencies/
