package de.bushnaq.abdalla.family;

import de.bushnaq.abdalla.family.tree.SplitMode;
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
    private static final String CLI_OPTION_OUTPUT = "output";
    private static final String CLI_OPTION_OUTPUT_FILE_DECORATIONS = "output_decorations";
    private static final String CLI_OPTION_V = "v";
    private static final String CLI_OPTION_SPLIT = "split";
    private static final String CLI_OPTION_MAX_ISO = "max_iso";
    private static final String CLI_OPTION_MIN_ISO = "min_iso";
    private static final String CLI_OPTION_GRID = "grid";

    private final boolean colorTrees = false;
    private final boolean drawTextMetric = false;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int minYDistanceBetweenTrees = 0;
    private final float pageMargin = 32f;                                        // unprintable margin
    private final boolean showImage = true;
    private final float zoom = 0.5f;
    String inputFolder;
    private boolean showGrid = false;
    private IsoPage targetPaperSize = new IsoPage(PDRectangle.A4, "A4");
    private IsoPage minPaperSize = new IsoPage(PDRectangle.A6, "A6");
    private boolean compact = false;                                    // compact tree (no birth, died, ID)
    private boolean coordinates = true;                                        // enable coordinates
    private boolean excludeSpouse = false;                                    // excluded the spouse in the tree if true otherwise do not include them
    private String familyName;
    private boolean followFemales = false;                                    // children will be shown under the mother if both parents are member of the
    // family
    private boolean h = false;                                    // draw a horizontal tree if true
    private String input;                                                                            // input excel file
    private String output;                                                                            // output pdf file
    private boolean originalLanguage = false;                                    // use original language fields for fist name and last name
    private String outputDecorator = "";                                        // additional decorations for the output file name
    private boolean v = true;                                        // vertical tree mode
    private boolean distributeOnPages = true;// distribute trees that do not fit on targetPaperSize
    private SplitMode distributeOnPagesMode = SplitMode.TOP_DOWN;

    public SplitMode getDistributeOnPagesMode() {
        return distributeOnPagesMode;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getInput() {
        return input;
    }

    public String getInputFolder() {
        return inputFolder;
    }

    public IsoPage getMinPaperSize() {
        return minPaperSize;
    }

    public int getMinYDistanceBetweenTrees() {
        return minYDistanceBetweenTrees;
    }

    public String getOutput() {
        return output;
    }

    public String getOutputDecorator() {
        return outputDecorator;
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

    public boolean isDistributeOnPages() {
        return distributeOnPages;
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

    public boolean isShowGrid() {
        return showGrid;
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

    public void setOutputDecorator(String outputDecorator) {
        this.outputDecorator = outputDecorator;
    }

    public void start(String[] args) throws Exception {
        resetOptions();
        Options options = new Options();
        options.addOption(Option.builder(CLI_OPTION_INPUT).hasArg().desc("Input excel file name. This parameter is not optional.").build());
        options.addOption(Option.builder(CLI_OPTION_OUTPUT).hasArg().desc("Output pdf file name. This parameter is optional. Default is input file name.").build());
        options.addOption(Option.builder(CLI_OPTION_FAMILY_NAME).hasArg().desc("Family name used to pic root of family. This parameter is optional.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_OUTPUT_FILE_DECORATIONS).hasArg().desc("Output file name decorations. This parameter is optional.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_H).desc("Generate horizontal tree. This parameter is optional. Default is false.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_V).desc("Generate vertical tree. This parameter is optional. Default is true.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_EXCLUDE_SPOUSE).desc("Exclude spouses if true. This parameter is optional. Default is false.").build());
        options.addOption(Option.builder(CLI_OPTION_FOLLOW_FEMALES).desc("If children can be visualized with the father or the mother, this parameter will decide. This parameter is optional. Default is false.").build());
        options.addOption(Option.builder(CLI_OPTION_FOLLOW_OL).desc("Use original language for first name and last name if they exist. This parameter is optional. Default is false.").build());
        options.addOption(Option.builder(CLI_OPTION_COMPACT).desc("Generate compact tree. This parameter is optional. Default is false.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_COORDINATES).desc("Generate coordinates. This parameter is optional. Default is false.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_GRID).desc("Generate a grid. This parameter is optional. Default is false.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_SPLIT).hasArg().desc("Splits trees, that do not fit onto max_iso page sizes onto several pages. Parameter must be one of : top-down, bottom-up.This parameter is optional. Default is false.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_MIN_ISO).hasArg().desc("Minimum iso page size allowed. Any page will be at least this size. This parameter is optional. Default is A6.").optionalArg(true).build());
        options.addOption(Option.builder(CLI_OPTION_MAX_ISO).hasArg().desc("Maximum iso page size allowed. Any tree that does not fit will be split ont o several pages. Ignored if split option is nto specified. This parameter is optional. Default is A4.").optionalArg(true).build());

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
        if (line.hasOption(CLI_OPTION_OUTPUT)) {
            output = line.getOptionValue(CLI_OPTION_OUTPUT);
        } else {
            output = input;
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
        if (line.hasOption(CLI_OPTION_COORDINATES)) {
            coordinates = true;
            logger.info("coordinates enabled.");
        } else {
            coordinates = false;
            logger.info("coordinates disabled.");
        }

        if (line.hasOption(CLI_OPTION_GRID)) {
            showGrid = true;
            logger.info("grid enabled.");
        } else {
            showGrid = false;
            logger.info("grid disabled.");
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
        if (line.hasOption(CLI_OPTION_SPLIT)) {
            distributeOnPages = true;
            String splitMode = line.getOptionValue(CLI_OPTION_SPLIT);
            distributeOnPagesMode = SplitMode.valueOf(splitMode);
            logger.info(String.format("split mode %s enabled.", splitMode));
        } else {
            distributeOnPages = false;
            logger.info("split disabled.");
        }
        if (line.hasOption(CLI_OPTION_MAX_ISO)) {
            String pageName = line.getOptionValue(CLI_OPTION_MAX_ISO);
            switch (pageName) {
                case "A0" -> targetPaperSize = new IsoPage(PDRectangle.A0, "A0");
                case "A1" -> targetPaperSize = new IsoPage(PDRectangle.A1, "A1");
                case "A2" -> targetPaperSize = new IsoPage(PDRectangle.A2, "A2");
                case "A3" -> targetPaperSize = new IsoPage(PDRectangle.A3, "A3");
                case "A4" -> targetPaperSize = new IsoPage(PDRectangle.A4, "A4");
                case "A5" -> targetPaperSize = new IsoPage(PDRectangle.A5, "A5");
                case "A6" -> targetPaperSize = new IsoPage(PDRectangle.A6, "A6");
                default -> throw new Exception(String.format("Unsupported max_iso size %s", pageName));
            }
            logger.info(String.format("max_iso page is %s.", pageName));
        } else {
            targetPaperSize = new IsoPage(PDRectangle.A4, "A4");
            logger.info("max_iso page is A4.");
        }
        if (line.hasOption(CLI_OPTION_MIN_ISO)) {
            String pageName = line.getOptionValue(CLI_OPTION_MIN_ISO);
            switch (pageName) {
                case "A0" -> minPaperSize = new IsoPage(PDRectangle.A0, "A0");
                case "A1" -> minPaperSize = new IsoPage(PDRectangle.A1, "A1");
                case "A2" -> minPaperSize = new IsoPage(PDRectangle.A2, "A2");
                case "A3" -> minPaperSize = new IsoPage(PDRectangle.A3, "A3");
                case "A4" -> minPaperSize = new IsoPage(PDRectangle.A4, "A4");
                case "A5" -> minPaperSize = new IsoPage(PDRectangle.A5, "A5");
                case "A6" -> minPaperSize = new IsoPage(PDRectangle.A6, "A6");
                default -> throw new Exception(String.format("Unsupported max_iso size %s", pageName));
            }
            logger.info(String.format("min_iso page is %s.", pageName));
        } else {
            minPaperSize = new IsoPage(PDRectangle.A6, "A6");
            logger.info("min_iso page is A4.");
        }
        if (line.hasOption(CLI_OPTION_FAMILY_NAME)) {
            familyName = line.getOptionValue(CLI_OPTION_FAMILY_NAME);
        } else {
        }
        inputFolder = FileUtil.extractFolderNamePart(input);
    }
}
