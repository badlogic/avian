#!/bin/sh

cd ../
#make platform=windows arch=i386 clean
make platform=windows arch=i386
cd test
javac -bootclasspath ../build/windows-i386/classpath.jar Buffers.java
../build/windows-i386/avian Buffers
rm *.class