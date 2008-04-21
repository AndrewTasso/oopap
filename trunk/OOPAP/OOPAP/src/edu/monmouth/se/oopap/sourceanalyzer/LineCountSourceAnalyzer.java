package edu.monmouth.se.oopap.sourceanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.monmouth.se.oopap.enumerator.LineType;
import edu.monmouth.se.oopap.analyzer.LineAnalyzer;

/**
 * This class is responsible for counting the total lines of a program/class.
 * Line counts are performed in total for the program, classes, and operations.
 * Any line (whether LOC or a blank line is considered to be a line in this
 * count.) The line count for an operation begins at the operation declaration
 * and ends at the final '}' ending the operation. The line count for the class
 * includes the operation line counts, and any additional lines as part of the
 * class such as import statements and method headers. Operation headers are not
 * counted as part of the operation count, but are counted as part of the class
 * count. The program line count includes all of the lines that are counted as
 * part of the class(es).
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public class LineCountSourceAnalyzer extends SourceAnalyzer
{

  /**
   * Integer to hold the number of lines within the program.
   */
  private int programLines;

  /**
   * Map to store the class to operation association. The key is the class name
   * the value is a map which contains the operation names as the key and the
   * line counts as the value.
   */
  private Map<String, Map<String, Integer>> classOperationLinesMap;

  /**
   * Map to store the class to line count association. The key is the class name
   * the value is the number of lines in that class.
   */
  private Map<String, Integer> classLinesMap;

  /**
   * Default constructor for the line count analyzer.
   */
  public LineCountSourceAnalyzer()
  {

    // use the reset analysis method to initialize the data members of the class
    this.resetAnalysis();

  }

  /**
   * Method used to reset the analysis of the analyzer. Resets the data members
   * of the class.
   */
  private void resetAnalysis()
  {

    this.programLines = 0;
    this.classOperationLinesMap = new HashMap<String, Map<String, Integer>>();
    this.classLinesMap = new HashMap<String, Integer>();

  }

  /**
   * Method responsible to analyzing the source code. After the report is run
   * the report methods must be used to obtain the results of the report.
   * 
   * @param theSourceMap
   *          The classname to class contents assocation map. The key is the
   *          class name. The value is a List of Strings representing the
   *          contents of the class.
   */
  public void analyzeSource(Map<String, List<String>> theSourceMap)
  {

    // reset the previous analysis
    this.resetAnalysis();

    // integer to hold the current physical LOC for the current class
    int currClassLines = 0;
    // Set to hold the list of key values (source file names) for the current
    // map of source files.
    Set<String> sourceKeySet = theSourceMap.keySet();
    // Stack to store braces
    Stack<LineType> braceStack = new Stack<LineType>();

    // Iterate through the entire list of files in the program
    // The algorithm for counting is as follows. For every line in the file
    // both the class and program count are incremented. Every time a new
    // class is analyzed the class line counter is reset. When a operation
    // declaration is encountered the operation count begins. As the counter
    // iterates through the operation, it pushes a opening braces onto the
    // brace stack and pops when a closing brace is encountered. When the
    // stack is empty again, we know we've reached the end of the method and
    // the count can stop.
    for (String currSourceFileName : sourceKeySet)
    {

      // get the contents of the current file
      List<String> currFileContents = theSourceMap.get(currSourceFileName);

      // String to hold the current line of the file
      String currLine = "";
      // String to hold the name of the current method being analyzed
      String currOperationName = "";
      // integer to hold the current physical LOC for the current operation
      int currOperationLines = 0;
      // LineType for the current line
      LineType currLineType = LineType.Unknown;
      // Reset the ClassLOC
      currClassLines = 0;
      // Map to hold the operation to line count association
      Map<String, Integer> operationCountMap = new HashMap<String, Integer>();

      // iterate over the entire content of the file, analyzing each line
      // and incrementing the proper counter when appropriate.

      for (int i = 0; i < currFileContents.size(); i++)
      {

        // get the current line
        currLine = currFileContents.get(i);

        // determine the current line type
        currLineType = LineAnalyzer.getLineType(currLine);

        // Check the current line type. With this analysis we are concerned with
        // class declarations so the name may be retrieved, method declarations
        // for the purpose of resetting the LOC count by operation and getting
        // the name
        switch (currLineType)
        {

        case MethodDeclaration:

          // get the name of the method being declared
          currOperationName = LineAnalyzer.getOperationName(currLine);

          // reset the LOC for the operation
          currOperationLines = 1;

          break;

        case OpeningBrace:

          // only add a brace to the stack if we have encountered an operation
          // declaration. If the current operation name is "" it means that
          // it has not been set since no operations have been encountered yet
          if (!currOperationName.equals(""))
          {

            braceStack.push(currLineType);

          }

          break;

        case ClosingBrace:

          // pop the opening brace from the stack only if there are items
          // left on the stack. This is to ensure that no exception is thrown
          // when the last brace in a class is encountered.
          if (!braceStack.empty())
          {

            // pop the previous brace from the stack
            braceStack.pop();

            // check to see if the stack is now empty. If it is the end of
            // the operation has been encountered. Add the operation and
            // count to the map.
            if (braceStack.empty())
            {

              operationCountMap.put(currOperationName, currOperationLines);

            }

          }

          break;

        }

        this.programLines++;
        currClassLines++;
        currOperationLines++;

      }

      // add the map of operation to line count association to the class to
      // line count association map
      classOperationLinesMap.put(currSourceFileName, operationCountMap);
      classLinesMap.put(currSourceFileName, currClassLines);

    }

  }

  /**
   * Method responsible for generating a report ready to be output to the
   * console.
   * 
   * @return a List of String containing the console output.
   */
  public List<String> generateConsoleReport()
  {

    // List to hold the string contents
    List<String> reportContents = new ArrayList<String>();
    // Set of strings to hold all of the keys (class names) in the map so that
    // it may be iterated through.
    Set<String> classKeySet = this.classOperationLinesMap.keySet();
    
    // add the title to the report
    reportContents.add("Line Count by Program Class and Operation\n");    

    // Iterate over the entire class to operation association map.
    for (String currClassKey : classKeySet)
    {

      // Map to hold the list of operations for the current class
      Map<String, Integer> operationLinesMap = this.classOperationLinesMap
          .get(currClassKey);
      // Set of strings to hold all of the keys (operation names) in the map
      // so that it may be iterated through.
      Set<String> operationKeySet = operationLinesMap.keySet();

      // add the class name to the output
      reportContents.add("  " + currClassKey);

      // Iterate of the entire set of operations.
      for (String currOperationName : operationKeySet)
      {

        // Add the operation name followed by the number of lines in that
        // operation. Get the line count from the operations lines map.
        reportContents.add("    " + currOperationName + ": "
            + operationLinesMap.get(currOperationName));

      }

      // Add the class total to the output
      reportContents.add("  Class Total: " + classLinesMap.get(currClassKey)
          + "\n");

    }

    // Add the program total to the output
    reportContents.add("Program Total: " + programLines);

    return reportContents;

  }

  /**
   * Method responsible for generating a 2 dimensional array of string ready to
   * be written to a work sheet within an excel workbook. The contents of the 2
   * dimensional array will directly reflect the contents of the workbook. Each
   * nested array represents a line within the work sheet.
   * 
   * The first column in the output represents the name of the classes being
   * analyzed. The second column in the output represents the name of the
   * operations within the class. The third column will contain the line counts
   * for each class, operation and a final line count for the current program.
   * 
   * 
   * @return the 2 dimensional array of strings ready to be written to the
   *         workbook.
   */
  public List<List<String>> generateWorksheetReport()
  {
    // the 2 dimensional array containing the output of the method
    List<List<String>> worksheetReport = new ArrayList<List<String>>();
    // Set of strings to hold all of the keys (class names) in the map so that
    // it may be iterated through.
    Set<String> classKeySet = this.classOperationLinesMap.keySet();

    // list containing the contents of the current row
    List<String> currRow = new ArrayList<String>();

    // add the column headings to the topmost row
    currRow.add("Class Name");
    currRow.add("Operation Name");
    currRow.add("Line Count");
    // add the row to the work sheet
    worksheetReport.add(currRow);

    // reset the row
    currRow = new ArrayList<String>();

    // Iterate over the entire class to operation association map.
    for (String currClassKey : classKeySet)
    {

      // Map to hold the list of operations for the current class
      Map<String, Integer> operationLinesMap = this.classOperationLinesMap
          .get(currClassKey);
      // Set of strings to hold all of the keys (operation names) in the map
      // so that it may be iterated through.
      Set<String> operationKeySet = operationLinesMap.keySet();

      // Iterate of the entire set of operations.
      for (String currOperationName : operationKeySet)
      {

        // reset the row
        currRow = new ArrayList<String>();

        // add the elements to the current row
        currRow.add(currClassKey);
        currRow.add(currOperationName);
        currRow.add(operationLinesMap.get(currOperationName).toString());

        // add the row to the work sheet
        worksheetReport.add(currRow);

      }

      // reset the current row
      currRow = new ArrayList<String>();

    }

    // add a blank row
    worksheetReport.add(new ArrayList<String>());

    // Iterate of the entire set of operations.
    for (String currClassKey : classKeySet)
    {

      // add the class name, a empty cell and the class count to the output
      currRow.add(currClassKey);
      currRow.add("");
      currRow.add(this.classLinesMap.get(currClassKey).toString());
      // add the row to the work sheet
      worksheetReport.add(currRow);

      // reset the current row
      currRow = new ArrayList<String>();

    }

    // add a blank row
    worksheetReport.add(new ArrayList<String>());

    // add the program total to the work sheet
    currRow.add("Program Total");
    currRow.add("");
    currRow.add(this.programLines + "");
    worksheetReport.add(currRow);

    return worksheetReport;

  }

}
