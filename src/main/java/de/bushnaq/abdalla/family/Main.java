package de.bushnaq.abdalla.family;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.bushnaq.abdalla.family.tree.HorizontalTree;
import de.bushnaq.abdalla.family.tree.Tree;
import de.bushnaq.abdalla.family.tree.VerticalTree;
import de.bushnaq.abdalla.util.FileUtil;

@Component
@Scope("prototype")
public class Main {
	@Autowired
	Context					context;
	private final Logger	logger	= LoggerFactory.getLogger(this.getClass());

	/**
	 * bean
	 */
	public Main() {

	}

	public BufferedImage start(String[] args) throws Exception {
		context.getParameterOptions().start(args);

		String	inputName		= context.getParameterOptions().getInput();
		String	outputDecorator	= context.getParameterOptions().getOutputDecorator();
		if (outputDecorator.isEmpty()) {
			if (context.getParameterOptions().isH()) {
				outputDecorator += "-h";
			}
			if (context.getParameterOptions().isV()) {
				outputDecorator += "-v";
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
		}
		Tree tree;
		if (context.getParameterOptions().isH()) {
			tree = new HorizontalTree(context);
		} else {
			tree = new VerticalTree(context);
		}
		tree.readExcel(inputName);
		String outputName = FileUtil.removeExtension(inputName) + outputDecorator;
		return tree.generate(context, outputName);
	}

}
