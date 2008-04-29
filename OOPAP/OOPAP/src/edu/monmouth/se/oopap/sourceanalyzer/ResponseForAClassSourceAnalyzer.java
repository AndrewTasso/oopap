package edu.monmouth.se.oopap.sourceanalyzer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.monmouth.se.oopap.analyzer.LineAnalyzer;
import edu.monmouth.se.oopap.enumerator.LineType;

/**
 * This class is responsible for analyzing the source code of a program and
 * producing a logical line count. A logical line count returns the number of
 * "useful" lines of code of each method and class, as well as a total count. In
 * particular, it does not count comments, blank lines, or logical lines of code
 * that cover multiple lines.
 * 
 * @author Kevin Gajdzis (with some source code from LineCountSourceAnalyzer by
 *         Andrew Tasso
 * @version %I% %G%
 */
public class ResponseForAClassSourceAnalyzer extends SourceAnalyzer
{
  // stores class to operation association
  private Map<String, Map<String, Integer>> classOperationLinesMap;
  // stores operation to line count association
  private Map<String, Integer> classLinesMap;

  /**
   * default constructor. Runs the resetAnalysis operation to initialize the
   * object
   */
  public ResponseForAClassSourceAnalyzer()
  {
    this.resetAnalysis();
  }

  /**
   * Method used to reset the analysis of the analyzer. Resets the data members
   * of the class.
   */
  private void resetAnalysis()
  {
    this.classOperationLinesMap = new HashMap<String, Map<String, Integer>>();
    this.classLinesMap = new HashMap<String, Integer>();
  }

  /**
   * analyzes a given source for logical LOC. reads in line types and classifies
   * them as valid logical LOC as required.
   */
  public void analyzeSource(Map<String, List<String>> theSourceMap)
  {
    // reset the previous analysis
    this.resetAnalysis();

    // integer to hold the current physical LOC for the current class
    int currClassResponse = 0;
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
      
      List<String> operationNames = new LinkedList<String>();

      // String to hold the current line of the file
      String currLine = "";
      // String to hold the name of the current method being analyzed
      String currOperationName = "";
      // integer to hold the current physical LOC for the current operation
      int currOperationLines = 0;
      // LineType for the current line
      LineType currLineType = LineType.Unknown;
      // Reset the ClassLOC
      currClassResponse = 0;
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
          operationNames.add(currOperationName);
          // reset the LOC for the operation
          currOperationLines = 1;
          
          //increment response for a class
          currClassResponse++;

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
        
        //increment response for this class for every method call
        if(LineAnalyzer.isMethodCall(currLine))
        {
          currClassResponse++;
        }

      // add the map of operation to line count association to the class to
      // line count association map
      classOperationLinesMap.put(currSourceFileName, operationCountMap);
      classLinesMap.put(currSourceFileName, currClassResponse);
      }
      for (int i = 0; i < currFileContents.size(); i++)
      {
        currLine = currFileContents.get(i);
        currLineType = LineAnalyzer.getLineType(currLine);
        for(int j=0; j < operationNames.size(); j++)
        {
          if (currLine.matches(".*"+operationNames.get(j)+"\\s*\\(.*") && !currLine.contains("public")
              && !currLine.contains("private") && !currLine.contains("protected"))
          {
            currClassResponse++;
            classLinesMap.put(currSourceFileName, currClassResponse);
          }
        }
      }
    }
  }

  /**
   * generates a report that writes the data collected to the console
   * 
   * @return
   */
  public List<String> generateConsoleReport()
  {

    // List to hold the string contents
    List<String> reportContents = new ArrayList<String>();
    // Set of strings to hold all of the keys (class names) in the map so that
    // it may be iterated through.
    Set<String> classKeySet = this.classOperationLinesMap.keySet();

    // add the title to the report
    reportContents.add("Response For A Class:\n");
    
    // Iterate over the entire class to operation association map.
    for (String currClassKey : classKeySet)
    {
      // add the class name to the output
      reportContents.add("    " + currClassKey);

      // Add the class total to the output
      reportContents.add("    Response Total For This Class: " + classLinesMap.get(currClassKey)
          + "\n");

    }

    return reportContents;

  }

  /**
   * Method responsible for generating a 2 dimensional array of string ready to
   * be written to a work sheet. The contents of the 2 dimensional array 
   * will directly reflect the contents of the workbook. Each
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
    currRow.add("Class Response");

    // add the row to the work sheet
    worksheetReport.add(currRow);
    
    // reset the row
    currRow = new ArrayList<String>();
    
    // add a blank row
    worksheetReport.add(new ArrayList<String>());

    // Iterate over the entire set of operations.
    //Add the class LOC total for each class
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

    return worksheetReport;

  }

}
