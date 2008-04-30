package edu.monmouth.se.oopap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.JLabel;

/**
 * Primary Graphical User Interface for the OOPAP application.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public class OOPAPGUI extends JFrame
{

  private JTextField inputPathTextField;
  private JTextField outputPathTextField;  
  private JTextField studentNameTextField;
  private JTextField projectNameTextField;
  private OOPAPController controller;
  private JFileChooser fileChooser;
  private String inputPath;
  private String outputPath;
  private static final String sourceExtension = ".java";
  
  /**
   * 
   */
  private OOPAPConsole console;
  
  /**
   * Create the frame
   */
  public OOPAPGUI(OOPAPController theController)
  {
  
    super();

    this.controller = theController;
    this.fileChooser = new JFileChooser();
    this.inputPath = System.getProperty("user.dir");
    this.outputPath = System.getProperty("user.dir");
    this.console = new OOPAPConsole();
    this.studentNameTextField = new JTextField();
    this.projectNameTextField = new JTextField();
    this.outputPathTextField = new JTextField();
    JLabel inputLabel = new JLabel("Input:");
    JLabel outputLabel = new JLabel("Output:");    
    JLabel studentNameLabel = new JLabel("Student Name:");
    JLabel projectNameLabel = new JLabel("Project Name:");

    getContentPane().setLayout(null);
    setBounds(100, 100, 510, 180);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    super.setTitle("OOPAP");

    //Bound the labels for the student an project name and add
    //them to the interface
    inputLabel.setBounds(10, 10, 50, 20);
    getContentPane().add(inputLabel);
    outputLabel.setBounds(10, 40, 50, 20);
    getContentPane().add(outputLabel);
    getContentPane().add(inputLabel);
    studentNameLabel.setBounds(10, 80, 100, 20);
    getContentPane().add(studentNameLabel);
    projectNameLabel.setBounds(10, 115, 100, 20);
    getContentPane().add(projectNameLabel);
    
    //set the bounds for the student and project name text fields
    this.studentNameTextField.setBounds(110, 80, 175, 26);
    getContentPane().add(this.studentNameTextField);
    this.projectNameTextField.setBounds(110, 115, 175, 26);
    getContentPane().add(this.projectNameTextField);

    
    inputPathTextField = new JTextField();
    inputPathTextField.setBounds(65, 10, 400, 25);
    getContentPane().add(inputPathTextField);
    this.inputPathTextField.setText(inputPath);

    final JButton chooseInputPathButton = new JButton();
    chooseInputPathButton.addActionListener(new ActionListener()
    {

      public void actionPerformed(final ActionEvent e)
      {

        selectFile();

      }

    });

    chooseInputPathButton.setText("...");
    chooseInputPathButton.setBounds(470, 10, 34, 26);
    getContentPane().add(chooseInputPathButton);
    
    outputPathTextField.setBounds(65, 40, 400, 25);
    getContentPane().add(outputPathTextField);
    this.outputPathTextField.setText(outputPath);
    
    final JButton chooseOutputPathButton = new JButton();
    chooseOutputPathButton.addActionListener(new ActionListener()
    {

      public void actionPerformed(final ActionEvent e)
      {

        selectOutputPath();

      }

    });
    chooseOutputPathButton.setText("...");
    chooseOutputPathButton.setBounds(470, 40, 34, 26);
    getContentPane().add(chooseOutputPathButton);    
    
    final JButton runAnalysisButton = new JButton();
    runAnalysisButton.addActionListener(new ActionListener()
    {

      public void actionPerformed(final ActionEvent arg0)
      {

        try
        {

          
          controller.clearFullConsoleReport();
        	
          inputPath = inputPathTextField.getText();
          controller.runAnalysis(inputPath, sourceExtension,
                                 outputPath,
                                 studentNameTextField.getText(),
                                 projectNameTextField.getText());
          
          List<String> fullConsoleReport = controller.getFullConsoleReport();
          console.displayConsoleReport(fullConsoleReport);
          console.setVisible(true);
          

        } catch (Exception e)
        {

          e.printStackTrace();

        }

      }

    });
    runAnalysisButton.setText("Run Analysis");
    runAnalysisButton.setBounds(335, 95, 150, 26);
    getContentPane().add(runAnalysisButton);    
    
    this.setVisible(true);
  }

  protected void selectFile()
  {
   
    this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    fileChooser.setCurrentDirectory(new File(this.inputPath));
    int returnVal = fileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {

      File file = fileChooser.getSelectedFile();
      inputPath = file.toString();
      inputPathTextField.setText(inputPath);

    }

  }
  
  protected void selectOutputPath()
  {

    this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    fileChooser.setCurrentDirectory(new File(this.outputPath));
    int returnVal = fileChooser.showOpenDialog(this);

    if (returnVal == JFileChooser.APPROVE_OPTION)
    {

      File file = fileChooser.getSelectedFile();
      this.outputPath = file.toString();
      this.outputPathTextField.setText(outputPath);

    }

  }
  
}
