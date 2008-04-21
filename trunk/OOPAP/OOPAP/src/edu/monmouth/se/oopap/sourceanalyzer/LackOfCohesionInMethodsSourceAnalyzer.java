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

 * This class is responsible for analyzing the source code of a program and 
 * producing the metric value that describes the Lack of Cohesion in Methods. 
 * -- Another way to define Lack of Cohesion is the number of pairs of 
 * methods in a class that don't have at least one field in common minus 
 * the number of pairs of methods in the class that do share at least one 
 * field. When this value is negative, the metric value is set to 0.
 * @author Jaidev Kochunni (with some source code from 
 * LineCountSourceAnalyzer by Andrew Tasso
 * @version %I% %G%
 */
public class LackOfCohesionInMethodsSourceAnalyzer extends SourceAnalyzer
{

  private Map<String, Map<String, Integer>> classOperationLinesMap;
  private Map<String, Integer> classLinesMap;
  private Map<String, Integer> classLCOMValMap;
  
  /**
   * Default constructor which calls the init method called resetAnalysis. 
   */
  public LackOfCohesionInMethodsSourceAnalyzer()
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
    this.classLCOMValMap = new HashMap<String, Integer>();
  }
  
  /**
   * Analyzes the source going line by line.  When finding the first method declaration,
   * in our coding standards, that being a constructor, we have found all the class fields and
   * stored them in a list.  Also during the normal count of operations of the class, we also
   * check if the class fields that we have stored exists in the lines of the current operation
   * that we are counting.
   * -- Finally with the map of fields to operations, get the LCOM value.
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
      // LineType for the current line
      LineType currLineType = LineType.Unknown;
      // Reset the ClassLOC
      currClassLines = 0;
      // Map to hold the operation to line count association
      Map<String, Integer> operationCountMap = new HashMap<String, Integer>();
      Map<String, List<String>> operationToLinesMap = new HashMap<String, List<String>>();
      
      List<String>  fieldsInClassList =  new ArrayList<String> ();
      List<String>  linesInMethodList  =  new ArrayList<String> ();
      
      Map<String, List<String>> fieldsToMethodsMap  = new HashMap <String, List<String>>();
      
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
            
            if (currOperationName.length() == 0)
            {
              // This must be an instance variable declaration since we have
              // not hit the constructor yet.
              String fieldName = LineAnalyzer.getInstanceVariableName (currLine);
              fieldsInClassList.add(fieldName);
              
            }
            else
            {
              linesInMethodList.add(currLine);
              
              // Check if this line contains any of the fields, if so add it to the map.
              for (int fieldsIndex = 0; fieldsIndex < fieldsInClassList.size(); fieldsIndex ++)
              {
                String fieldName = fieldsInClassList.get(fieldsIndex);
                
                if (currLine.indexOf(fieldName) > 0)
                {
                  List<String> opNamesList  = (ArrayList<String>) fieldsToMethodsMap.get(fieldName);
                  if (null == opNamesList)
                  {
                    opNamesList = new ArrayList<String>();
                  }
                  
                  if (!opNamesList.contains(currOperationName))
                  {
                    opNamesList.add(currOperationName);                    
                  }
                  
                  fieldsToMethodsMap.put(fieldName, opNamesList);
                }
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
            linesInMethodList.clear();
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
                
                operationToLinesMap.put(currOperationName, linesInMethodList);
                linesInMethodList.clear();
  
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
      classLinesMap.put(currSourceFileName, currClassLines);

      Integer lcomVal = 0;
      //
      // If there are no fields for the class, then no need to calculat the LCOM stats,
      // just set the value to 0 in the map.
      //
      if (fieldsToMethodsMap.size() > 0)
      {
        lcomVal = getLCOMStat (currSourceFileName, fieldsToMethodsMap);
      }
      
      classLCOMValMap.put (currSourceFileName, lcomVal);
      
    }
  }

  /**
   * Generates the console report - printing the LCOM value per class.
   */
  public List<String> generateConsoleReport()
  {
    
    List<String> consoleReport = new ArrayList<String>();

    consoleReport.add("Lack of cohesion in methods Report");
    
    Set<String> classNames  = classLCOMValMap.keySet();
    for (String currSourceFileName : classNames)
    {
      Integer lcomVal  =  (Integer) classLCOMValMap.get(currSourceFileName); 
      consoleReport.add ("LCOM value for Class [" + currSourceFileName + "] is [" + lcomVal.toString() + "]");
    }
    
    return consoleReport;
    
  }



  /**
   * For the given source file and its map of found fields to fields' methods, calculate the 
   * LCOM value by subtracting the number of pairs of methods that do not share any instance variable
   * by the pairs that do.  The calculation to find pairs that do are done with help of the
   * 'combination formula' as mentioned in the commented link below.
   * 
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

  private Integer getLCOMStat (String currSourceFile, Map<String, List<String>> fieldsToMethodsMap)
  {
    int lcomVal = 0;
    
    int numPairsSharing = 0;
   
    //
    // Go through the map and see how many methods use each of the class fields.
    //
    Set<String> fieldKeySet = fieldsToMethodsMap.keySet();

    List<String> currMethodsList = new ArrayList <String>();
    
    for (String currField : fieldKeySet)
    {
      List<String>  methodsSharingThisFieldList = fieldsToMethodsMap.get(currField);
      
      methodsSharingThisFieldList.removeAll(currMethodsList);
      
      if ( (null != methodsSharingThisFieldList) && (methodsSharingThisFieldList.size() > 0) )
      {
        //
        // This calculation is for getting the 'number of pairs' that share this field.
        // -- It's done via the 'combination' formula described in the following site:
        // http://www.themathpage.com/aPreCalc/permutations-combinations-2.htm
        // -- In this case, it's "number of ways to take 2 things from" the num of elements
        // in the list.
        //
        
        numPairsSharing += (methodsSharingThisFieldList.size() * (methodsSharingThisFieldList.size()-1) ) / 2;   
      
        if (numPairsSharing > 0)
        {
          currMethodsList.addAll(methodsSharingThisFieldList);
        }
      }
    }
    
    Map<String, Integer> opsToLineMap = classOperationLinesMap.get(currSourceFile);
    
    Set<String> operationsSet = opsToLineMap.keySet();
    
    operationsSet.removeAll(currMethodsList);
    
    // According to the definition: 
    // LCOM is the number of pairs of methods in a class that don't have at least one field 
    // in common minus the number of pairs of methods in the class that do share at least 
    // one field. When this value is negative, the metric value is set to 0.
    lcomVal = ((operationsSet.size() * (operationsSet.size()-1)) / 2) - numPairsSharing;
    
    if (lcomVal < 0)
    {
      lcomVal = 0;
    }
    
    return (lcomVal);
  }
  
  
}
