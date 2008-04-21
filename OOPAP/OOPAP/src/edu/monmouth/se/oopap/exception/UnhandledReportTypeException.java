package edu.monmouth.se.oopap.exception;

/**
 * Exception thrown when an unexpected ReportType is encountered by the analyzer
 * factory.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public class UnhandledReportTypeException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /**
   * Default constructor
   * 
   * @return edu.monmouth.se.oopap.exception.UnhandledReportTypeException
   */

  public UnhandledReportTypeException()

  {

    super();

  }

  /**
   * Constructor that takes the exception message
   * 
   * @param aMessage
   * @return edu.monmouth.se.oopap.exception.UnhandledReportTypeException
   */

  public UnhandledReportTypeException(String aMessage)

  {

    super(aMessage);

  }

  /**
   * Constructor that takes the exception message and the exception
   * 
   * @param aMessage
   * @param anException
   * @return edu.monmouth.se.oopap.exception.UnhandledReportTypeException
   */

  public UnhandledReportTypeException(String aMessage, Exception anException)

  {

    super(aMessage, anException);

  }

  /**
   * 
   * Constructor that takes the exception object as a parameter.
   * 
   * @param anException
   * @return edu.monmouth.se.oopap.exception.UnhandledReportTypeException
   */

  public UnhandledReportTypeException(Exception anException)

  {

    super(anException);

  }

} // end class

