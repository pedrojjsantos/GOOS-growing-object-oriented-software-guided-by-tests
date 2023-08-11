#!/bin/bash
set -e

error_exit() {
  echo "$1" 1>&2
  exit 1
}

COOKIES="/tmp/openfire-cookies"

# login and store cookies
URL_EFFECTIVE=$(curl -sSL -o /dev/null -w '%{url_effective}' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -H 'Cookie: csrf=h6uG55ROkOIPbp8' \
  --data-raw 'url=%2Findex.jsp&login=true&csrf=h6uG55ROkOIPbp8&username=admin&password=admin' \
  -c "$COOKIES" 'http://localhost:9090/login.jsp') ||
  error_exit "Login failed"
if [[ "$URL_EFFECTIVE" == "http://localhost:9090/index.jsp" ]]; then
  echo 'Successful login.'
else
  error_exit "Login failed, curl was redirected to $URL_EFFECTIVE"
fi


CSRF=$(grep csrf $COOKIES | cut -f 7)
EXPECTED='Settings updated successfully.'

# resource policy
ACTUAL=$(curl -sS  \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  --data-raw "csrf=$CSRF&port=7777&hardcodedAddress=&proxyEnabled=false&update=Save+Settings" \
  -b "$COOKIES" 'http://localhost:9090/file-transfer-proxy.jsp' |
  grep "$EXPECTED" |
  awk '{$1=$1};1') ||
  error_exit "Updating file transfer proxy failed"
if [[ "$ACTUAL" == "$EXPECTED" ]]; then
  echo "File transfer proxy disabled"
else
  echo "> " "$ACTUAL"
  error_exit "Could not update file transfer proxy"
fi