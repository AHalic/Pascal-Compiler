#!/bin/bash
IN=examples
OUT=./out

ANTLR_PATH=./lib/antlr-4.9.3-complete.jar
CLASS_PATH_OPTION="-cp .:$ANTLR_PATH"

rm -rf $OUT
mkdir $OUT

for infile in `ls $IN/c*.pas`; do
    base=$(basename $infile)
    outfile=$OUT/${base/.pas/.out}
    dotfile=$OUT/${base/.pas/.dot}
    pdffile=$OUT/${base/.pas/.pdf}
    echo Running $base
    # java $CLASS_PATH_OPTION:bin checkr/Main $infile 1> $outfile 2> $dotfilee
    java $CLASS_PATH_OPTION:bin Main $infile 1> $outfile
    # dot -Tpdf $dotfile -o $pdffile
done