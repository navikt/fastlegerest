#!/bin/sh
export CLIENT_ID=$(cat /secrets/azuread/fastlegerest/client_id)
export CLIENT_SECRET=$(cat /secrets/azuread/fastlegerest/client_secret)

export NO_NAV_SECURITY_JWT_ISSUER_VEILEDERAAD_ACCEPTEDAUDIENCE=$(cat /secrets/azuread/fastlegerest/client_id)

export SRV_USERNAME=$(cat /secrets/serviceuser/fastlegerest/username)
export SRV_PASSWORD=$(cat /secrets/serviceuser/fastlegerest/password)
