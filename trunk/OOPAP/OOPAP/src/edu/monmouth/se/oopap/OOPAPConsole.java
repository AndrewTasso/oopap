package edu.monmouth.se.oopap;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;

public class OOPAPConsole extends JFrame
{

    JScrollPane scrollPane;
    JTextPane consolePane;
    JScrollBar scrollBar;
    
    public OOPAPConsole()
    {
      
      this.scrollPane = new JScrollPane();
      this.consolePane = new JTextPane();
    
      this.scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      this.scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
      
      this.scrollPane.getViewport().add(consolePane);
      this.getContentPane().add(scrollPane);
      this.setSize(500, 600);
      
      pack();
      
    }
    
    public void displayConsoleReport(List<String> theConsoleReport)
    {
      
      //Build a string from the console report so the text of the console
      //may be set
      StringBuilder sb = new StringBuilder();
      //iterate over the collection of strings for the
      for(String currReportLine: theConsoleReport)
      {
        
        sb.append(currReportLine + "\n");
        
      }
      
      consolePane.setText(sb.toString());
      pack();
      
    }
  
}
