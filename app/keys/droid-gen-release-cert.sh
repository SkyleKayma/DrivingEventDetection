#!/bin/bash

if [ $# -lt 1 ]; then
    echo "Usage: $0 alias"
    exit 1
fi

keytool -genkey -v -keystore release.keystore -alias $1 -keyalg RSA -keysize 2048 -validity 10000