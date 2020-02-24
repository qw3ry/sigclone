#!/bin/sh

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

"${SCRIPT_DIR}/runner-base.sh" \
    --tolerance 7.2 --type "EUCLID_W2V" -f --model ${SCRIPT_DIR}/../../w2v-models/w2v-w5-l100 \
    $@
