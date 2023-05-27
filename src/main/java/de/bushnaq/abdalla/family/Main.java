package de.bushnaq.abdalla.family;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.HorizontalTree;
import de.bushnaq.abdalla.family.tree.Tree;
import de.bushnaq.abdalla.family.tree.TreeExcelReader;
import de.bushnaq.abdalla.family.tree.VerticalTree;
import de.bushnaq.abdalla.pdf.PdfDocument;
import de.bushnaq.abdalla.util.FileUtil;

@Component
@Scope("prototype")
public class Main {
	static <T> T[] concat(T[] array1, T[] array2) {
		return Stream.concat(Arrays.stream(array1), Arrays.stream(array2)).toArray(size -> (T[]) Array.newInstance(array1.getClass().getComponentType(), size));
	}

	@Autowired
	Context					context;

	private final Logger	logger	= LoggerFactory.getLogger(this.getClass());

	/**
	 * bean
	 */
	public Main() {

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

	private void generate(String inputFileName) throws Exception, IOException, TransformerException {
		PersonList	personList	= readExcel(inputFileName);
		PdfDocument	pdfDocument	= new PdfDocument(FileUtil.removeExtension(inputFileName) + ".pdf");

		String[]	base		= { "-input", context.getParameterOptions().getInput(), "-family_name", context.getParameterOptions().getFamilyName() };
		String[][]	parameters	= {																															//
//				{ "-v" },																																//
//				{ "-v", "-ol" },																														//
//				{ "-v", "-c" },																															//
//				{ "-v", "-c", "-ol" },																													//
				{ "-h", "-coordinates" },																													//
//				{ "-h", "-ol" },																														//
				{ "-h", "-c" },																																//
//				{ "-h", "-c", "-ol" },																													//
		};

		for (int i = 0; i < parameters.length; i++) {
			context.getParameterOptions().start(concat(base, parameters[i]));
			createTree(personList).generate(context, pdfDocument, FileUtil.removeExtension(inputFileName) + generateOutputDecoration());
		}

		createTree(personList).generateErrorPage(pdfDocument);
		pdfDocument.endDocument();
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

	public PersonList readExcel(String fileName) throws Exception {
		TreeExcelReader excelReader = new TreeExcelReader();
		return excelReader.readExcel(fileName);
	}

	public void start(String[] args) throws Exception {

		context.getParameterOptions().start(args);

		String inputName = context.getParameterOptions().getInput();
		generate(inputName);

	}

}
