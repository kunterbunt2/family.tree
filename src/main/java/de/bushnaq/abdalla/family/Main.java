package de.bushnaq.abdalla.family;

import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.*;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@Scope("prototype")
public class Main {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    Context context;
    private List<PageError> pageErrors;
    private PdfDocument pdfDocument;

    /**
     * bean
     */
    public Main() {

    }

    static <T> T[] concat(T[] array1, T[] array2) {
        return Stream.concat(Arrays.stream(array1), Arrays.stream(array2)).toArray(size -> (T[]) Array.newInstance(array1.getClass().getComponentType(), size));
    }

    private Tree createTree(PersonList personList) {
        Tree tree;
        if (context.getParameterOptions().isH()) {
            tree = new HorizontalTree(context, personList);
        } else {
            tree = new VerticalTree(context, personList);
        }
        return tree;
    }

    private PersonList generate(String inputFileName) throws Exception {
        PersonList personList = importPersonList(inputFileName);
        pdfDocument = new PdfDocument(FileUtil.removeExtension(inputFileName) + ".pdf");

        //String[] base = {"-input", context.getParameterOptions().getInput(), "-family_name", context.getParameterOptions().getFamilyName()};
        //String[][] parameters = {                                                                                                                                                //
//				{ "-v", "-coordinates" },																																//
//				{ "-v", "-ol", "-coordinates" },																														//
//				{ "-v", "-c", "-coordinates" },																															//
//				{ "-v", "-c", "-ol", "-coordinates" },																													//
//                {"-h", "-coordinates"},                                                                                                                                        //
//				{ "-h", "-ol", "-coordinates" },																															//
//				{ "-h", "-c", "-coordinates" },																																//
//				{ "-h", "-c", "-ol", "-coordinates" },																														//
//        };

//        for (int i = 0; i < parameters.length; i++) {
//            context.getParameterOptions().start(concat(base, parameters[i]));
//            createTree(personList).generate(context, pdfDocument, FileUtil.removeExtension(inputFileName) + generateOutputDecoration());
//        }

        createTree(personList).generate(context, pdfDocument, FileUtil.removeExtension(inputFileName) + generateOutputDecoration());
        pageErrors = createTree(personList).generateErrorPage(pdfDocument);
        pdfDocument.endDocument();
        return personList;
    }

    private String generateOutputDecoration() {
        String outputDecorator = "";
        if (context.getParameterOptions().isH()) {
            outputDecorator += "-h";
        }
        if (context.getParameterOptions().isV()) {
            outputDecorator += "-v";
        }
        if (context.getParameterOptions().isCompact()) {
            outputDecorator += "-c";
        }
        if (context.getParameterOptions().isCoordinates()) {
            outputDecorator += "-coordinates";
        }
        if (context.getParameterOptions().isFollowFemales()) {
            outputDecorator += "-ff";
        }
        if (context.getParameterOptions().isExcludeSpouse()) {
            outputDecorator += "-es";
        }
        if (context.getParameterOptions().isOriginalLanguage()) {
            outputDecorator += "-ol";
        }
        context.getParameterOptions().setOutputDecorator(outputDecorator);
        return outputDecorator;
    }

    public List<PageError> getPageErrors() {
        return pageErrors;
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public PersonList importPersonList(String fileName) throws Exception {

        TreeExcelReader excelReader = new TreeExcelReader();
        PersonList personList = excelReader.importPersonList(fileName);
        logger.info(String.format("Read %d person from Excel file.", personList.size()));
        return personList;

    }

    public PersonList start(String[] args) throws Exception {

        context.getParameterOptions().start(args);

        String inputName = context.getParameterOptions().getInput();
        return generate(inputName);

    }

}
