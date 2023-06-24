package de.bushnaq.abdalla.family.tree.util;

import de.bushnaq.abdalla.family.Context;
import de.bushnaq.abdalla.family.Main;
import de.bushnaq.abdalla.family.person.Person;
import de.bushnaq.abdalla.family.person.PersonList;
import de.bushnaq.abdalla.family.tree.ui.MyCanvas;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base {
    static {
        System.setProperty("java.awt.headless", "false");
    }

    private final String familyName;
    @Autowired
    protected Context context;
    @Autowired
    protected Main main;

    public Base(String familyName) {
        this.familyName = familyName;
    }

    protected String buildFileName() {
        return "reference/" + getFamilyName() + "/" + getFamilyName() + ".xlsx";
    }

    public PersonList generate(String[] args) throws Exception {
//		BufferedImage	image			=
        PersonList personList = main.start(args);
//        String inputName = context.getParameterOptions().getInput();
//        String outputDecorator = context.getParameterOptions().getOutputDecorator();
//        String outputName = inputName + outputDecorator;
//		if (TestUtil.isRunningInEclipse())
//			showImage(image, outputName);
        return personList;
    }

    public String getFamilyName() {
        return familyName;
    }

    private void showImage(BufferedImage image, String title) {
        MyCanvas c = new MyCanvas(image);
        c.f.setTitle(title);
        while (c.f.isVisible())
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    protected void testResult(PersonList personList, ExpectedResult[] expectedResult) {
        for (int i = 0; i < personList.size(); i++) {
            Person person = personList.get(i);
            assertEquals(expectedResult[i].getX(), person.getX(), String.format("[%d] bad x", person.getId()));
            assertEquals(expectedResult[i].getY(), person.getY(), String.format("[%d] bad y", person.getId()));
        }
        assertEquals(0, main.getPageErrors().size(), "Unexpected number of errors");
    }

    protected void writeResult(PersonList personList) throws Exception {
        FileWriter fileWriter = new FileWriter(String.format("reference/%s/%s.csv", getFamilyName(), getFamilyName()));
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("        ExpectedResult[] expectedResult = {//\n");
        for (Person person : personList) {
            printWriter.printf("                new ExpectedResult( %d, %.0ff, %.0ff ),//\n", person.getId(), person.getX(), person.getY());
        }
        printWriter.printf("        };\n");
        printWriter.close();
    }

}
