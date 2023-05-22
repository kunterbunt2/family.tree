package de.bushnaq.abdalla.family;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterOptions {
	private static final String	CLI_OPTION_COMPACT					= "c";
	private static final String	CLI_OPTION_COORDINATES				= "coordinates";
	private static final String	CLI_OPTION_EXCLUDE_SPOUSE			= "exclude_spouse";
	private static final String	CLI_OPTION_FAMILY_NAME				= "family_name";
	private static final String	CLI_OPTION_FOLLOW_FEMALES			= "follow_females";
	private static final String	CLI_OPTION_FOLLOW_OL				= "ol";
	private static final String	CLI_OPTION_H						= "h";
	private static final String	CLI_OPTION_INPUT					= "input";
	private static final String	CLI_OPTION_OUTPUT_FILE_DECORATIONS	= "output_decorations";
	private static final String	CLI_OPTION_V						= "v";

	private boolean				compact								= false;									// compact tree (no birth, died, ID)
	private boolean				coordinates							= true;										// enable coordinates
	private boolean				drawTextBorders						= false;
	private boolean				excludeSpouse						= false;									// excluded the spouse in the tree if true otherwise do not include them
	private String				familyName;
	private boolean				followFemales						= false;									// children will be shown under the mother if both parents are member of the family
	private boolean				h									= false;									// draw a horizontal tree if true
	private String				input;																			// input excel file
	private final Logger		logger								= LoggerFactory.getLogger(this.getClass());
	private boolean				originalLanguage					= false;									// use original language fields for fist name and last name
	private String				outputDecorator						= "";										// additional decorations for the output file name
	private boolean				v									= true;										// vertical tree mode

	public String getFamilyName() {
		return familyName;
	}

	public String getInput() {
		return input;
	}

	public String getOutputDecorator() {
		return outputDecorator;
	}

	public boolean isCompact() {
		return compact;
	}

	public boolean isCoordinates() {
		return coordinates;
	}

	public boolean isDrawTextBorders() {
		return drawTextBorders;
	}

	public boolean isExcludeSpouse() {
		return excludeSpouse;
	}

	public boolean isFollowFemales() {
		return followFemales;
	}

	public boolean isH() {
		return h;
	}

	public boolean isOriginalLanguage() {
		return originalLanguage;
	}

	public boolean isV() {
		return v;
	}

	private void resetOptions() {
		excludeSpouse = false;
		followFemales = false;
		h = false;
		originalLanguage = false;
		outputDecorator = "";
		v = true;
	}

	public void start(String[] args) throws Exception {
		resetOptions();
		Options options = new Options();
		options.addOption(Option.builder(CLI_OPTION_INPUT).hasArgs().desc("Input excel file name. This parameter is not optional.").build());
		options.addOption(Option.builder(CLI_OPTION_FAMILY_NAME).hasArgs().desc("Family name used to pic root of family. This parameter is optional.").optionalArg(true).build());
		options.addOption(Option.builder(CLI_OPTION_OUTPUT_FILE_DECORATIONS).hasArgs().desc("Output file name decorations. This parameter is optional.").optionalArg(true).build());
		options.addOption(Option.builder(CLI_OPTION_H).desc("Generte horizontal tree. This parameter is optional. Default is false.").optionalArg(true).build());
		options.addOption(Option.builder(CLI_OPTION_V).desc("Generte vertical tree. This parameter is optional. This parameter is optional. Default is true.").optionalArg(true).build());
		options.addOption(Option.builder(CLI_OPTION_EXCLUDE_SPOUSE).desc("Exclude spouses if true. This parameter is optional. Default is false.").build());
		options.addOption(Option.builder(CLI_OPTION_FOLLOW_FEMALES).desc("If children can be visualized with the father or the mother, this parameter will decide. This parameter is optional. Default is false.").build());
		options.addOption(Option.builder(CLI_OPTION_FOLLOW_OL).desc("Use original language for first name and last name if they exist. This parameter is optional. Default is false.").build());
		options.addOption(Option.builder(CLI_OPTION_COMPACT).desc("Generte compact tree. This parameter is optional. This parameter is optional. Default is false.").optionalArg(true).build());
		options.addOption(Option.builder(CLI_OPTION_COORDINATES).desc("Generte coordinates. This parameter is optional. This parameter is optional. Default is false.").optionalArg(true).build());

		// create the parser
		CommandLineParser	parser	= new DefaultParser();
		// parse the command line arguments
		CommandLine			line	= parser.parse(options, args);
		if (line.hasOption(CLI_OPTION_INPUT)) {
			input = line.getOptionValue(CLI_OPTION_INPUT);
		} else {
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("family.tree", "", options, "", true);
			return;
		}

		if (line.hasOption(CLI_OPTION_OUTPUT_FILE_DECORATIONS)) {
			outputDecorator = line.getOptionValue(CLI_OPTION_OUTPUT_FILE_DECORATIONS);
		}
		if (line.hasOption(CLI_OPTION_H)) {
			h = true;
			v = false;
			logger.info("horizontal tree mode enabled.");
		} else {
			logger.info("horizontal tree mode disabled.");
		}
		if (line.hasOption(CLI_OPTION_COMPACT)) {
			compact = true;
			logger.info("compact tree enabled.");
		} else {
			logger.info("compact tree disabled.");
		}
		if (line.hasOption(CLI_OPTION_COORDINATES) && !compact) {
			coordinates = true;
			logger.info("coordinates enabled.");
		} else {
			logger.info("coordinates disabled.");
		}

		if (line.hasOption(CLI_OPTION_V)) {
			v = true;
			h = false;
			logger.info("vertical tree mode enabled.");
		} else {
			logger.info("vertical tree mode disabled.");
		}

		if (line.hasOption(CLI_OPTION_EXCLUDE_SPOUSE)) {
			excludeSpouse = true;
			logger.info("include spouse mode enabled.");
		} else {
			logger.info("include spouse mode disabled.");
		}

		if (line.hasOption(CLI_OPTION_FOLLOW_FEMALES)) {
			followFemales = true;
			logger.info("follow females mode enabled.");
		} else {
			logger.info("follow females mode disabled.");
		}

		if (line.hasOption(CLI_OPTION_FOLLOW_OL)) {
			originalLanguage = true;
			logger.info("original language mode enabled.");
		} else {
			logger.info("original language mode disabled.");
		}
		if (line.hasOption(CLI_OPTION_FAMILY_NAME)) {
			familyName = line.getOptionValue(CLI_OPTION_FAMILY_NAME);
		} else {
		}
	}

}
