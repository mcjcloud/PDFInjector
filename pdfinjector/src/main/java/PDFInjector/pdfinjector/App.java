package PDFInjector.pdfinjector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
//import com.codesnippets4all.*;

/**
 * App entry
 * check parameters passed to application, if everything is good, process params
 *
 * @author mcjcloud
 */
public class App 
{
	// PROPERTIES
	private static HashMap<String, Boolean> flags;		// keeps track of which flags are set
	private static HashMap<String, String> parameters;	// keeps track of the parameters for the set flags
	
	/**
	 * main method, program entry
	 * Commands:
	 * -d data either in json format or a file path
	 * -i input PDF document to me injected
	 * -o output path for injected document
	 * -p don't use data, just put placeholders in every input, and create a txt file with the variable names
	 * -h print commands
	 * 
	 * @param args
	 */
    public static void main(String[] input) 
    {
    	// initialize the flags dictionary
        flags = new HashMap<String, Boolean>();
        flags.put("d", false);
        flags.put("i", false);
        flags.put("o", false);
        flags.put("p", false);
        flags.put("h", false);
        // init the parameters hashmap
        parameters = new HashMap<String, String>();
        
        // format the program input
        String[] args = {};
        try
        {
        	args = formatArgs(input);
        } 
        catch(Exception e)
        {
        	// invalid quotes
        	System.out.println("Parameters missing closing quotation mark.");
        	System.exit(1);
        }
        System.out.println(Arrays.toString(args));
        
        // loop through the arguments, setting the flags
        for(int i = 1; i < args.length; i++) 
        {
        	// try to populate the parameters
        	try
        	{
	        	switch(args[i]) 
	        	{
	        	case "-d":
	        		flags.put("d", true);
	        		parameters.put("d", args[i + 1]);
	        		break;
	        	case "-i":
	        		flags.put("i", true);
	        		parameters.put("i", args[i + 1]);
	        		break;
	        	case "-o":
	        		flags.put("o", true);
	        		parameters.put("o", args[i + 1]);
	        		break;
	        	case "-p":
	        		flags.put("p", true);
	        		break;
	        	case "-h":
	        		flags.put("h", true);
	        		break;
	        	}
        	}
        	catch(ArrayIndexOutOfBoundsException aiofbe)
        	{
        		// arrayindexoutofbounds exception
        		System.out.println("Missing data for parameter " + args[i]);
        		System.exit(1);
        	}
        }
        
        // check for flag conflicts
        // h overrides everything
        if(flags.get("h"))
        {
        	// print help and exit
        	printHelp();
        	System.exit(0);
        }
        // p cannot be used with d
        if(flags.get("d") && flags.get("p"))
        {
        	// error
        	System.out.println("Cannot use -d and -p together.");
        	System.exit(1);
        }
        else if((!flags.get("d") && !flags.get("p")) || !flags.get("i") || !flags.get("o"))
        {
        	// not enough data
        	System.out.println("Not enough data to run.");
        	printHelp();
        	System.exit(1);
        }
        
        JsonObject data = new JsonObject();
        // check that the input is valid
        if(flags.get("d"))
        {
        	// try to parse the input as a json
        	try
        	{
        		data = Json.parse(parameters.get("d")).asObject(); 
        	}
        	catch(Exception e)
        	{
        		// error parsing json, try to open file
        		File jsonFile = new File(parameters.get("d"));
        		if(jsonFile.isFile())
        		{
        			// read data from file
        			try 
        			{
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile)));
						String jsonStr = "";
						String line = "";
						while(line != null)
						{
							line = br.readLine();
							jsonStr += line;
						}
						data = Json.parse(jsonStr).asObject();
						
					} 
        			catch (FileNotFoundException fnfe) 
        			{
        				// input json file not found
						System.out.println("File " + parameters.get("d") + " not found.");
						System.exit(1);
					} 
        			catch (IOException ioe) 
        			{
						// error reading file
        				System.out.println("Error reading the input json file");
        				System.exit(1);
					}
        			catch(Exception e2)
        			{
        				System.out.println("Error parsing json from file.");
        				System.exit(1);
        			}
        		}
        		else
        		{
        			// invalid data
        			System.out.println("-d parameter is invalid.");
        			printHelp();
        			System.exit(1);
        		}
        	}
        }
        
        // check that the input pdf file is valid
        File pdfInput = new File(parameters.get("i"));
        if(!pdfInput.isFile())
        {
        	System.out.println("File " + parameters.get("i") + " does not exist.");
        	System.exit(1);
        }
        String[] parts = pdfInput.getName().split(".");
    	if(!parts[parts.length - 1].toLowerCase().equals("pdf"))
    	{
    		// not a valid PDF
    		System.out.println("-i argument must be a PDF");
    		System.exit(1);
    	}
        
    	// check output file name
    	if(!parameters.get("o").endsWith(".pdf"))
    	{
    		System.out.println("Output file must end with .pdf");
    		System.exit(1);
    	}
    	// try to create the output file
    	File pdfOutput = new File(parameters.get("o"));
    	try
    	{
    		pdfOutput.createNewFile();
    	}
    	catch(IOException ioe)
    	{
    		System.out.println("Could not create output PDF");
    		System.exit(1);
    	}
    	
    	// everything checks out here, move to processing
    	boolean success = Injector.inject(data, pdfInput, pdfOutput);
    	if(success)
    	{
    		System.out.println("Success. Output file at " + pdfOutput.getAbsolutePath());
    		System.exit(0);
    	}
    	else
    	{
    		System.out.println("Fail.");
    		System.exit(1);
    	}
    }
    
    /**
     * prints the help message for invalid input or -h flag
     */
    public static void printHelp() 
    {
    	System.out.println("\nHELP MESSAGE\n");
    }
    
    /**
     * takes an array of Strings and returns a new array where any args with quotes are put together
     * 
     * @param args
     * @return
     */
    public static String[] formatArgs(String[] args) throws Exception
    {
    	ArrayList<String> result = new ArrayList<String>();
    	// format the arguments so that quotes are together
        String arg = "";
        boolean append = false;
        for(int i = 0; i < args.length; i++) 
        {
        	// 
        	if(args[i].startsWith("\""))
        	{
        		args[i] = args[i].substring(1);
        		append = true;
        	}
        	else if(args[i].endsWith("\""))
        	{
        		append = false;
        		arg += args[i].substring(0, args[i].length() - 1);
        		result.add(arg);
        		continue;
        	}
        	
        	// if append == true, append the argument to string, else just put it on the result
        	if(append)
        	{
        		arg += args[i];
        		
        		// if this is the last argument, then a closing " was never provided
        		if(i == args.length - 1)
        		{
        			throw new Exception();
        		}
        	}
        	else 
        	{
        		result.add(args[i]);
        	}
        }
        
        // return the result as a normal array
        return result.toArray(new String[result.size()]);
    }
}
