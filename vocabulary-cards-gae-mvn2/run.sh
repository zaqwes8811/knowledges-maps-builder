#!/bin/bash

ROOT="/mnt/d2/voc-cards-test-data/Merlin - season 1.en/"
export STR_FILENAME="$ROOT/Merlin - 1x08 - The Beginning Of The End.PDTV.AFFiNiTY.en.srt"

# FIXME: need deep regex knowl.

# export PATTERN="('ve)|(have)"
# export PATTERN="('ve been)"
# export PATTERN="('ve been)"
# export PATTERN="(either)"
# export PATTERN="(used to)"
# export PATTERN="(would)|(wouldn't)"
#export PATTERN="(ed )"
export PATTERN="(would)|(wouldn't)"

# FIXME: faster!
#mvn test -Dtest=SpecWordsApp.select
mvn test -Dtest=SpecWordsApp

# /home/zaqwes/.m2/repository/org/fxmisc/richtext/richtextfx/0.9.3/richtextfx-0.9.3.jar
#  whereis java
 # https://stackoverflow.com/questions/51112367/where-is-located-jar-fxml-manager-option-in-javafx-scene-builder-1-1
