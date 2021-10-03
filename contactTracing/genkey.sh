#!/bin/bash

set -eu

if [ "$#" -lt 1 ]; then
    echo "Usage: $0 <id> [keystore]"
    exit 1
fi

STOREPASS="globalpass"

KEY_ID=$1
KEYSTORE=${2:-"global.keystore.jks"}
KEY_PASS="${KEY_ID}pass"

# We must use the JKS store format in order to have
# passwords for private keys different than the keystore
# password. PKCS12, the default format and industry standard,
# demands that these two passwords be the same.

keytool -genkeypair -keystore $KEYSTORE -alias $KEY_ID \
        -storetype jks \
        -keypass $KEY_PASS -storepass $STOREPASS \
        -keyalg RSA -validity 365 -keysize 2048 \
        -dname "CN=$KEY_ID,OU=SEC_G01,O=IST,L=Lisbon,ST=Lisbon,C=PT"
