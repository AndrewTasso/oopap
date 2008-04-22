package edu.monmouth.se.oopap.sourceanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.monmouth.se.oopap.ClassStructureNode;
import edu.monmouth.se.oopap.enumerator.LineType;
import edu.monmouth.se.oopap.analyzer.LineAnalyzer;

/**
 * This class is responsible for depth of inheritance of a class.
 * Depth is found recursively, with each class with children being 
 * counted as 1 level.
 * Depth from each class to its farthest child is then output.
 * 
 * @author Jason Schramm
 * @version %I% %G% 
 */
public class DepthOfInheritanceTreeSourceAnalyzer extends SourceAnalyzer
{
  /**
   * Map to store each class. The key is the class's name, and the value 
   * is the number of children it has. It will always be 0, as the size is 
   * calculated before being output. 
   */
  private Map<String, Integer> classMap;

  /**
   * List to store the children each class has.
   */
  private ArrayList<ClassStructureNode> classTree;
  
  /**
   * 
   */
  public DepthOfInheritanceTreeSourceAnalyzer()
  {

  }


  /**
   * Method used to reset the analysis of the analyzer. Resets the data members
   * of the class.
   */
  private void resetAnalysis()
  {

    this.classMap = new HashMap<String, Integer>();
    this.classTree = new ArrayList<ClassStructureNode>();

  }
  
  /**
   * 
   */
  public void analyzeSource(Map<String, List<String>> theSourceMap)
  {
      this.resetAnalysis();

      // Set to hold the list of key values (source file names) for the current
      // map of source files.
      Set<String> sourceKeySet = theSourceMap.keySet();
      
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
        // LineType for the current line
        LineType currLineType = LineType.Unknown;
        // String to hold current parent class name of the extends line
        String currParent = "";
        // String to hold current child class name of the extends line
        String currChild = "";
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

          case ClassDeclaration:

            //store class name in child class name string
            currChild = LineAnalyzer.getClassName(currLine);

            //store superclass name in parent class name string
            currParent = LineAnalyzer.getSuperclassName(currLine);
            
            //store inheritance relationship in arraylist if it exists
            //if(currParent != "" && currChild != "")
            //{
              this.addRelationship(currParent,currChild);
            //}
            //stores current class name in classMap
            //if(currChild != "")
            //{
              //initializes depth of inheritance value to 0
              //will be updated later in the code
              classMap.put(currChild, 0);
            //}

            break;
          }
        }
      }
      
      /**
       * Calculate the number of children for each class and store 
       * the value in the class map.
       */
      //store key set of map
      Set<String> classMapSet = this.classMap.keySet();
      //initialize inheritanceDepth holder to 0
      int inheritanceDepth = 0;
      //Iterate over the entire class to operation association map.
      for (String currClassKey : classMapSet)
      {
        //get depth of inheritance
        inheritanceDepth = getDepth(currClassKey);
        //store updated depth of inheritance in map
        classMap.put(currClassKey,inheritanceDepth);
    }
  }

  /**
   * 
   * @return
   */
  public List<String> generateConsoleReport()
  {
    List<String> consoleReport = new ArrayList<String>();
    
    consoleReport.add("Depth Of Inheritance Report");
    consoleReport.add("");
    
    //Set of strings to hold all of the keys (class names) in the analyzer so that
    //it may be iterated through.
    Set<String> classKeySet = this.classMap.keySet();
    
    //Iterate over the all class names selected for analysis
    for (String currClassKey : classKeySet)
    {
      //get depth of inheritance for each class, then output it with class name
      consoleReport.add(currClassKey + ": " + this.classMap.get(currClassKey));
  }

    return consoleReport;

  }

  /**
   * 
   * @return
   */
  public List<List<String>> generateWorksheetReport()
  {

    List<List<String>> worksheetReport = new ArrayList<List<String>>();

    worksheetReport.add(new ArrayList<String>());

    return worksheetReport;

  }

  /**
   * Method to add a parent class and its child to the class tree, stored 
   * as an ArrayList that maintains the name of every class and a list of 
   * its children.
   * 
   * @param parentClass: name of parent class being added (extended)
   * @param childClass: name of child class extending the parent class
   */
  public void addRelationship(String parentClass, String childClass)
  {
    //has the relationship been added? initialize to false
    boolean isAdded = false;
    //iterate over all classes.
    for (ClassStructureNode Node : classTree)
    {
      //check if current node is the node for the parent class we are adding
      if(parentClass.compareTo(Node.getParent()) == 0)
      {
        //check if node doesn't already contain the child being added
        if(!Node.getChildren().contains(childClass))
        {
          //add child to parent node
          Node.addChild(childClass);
        }
        //child has been added or was already there, so set isAdded to true
        isAdded = true;
      }
    }
    // if parent does not exist then relationship hasn't been added 
    // and this is a new node.
    if(!isAdded)
    {
      //Initialize new node, setting it's parent and child names
      ClassStructureNode newNode = new ClassStructureNode(parentClass,childClass);
      //Add new node to class tree
      classTree.add(newNode);
    }
  }
  
  /**
   * Method to recursively find the depth of inheritance of a given class name.
   * 
   * @param className: Name of the class to find the depth of inheritance of.
   * @return
   */
  public int getDepth(String className)
  {
    //each class has a 0 depth to start
    int depthLength = 0;
    
    //iterate over all classes
    for (ClassStructureNode Node : classTree)
    {
      //check if current node is the node for the class we are 
      //getting the depth of inheritance for
      if(className.compareTo(Node.getParent()) == 0)
      {
        //get a list of the class's children
        ArrayList<String> childrenNode = Node.getChildren();
        //check if node has children
        if(childrenNode.size() > 0)
        {
          //add 1 to depth of inheritance length to account for class's children
          depthLength = depthLength + 1;
          //initialize variable to hold the longest depth out of all the class's children
          int longestDepth = 0;
          //temp variable to hold each child's depth for comparison
          int tempDepth = 0;
          //iterate through each child class, comparing its depth
          for (String childName : childrenNode)
          {
            //add the depth of inheritance a child has to tempDepth 
            tempDepth = getDepth(childName);
            //if the current child's depth is longer than the last stored,
            //update longestDepth with current child's depth
            if(tempDepth > longestDepth)
              longestDepth = tempDepth;
          }
          
          //add longest child depth to current class's depthLength
          depthLength = depthLength + longestDepth;
        }
        else
        {
          //return 0 depth if class has no children
          return 0;
        }
      }
    }

    return depthLength;
  }
}