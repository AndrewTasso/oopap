package test.manual.edu.monmouth.se.oopap.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.monmouth.se.oopap.io.FileUtil;

public class FileUtilStringTest
{

  public void testGetSourceFileContents()
  {

    String path = "Z:\\OOPAP\\OOPAP\\src\\edu\\monmouth\\se\\oopap";
    String file = "OOPAP.java";

    try
    {
      List<String> sourceContents = FileUtil.getSourceFileContents(new File(
          path + File.separatorChar + file));

      for (String s : sourceContents)
      {

        System.out.println(s);

      }

    } catch (IOException e)
    {
      e.printStackTrace();
    }

  }

}
