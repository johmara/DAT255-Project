#! /bin/bash

if ! pgrep -x "rmiregistry" > /dev/null
then
    rmiregistry &
fi
java -cp "jna-4.5.0.jar:." absolut.acc.Main