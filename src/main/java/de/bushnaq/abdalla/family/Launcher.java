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
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

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
		frmFamilytree.setBounds(100, 100, 734, 452);
		frmFamilytree.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		frmFamilytree.getContentPane().add(panel, BorderLayout.CENTER);
		
		JPanel panelOptions = new JPanel();
		panelOptions.setBorder(new EmptyBorder(10, 10, 10, 10));
		panel.add(panelOptions, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 283, 283, 0 };
		gbl_panel.rowHeights = new int[] { 23, 0, 23, 0, 23, 23, 23, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelOptions.setLayout(gbl_panel);

		JLabel				lblNewLabel		= new JLabel("Tree Type");
		GridBagConstraints	gbc_lblNewLabel	= new GridBagConstraints();
		gbc_lblNewLabel.gridheight = 2;
		gbc_lblNewLabel.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panelOptions.add(lblNewLabel, gbc_lblNewLabel);

		vtreeRadioButton = new JRadioButton("Vertical tree");
		vtreeRadioButton.setSelected(true);
		treeGroup.add(vtreeRadioButton);
		GridBagConstraints gbc_vtreeRadioButton = new GridBagConstraints();
		gbc_vtreeRadioButton.fill = GridBagConstraints.BOTH;
		gbc_vtreeRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_vtreeRadioButton.gridx = 1;
		gbc_vtreeRadioButton.gridy = 0;
		panelOptions.add(vtreeRadioButton, gbc_vtreeRadioButton);

		htreeRadioButton = new JRadioButton("Horizontal tree");
		treeGroup.add(htreeRadioButton);
		GridBagConstraints gbc_htreeRadioButton = new GridBagConstraints();
		gbc_htreeRadioButton.anchor = GridBagConstraints.WEST;
		gbc_htreeRadioButton.fill = GridBagConstraints.VERTICAL;
		gbc_htreeRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_htreeRadioButton.gridx = 1;
		gbc_htreeRadioButton.gridy = 1;
		panelOptions.add(htreeRadioButton, gbc_htreeRadioButton);

		JLabel				lblNewLabel_1		= new JLabel("Children position");
		GridBagConstraints	gbc_lblNewLabel_1	= new GridBagConstraints();
		gbc_lblNewLabel_1.gridheight = 2;
		gbc_lblNewLabel_1.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 0;
		gbc_lblNewLabel_1.gridy = 2;
		panelOptions.add(lblNewLabel_1, gbc_lblNewLabel_1);

		followMalesRadioButton = new JRadioButton("Follow males");
		followMalesRadioButton.setSelected(true);
		GridBagConstraints gbc_followMalesRadioButton = new GridBagConstraints();
		gbc_followMalesRadioButton.fill = GridBagConstraints.BOTH;
		gbc_followMalesRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_followMalesRadioButton.gridx = 1;
		gbc_followMalesRadioButton.gridy = 2;
		panelOptions.add(followMalesRadioButton, gbc_followMalesRadioButton);

		followFemalesRadioButton = new JRadioButton("Follow females");
		GridBagConstraints gbc_followFemalesRadioButton = new GridBagConstraints();
		gbc_followFemalesRadioButton.fill = GridBagConstraints.BOTH;
		gbc_followFemalesRadioButton.insets = new Insets(0, 0, 5, 0);
		gbc_followFemalesRadioButton.gridx = 1;
		gbc_followFemalesRadioButton.gridy = 3;
		panelOptions.add(followFemalesRadioButton, gbc_followFemalesRadioButton);

		JLabel				lblNewLabel_2		= new JLabel("Spouses from other families");
		GridBagConstraints	gbc_lblNewLabel_2	= new GridBagConstraints();
		gbc_lblNewLabel_2.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 4;
		panelOptions.add(lblNewLabel_2, gbc_lblNewLabel_2);

		excludeSpousesCheckBox = new JCheckBox("Exclude spouses");
		GridBagConstraints gbc_excludeSpousesCheckBox = new GridBagConstraints();
		gbc_excludeSpousesCheckBox.fill = GridBagConstraints.BOTH;
		gbc_excludeSpousesCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_excludeSpousesCheckBox.gridx = 1;
		gbc_excludeSpousesCheckBox.gridy = 4;
		panelOptions.add(excludeSpousesCheckBox, gbc_excludeSpousesCheckBox);

		JLabel				lblNewLabel_3		= new JLabel("Original Language");
		GridBagConstraints	gbc_lblNewLabel_3	= new GridBagConstraints();
		gbc_lblNewLabel_3.fill = GridBagConstraints.BOTH;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 5;
		panelOptions.add(lblNewLabel_3, gbc_lblNewLabel_3);

		originalLanguageCheckBox = new JCheckBox("Show if available");
		GridBagConstraints gbc_originalLanguageCheckBox = new GridBagConstraints();
		gbc_originalLanguageCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_originalLanguageCheckBox.fill = GridBagConstraints.BOTH;
		gbc_originalLanguageCheckBox.gridx = 1;
		gbc_originalLanguageCheckBox.gridy = 5;
		panelOptions.add(originalLanguageCheckBox, gbc_originalLanguageCheckBox);

		JPanel panelButton = new JPanel();
		panel.add(panelButton, BorderLayout.SOUTH);

		JButton btnNewButton = new JButton("Select Excel File");
		btnNewButton.addActionListener(selectFile());
		panelButton.add(btnNewButton);

		JPanel panelText = new JPanel();
		panel.add(panelText, BorderLayout.CENTER);
		panelText.setLayout(new BorderLayout(0, 0));

		textPane = new JTextPane();
		textPane.setEnabled(false);
		textPane.setEditable(false);
		panelText.add(textPane, BorderLayout.NORTH);
	}

	private ActionListener selectFile() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser	fileChooser	= new JFileChooser();
				int				returnVal	= fileChooser.showOpenDialog(frmFamilytree);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					List<String>	arguments	= new ArrayList<>();
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
		};
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
