#! /bin/bash

javac -cp "jna-4.5.0.jar:." -sourcepath . absolut/can/*.java
javac absolut/rmi/*.java
javac -cp "jna-4.5.0.jar:." -sourcepath . absolut/acc/*.java

./run.sh