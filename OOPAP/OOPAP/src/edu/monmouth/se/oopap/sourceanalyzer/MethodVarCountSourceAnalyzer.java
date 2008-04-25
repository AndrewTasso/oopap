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
 *
 * This class finds the number of variables in all the methods.
 *
 * @author Jaidev Kochunni (with some source code from 
 * LineCountSourceAnalyzer by Andrew Tasso
 * @version %I% %G%
 */
public class MethodVarCountSourceAnalyzer extends SourceAnalyzer
{

  private Map<String, Map<String, Integer>> classOperationLinesMap;
  private Map<String, Integer> classLinesMap;
  
  /**
   * Default constructor which calls the init method called resetAnalysis. 
   */
  public MethodVarCountSourceAnalyzer()
  {
    this.resetAnalysis();
  }
  
  /**
   * Method used to reset the analysis of the analyzer. Resets the
   * data members of the class.
   */  
  private void resetAnalysis ()
  {
    this.classOperationLinesMap = new HashMap<String, Map<String, Integer>>();
    this.classLinesMap = new HashMap<String, Integer>();
  }
  
  /**
   * Analyzes the source going line by line.  
   * For each method LOC we find, check if that line is a variable declaration by calling
   * the appropriate LinAnalyzer method.  If so, increment the count of the variables for this method.
   */
  public void analyzeSource(Map<String, List<String>> theSourceMap)
  {

    //reset the previous analysis
    this.resetAnalysis();

    // integer to hold the current physical LOC for the current class
    int currClassLines = 0;
    // Set to hold the list of key values (source file names) for the current
    // map of source files.
    Set<String> sourceKeySet = theSourceMap.keySet();
    // Stack to store braces
    Stack<LineType> braceStack = new Stack<LineType>();

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

	  // integer to hold the number of variables in the current operation.
      int currVariableCount = 0;

      // LineType for the current line
      LineType currLineType = LineType.Unknown;
      // Reset the ClassLOC
      currClassLines = 0;
	  
      // Map to hold the operation to line count association
      Map<String, Integer> operationCountMap = new HashMap<String, Integer>();

      Map<String, Integer> operationToVariablesMap = new HashMap<String, Integer>();
      
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

          case SingleLineLogical:
          {
            
            //  If we're in a method, then check if this line is actually a variable declaration.
            if (currOperationName.length() != 0)
            {
              if (LineAnalyzer.isVariableDeclaration(currLine))
              {
                currVariableCount ++;
              }
            }
            
          }
          break;
        
          case MethodDeclaration:
          {
            // get the name of the method being declared
            currOperationName = LineAnalyzer.getOperationName(currLine);
  
            // reset the LOC for the operation
            currOperationLines = 1;
            currVariableCount = 0;
          }
          break;

          case OpeningBrace:
          {
            // only add a brace to the stack if we have encountered an operation
            // declaration. If the current operation name is "" it means that
            // it has not been set since no operations have been encountered yet
            if (!currOperationName.equals(""))
            {
  
              braceStack.push(currLineType);
              
  
            }
          }
          break;

          case ClosingBrace:
          {
            // pop the opening brace from the stack only if there are items
            // left on the stack. This is to ensure that no exception is thrown
            // when the last brace in a class is encountered.
            if (!braceStack.empty())
            {
  
              //pop the previous brace from the stack
              braceStack.pop();
  
              // check to see if the stack is now empty. If it is the end of
              // the operation has been encountered. Add the operation and
              // count to the map.
              if (braceStack.empty())
              {
  
                operationCountMap.put(currOperationName, currOperationLines);
                
                operationToVariablesMap.put(currOperationName, currVariableCount);
                currVariableCount = 0;
  
              }
  
            }
          }
          break;

        }

        currClassLines++;
        currOperationLines++;

      }

      
      // add the map of operation to line count association to the class to
      // line count association map
      //classOperationLinesMap.put(currSourceFileName, operationCountMap);
      classOperationLinesMap.put(currSourceFileName, operationToVariablesMap);
      classLinesMap.put(currSourceFileName, currClassLines);

     
    }
  }

  /**
   * Generates the console report - printing the LCOM value per class.
   */
  public List<String> generateConsoleReport()
  {
    
    List<String> consoleReport = new ArrayList<String>();

    consoleReport.add("Number of Variables in Methods:");
    consoleReport.add("");
    
    Set<String> classKeySet = this.classOperationLinesMap.keySet();
    
    
    for (String currClassKey : classKeySet)
    {
      Map<String, Integer> operationLinesMap = this.classOperationLinesMap.get(currClassKey);
      // Set of strings to hold all of the keys (operation names) in the map
      // so that it may be iterated through.
      Set<String> operationKeySet = operationLinesMap.keySet();
    
      // add the class name to the output
      consoleReport.add("    " + currClassKey);
    
      Integer totalVarsInClass = 0;
      
      // Iterate of the entire set of operations.
      for (String currOperationName : operationKeySet)
      {
    
        Integer numVars = (Integer) operationLinesMap.get(currOperationName);
        
        // Add the operation name followed by the number of variables in that
        // operation. 
        consoleReport.add("        " + currOperationName + ": "
            + numVars);
        
        totalVarsInClass += numVars;
    
      }
    
      // Add the class total to the output
      consoleReport.add("    Class Total: " +totalVarsInClass);
      consoleReport.add("");
    }
    
    return consoleReport;
    
  }



  /**
   * @param currSourceFile
   * @param fieldsToMethodsMap
   * @return

   */
  public List<List<String>> generateWorksheetReport()
  {

    List<List<String>> worksheetReport = new ArrayList<List<String>>();

    worksheetReport.add(new ArrayList<String>());

    return worksheetReport;
    
  }  

  
  
}
