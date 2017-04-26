#!/usr/bin/env bash

#deb-package installation
#note: will be something like 'apt-get bla-bla'
#but now...
echo "Installing deb package"

if dpkg -i deb/target/safebox.deb ; then
    echo "Package installed"
else
    apt-get -fy install
    dpkg -i deb/target/safebox.deb
    echo "Package installed"
fi

echo "Pulling docker image"
docker pull vorobey92/sandbox
