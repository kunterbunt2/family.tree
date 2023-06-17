package de.bushnaq.abdalla.family;

import de.bushnaq.abdalla.pdf.IsoPage;
import de.bushnaq.abdalla.util.FileUtil;
import org.apache.commons.cli.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParameterOptions {
    private static final String CLI_OPTION_COMPACT = "c";
    private static final String CLI_OPTION_COORDINATES = "coordinates";
    private static final String CLI_OPTION_EXCLUDE_SPOUSE = "exclude_spouse";
    private static final String CLI_OPTION_FAMILY_NAME = "family_name";
    private static final String CLI_OPTION_FOLLOW_FEMALES = "follow_females";
    private static final String CLI_OPTION_FOLLOW_OL = "ol";
    private static final String CLI_OPTION_H = "h";
    private static final String CLI_OPTION_INPUT = "input";
    private static final String CLI_OPTION_OUTPUT_FILE_DECORATIONS = "output_decorations";
    private static final String CLI_OPTION_V = "v";

    private final boolean colorTrees = false;
    private final boolean drawGrid = false;
    private final boolean drawTextMetric = false;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int minYDistanceBetweenTrees = 0;
    private final float pageMargin = 32f;                                        // unprintable margin
    private final boolean showImage = true;
    private final IsoPage targetPaperSize = new IsoPage(PDRectangle.A4, "A4");
    private final float zoom = 0.5f;
    String inputFolder;
    private boolean compact = false;                                    // compact tree (no birth, died, ID)
    private boolean coordinates = true;                                        // enable coordinates
    private boolean excludeSpouse = false;                                    // excluded the spouse in the tree if true otherwise do not include them
    private String familyName;
    private boolean followFemales = false;                                    // children will be shown under the mother if both parents are member of the
    // family
    private boolean h = false;                                    // draw a horizontal tree if true
    private String input;                                                                            // input excel file
    private boolean originalLanguage = false;                                    // use original language fields for fist name and last name
    private String outputDecorator = "";                                        // additional decorations for the output file name
    private boolean v = true;                                        // vertical tree mode

    public String getFamilyName() {
        return familyName;
    }

    public String getInput() {
        return input;
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public int getMinYDistanceBetweenTrees() {
        return minYDistanceBetweenTrees;
    }

    public String getOutputDecorator() {
        return outputDecorator;
    }

    public void setOutputDecorator(String outputDecorator) {
        this.outputDecorator = outputDecorator;
    }

    public float getPageMargin() {
        return pageMargin;
    }

    public IsoPage getTargetPaperSize() {
        return targetPaperSize;
    }

    public float getZoom() {
        return zoom;
    }

    public boolean isColorTrees() {
        return colorTrees;
    }

    public boolean isCompact() {
        return compact;
    }

    public boolean isCoordinates() {
        return coordinates;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public boolean isDrawTextMetric() {
        return drawTextMetric;
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

    public boolean isShowImage() {
        return showImage;
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
        CommandLineParser parser = new DefaultParser();
        // parse the command line arguments
        CommandLine line = parser.parse(options, args);
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
            h = false;
            v = true;
            logger.info("horizontal tree mode disabled.");
        }
        if (line.hasOption(CLI_OPTION_COMPACT)) {
            compact = true;
            logger.info("compact tree enabled.");
        } else {
            compact = false;
            logger.info("compact tree disabled.");
        }
        if (line.hasOption(CLI_OPTION_COORDINATES) /* && !compact */) {
            coordinates = true;
            logger.info("coordinates enabled.");
        } else {
            coordinates = false;
            logger.info("coordinates disabled.");
        }

        if (line.hasOption(CLI_OPTION_V)) {
            v = true;
            h = false;
            logger.info("vertical tree mode enabled.");
        } else {
            v = false;
            h = true;
            logger.info("vertical tree mode disabled.");
        }

        if (line.hasOption(CLI_OPTION_EXCLUDE_SPOUSE)) {
            excludeSpouse = true;
            logger.info("include spouse mode disabled.");
        } else {
            excludeSpouse = false;
            logger.info("include spouse mode enabled.");
        }

        if (line.hasOption(CLI_OPTION_FOLLOW_FEMALES)) {
            followFemales = true;
            logger.info("follow females mode enabled.");
        } else {
            followFemales = false;
            logger.info("follow females mode disabled.");
        }

        if (line.hasOption(CLI_OPTION_FOLLOW_OL)) {
            originalLanguage = true;
            logger.info("original language mode enabled.");
        } else {
            originalLanguage = false;
            logger.info("original language mode disabled.");
        }
        if (line.hasOption(CLI_OPTION_FAMILY_NAME)) {
            familyName = line.getOptionValue(CLI_OPTION_FAMILY_NAME);
        } else {
        }
        inputFolder = FileUtil.extractFolderNamePart(input);
    }

}
