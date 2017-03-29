#!/bin/bash

compiler=$1
code=$2
javaRunner=$3

if [ "$javaRunner" = "" ]; then
    $compiler /sharedDir/"$code" < $"/sharedDir/args"
else
	if $compiler /sharedDir/"$code"; then
	    $javaRunner < $"/sharedDir/args"
	else
	    echo "Compilation Failed"
	fi
fi
