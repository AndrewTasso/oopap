package edu.monmouth.se.oopap.sourceanalyzer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.monmouth.se.oopap.enumerator.LineType;
import edu.monmouth.se.oopap.analyzer.LineAnalyzer;

/**
 * This class is responsible for counting the total number of comment lines of a 
 * program/class. Comment counts are performed in total for the program, classes, 
 * and operations. Percentages are also calculated (comments/comments+LogicalLOC).
 * The line count for an operation begins at the operation declaration
 * and ends at the final '}' ending the operation. The comment line count for the class
 * includes the operation comment line counts. Operation header comments are not
 * counted as part of the operation count, but are counted as part of the class
 * count. The program line count includes all of the comment lines that are counted as
 * part of the class(es).
 * 
 * @author Jason Schramm (based on LineCountSourceAnalyzer by Andrew Tasso, with some 
 *    logic  from PSPLogicalLOCSourceAnalyzer by Kevin Gajdzis)
 * @version %I% %G%
 */
public class CommentLinesSourceAnalyzer extends SourceAnalyzer
{

  /**
   * Integer to hold the number of comment lines within the program.
   */
  private int commentLines;

  /**
   * Map to store the class to operation association. The key is the class name
   * the value is a map which contains the operation names as the key and the
   * line counts as the value.
   */
  private Map<String, Map<String, Integer>> classOperationCommentLinesMap;

  /**
   * Map to store the class to line count association. The key is the class name
   * the value is the number of lines in that class.
   */
  private Map<String, Integer> classCommentLinesMap;
  
  // stores logical LOC for a given program
  private int programLogicalLOC;
  // stores class to operation association
  private Map<String, Map<String, Integer>> classLogicalOperationLinesMap;
  // stores operation to line count association
  private Map<String, Integer> classLogicalLinesMap;

  /**
   * Default constructor for the line count analyzer.
   */
  public CommentLinesSourceAnalyzer()
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
    this.commentLines = 0;
    this.classOperationCommentLinesMap = new HashMap<String, Map<String, Integer>>();
    this.classCommentLinesMap = new HashMap<String, Integer>();

    this.programLogicalLOC = 0;
    this.classLogicalOperationLinesMap = new HashMap<String, Map<String, Integer>>();
    this.classLogicalLinesMap = new HashMap<String, Integer>();
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

    // integer to hold the current comment line count for the current class
    int currClassCommentLines = 0;
    // integer to hold the current logical line count for the current class
    int currClassLogicalLines = 0;
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
      // integer to hold the current comment lines for the current operation
      int currOperationLines = 0;
      // integer to hold the current logical LOC for the current operation
      int currOperationLogicalLines = 0;
      //check if in an open comment
      boolean openCommentStatus = false;
      // integer to hold the current commented lines for the current operation
      //int currOperationCommentedLines = 0;
      // LineType for the current line
      LineType currLineType = LineType.Unknown;
      // Reset the Class comment line count
      currClassCommentLines = 0;
      // Reset the Class logical line count
      currClassLogicalLines = 0;
      // Map to hold the operation to comment line count association
      Map<String, Integer> operationCountMap = new HashMap<String, Integer>();
      // Map to hold the operation to logical line count association
      Map<String, Integer> operationCountMapLogical = new HashMap<String, Integer>();

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

