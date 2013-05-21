package org.sky.util.json;

import com.sanityinc.jargs.CmdLineParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public class Props2Js {
	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		// default settings
		boolean verbose = false;
		String outputFilename = null;
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		Writer out = null;
		Reader in = null;
		// initialize command line parser
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option verboseOpt = parser.addBooleanOption('v',
				"verbose");
		CmdLineParser.Option helpOpt = parser.addBooleanOption('h', "help");
		CmdLineParser.Option outputFilenameOpt = parser.addStringOption('o',
				"output");
		CmdLineParser.Option nameOpt = parser.addStringOption('n', "name");
		CmdLineParser.Option outputTypeOpt = parser.addStringOption('t', "to");
		try {
			// parse the arguments
			parser.parse(args);
			// figure out if the help option has been executed
			Boolean help = (Boolean) parser.getOptionValue(helpOpt);
			if (help != null && help.booleanValue()) {
				// usage();
				System.exit(0);
			}
			// determine boolean options
			verbose = parser.getOptionValue(verboseOpt) != null;
			// get the file arguments
			String[] fileArgs = parser.getRemainingArgs();
			String inputFilename = fileArgs[0];
			if (fileArgs.length == 0) {
				throw new Exception("No input filename specified.");
			}
			in = new InputStreamReader(new FileInputStream(inputFilename),
					"UTF-8");
			Properties properties = new Properties();
			properties.load(in);
			// get output type
			String outputType = (String) parser.getOptionValue(outputTypeOpt);
			if (outputType == null) {
				outputType = "json";
				if (verbose) {
					System.err
							.println("[INFO] No output type specified, defaulting to json.");
				}
			} else {
				if (verbose) {
					System.err.println("[INFO] Output type set to "
							+ outputType + ".");
				}
			}
			// get output filename
			outputFilename = (String) parser.getOptionValue(outputFilenameOpt);
			if (outputFilename == null) {
				if (verbose) {
					System.err
							.println("[INFO] No output file specified, defaulting to stdout.");
				}
				out = new OutputStreamWriter(System.out);
			} else {
				File outputFile = new File(outputFilename);
				if (verbose) {
					System.err.println("[INFO] Output file is '"
							+ outputFile.getAbsolutePath() + "'");
				}
				out = new OutputStreamWriter(bytes, "UTF-8");
			}
			String name = (String) parser.getOptionValue(nameOpt);
			if (name == null && !outputType.equalsIgnoreCase("json")) {
				throw new Exception("Missing --name option.");
			}
			String result = "";

			result = PropertyConverter.convertToJson(properties);

			out.write(result);
		} catch (CmdLineParser.OptionException e) {

			System.exit(1);
		} catch (Exception e) {
			System.err.println("[ERROR] " + e.getMessage());
			if (verbose) {
				e.printStackTrace();
			}
			System.exit(1);
		} finally {
			if (out != null) {
				try {
					out.close();
					if (bytes.size() > 0) {
						bytes.writeTo(new FileOutputStream(outputFilename));
					}
				} catch (IOException e) {
					System.err.println("[ERROR] " + e.getMessage());
					if (verbose) {
						e.printStackTrace();
					}
				}
			}
			try {
				in.close();
			} catch (IOException e) {
				System.err.println("[ERROR] " + e.getMessage());
				if (verbose) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Outputs help information to the console.
	 */
	private static void usage() {
		System.out
				.println("\nUsage: java -jar props2js-x.y.z.jar [options] [input file]\n\n"
						+ "Global Options\n"
						+ " -h, --help Displays this information.\n"
						+ " -v, --verbose Display informational messages and warnings.\n"
						+ " --name <name> The variable/callback name.\n"
						+ " --to <format> The output format: json (default), jsonp, or js.\n"
						+ " -o <file> Place the output into <file>. Defaults to stdout.");
	}
}
