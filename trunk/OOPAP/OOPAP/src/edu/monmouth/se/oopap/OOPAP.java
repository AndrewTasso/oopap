package edu.monmouth.se.oopap;

/**
 * Primary Controller for the OOPAP application.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public class OOPAP
{

  /**
   * Launch the application. No arguments.
   * 
   * @param args
   *          None arguments needed
   */
  public static void main(String args[])
  {

    try
    {

      // Initialize the controller for the application
      OOPAPController controller = new OOPAPController();

      // Create a new UI object
      OOPAPGUI ui = new OOPAPGUI(controller);

    }
    // catch any unexpected exceptions
    catch (Exception e)
    {

      e.printStackTrace();

    }

  }

}
