package edu.monmouth.se.oopap;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import java.util.List;
import java.awt.Color;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Document;

public class OOPAPConsole extends JFrame
{

    JScrollPane scrollPane;
    JTextPane consolePane;
    JScrollBar scrollBar;
    
    static SimpleAttributeSet REPORT_HEADER = new SimpleAttributeSet();

    static SimpleAttributeSet CLASS_HEADER = new SimpleAttributeSet();

    static SimpleAttributeSet NORMAL = new SimpleAttributeSet();

    // Best to reuse attribute sets as much as possible.
    static {
      
      StyleConstants.setForeground(REPORT_HEADER, Color.black);
      StyleConstants.setBold(REPORT_HEADER, true);     
      StyleConstants.setUnderline(REPORT_HEADER, true);     
      StyleConstants.setFontFamily(REPORT_HEADER, "Helvetica");
      StyleConstants.setFontSize(REPORT_HEADER, 14);

      StyleConstants.setForeground(CLASS_HEADER, Color.black);
      StyleConstants.setBold(CLASS_HEADER, true);
      StyleConstants.setFontFamily(CLASS_HEADER, "Helvetica");
      StyleConstants.setFontSize(CLASS_HEADER, 13);

      StyleConstants.setForeground(NORMAL, Color.black);
      StyleConstants.setFontFamily(NORMAL, "Helvetica");
      StyleConstants.setFontSize(NORMAL, 12);
      
    }    
    
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

      this.clearConsole();

      //iterate over the collection of strings for the
      for(String currReportLine: theConsoleReport)
      {

        if(currReportLine.startsWith("        "))
        {
          
          this.insertText(currReportLine + "\n", this.NORMAL);          
          
        }
        else if(currReportLine.startsWith("    "))
        {
          
          this.insertText(currReportLine + "\n", this.CLASS_HEADER);                    
          
        }
        else
        {
          
          this.insertText(currReportLine + "\n", this.REPORT_HEADER);                    
          
        }
        
      }
      
      pack();
      
    }
    
    private void insertText(String text, AttributeSet set) {
      
      try 
      {
      
        consolePane.getDocument().insertString(
            consolePane.getDocument().getLength(), text, set);
        
      } 
      catch (BadLocationException e) 
      {
      
        e.printStackTrace();
      }
    }    
    
    private void clearConsole()
    {

      
      try
      {
               
        Document document = this.consolePane.getDocument();
        document.remove(0, document.getLength());
        
        pack();
        
      } catch (BadLocationException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
            
    }
  
}
