package edu.monmouth.se.oopap.io;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;

/**
 * Class responsible for interacting with the file system.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public class FileUtil
{

  /**
   * x Method used to obtain the list of java source files at a specified path.
   * It will only return files with an extension provided.
   * 
   * @param thePathName
   *          The full absolute path for which files are to be listed
   * @param theSourceExtension
   *          The extension, including the period, for the extension.
   * @return a List of File objects representing the java source files in the
   *         specified path.
   */
  public static List<File> getSourceFileList(String thePathName,
      String theSourceExtension)
  {

    // File object to hold the path
    File aPath = new File(thePathName);
    // List of Files to hold the filtered files
    List<File> pathList = new ArrayList<File>();

    // Check to see if the current file object points to a single file or
    // too a directory. If the object is a file add only it to the list
    // and return. If not, iterate through the list of files in the directory
    // and add only the files with the provided extension to the list
    if (aPath.isFile())
    {

      pathList.add(aPath);

    } else
    {

      // iterate through the list of files in the directory
      for (File currFile : aPath.listFiles())
      {

        // obtain the string value of the absolute path to the file
        String tempName = currFile.toString();

        // ensure that the file has an extension of .java
        if (tempName.contains(theSourceExtension))
        {

          // add it if it is a source file
          pathList.add(currFile);

        }

      }

    }

    // Return the list of java source files
    return pathList;

  }

  /**
   * Method used to obtain the string contents of a file.
   * 
   * @param theSourcePath
   *          The File representing the path to the file to be read.
   * @return A list of strings containing the source contents
   * @throws IOException
   *           If the file cannot be read.
   */
  public static List<String> getSourceFileContents(File theSourceFile)
      throws IOException
  {

    // List to hold the contents of the source file
    List<String> sourceContents = new ArrayList<String>();
    // BufferedReader to read the elements in the file
    BufferedReader in = new BufferedReader(new FileReader(theSourceFile
        .toString()));
    // String to hold the value of the current line
    String currLine = "";

    // read each line of the file until we hit the end
    while ((currLine = in.readLine()) != null)
    {

      sourceContents.add(currLine);

    }

    return sourceContents;

  }

  /**
   * Method used to write any reports, in the form of a list of strings
   * to the specified path. This method will automatically insert a
   * newline character at the end of each string. There is no need to
   * insert such character at the end of the reports being generated.
   * 
   * @param theFilePath the path of the file to be written
   * @param theReportContents the contents of the report to be written
   * @throws FileNotFoundException 
   */
  public static void writeReport(File theFilePath,
		  						 List<String> theReportContents) 
    throws IOException
  {

	  FileWriter fout = new FileWriter(theFilePath);
	  
	  for(String currLine : theReportContents)
	  {
		  
		  fout.write(currLine + "\n");
		  
	  }
	  
	  fout.close();
	  
  }
  
  /**
   * Method used to create a directory. The new directory will be created at
   * the path provided with the name provided
   * 
   * @param theParentDirectory The parent director
   * @param theNewDirectoryName The new directory name
   * @return A File object representing the new Directory
   */  
  public static File mkDir(File theParentDirectory,
		                   String theNewDirectoryName)
  {
	  //Create a new file representation of the new directory
	  File newDir = 
		  new File(theParentDirectory, theNewDirectoryName);
	  
	  //check to see if the directory already exists. If it does there 
	  //is no need to create a new one and the mkdir call can be ignored.
	  if(!newDir.exists())
	  {
		  
		  //create the directory
		  newDir.mkdir();
		  
	  }
	  
	  //return the newly created path
	  return newDir;
	  
  }
  
}
