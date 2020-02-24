#!/bin/sh

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

"${SCRIPT_DIR}/runner-base.sh" \
    --type "COSINE_W2V" -f \
    $@
