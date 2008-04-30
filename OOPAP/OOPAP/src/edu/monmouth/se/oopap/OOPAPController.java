package edu.monmouth.se.oopap;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import edu.monmouth.se.oopap.enumerator.ReportType;
import edu.monmouth.se.oopap.exception.UnhandledReportTypeException;
import edu.monmouth.se.oopap.io.FileUtil;
import edu.monmouth.se.oopap.io.CSVUtil;
import edu.monmouth.se.oopap.sourceanalyzer.SourceAnalyzer;
import edu.monmouth.se.oopap.sourceanalyzer.SourceAnalyzerFactory;

/**
 * Primary controller for the OOPAP Application. Responsible for executing the
 * analysis of the source.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public class OOPAPController
{

  /**
   * List to hold the report types to be performed.
   */
  List<ReportType> reportList;
  
  /**
   * List to contain the full console report containing all of the 
   * reports run by the controller
   */
  List<String> fullConsoleReport;
  
  /**
   * Primary constructor.
   */
  public OOPAPController()
  {

    fullConsoleReport = new ArrayList<String>();
    // initialize the list to hold the reports
    reportList = new ArrayList<ReportType>();

    // add the available reports to the list
    // 1st release reports
    reportList.add(ReportType.LineCountByPCO);
    // 2nd release reports
    reportList.add(ReportType.PSPPhysicalLOCByPCO);
    reportList.add(ReportType.PSPLogicalLOCByPCO);
    reportList.add(ReportType.LackOfCohesionInMethods);
    // 3rd release reports
    reportList.add(ReportType.PSPClassOperation);
    reportList.add(ReportType.DepthOfInheritanceTree);
    reportList.add(ReportType.NumberOfChildren);
    reportList.add(ReportType.PrivateVarCountByPC);
    reportList.add(ReportType.PublicVarCountByPC);
    reportList.add(ReportType.MethodVarCountByPC);
    //4th release reports
    reportList.add(ReportType.CommentLines);
    reportList.add(ReportType.CommentedLines);
    reportList.add(ReportType.SumOfCommentAndCommented);
    reportList.add(ReportType.ResponseForAClass);
    reportList.add(ReportType.CodingViolation);
    

  }

  /**
   * Method used to run the analysis of the source. The application will run an
   * analysis on all of the files within a directory with the provided
   * extension. All the files within the directory are considered to be part of
   * the same program.
   * 
   * @param theSourcePath
   *          The path containing the file(s) to be analyzed
   * @param theSourceExtension
   *          The extension of the files to be analyzed
   * @throws IOException
   *           If there are any problems reading data from the file.
   * @throws UnhandledReportTypeException
   *           If a report type is passed to the method, but is not handled by
   *           the application.
   */
  public void runAnalysis(String theSourcePath, String theSourceExtension,
          String theOutputPath,
          String theStudentName, String theProjectName)
      throws IOException, UnhandledReportTypeException
  {

    // list that contains the list of source files to be analyzed
    // obtained by the file util based on the provided path and extension
    List<File> sourceFileList = new ArrayList<File>();
    // Map of lists to contain the contents of each file. They key is the
    // filename, the value is the contents of that file
    Map<String, List<String>> sourceContentsMap = new HashMap<String, List<String>>();
    // SourceAnalyer to be used to analyze the source
    SourceAnalyzer sourceAnalyzer;
    //File to point to the directory the csv files are going to be output to
    File newOutputPath = new File(theOutputPath);

    // Get the list of files from the path provided by the application
    sourceFileList = FileUtil.getSourceFileList(theSourcePath,
        theSourceExtension);
    
    //create the directory that is going to contain the csv reports. This will
    //be in the format of .../<Project Name>/<Student Name>
    newOutputPath = FileUtil.mkDir(newOutputPath, theProjectName);
    newOutputPath = FileUtil.mkDir(newOutputPath, theStudentName);
    
    //add the student and project name to the full console report
    this.fullConsoleReport.add("Student Name: " + theStudentName);
    this.fullConsoleReport.add("Project Name: " + theProjectName);
    this.fullConsoleReport.add("");

    // Iterate through the entire list of files provided and obtain the
    // source contents of each file.
    for (File currFile : sourceFileList)
    {

      // get the contents of the current source file and add it to the map
      List<String> currFileContents = FileUtil.getSourceFileContents(currFile);

      sourceContentsMap.put(currFile.getName(), currFileContents);

    }

    // iterate through the list of report types and run the analysis on all
    // of the files in the source contents map
    for (ReportType currReport : reportList)
    {

      File outputFile = new File(newOutputPath, theStudentName + "_" + theProjectName + "_" + currReport + ".csv");
      
      // get the concrete implementation of the
      sourceAnalyzer = SourceAnalyzerFactory
          .getConcreteSourceAnalyzer(currReport);

      // run the analysis on the map of source contents
      sourceAnalyzer.analyzeSource(sourceContentsMap);

      List<String> consoleReportContents = 
    	  sourceAnalyzer.generateConsoleReport();
      
      List<String> csvReportContents = CSVUtil.generateCSVReport(
          sourceAnalyzer.generateWorksheetReport());

      //add the current analyzers contents to the entire console report
      this.fullConsoleReport.addAll(consoleReportContents);
      this.fullConsoleReport.add("\n");
      
      //write the csv report to disk
      FileUtil.writeReport(outputFile, csvReportContents);
      
    }

  }
  
  /**
   * Method to return the full console report. The full console report
   * contains the entire list of SourceAnalyzer console reports, one
   * for each of the source analyzers executed.
   * 
   * @return The full console report ready to be written to the console
   */
  public List<String> getFullConsoleReport()
  {
    
    return this.fullConsoleReport;
    
  }
  
  /**
   * Method to clear the full console report. The full console report
   * contains the entire list of SourceAnalyzer console reports, one
   * for each of the source analyzers executed. This function clears it,
   * so only one analysis run is lised in the console at a time.
   * 
   * @return nothing
   */
  public void clearFullConsoleReport()
  {
    
    this.fullConsoleReport = new ArrayList<String>();
    
  }

}
