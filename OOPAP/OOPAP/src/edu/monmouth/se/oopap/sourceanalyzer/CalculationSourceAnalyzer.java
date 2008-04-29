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
 * This class analyzes the calculations in the program.
 *
 * @author Jaidev Kochunni (with some source code from 
 * LineCountSourceAnalyzer by Andrew Tasso
 * @version %I% %G%
 */
public class CalculationSourceAnalyzer extends SourceAnalyzer
{
  private Map<String, Map<String, Integer>> classOperationLinesMap;
  private Map<String, Integer> classLinesMap;
  
  /**
   * Default constructor which calls the init method called resetAnalysis. 
   */
  public CalculationSourceAnalyzer()
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

    // integer to hold the number of calculations in the current operation.
      int currCalcCount = 0;
      int currOpCalcCount = 0;

      // LineType for the current line
      LineType currLineType = LineType.Unknown;
      // Reset the ClassLOC
      currClassLines = 0;
    
      // Map to hold the operation to line count association
      Map<String, Integer> operationCountMap = new HashMap<String, Integer>();
      Map<String, List<String>> operationToLinesWithOpsMap = new HashMap <String, List<String>>();

      List<String> linesWithOps = new ArrayList<String>();
      List<String> linesWithOpsForMethod = new ArrayList<String> ();
      
      // iterate over the entire content of the file, analyzing each line
      // and incrementing the proper counter when appropriate.

      for (int i = 0; i < currFileContents.size(); i++)
      {

        // get the current line
        currLine = currFileContents.get(i);

        // determine the current line type
        currLineType = LineAnalyzer.getLineType(currLine);

        switch (currLineType)
        {

          case SingleLineLogical:
          {
            int   numOps  = 0;
            
            if ( (numOps = LineAnalyzer.getNumOperators (currLine)) > 0)
            {
              if (currOperationName.length() != 0)
              {
                currOpCalcCount += numOps;
                
                linesWithOpsForMethod.add(currLine);
              }
              currCalcCount += numOps;
              linesWithOps.add(currLine);
            }
          }
          break;
        
          case MethodDeclaration:
          {
            // get the name of the method being declared
            currOperationName = LineAnalyzer.getOperationName(currLine);
  
            // reset the LOC for the operation
            currOperationLines = 1;
            
            currOpCalcCount = 0;
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
                
                operationToLinesWithOpsMap.put (currOperationName, linesWithOpsForMethod);
                linesWithOpsForMethod.clear();  
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
      classOperationLinesMap.put(currSourceFileName, operationCountMap);
      //classLinesMap.put(currSourceFileName, currClassLines);
      classLinesMap.put(currSourceFileName, currCalcCount);

     
    }
  }


  /**
   * Generates the console report.
   */
  public List<String> generateConsoleReport()
  {
    
    List<String> consoleReport = new ArrayList<String>();

    consoleReport.add("Number of Private Variables:");
    consoleReport.add("");
    
    Set<String> classKeySet = this.classLinesMap.keySet();
    
    Integer totalVars = 0;
    
    for (String currClassKey : classKeySet)
    {
      Integer privateVars = (Integer) classLinesMap.get(currClassKey);
        
        consoleReport.add("    " + currClassKey + ": "
            + privateVars);
        
        totalVars += privateVars;
        
    }

    consoleReport.add("");
      // Add the class total to the output
    consoleReport.add("Program Total: " +totalVars);
    
    return consoleReport;
    
  }
  @Override
  public List<List<String>> generateWorksheetReport()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
