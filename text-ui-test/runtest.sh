#!/usr/bin/env bash

# create bin directory if it doesn't exist
if [ ! -d "../bin" ]
then
    mkdir ../bin
fi

# delete output from previous run
if [ -e "./ACTUAL.TXT" ]
then
    rm ACTUAL.TXT
fi

# delete local data file from previous run
if [ -e "../data.local.json" ]
then
    rm ../data.local.json
fi

# run the program, feed commands from input.txt file and redirect the output to the ACTUAL.TXT
(cd .. && ./gradlew run -q < text-ui-test/input.txt > text-ui-test/ACTUAL.TXT)

# convert to UNIX format
cp EXPECTED.TXT EXPECTED-UNIX.TXT

# compare the output to the expected output
diff ACTUAL.TXT EXPECTED-UNIX.TXT
if [ $? -eq 0 ]
then
    echo "Test result: PASSED"
    exit 0
else
    echo "Test result: FAILED"
    exit 1
fi
