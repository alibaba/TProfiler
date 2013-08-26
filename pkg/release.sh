#!/bin/sh

rm -rf ../dist/*
mvn -f ../pom.xml clean package
cp ../COPYING.txt ../README TProfiler
cp ../src/main/resources/profile.properties TProfiler
cd TProfiler
zip -r ../../dist/TProfiler_1.0.1.zip *
cd ..

