#!/bin/bash
# Note, this will process N files at a time in parallel
FILES=./*.sam

N=20
(
((i = 0))
((j = 0))
for f in $FILES
do
  #((i=i%N)); ((i++=0)) && wait
  #test with this line
  echo "${f%.*}".bam 
  samtools view -bS $f | samtools sort -o "${f%.*}".bam &
  ((i++))
  ((i=i%N))
  ((j++))
  if((i == 0)); then
       echo wait $j
  fi
done
)
