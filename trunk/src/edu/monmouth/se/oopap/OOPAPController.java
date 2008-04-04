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
import edu.monmouth.se.oopap.io.ConsoleUtil;
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
   * Primary constructor. 
   */
  public OOPAPController()
  {

    // initialize the list to hold the reports
    reportList = new ArrayList<ReportType>();

    // add the available reports to the list
    reportList.add(ReportType.LineCountByPCO);

  }

  /**
   * Method used to run the analysis of the source. The application will
   * run an analysis on all of the files within a directory with the provided
   * extension. All the files within the directory are considered to be part
   * of the same program.
   * 
   * @param theSourcePath The path containing the file(s) to be analyzed
   * @param theSourceExtension The extension of the files to be analyzed
   * @throws IOException If there are any problems reading data from the 
   * file.
   * @throws UnhandledReportTypeException If a report type is passed to the 
   * method, but is not handled by the application.
   */
  public void runAnalysis(String theSourcePath, String theSourceExtension)
      throws IOException, UnhandledReportTypeException
  {

    // list that contains the list of source files to be analyzed
    // obtained by the file util based on the provided path and extension
    List<File> sourceFileList = new ArrayList<File>();
    // Map of lists to contain the contents of each file. They key is the
    // filename, the value is the contents of that file
    Map<String, List<String>> sourceContentsMap = 
        new HashMap<String, List<String>>();
    // SourceAnalyer to be used to analyze the source
    SourceAnalyzer sourceAnalyzer;

    // Get the list of files from the path provided by the application
    sourceFileList = 
        FileUtil.getSourceFileList(theSourcePath, theSourceExtension);

    // Iterate through the entire list of files provided and obtain the
    // source contents of each file.
    for (File currFile : sourceFileList)
    {

      //get the contents of the current source file and add it to the map
      List<String> currFileContents = FileUtil.getSourceFileContents(currFile);

      sourceContentsMap.put(currFile.getName(), currFileContents);

    }

    // iterate through the list of report types and run the analysis on all
    // of the files in the source contents map
    for (ReportType currReport : reportList)
    {

      // get the concrete implementation of the
      sourceAnalyzer = SourceAnalyzerFactory
          .getConcreteSourceAnalyzer(currReport);

      // run the analysis on the map of source contents
      sourceAnalyzer.analyzeSource(sourceContentsMap);

      List<String> reportContents = sourceAnalyzer.generateConsoleReport();
      
      ConsoleUtil.displayReport(reportContents);

    }

  }

}
