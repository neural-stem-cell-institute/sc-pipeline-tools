##
## makeMetaFromWellList.R
##
## To rearrange the WellList.TXT files for use as metadata
## For wafergen pipeline
## Initial Version: 10/28/2016
## Latest Version:10/28/2016
## Author(s): Tom Kiehl
##
## Usage: Rscript --vanilla makeMetaFromWellList.R [welllist file] [new metadata file] [new illumina sample sheet] 

## For testing in RStudio: 
## 

## Read in file from filename on command line
args = commandArgs(trailingOnly = TRUE) ## get only the arguments

# This line for testing in RSTUDIO
#args <- c("/Users/kiehlt/Downloads/chip_89547_WellList.TXT", "/Users/kiehlt/Downloads/newmetadata.txt")

## Read in the input
## TODO: make sure file exists
welllist <- read.table(args[1], sep='\t', header=TRUE)

## Rearrange columns for new metadata file
collist <- c("Barcode", "Sample", "Sample", "Sample", 
	"Global.drop.index", "Source.well", "Row", "Col", "Image1")
#metaout <- welllist[,c(6,5,5,5,23,24,1,2,26)]
metaout <- welllist[,collist]

## Rearrange columns for new illumina sample sheet
## sample_id, sample_name, sample_plate, sample_well, index_plate_well, i7_index_id, index, i5_index_id, index2, sample_project
#col1 <- gsub(" ", "-, paste(wellist[,"Sample"], wellist

## Output to file name
#write.table(metaout, file=args[2], sep='\t', quote=FALSE, row.names = FALSE, col.names = FALSE )
write.table(metaout, file=args[2], sep='\t', quote=FALSE, row.names = FALSE, col.names = FALSE )
