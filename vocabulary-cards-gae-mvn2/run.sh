#!/bin/bash

ROOT="/mnt/d2/voc-cards-test-data/Merlin - season 1.en/"
export STR_FILENAME="$ROOT/Merlin - 1x08 - The Beginning Of The End.PDTV.AFFiNiTY.en.srt"

# export PATTERN="('ve)|(have)"
# export PATTERN="('ve been)"
# export PATTERN="('ve been)"
# export PATTERN="(either)"
# export PATTERN="(used to)"
export PATTERN="(would)|(wouldn't)"

# FIXME: faster!
#mvn test -Dtest=SpecWordsApp.select
mvn test -Dtest=SpecWordsApp
