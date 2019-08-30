#!/bin/bash

# This script maps the files in a directory
#   *** This currently only maps to mouse ***
#   USAGE:
#     nsci-align.sh /directory/with/fastqs 123455 X1 X2 X3 ...
#
#   Arguments:
#       1) directory path to fastq files to be processed
#       2) chip id. no spaces. this will be used to annotate final output
#       3...n) optionally provide a prefix. Only files matching these prefixes will be processed
#
#   Output:
#
#
GENOMEDIR="/data/genomes/STAR/mm10/"
NTHREADS=30
PROCESSDIR=$1
CHIPID=$2
PREFIX=$3
FILES=()
TMPFILES=()

if [ "$#" -lt 2 ]; then
	echo "Insufficent number of arguments."
	echo "USAGE: nsci-align.sh /directory/with/fastqs chipid prefix1 prefix2 ... prefixx"
exit
fi

cd $PROCESSDIR 

# for each prefix, list the files and add to list
#If there are only two arguments then there is no prefix
#   and we are processing all the files in a particular directory
if [ "$#" -eq 2 ]; then
	FILES=$PROCESSDIR/*R2*.fastq
else
        #Shifting allows us to ignore the first argument sent to the script
	shift
	shift
	echo "More parameters to process"
	while test ${#} -gt 0
		do
  		echo $1
		TMPFILES=$PROCESSDIR/$1*.fastq
		TMP2=("${TMPFILES[@]}" "${FILES[@]}" )
		FILES=("${TMP2[@]}")	
		#FILES+=("${TMPFILES[@]}")
  		shift
	done
fi

#FILES=("${FILES[@]}" $PROCESSDIR/*AATT.fastq)

echo "Preparing to process files."
for i in "${FILES[@]}" 
do
	for f in $i
	do
  		echo "Aligning $f with STAR aligner ..."
  		SHORT=$(basename "$f")
		/opt/STAR/bin/Linux_x86_64/STAR --genomeDir $GENOMEDIR --runThreadN $NTHREADS --readFilesIn $f --outFileNamePrefix $CHIPID_"${SHORT%.*}" 
		#  /opt/STAR/bin/Linux_x86_64/STAR --genomeDir /data/genomes/STAR/mm10/ --runThreadN 30 --readFilesIn $f --outFileNamePrefix $CHIPID_"${SHORT%.*}" 
	
	done
done
