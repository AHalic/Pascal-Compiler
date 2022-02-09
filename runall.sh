#!/bin/bash

YEAR=$(pwd | grep -o '20..-.')
# Ajeite essa variável como feito no Makefile
ROOT=/usr/local/lib
IN=examples
OUT=./out

ANTLR_PATH=/usr/local/lib/antlr-4.9-complete.jar
CLASS_PATH_OPTION="-cp .:$ANTLR_PATH"

rm -rf $OUT
mkdir $OUT
for infile in `ls $IN/c*.pas`; do
    base=$(basename $infile)
    outfile=$OUT/${base/.pas/.out}
    dotfile=$OUT/${base/.pas/.dot}
    pdffile=$OUT/${base/.pas/.pdf}
    echo Running $base
    java $CLASS_PATH_OPTION:bin checker/Main $infile 1> $outfile 2> $dotfile
    # java $CLASS_PATH_OPTION:bin checker/Main $infile 1> $outfile
    dot -Tpdf $dotfile -o $pdffile
done
