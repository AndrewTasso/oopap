package edu.monmouth.se.oopap.enumerator;

/**
 * Enumerator to represent that various types of line classifications.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public enum LineType
{

  SingleLineLogical, MultiLineLogical, ClassDeclaration, MethodDeclaration, Comment, OpenComment, CloseComment, Blank, OpeningBrace, ClosingBrace, ImportStatement, PackageDeclaration, Unknown;

}
