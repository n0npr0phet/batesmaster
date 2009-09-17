package com.batesmaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import static java.util.Arrays.*;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * main program class.
 * @author Mark Manoukian
 * @author Gregory Pruden
 * @version 0.1
 *
 * copyright 2009 Mark Manoukian and Gregory Pruden
 * 
 *
 */
public class Main {


	static OptionParser cmdline;
	static OptionSpec<Integer> seed;
	static OptionSpec<Integer> xoff;
	static OptionSpec<Integer> yoff;
	static OptionSpec<Integer> rotate;
	static OptionSpec<Integer> origin;
	/**
	 * command line interface
	 */
	public static void main(String[] args) 
	{
		//print header
		header();
		
		//create command line interface
        cmdlines();
		
        //parse command line
        OptionSet options = null;
        try
        {
        	options = cmdline.parse(args);
        }
        catch (joptsimple.OptionException ex)
        {
        	usage(ex.getMessage());
        	System.exit(1);
        }
        
        //check for usage request
        if (options.has("help")||options.has("?"))
        {
        	usage("Visit http://batesmaster.com for more info!\n",0);
        	System.exit(0);
        }
        
		//check input file
		if (!options.hasArgument("inpdf") && !options.hasArgument("dirin") && !options.hasArgument("textfile") )
		{
			//require input file
			usage("Invalid arguments. An input file, directory, or textfile is required.");
			System.exit(1);
		}
				
		//create instance of bater class
		batesStamper bater = new batesStamper();
		
		//set properties
		//set seed
		if (options.has(seed))
			bater.setSeed(seed.value(options));
		
		//set offsets
		if (options.has(xoff))
			bater.setOffsetx(xoff.value(options));
		if (options.has(yoff))
			bater.setOffsety(yoff.value(options));		
		//set format
		if (options.has("format"))
			bater.setFormat((String)options.valueOf("format"));
		
		//set rotation
		if (options.has("rotate"))
			bater.setRotation(rotate.value(options));
		
		//set origin
		if (options.has("location"))
			bater.setOrigin(origin.value(options));

		//set input file
		if (options.hasArgument("inpdf"))
		{
			bater.setInputFileName(options.valuesOf("inpdf").toString().substring(1, options.valuesOf("inpdf").toString().length()-1 ));
			if (! new File(bater.inputFileName).exists())
			{
				//check that the input file exists
				usage("Input file does not exist.");
				System.exit(1);
			}
			//set output file
			if (options.has("pdfout"))
			{
				
				bater.setOutputFileName(options.valuesOf("pdfout").toString().substring(1, options.valuesOf("pdfout").toString().length()-1 ));
			}
			else if (options.hasArgument("dirout"))
			{
				String odir = options.valuesOf("dirout").toString().substring(1, options.valuesOf("dirout").toString().length()-1 );
				File fodir = new File(odir);
				if (!fodir.exists())
				{
					usage("--dirout must be a valid path and exist.");
					System.exit(1);
				}
				bater.setOutputFileName(odir+File.separator+new File(bater.inputFileName).getName());

			}
			
			if (bater.outputFileName==bater.inputFileName)
			{
				usage("the input and output files cannot be the same.");
				System.exit(1);
			}
			
			//check that the output file doesn't exist
			if (new File(bater.outputFileName).exists() && !options.has("overwrite") )
			{
				usage("Output file exists. Please enter a different output file or use the --overwrite flag.");
				System.exit(1);
			}
			else
			{
				File outfile = new File(bater.outputFileName);
				if (outfile.exists() && options.has("overwrite"))
					outfile.delete();
			}
			//stamp away
			if (bater.ProcessDoc())
			{
				displayln("Document successfully bates mastered.");
				//done
				System.exit(0);
			}
			else
			{
				displayln("problems occured document may not be mastered.");
				//done
				System.exit(1);
			}
		}
		else if (options.hasArgument("dirin") )
		{
			String odir = "";
			// list of files
			if (options.hasArgument("dirout"))
			{
				//get output directory
				odir = options.valuesOf("dirout").toString().substring(1, options.valuesOf("dirout").toString().length()-1 );
			}
			if (options.hasArgument("dirin"))
			{
				//get input directory
				String sdir = options.valuesOf("dirin").toString().substring(1, options.valuesOf("dirin").toString().length()-1 );
				File dir = new File(sdir);
				if (!dir.exists())
				{
					usage("The --dirin directory path must exist.");
					System.exit(1);
				}
				
				stampfiles(bater, dir, new File(odir), options.has("recurse"),  options.has("overwrite"));

			}
			
			
			
		}
		else if (options.hasArgument("textfile"))
		{
			try {
		        BufferedReader in = new BufferedReader(new FileReader((String)options.valueOf("textfile")));
		        String str;
		        while ((str = in.readLine()) != null) {
		            File inf = new File( str  );
		            if (inf.exists())
		            {
		        		bater.inputFileName = inf.getAbsolutePath();
		        		
		    			if (options.hasArgument("dirout"))
		    			{
		    				//get output directory
		    				String odir = options.valuesOf("dirout").toString().substring(1, options.valuesOf("dirout").toString().length()-1 );
		    				File fodir = new File(odir);
		    				if (!fodir.exists())
		    				{
		    					usage("dirout must be a valid path and exist.");
		    				}
		    				
		    				bater.outputFileName = fodir.getAbsolutePath()+File.separator+inf.getName();
		    			}

		        		if (!bater.ProcessDoc())
		        		{
		        			displayln("PROBLEM: "+bater.inputFileName+" was not stamped. ");
		        		}
		        		else
		        		{
		        			if (options.has("verbose")) displayln("SUCCESS: "+bater.inputFileName+" stamped. ");
		        		}
		            }
		            else
		            	displayln("Error: "+bater.inputFileName+" does not exist!");
		            
		            //give something back to the community
		            Thread.yield();
		        }
		        in.close();
		    } catch (IOException e) {
		    	displayln("Error: "+e.getMessage());
		    }
		}

	}
	static boolean stampfiles(batesStamper bater, File dir, File odir, Boolean recurse, Boolean overwrite)
	{
		String[] contents;
		FilenameFilter filter;
		if (!recurse)
		{
		    filter = new FilenameFilter() {
		        public boolean accept(File dir, String name) {
		            return name.toLowerCase().endsWith(".pdf");
		        }
		    };  

			
		}
		else
		{
		    filter = new FilenameFilter() {
		        public boolean accept(File dir, String name) {
		            return !name.startsWith(".");
		        }
		    };  

		}
		contents = dir.list(filter);
		if (contents==null)
		{
			return false;
		}
		for (int i=0; i<contents.length; i++)
		{
			File path = new File(dir.getAbsolutePath()+File.separator+contents[i]);
			if (path.isFile()) 
			{
				if (!recurse)
				{
					stampFile(bater, path, odir, overwrite);
				}
				else if (path.getAbsolutePath().endsWith(".pdf"))
				{
					stampFile(bater, path, odir, overwrite);
				}
			}
			else if (path.isDirectory() && recurse) 
			{
				if (!stampfiles(bater, path, new File(odir.getAbsolutePath()+File.separator+dir.getName()), recurse, overwrite))  //recurse
				{
					return false;
				}
				
			}
			
			// stop and smell the roses...
			Thread.yield();
		}
		return true;
	}

