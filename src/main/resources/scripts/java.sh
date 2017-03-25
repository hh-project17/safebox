#!/usr/bin/env sh

#	http://stackoverflow.com/a/8870863

cd /sharedDir/ || exit

for classfile in *.class; do
    classname=${classfile%.*}

    if javap -public "$classname" | grep -Fq 'public static void main('; then
        java "$classname" "$@"
        exit 0;
    fi
done