          // reset the comment LOC for the operation
          currOperationLines = 0;
          //reset logical LOC for the operation
          currOperationLogicalLines = 1;

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
              operationCountMapLogical.put(currOperationName, currOperationLogicalLines);

            }

          }

          break;

        case Comment:
          if(currLine.trim().startsWith("//"))
          {
            this.commentLines++;
            currClassCommentLines++;
            currOperationLines++;
          }
          break;
          
        case OpenComment:
          if(currLine.trim().startsWith("/*"))
          {
            this.commentLines++;
            currClassCommentLines++;
            currOperationLines++;
            openCommentStatus = true;
          }
          break;
          
        case CloseComment:
          openCommentStatus = false;
          if(currLine.trim().endsWith("*/"))
          {
            this.commentLines++;
            currClassCommentLines++;
            currOperationLines++;
          }
          break;
        
        case MultiLineLogical:
          if(openCommentStatus)
          {
            this.commentLines++;
            currClassCommentLines++;
            currOperationLines++;
          }
          break;
        }
        
     // determine whether the current line is a logical LOC
        switch (currLineType)
        {

        // in the case of a comment, open comment, close comment, blank line,
        // beginning of multi-line logical
        // or unknown line type, do not increment logical loc count for
        // operation or method.
        case Comment:
        case OpenComment:
        case CloseComment:
        case Blank:
        case MultiLineLogical:
        case Unknown:
          break;

        // in the case of Class and method declarations, as well as open/close
        // braces and
        // single lines of logical code, increment the class, method, and
        // program line count
        case ClassDeclaration:
        case MethodDeclaration:
        case OpeningBrace:
        case ClosingBrace:
        case SingleLineLogical:
          this.programLogicalLOC++;
          currClassLogicalLines++;
          currOperationLogicalLines++;
          break;

        // in the case of package declarations and import statements, increment
        // program count
        // only because these statements occur at the header of a file and not
        // inside a class
        case PackageDeclaration:
        case ImportStatement:
          this.programLogicalLOC++;
        }
      }

      // add the map of operation to comment line count association to the class to
      // comment line count association map
      classOperationCommentLinesMap.put(currSourceFileName, operationCountMap);
      classCommentLinesMap.put(currSourceFileName, currClassCommentLines);
      // add the map of operation to logical line count association to the class to
      // logical line count association map
      classLogicalOperationLinesMap.put(currSourceFileName, operationCountMapLogical);
      classLogicalLinesMap.put(currSourceFileName, currClassLogicalLines);

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
    //set decimal format for percentages
    DecimalFormat df = new DecimalFormat("0.0");
    // List to hold the string contents
    List<String> reportContents = new ArrayList<String>();
    // Set of strings to hold all of the keys (class names) in the map so that
    // it may be iterated through.
    Set<String> classKeySet = this.classOperationCommentLinesMap.keySet();
    
    // add the title to the report
    reportContents.add("Comment Lines Count by Program Class and Operation\n");    

    // Iterate over the entire class to operation association map.
    for (String currClassKey : classKeySet)
    {

      // Map to hold the list of operations for the current class
      Map<String, Integer> operationLinesMap = this.classOperationCommentLinesMap
          .get(currClassKey);
      // Map to hold the list of operations for the current class
      Map<String, Integer> operationLinesMapLogical = this.classLogicalOperationLinesMap
          .get(currClassKey);
      // Set of strings to hold all of the keys (operation names) in the map
      // so that it may be iterated through.
      Set<String> operationKeySet = operationLinesMap.keySet();

      // add the class name to the output
      reportContents.add("    " + currClassKey);

      //variable to hold the comment and logical LOC total
      int operationCountTotal = 0;
      //variable to hold the comment total
      int operationCommentCount = 0;
      // Iterate of the entire set of operations.
      for (String currOperationName : operationKeySet)
      {
        operationCountTotal = operationLinesMap.get(currOperationName) + operationLinesMapLogical.get(currOperationName);
        operationCommentCount = operationLinesMap.get(currOperationName);
        // Add the operation name followed by the number of lines in that
        // operation. Get the line count from the operations lines map.
        reportContents.add("        " + currOperationName + ": "
            + operationCommentCount + " (" 
            + df.format(((double)operationCommentCount/operationCountTotal)*100) + "%)");

      }
      //store total number of comment lines and Logical LOC for each class
      double classTotal = classCommentLinesMap.get(currClassKey) + classLogicalLinesMap.get(currClassKey);

      // Add the class total to the output
      reportContents.add("    Class Total: " + classCommentLinesMap.get(currClassKey) 
          + " (" + df.format(classCommentLinesMap.get(currClassKey)/classTotal*100) + "%)" + "\n");

    }

    //store the percentage of comment lines to comment lines and logical lines for the program
    double programPercentage = (double)commentLines/(commentLines+programLogicalLOC)*100;
    // Add the program total to the output
    reportContents.add("Program Total: " + commentLines + " comment lines ("+ df.format(programPercentage) +"%)");

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
   * operations within the class. The third column will contain the comment line counts
   * for each class, operation and a final line count for the current program.
   * The fourth column will contain the logical line counts
   * for each class, operation and a final line count for the current program.
   * The fifth column will contain the percentage of comments to comments+Logical
   * lines for each class, operation and a final line count for the current program.
   * 
   * @return the 2 dimensional array of strings ready to be written to the
   *         workbook.
   */
  public List<List<String>> generateWorksheetReport()
  {
    //set decimal format for percentages
    DecimalFormat df = new DecimalFormat("0.0");
    // the 2 dimensional array containing the output of the method
    List<List<String>> worksheetReport = new ArrayList<List<String>>();
    // Set of strings to hold all of the keys (class names) in the map so that
    // it may be iterated through.
    Set<String> classKeySet = this.classOperationCommentLinesMap.keySet();

    // list containing the contents of the current row
    List<String> currRow = new ArrayList<String>();

    // add the column headings to the topmost row
    currRow.add("Class Name");
    currRow.add("Operation Name");
    currRow.add("Comment Line Count");
    currRow.add("Logical Line Count");
    currRow.add("Percentage of Comment Lines");
    // add the row to the work sheet
    worksheetReport.add(currRow);

    // reset the row
    currRow = new ArrayList<String>();

    // Iterate over the entire class to operation association map.
    for (String currClassKey : classKeySet)
    {

      // Map to hold the list of operations for the current class
      Map<String, Integer> operationLinesMap = this.classOperationCommentLinesMap
          .get(currClassKey);
      // Map to hold the list of operations for the current class (logical count)
      Map<String, Integer> operationLinesMapLogical = this.classLogicalOperationLinesMap
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
        currRow.add(operationLinesMapLogical.get(currOperationName).toString());

        //calculate the percentage of the operation that the comments take up
        Double rowPercentage = (double)operationLinesMap.get(currOperationName)/(operationLinesMapLogical.get(currOperationName)+operationLinesMap.get(currOperationName));
        rowPercentage = rowPercentage*100;
        //add percentage to the current row
        currRow.add(df.format(rowPercentage));
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
      currRow.add(this.classCommentLinesMap.get(currClassKey).toString());
      currRow.add(this.classLogicalLinesMap.get(currClassKey).toString());
      
      //calculate the percentage of the class that the comments take up
      Double rowPercentage = (double)this.classCommentLinesMap.get(currClassKey)/(this.classCommentLinesMap.get(currClassKey)+this.classLogicalLinesMap.get(currClassKey));
      rowPercentage = rowPercentage*100;
      //add percentage to the current row
      currRow.add(df.format(rowPercentage));
      
      
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
    currRow.add(this.commentLines + "");
    currRow.add(this.programLogicalLOC + "");
    
    double programPercentage = (double)this.commentLines/(this.commentLines+this.programLogicalLOC)*100;
    currRow.add(df.format(programPercentage));
    worksheetReport.add(currRow);

    return worksheetReport;

  }

}
