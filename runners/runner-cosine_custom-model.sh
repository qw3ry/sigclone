#!/bin/sh

SCRIPT_DIR="$(dirname "$(readlink -f "$0")")"

"${SCRIPT_DIR}/runner-base.sh" \
    --tolerance 0.1 --type "COSINE_W2V" -f --model ${SCRIPT_DIR}/../../w2v-models/w2v-w5-l100 \
    $@
