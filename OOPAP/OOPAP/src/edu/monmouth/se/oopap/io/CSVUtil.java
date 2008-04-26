package edu.monmouth.se.oopap.io;

import java.util.List;
import java.util.ArrayList;

/**
 * CSV Helper class. This class is used to generate CSV formatted reports. 
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 *
 */
public class CSVUtil {

	/**
	 * Method to generate CSV formatted reports. The output will consist of 
	 * a collection of strings, each one representing a single line in the 
	 * final report. 
	 * 
	 * @param theReportContents The 2 dimensional collection containing the 
	 * elements to be included in the report. 
	 * @return A collection of strings containing the final formatted report
	 * ready to be written to disk.
	 */
	public static List<String> generateCSVReport(List<List<String>> theReportContents)
	{
		
		List<String> csvReportContents = new ArrayList<String>();
		
		//iterate over the collection of lists
		for(List<String> currLineContents : theReportContents)
		{
			
		  
			String currLine = "";
			
			//iterate over the entire list of tokens to be inserted into the
			//line. Separate each on by a comma.
			for(int i = 0; i < currLineContents.size(); i++)
			{
				
				//insert the token
				currLine += currLineContents.get(i);
				//insert a comma after the token, with the exception
				//of the final token.
				if(i < currLineContents.size() - 1) 
				{
				
					currLine += ",";
					
				}
				
			}
						
			csvReportContents.add(currLine);
			
		}
		
		return csvReportContents;
		
	}
}
