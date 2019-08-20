# sc-pipeline
Our internal single cell pipeline

Information/Data/Input required:
   1) Raw Illumina read data (BCL Files)
   2) Well list file from ICell8 machine
   
Software required:
   1) Bcl2fastq utility from Illumina
   2) makeMetaFromWellList.R script  (R)
   3) singlecell.PreprocessWafergenFastq (java)
   4) singlecell.GroupFiles (java)
   5) nsci-align.sh (shell script)
   6) nsci-sort-samtobam.sh (shell script)
   7) load_and_count_reads_from_bams.R (R)
   
   
The Process   
   
1) Convert new bcl files to fastq
```  
   $ mkdir ./Data/fastq . (whereever you want it)
   $ cd (directory of unpacked data from illumina)`
   $ ulimit -n 4000   ## sets the limit on number of open files
   NOTE: Add bcl2fastq to path 
   $ PATH=$PATH:/usr/local/bcl2fastq2-v2.19.1/bin; export PATH
   $ bcl2fastq --minimum-trimmed-read-length 21 --mask-short-adapter-reads 0 --input-dir ./Data/Intensities/BaseCalls --output-dir ./Data/fastq &> bcl2fastq.out.txt &

   NOTE: For full length, the sample sheet must be included
   $ /usr/local/bcl2fastq-v2.19.1/bin/bcl2fastq --mask-short-adapter-reads 0 --input-dir ./Data/Intensities/BaseCalls --output-dir ./Data/fastq --sample-sheet ./Data/SampleSheet-meninges.csv
``` 
2) Merge the fastq files for Read 1 and Read 2
```
   $ cat *R1* output-file-name-etc_R1.fastq.gz
   $ cat *R2* output-file-name-etc_R1.fastq.gz
```
3) Create the metadata file from well list file
```
   $ Rscript --vanilla /data/new-pipeline/makeMetaFromWellList.R 105152-meninges-and-others_WellList.TXT 105152.metadata
```
4)  Split fastq files based on barcode. PreprocessWafergenFastq takes four arguments:
                      R1, the tag sequence fastq file
                      R2, the sequncing fastq
                      output directory (with trailing slash)
                      metadata file (to doublecheck barcode sequences)Split combined fastq on barcodes
                      
```
   $ cd /data/new-pipeline/java
   $ export SCWORKING=/data/project/processing/fastq/directory
   $ java singlecell.PreprocessWafergenFastq $SCWORKING/aug2018-meninges-etc_R1.fastq.gz $SCWORKING/aug2018-meninges-etc_R2.fastq.gz $SCWORKING/output/ $SCWORKING/105152.metadata > $SCWORKING/logs/105152-stdout.txt 2> $SCWORKING/logs/105152-stderr.txt &
```
5) Rename fastq files to associate wells with an experimental condition or sample.  GroupFiles takes two arguments:
    * The directory to process (without trailing slash)
    * metadata file
```
   $ java singlecell.GroupFiles /data/aug2017-sci/day3/processing /data/aug2017-sci/wafergen/89500-day-03-SCI/89500.metadata > /data/aug2017-sci/day3/processing/group-stdout.txt 2> /data/aug2017-sci/day3/processing/group-stderr.txt &
```
5) Map files with nsci-align-paired.sh script (script updated to handle prefixes to identify a subset of files to process, also updated from nsci-align.sh to handle paired sequencing see attached) <mapping started 10/02/2018@16:26>
```
   $ export SCWORKING=/data/aug2018-meninges/processing/fl-take2
   $ /data/new-pipeline/nsci-align.sh $SCWORKING 106711 18A 18P 3A 3P > $SCWORKING/logs/nsci-align.stdout.txt 2> $SCWORKING/logs/nsci-align.stderr.txt
```
6) Convert sam file to bam files for import into R
```
   $ cd $SCWORKING/output
   $ /data/new-pipeline/nsci-sort-samtobam.sh > $SCWORKING/logs/sam2bam.stdout.txt 2> $SCWORKING/logs/sam2bam.stderr.txt &
```
7) Count the reads from all bam files, save RData file
```
   $ Rscript load_and_count_reads_from_bams.R /data/aug2018-meninges/processing/fl-take2/output/3A  /data/genomes/wafergen/Mus_musculus.GRCm38.75.gtf /data/aug2018-meninges/processing/fl-take2/meninges3A.RData meninges3A
   $ Rscript load_and_count_reads_from_bams.R /data/aug2018-meninges/processing/fl-take2/output/3P  /data/genomes/wafergen/Mus_musculus.GRCm38.75.gtf /data/aug2018-meninges/processing/fl-take2/meninges3P.RData meninges3P
   $ Rscript load_and_count_reads_from_bams.R /data/aug2018-meninges/processing/fl-take2/output/18A  /data/genomes/wafergen/Mus_musculus.GRCm38.75.gtf /data/aug2018-meninges/processing/fl-take2/meninges18A.RData meninges18A
   $ Rscript load_and_count_reads_from_bams.R /data/aug2018-meninges/processing/fl-take2/output/18P  /data/genomes/wafergen/Mus_musculus.GRCm38.75.gtf /data/aug2018-meninges/processing/fl-take2/meninges18P.RData meninges18P
```
