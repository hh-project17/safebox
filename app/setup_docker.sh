#!/usr/bin/env bash

apt-get update
apt-get install -y docker.io
chmod 777 /var/run/docker.sock
echo "Docker installed"

chmod 777 src/main/resources/scripts/*

docker build -t sandbox src/main/resources/docker
echo "Image sandbox build"
