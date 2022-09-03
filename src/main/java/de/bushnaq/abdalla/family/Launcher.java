package de.bushnaq.abdalla.family;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

@Component
public class Launcher {

	private final Logger		logger		= LoggerFactory.getLogger(this.getClass());
	JFrame						frmFamilytree;
	private final ButtonGroup	treeGroup	= new ButtonGroup();
	@Autowired
	Main						main;
	@Autowired
	Context						context;
	private JRadioButton		vtreeRadioButton;
	private JRadioButton		htreeRadioButton;
	private JRadioButton		followMalesRadioButton;
	private JRadioButton		followFemalesRadioButton;
	private JCheckBox			excludeSpousesCheckBox;
	private JCheckBox			originalLanguageCheckBox;
	private JTextPane			textPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Launcher window = new Launcher();
					window.frmFamilytree.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Launcher() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		System.setProperty("flatlaf.animatedLafChange", String.valueOf(true));
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFamilytree = new JFrame();
		frmFamilytree.setTitle("family.tree");
		frmFamilytree.setBounds(100, 100, 603, 319);
		frmFamilytree.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		frmFamilytree.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.BUTTON_COLSPEC, FormSpecs.BUTTON_COLSPEC, FormSpecs.BUTTON_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, },
				new RowSpec[] { FormSpecs.DEFAULT_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, }));

		JLabel lblNewLabel = new JLabel("Tree Type");
		panel.add(lblNewLabel, "1, 1");

		vtreeRadioButton = new JRadioButton("Vertical tree");
		vtreeRadioButton.setSelected(true);
		treeGroup.add(vtreeRadioButton);
		panel.add(vtreeRadioButton, "3, 1, fill, fill");

		htreeRadioButton = new JRadioButton("Horizontal tree");
		treeGroup.add(htreeRadioButton);
		panel.add(htreeRadioButton, "5, 1, fill, fill");

		JLabel lblNewLabel_1 = new JLabel("Children position");
		panel.add(lblNewLabel_1, "1, 2, left, default");

		followMalesRadioButton = new JRadioButton("Follow males");
		followMalesRadioButton.setSelected(true);
		panel.add(followMalesRadioButton, "3, 2");

		followFemalesRadioButton = new JRadioButton("Follow females");
		panel.add(followFemalesRadioButton, "5, 2, fill, fill");

		JLabel lblNewLabel_2 = new JLabel("Spouses from other families");
		panel.add(lblNewLabel_2, "1, 3");

		excludeSpousesCheckBox = new JCheckBox("Exclude spouses");
		panel.add(excludeSpousesCheckBox, "3, 3, fill, fill");

		JLabel lblNewLabel_3 = new JLabel("Original Language");
		panel.add(lblNewLabel_3, "1, 4, left, default");

		originalLanguageCheckBox = new JCheckBox("Show if available");
		panel.add(originalLanguageCheckBox, "3, 4");

		JPanel panel_1 = new JPanel();
		frmFamilytree.getContentPane().add(panel_1, BorderLayout.SOUTH);

		JButton btnNewButton = new JButton("Select Excel File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser	fileChooser	= new JFileChooser();
				int				returnVal	= fileChooser.showOpenDialog(frmFamilytree);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					List<String>	arguments	= new ArrayList<>();

//					String[]		args		= { "-input", "bushnaq" };
					File			file		= fileChooser.getSelectedFile();
					String			fileName	= file.getAbsolutePath();
					arguments.add("-input");
					arguments.add(fileName);
					if (vtreeRadioButton.isSelected())
						arguments.add("-v");
					if (htreeRadioButton.isSelected())
						arguments.add("-h");
					if (followFemalesRadioButton.isSelected())
						arguments.add("-follow_females");
					if (excludeSpousesCheckBox.isSelected())
						arguments.add("-exclude_spouse");
					String[] args = arguments.toArray(new String[0]);
					try {
						BufferedImage	image			= main.start(args);
						String			inputName		= context.getParameterOptions().getInput();
						String			outputDecorator	= context.getParameterOptions().getOutputDecorator();
						String			outputName		= inputName + outputDecorator;
						showImage(image, outputName);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						String message = String.format("%s", e.getMessage());
						textPane.setText(message);
					}
				} else {
					System.out.println("File access cancelled by user.");
				}
			}
		});
		panel_1.add(btnNewButton);

		JPanel panel_2 = new JPanel();
		frmFamilytree.getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		textPane = new JTextPane();
		textPane.setEnabled(false);
		textPane.setEditable(false);
		panel_2.add(textPane, BorderLayout.NORTH);
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

}