	static boolean stampFile(batesStamper bater, File infile, File outfile, boolean overwrite)
	{
		bater.inputFileName = infile.getAbsolutePath();

		if (outfile!=null)
		{
			if (!outfile.exists())
			{
				if (outfile.isDirectory())
					try {
						outfile.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						usage(e.getMessage());
						return false;
					}
				
			}
			else if (outfile.isFile() && !overwrite)
			{
				return false;
			}
			bater.outputFileName = outfile.getAbsolutePath();
		}
		return bater.ProcessDoc();
	}

	static void usage(String string, int i) {
		if (i==0)
		{
			// just print without error
			displayln(string);
			usage();
		}
	}
    static void displayln(String string)
    {
    	//just print.
    	System.out.println(string);
		System.out.println();
    }
	/**
	 * print the usage statement
	 * @param err add an optional error.
	 */
	public static void usage(String err)
	{
		displayln("Error: "+err);

		usage();
	}
	/**
	 * print the usage statement.
	 */
	public static void usage()
	{

		System.out.println("usage:");
		try {
			cmdline.printHelpOn(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.exit(1);
		}

		
	}
	
	/**
	 * print the header statement.
	 */
	public static void header()
	{
		System.out.println("batesmaster - pdf bates stamper");
		System.out.println("--------------------------------------------------------");
		System.out.println("batesmaster is Open Source software licensed under GPLv3");		
		System.out.println("so far written by Mark Manoukian and Gregory Pruden");
		System.out.println("");
		
	}
	/**
	 * setup the command line options
	 */
	public static void cmdlines()
	{
		//define all program options for command line interface
		cmdline = new OptionParser(); 
		cmdline.acceptsAll(asList("overwrite"),"output file name for bates stamped pdf");		
		cmdline.acceptsAll(asList("help", "?"),"output file name for bates stamped pdf");

		seed = cmdline.acceptsAll(asList("seed","startnum"),"set begining bates number, default: 1").withRequiredArg().ofType( Integer.class ).describedAs("new number");
		xoff = cmdline.acceptsAll(asList("xoffset"),"offset location from left of page, default: 10").withRequiredArg().ofType( Integer.class ).describedAs("offset in pixels");
		yoff = cmdline.acceptsAll(asList("yoffset"),"offset location from bottom of page, default: 10").withRequiredArg().ofType( Integer.class ).describedAs("offset in pixels");
		rotate = cmdline.acceptsAll(asList("rotate"),"rotation of the number on the page, default: 0").withRequiredArg().ofType( Integer.class ).describedAs("rotation in degrees");
		origin = cmdline.acceptsAll(asList("location"),"location of the stamp on the page, default: 0").withRequiredArg().ofType( Integer.class ).describedAs("location number");
		cmdline.acceptsAll(asList("format"),"advanced java style format string for bastes number, default: %05d ").withRequiredArg().ofType( String.class ).describedAs("format string");		
		cmdline.acceptsAll(asList("inpdf"),"REQUIRED pdf file to add bates stamps").withRequiredArg().ofType( String.class ).describedAs("file name");	
		cmdline.acceptsAll(asList("pdfout"),"output file name for bates stamped pdf").withRequiredArg().ofType( String.class ).describedAs("file name");
		cmdline.acceptsAll(asList("textfile"),"a text file list of paths to pdf files to stamp.").withRequiredArg().ofType( String.class ).describedAs("file name");	
		cmdline.acceptsAll(asList("dirin"),"a directory to look for pdf files in to stamp.").withRequiredArg().ofType( String.class ).describedAs("path name");	
		cmdline.acceptsAll(asList("dirout"),"the output directory for stamped files.").withRequiredArg().ofType( String.class ).describedAs("path name");	
		cmdline.acceptsAll(asList("recurse"),"include subdirectories in directory stamping.");		
		cmdline.acceptsAll(asList("verbose"),"output more information than normal.");		

	}

}
