package singlecell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class GroupFiles {
/**
 * 
 * @param args
 * 	args[0] is the directory we are working with
 *  args[1] is the file that contains an index (metadata file)
 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//Read the index file by row
		final int DEFAULT_BUFFER_SIZE=5096;
		// First, read in all the possible/interesting labels
		BufferedReader metadata = null;
		try {
			metadata = new BufferedReader(new FileReader(args[1]),DEFAULT_BUFFER_SIZE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		//get the sequence
		String md;
		try {
			while((md = metadata.readLine())!=null){
				String[] cols = md.split("\\s+");
				//First column is sequence
				//Fourth column is sample name
				//TODO: we should deal with spaces in the sample name
			        String[] samplename = cols[3].split(" ");	
				String oldfilename = args[0] + "/R2_"+cols[0] +".fastq";
				String newfilename = args[0] + "/" + samplename[0] + "_R2_" + cols[0] + ".fastq";
				//Path filetomove = Files.createFile(Paths.get(oldfilename));
				//Path targetpath = Paths.get(newfilename);
				//Files.move(filetomove, targetpath, java.nio.file.attribute.);
				
				
				System.out.println("mv " + oldfilename + " " + newfilename);
				//move the file named R2_SEQUENCE.fastq to
				File moveme = new File(oldfilename);
				boolean moved = moveme.renameTo(new File(newfilename));
				if(!moved){
					System.err.println("Couldn't move: "+ oldfilename + " to " + 
							newfilename);
				
				}
				
				
				oldfilename = args[0] + "/R1_"+cols[0] +".fastq";
				newfilename = args[0] + "/" + cols[3] + "_R1_" + cols[0] + ".fastq";
				//Path filetomove = Files.createFile(Paths.get(oldfilename));
				//Path targetpath = Paths.get(newfilename);
				//Files.move(filetomove, targetpath, java.nio.file.attribute.);
				
				
				System.out.println("mv " + oldfilename + " " + newfilename);
				//move the file named R2_SEQUENCE.fastq to
				moveme = new File(oldfilename);
				moved = moveme.renameTo(new File(newfilename));
				if(!moved){
					System.err.println("Couldn't move: "+ oldfilename + " to " + 
							newfilename);
				
				}
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			metadata.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//get the sample type
		
		//find the file for that sequence
		
		//move or rename
		
	}

}
