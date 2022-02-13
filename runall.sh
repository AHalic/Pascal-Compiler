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
    pngfile=$OUT/${base/.pas/.png}
    echo Running $base
    java $CLASS_PATH_OPTION:bin Main $infile 1> $outfile 2> $dotfile
    dot -Tpng $dotfile -o $pngfile
done