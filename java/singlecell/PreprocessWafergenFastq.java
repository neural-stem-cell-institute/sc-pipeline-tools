package singlecell;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.HashMap;

public class PreprocessWafergenFastq {

	/**
	 * 
	 * @param args First argument is the R1, tag sequence file
	 *             Second argument is R2, corresponding sequence file
	 *             Third argument is the output directory
	 *             Fourth argument is the metadata file
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		final int DEFAULT_BUFFER_SIZE=5096;
		// First, read in all the possible/interesting labels
		//BufferedReader ls ..metadata = new BufferedReader(new FileReader("/Users/kiehlt/Downloads/chip_89547.metadata"),DEFAULT_BUFFER_SIZE);
		BufferedReader metadata = new BufferedReader(new FileReader(args[3]),DEFAULT_BUFFER_SIZE);
		HashSet <String> labels = new HashSet<String>();
		String md;
		while((md = metadata.readLine())!=null){
			labels.add(md.substring(0,11)); //to get the first 11 characters
		}
		metadata.close();

		//Now dig through the reads to find labels that we are interested in.
		String prefix = args[2];
//		String prefix = "/Users/kiehlt/Documents/tmp/";
		int openfiles =0;
		//A hash of all the sequences and files
		HashMap<String, PrintWriter> readfiles = new HashMap<String, PrintWriter>();
		HashMap<String, PrintWriter> indexfiles = new HashMap<String, PrintWriter>();

		BufferedReader r1, r2;

		// Open R1
		if(args[0].endsWith(".gz"))
		{
			r1=new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[0]),DEFAULT_BUFFER_SIZE)),DEFAULT_BUFFER_SIZE);
		}
		else
		{
			r1=new BufferedReader(new FileReader(args[0]),DEFAULT_BUFFER_SIZE);
		}

		//Open R2
		if(args[1].endsWith(".gz"))
		{
			r2=new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(args[1]),DEFAULT_BUFFER_SIZE)),DEFAULT_BUFFER_SIZE);
		}
		else
		{
			r2=new BufferedReader(new FileReader(args[1]),DEFAULT_BUFFER_SIZE);
		}

		// Fastq entries are four lines, second is the sequence

		long nLine=-1L;
		String line1;
		while((line1=r1.readLine())!=null)
		{
			PrintWriter r1out, r2out;
			String line2 = r1.readLine();
			String seq = line2.substring(0, 11); //check the first 11 characters

			if(labels.contains(seq)){ // make sure the sequence contains a label we are interested in
				//Get a handle on the correct output files, 
				//  either previously 
				if(readfiles.containsKey(seq)){
					r1out = readfiles.get(seq);
				}else{
					FileWriter fw = new FileWriter(prefix + "R1_"+seq + ".fastq");
					BufferedWriter bw = new BufferedWriter(fw);
					r1out = new PrintWriter(bw);
					readfiles.put(seq, r1out);
					openfiles++;
					System.out.println(openfiles);
				}

				if(indexfiles.containsKey(seq)){
					r2out = indexfiles.get(seq);
				}else{
					FileWriter fw = new FileWriter(prefix + "R2_"+seq + ".fastq");
					BufferedWriter bw = new BufferedWriter(fw);
					r2out = new PrintWriter(bw);
					indexfiles.put(seq, r2out);
					openfiles++;
					System.out.println(openfiles);
				}
				
				if(openfiles > 6000){
					System.err.println("File limit, 6000 files, exceeded. You are an idiot.");
					System.exit(0);
				}
				r1out.println(line1);
				r1out.println(line2);
				r1out.println(r1.readLine());
				r1out.println(r1.readLine());
				r1out.flush();

				r2out.println(r2.readLine());
				r2out.println(r2.readLine());
				r2out.println(r2.readLine());
				r2out.println(r2.readLine());
				r2out.flush();
			}else{ // Just bypass these lines
				r1.readLine();
				r1.readLine();
				r2.readLine();
				r2.readLine();
				r2.readLine();
				r2.readLine();
			}
		}

		//Close all the files
		r1.close();
		r2.close();
		readfiles.forEach((s,f) -> {
			f.flush();
			f.close();
		});
		indexfiles.forEach((s,f) -> {
			f.flush();
			f.close();
		});

	}
}
