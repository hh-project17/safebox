#!/bin/bash

compiler=$1
code=$2
javaHeapLim=$3
javaRunner=$4

if [ "$javaRunner" = "" ]; then
    $compiler /sharedDir/"$code" < $"/sharedDir/args"
else
	if $compiler /sharedDir/"$code"; then
	    $javaRunner $javaHeapLim < $"/sharedDir/args"
	else
	    echo "Compilation Error"
	fi
fi
