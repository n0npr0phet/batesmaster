package com.batesmaster;

import java.io.File;
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
 */
public class Main {


	static OptionParser cmdline;
	static OptionSpec<Integer> seed;
	static OptionSpec<Integer> xoff;
	static OptionSpec<Integer> yoff;
	
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
        
		//check input file
		if (!options.has("inpdf") || !options.hasArgument("inpdf"))
		{
			//require input file
			usage("Invalid arguments. An input file is required.");
			System.exit(1);
		}
				
		//create instance of bater class
		batesStamper bater = new batesStamper();
		
		//set properties
		
		//set input file
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
		
		//check that the output file doesn't exist
		if (new File(bater.outputFileName).exists() && !options.has("overwrite") )
		{
			usage("Output file exists. Please enter a different output file.");
			System.exit(1);
		}
		else
		{
			File outfile = new File(bater.outputFileName);
			if (outfile.exists() && options.has("overwrite"))
				outfile.delete();
		}
			
		
		//set seed
		if (options.has(seed))
			bater.setSeed(seed.value(options));
		
		//set offsets
		if (options.has(xoff))
			bater.setSeed(xoff.value(options));
		if (options.has(yoff))
			bater.setSeed(yoff.value(options));			
		//set format
		if (options.has("format"))
			bater.setFormat((String)options.valueOf("format"));
		
		//stamp away
		if (bater.ProcessDoc())
		{
			System.out.println("Document successfully mastered.");
			//done
			System.exit(0);
		}
		else
		{
			System.out.println("problems occured document may not be mastered.");
			//done
			System.exit(0);
		}
	}
	
	/**
	 * print the usage statement
	 * @param err add an optional error.
	 */
	public static void usage(String err)
	{
		System.out.println("Error: "+err);
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
			e.printStackTrace();
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
		cmdline.acceptsAll(asList("format"),"advanced java style format string for bastes number, default: %05d ").withRequiredArg().ofType( String.class ).describedAs("format string");		
		cmdline.acceptsAll(asList("inpdf"),"REQUIRED pdf file to add bates stamps").withRequiredArg().ofType( String.class ).describedAs("file name");	
		cmdline.acceptsAll(asList("pdfout"),"output file name for bates stamped pdf").withRequiredArg().ofType( String.class ).describedAs("file name");
	}

}
