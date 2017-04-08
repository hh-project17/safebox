#!/bin/bash
#http://stackoverflow.com/a/8870863

heapLim=$1

cd /sharedDir/ || exit

for classfile in *.class; do
    classname=${classfile%.*}

    if javap -public "$classname" | grep -Fq 'public static void main('; then
        java -Xmx"$heapLim"m "$classname" "$@"
        exit 0;
    fi
done
