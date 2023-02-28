#!/bin/sh

# Script waits for keycloak to be reachable by an external url. After that, this scipt starts testdata
# population via its REST API

function log {
     echo "$(date) ########## $1 "
}

KEYCLOAK_BIND_ADRESS=$(tail -1 /etc/hosts | cut -f1)
REST_API_BASE_URL="http://$KEYCLOAK_BIND_ADRESS:8080"
KEYCLOAK_READINESS_CHECK_URL="$REST_API_BASE_URL/auth/"
log "Waiting for keycloak to start to populate test data. Wait for $KEYCLOAK_READINESS_CHECK_URL to return Status 200 ..."

#curl is not available in official keycloak 21 images anymore
#while [[ "$(curl -s -o /dev/null -w ''%{http_code}'' $KEYCLOAK_READINESS_CHECK_URL)" != "200" ]]; do sleep 20; done
sleep 60

log "Keycloak has hopefully been started, start data population against url $REST_API_BASE_URL ... "
java -jar /opt/keycloak/data-setup.jar --user="$KEYCLOAK_ADMIN" --password="$KEYCLOAK_ADMIN_PASSWORD" --restApiBaseUrl="$REST_API_BASE_URL"
log "Finished data population"


