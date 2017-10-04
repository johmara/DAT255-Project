#! /bin/bash

javac -cp "/home/pi/can-utils/absolut/jna-4.5.0.jar" -sourcepath . absolut/can/*.java
javac -cp "/home/pi/can-utils/absolut/jna-4.5.0.jar" -sourcepath . absolut/acc/*.java
java -cp "jna-4.5.0.jar:." absolut.acc.Main
