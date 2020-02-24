#!/bin/sh

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

"${SCRIPT_DIR}/runner-base.sh" \
    --type "RWD" -f --tolerance 0.3965 \
    $@
