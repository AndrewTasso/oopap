package edu.monmouth.se.oopap.sourceanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import edu.monmouth.se.oopap.enumerator.LineType;
import edu.monmouth.se.oopap.analyzer.LineAnalyzer;

public class DepthOfInheritanceTreeSourceAnalyzer extends SourceAnalyzer
{

  /**
   * 
   */
  public DepthOfInheritanceTreeSourceAnalyzer()
  {

  }

  /**
   * 
   */
  public void analyzeSource(Map<String, List<String>> theSourceMap)
  {

  }

  /**
   * 
   * @return
   */
  public List<String> generateConsoleReport()
  {

    List<String> consoleReport = new ArrayList<String>();

    consoleReport.add("DepthOfInheritanceTree Report");

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

}
