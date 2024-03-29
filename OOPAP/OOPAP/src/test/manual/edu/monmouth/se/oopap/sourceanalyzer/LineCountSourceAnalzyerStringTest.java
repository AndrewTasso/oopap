package test.manual.edu.monmouth.se.oopap.sourceanalyzer;

import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

import edu.monmouth.se.oopap.sourceanalyzer.SourceAnalyzer;
import edu.monmouth.se.oopap.sourceanalyzer.LineCountSourceAnalyzer;
import edu.monmouth.se.oopap.io.FileUtil;

public class LineCountSourceAnalzyerStringTest
{

  public static void runStringTest(String theSourcePath)
  {

    try
    {

      // Map to hold the contents of the source
      Map<String, List<String>> sourceMap = new HashMap<String, List<String>>();

      // instance the analyzer to be tested
      SourceAnalyzer testAnalyzer = new LineCountSourceAnalyzer();

      // get the source contents
      List<String> sourceContents = FileUtil.getSourceFileContents(new File(
          theSourcePath));

      sourceMap.put(theSourcePath, sourceContents);

      // run the analysis
      testAnalyzer.analyzeSource(sourceMap);

      // display the results
      List<String> testResults = testAnalyzer.generateConsoleReport();

      for (String currLine : testResults)
      {

        System.out.println(currLine);

      }

    } catch (Exception e)
    {

      e.printStackTrace();

    }

  }

}
