package driver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import ndfs.NDFS;
import ndfs.NDFSFactory;

/**
 * This is the main program of the NDFS skeleton.
 */
public class Main {

    // Prevent accidental construction.
    private Main() {
        // nothing
    }

    private static void printUsage() {
        System.out.println("Usage: bin/ndfs <file> <version> <nrWorkers> <nrCycles>");
        System.out.println("  where");
        System.out.println("    <file> is a Promela file (.prom),");
        System.out.println(
                "    <version> is either \"seq\" or one of the mc versions (for instance, \"1_naive\"),");
        System.out.println(
                "    <nWorkers> indicates the number of worker threads.");
        System.out.println(
                "    <nrCycles> indicates the number of repetitions of the algorithm.");
    }

    private static void runNDFS(File promelaFile) throws FileNotFoundException {

        NDFS ndfs = NDFSFactory.createNNDFS(promelaFile);
        long start = System.currentTimeMillis();

        boolean result = ndfs.ndfs();
        long end = System.currentTimeMillis();
        System.out.println("Graph " + promelaFile.getName() + " does "
                + (result ? "" : "not ") + "contain an accepting cycle.");
        System.out.println("seq took " + (end - start) + " ms.");
    }

    private static float runMCNDFS(File promelaFile, String version,
            int nrWorkers) throws Exception {

        NDFS ndfs = NDFSFactory.createMCNDFS(promelaFile, nrWorkers, version);
        long start = System.currentTimeMillis();
        //Benchmark Start
        boolean result = ndfs.ndfs();
        long end = System.currentTimeMillis();
        //Benchmark End
        System.out.println("Graph " + promelaFile.getName() + " does "
                + (result ? "" : "not ") + "contain an accepting cycle.");
        System.out.println(version + " took " + (end - start) + " ms.");
        return end-start;


    }
    
    private static void Log_Data(File promelaFile, String version,
            int nrWorkers, float mean_time, int nrCycles)
    {
	    try {
			    //IF Directory don't exist create one -> Log_Data
			    File dir=new File(new java.io.File( "." ).getCanonicalPath()  + "/Log_Data/");
			    if(!dir.exists())
			    	dir.mkdir();

			    //File String Pre-Process according to pattern Filename_[T:][C:], EXTRACT Filename
			    String t_fileName = promelaFile.toString().substring(promelaFile.toString().lastIndexOf('/') + 1).replace(".prom", "");
                String fileName = t_fileName + "_[T:" + nrWorkers + "]"+"[C:" + nrCycles + "]";
			    
                String PATH = new java.io.File( "." ).getCanonicalPath() + "/Log_Data/" + fileName + ".csv";
                
                File tagFile=new File(PATH);
                
                if(!tagFile.exists()){
			    	tagFile.createNewFile();
			    }
                /*
                 * 
                 * CSV WRITER PRINT ORDER: dos.println("MCNDFS\tTime(ms)\t");
                 *
                 */
		    	FileWriter fos = new FileWriter(tagFile, true);
			    PrintWriter dos = new PrintWriter(fos, true);
			    dos.print(version+"\t");
				dos.print(mean_time);
				dos.println();	
	
				    
			    dos.close();
			    fos.close();
		 } catch (IOException e) {
			 e.printStackTrace();
	    }
    }
    
    
    private static void dispatch(File promelaFile, String version,
            int nrWorkers, int nrCycles)
            throws IllegalArgumentException, FileNotFoundException {
    		List<Float> measurement_array = new ArrayList<>(nrCycles);
    		
        if (version.equals("seq")) {
            if (nrWorkers != 1) {
                throw new IllegalArgumentException(
                        "seq can only run with 1 worker");
            }
            runNDFS(promelaFile);
        } else { //If multithreaded version, for each cycle/repetition call runMCNDFS and take back results(float)
            try {
            	IntStream.range(0, nrCycles).forEach(
            		cycle->
            		{
						try{
							measurement_array.add(runMCNDFS(promelaFile, version, nrWorkers));
						}
						catch (Exception e) {
							System.err.println("Unusual Exception After Execution of MCNDFS! Printing Log \n");
							e.printStackTrace();
							System.exit(-1);
						}
					}	
            	);
                //Store and accumulate the results
            	Float measurements = (float) 0;
            	for(Float measurement: measurement_array) {
            		measurements=measurement + measurements;
            	}
            	//Call Log_Data to 'stash' the parameters and MEAN_VALUE of measurements
            	Log_Data(promelaFile,  version,  nrWorkers,  measurements/(float)measurement_array.size(), nrCycles);
            	
            	
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Throwable e) {
                throw new Error("Could not run version " + version + ": " + e,
                        e);
            }
        }
    }

    /**
     * This is the <code>main</code> method of the NDFS skeleton. It takes three
     * arguments: <br>
     * - a filename of a file containing a Promela program; this describes the
     * graph. <br>
     * - a version string, see
     * {@link NDFSFactory#createMCNDFS(File, int, String)}, but it could also be
     * "seq", for the sequential version. <br>
     * - a number representing the number of worker threads to be used.
     *
     * @param argv
     *            the arguments.
     */
    public static void main(String[] argv) {
        try {//ADD 4TH ARGUMENT REPETITIONS/CYCLES
            if (argv.length != 4)
                throw new IllegalArgumentException("Wrong number of arguments");
            File file = new File(argv[0]);
            String version = argv[1];
            int nrWorkers = new Integer(argv[2]);
            int nrCycles = new Integer(argv[3]);

            dispatch(file, version, nrWorkers, nrCycles);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            printUsage();
        }
    }
}
