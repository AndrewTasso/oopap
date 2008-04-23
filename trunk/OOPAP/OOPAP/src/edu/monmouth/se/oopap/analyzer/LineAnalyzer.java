package edu.monmouth.se.oopap.analyzer;

import edu.monmouth.se.oopap.enumerator.LineType;

/**
 * Class responsible for analyzing lines of code. It provides various helper
 * functions to perform various operations on lines of code.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public class LineAnalyzer
{

  /**
   * Method used to determine what type of line of code the specified string of
   * code is.
   * 
   * @param aLine
   *          A string containing a line of code.
   * @return the enumerated type of the line type.
   */
  public static LineType getLineType(String aLine)
  {

    // The final line type to be returned
    LineType returnLineType = LineType.Unknown;

    // FOR ALL OF THE FOLOWING REGEX STRINGS THE CHAR \ NEEDS TO HAVE AN
    // ESCAPE CHAR OF \. THEREFORE THE STRING "\\" IS ACTUALLY EQUIVALENT TO
    // "\." THEREFORE, IF AN ESCAP CHAR OF '\' IS NEEDED WITHIN A REGEX
    // THE STRING "\\" MUST BE USED TO ESCAPE THE ESCAPE CHAR.

    // Check to see if the line is a blank line. //s represents a white space
    // character and * indicates any number of said characters.
    if (aLine.matches("\\s*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.Blank;

    }

    // Check to see if the line is a comment - '//' . indicates any char and
    // * indicates any number of them.
    else if (aLine.matches(".*//.*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.Comment;

    }

    // Check to see if the line is a opening comment - '/*' - "/\\*" indicates
    // the string "/*" . indicates any char and * indicates any number of them.
    else if (aLine.matches(".*/\\*.*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.OpenComment;

    }

    // Check to see if the line is a closing comment - '*/' - "\\*/" indicates
    // the string "*/" . indicates any char and * indicates any number of them.
    else if (aLine.matches(".*\\*/.*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.CloseComment;

    }

    // Check to see if the line is a opening brace - '{'. This regex checks
    // for any number of white space chars, followed by a '{' and any number
    // of white space chars after that
    else if (aLine.matches("\\s*\\{\\s*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.OpeningBrace;

    }

    // Check to see if the line is a closing brace - '}'. This regex checks
    // for any number of white space chars, followed by a '}' and any number
    // of white space chars after that
    else if (aLine.matches("\\s*\\}\\s*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.ClosingBrace;

    }

    // check to see if the line is a an import statement. This is the string
    // "import." that occurs at the beginning of a line.
    else if (aLine.matches("import .*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.ImportStatement;

    }

    // check to see if the line is a package declaration. This is the string
    // "package." that occurs at the beginning of a line.
    else if (aLine.matches("package .*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.PackageDeclaration;

    }

    // check to see if the line is a class declaration. This is includes the
    // scope modifier, followed by any number of chars and the class or enum
    // keyword.
    // Ignore any lines that contain the ".matches(" indicating it would be a
    // regex search
    else if (aLine.matches(".*(public|private).* (class|enum) .*")
        && !aLine.contains(".matches("))
    {

      returnLineType = LineType.ClassDeclaration;

    }

    // Check to see if the line is a method declaration. This is indicated
    // by a scope modifier followed by any number of chars then a '(' char.
    // If a '.' occurs in the string it is not a method declaration
    // Ignore any lines that contain the ".matches(" indicating it would be a
    // regex
    else if (aLine.matches (".*(public|private|protected).*\\(.*")
        && !aLine.contains(".*\\..*") && !aLine.contains(".matches("))
    {

      returnLineType = LineType.MethodDeclaration;

    }

    // A single line logical is indicated by a ';' or a ':' in the line.
    // By this point all of the non logical lines should be filtered out.
    // Time to get to the good stuff
    else if (aLine.matches(".*(;|:).*"))
    {

      returnLineType = LineType.SingleLineLogical;

    }

    // We come to the end of line for filtering. The only thing that should hit
    // the default conditional are lines that are logical but do not contain
    // a ';' and those that are rare exceptions to the sorting rules, neither
    // of which will be counted as a logical line. Therefore, we will classify
    // it as part of a multi-line logical.
    // TODO this might need more refinement in the future.
    else
    {

      returnLineType = LineType.MultiLineLogical;

    }

    return returnLineType;

  }
  
  /**
   * Method used to extract the name of a class from a line containing a
   * class declaration. This method will return a blank line if an invalid
   * method declaration in provided.
   * 
   * @param theSourceLine
   *          the Line containing the class declaration
   * @return The class name
   * @author Jason Schramm
   */
  public static String getClassName(String theSourceLine)
  {
    //split line into tokens, split on whitespace
    String[] tokens = theSourceLine.split(" ");
    
    //initialize class name string
    String className = "";
    
    //iterate through tokens to find the class name
    for(int tokenCount = 0; tokenCount < tokens.length;tokenCount++)
    {
      // Checks if token contains "class". If it does, then next
      // token must be class name. Save it.
      if(tokens[tokenCount].equalsIgnoreCase("class"))
      {
        tokenCount++;
        className = tokens[tokenCount];
        break;
      }
    }
    
    return className;
  }
  
  /**
   * Method used to extract the name of a superclass from a line containing a
   * class declaration. This method will return a blank line if there is no
   * superclass referenced in the class declaration provided.
   * 
   * @param theSourceLine
   *          the Line containing the class declaration
   * @return The superclass name
   * @author Jason Schramm
   */
  public static String getSuperclassName(String theSourceLine)
  {
    //split line into tokens, split on whitespace
    String[] tokens = theSourceLine.split(" ");
    
    //initialize superclass name string
    String superclassName = "";
    
    //iterate through tokens to find the class name
    for(int tokenCount = 0; tokenCount < tokens.length;tokenCount++)
    {
      // Checks if token contains "class". If it does, then next
      // token must be class name. Save it.
      if(tokens[tokenCount].equalsIgnoreCase("extends"))
      {
        tokenCount++;
        superclassName = tokens[tokenCount];
        break;
      }
    }
    
    return superclassName;
  }
  
  /**
   * Method used to extract the name of an operation from a line containing an
   * operation declaration. This method will return a blank line if an invalid
   * method declaration in provided.
   * 
   * @param theSourceLine
   *          the Line containing the method declaration
   * @return The method name
   */
  public static String getOperationName(String theSourceLine)
  {

    String operationName = "";

    // get the position of the '(' within the line of code. If the
    // '(' char cannot be found, negative result will be returned
    int bracePos = theSourceLine.lastIndexOf('(');

    // if the line contains a '(' char
    if (bracePos > 0)
    {

      // cut off the rest of the line, including the '('. This is
      // done by obtaining the substring from the beginning of the string
      // to the char before the '('
      operationName = theSourceLine.substring(0, bracePos);

      // Split the string into tokens. The method name is the last token in
      // the list
      String[] tokens = operationName.split(" ");
      operationName = tokens[tokens.length - 1];

    }

    return operationName;

  }
  
  /**
   * Method used to extract the name of the variable from a line
   * containing instance variable declaration. This method will return a blank
   * if an invalid instance variable declaration is provided.
   * 
   * @param theSourceLine the Line containing the instance variable declaration.
   * @return the variable name.
   */
  public static String getInstanceVariableName(String theSourceLine)
  {
    String variableName = "";

    // get the position of the ';' within the line of code. If the
    // ';' char cannot be found, negative result will be returned
    int semiColonPos = theSourceLine.lastIndexOf(';');

    // if the line contains a ';' char
    if (semiColonPos > 0)
    {

      variableName = theSourceLine.substring(0, semiColonPos);

      // Split the string into tokens. The variable name is the last token in
      // the list
      String[] tokens = variableName.split(" ");
      variableName = tokens[tokens.length - 1];

    }

    return variableName;

  }
  
  public static boolean isMethodCall(String theSourceLine)
  {
    
    
    return false;
    
  }

}
