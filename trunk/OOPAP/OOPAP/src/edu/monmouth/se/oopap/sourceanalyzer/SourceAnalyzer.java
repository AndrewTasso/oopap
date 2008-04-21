package edu.monmouth.se.oopap.sourceanalyzer;

import java.util.List;
import java.util.Map;

/**
 * Abstract representation of a source analyzer.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public abstract class SourceAnalyzer
{

  /**
   * Method responsible to analyzing the source code. After the report is run
   * the report methods must be used to obtain the results of the report.
   * 
   * @param theSourceMap
   *          Theclass name to class contents association map. The key is the
   *          class name. The value is a List of Strings representing the
   *          contents of the class.
   */
  public abstract void analyzeSource(Map<String, List<String>> theSourceMap);

  /**
   * Method responsible for generating a report ready to be output to the
   * console.
   * 
   * @return a List of String containing the console output.
   */
  public abstract List<String> generateConsoleReport();

  /**
   * Method responsible for generating a 2 dimensional array of strings ready to
   * be written to a Comma Separate Value file. The contents of the 2
   * dimensional array will directly reflect the contents of the CSV file. 
   * 
   * @return the 2 dimensional array of strings ready to be written to the
   *         CSV file.
   */
  public abstract List<List<String>> generateWorksheetReport();

}
