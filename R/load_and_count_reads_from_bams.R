# load_and_ount_reads_from_bams.R
#  Usage:
#  Rscript load_and_count_reads_from_bams.R /path/to/bam/dir /path/to/gtf /path/to/output/files summaryVariableName
#  Date: 10/24/2017
#  Author: Tom Kiehl, tomkiehl@neuralsci.org
#
#  Double checking file name patterns for each day ...
#       SCI_R2_SEQUENCEAligned.out.bam
#  Sample types:
#       >Uninjured Control: uninjured control, Neg Ctrl, Pos Ctrl 
#       >Day 3 levels: Day 3 SCI, Neg Ctrl, Pos Ctrl
#       >Day 7 levels: day 7 SCI, Neg Ctrl, Pos Ctrl
#       >Day 60 levels: day 60 SCI, Neg Ctrl, Pos Ctrl
library("Rsamtools")
library("GenomicFeatures")
library("GenomicAlignments")

args = commandArgs(trailingOnly=TRUE)
bamDir = args[1]
gtfFile = args[2]
outFile = args[3]
summaryVariableName= args[4]

fileNames <- Sys.glob(file.path(bamDir, "*.bam"))

#load the bam files in the given path
bamFiles <- Rsamtools::BamFileList(fileNames, yieldSize=2000000)

#output seq infor for one file for visual verification
Rsamtools::seqinfo(bamFiles[1])


# Use Genomic Features
gtffile <- file.path(gtfFile)
txdb <- GenomicFeatures::makeTxDbFromGFF(gtffile, format="gtf")

genes <- GenomicFeatures::exonsBy(txdb, by="gene")

se <- GenomicAlignments::summarizeOverlaps(features=genes,
                        mode="IntersectionNotEmpty",
	                reads=bamFiles,
                        singleEnd=TRUE,
                        ignore.strand=FALSE)

# rename working variable for output
print(summaryVariableName)
assign(summaryVariableName, se)
rm(se)
#save this data
save(list=summaryVariableName, file=outFile)







