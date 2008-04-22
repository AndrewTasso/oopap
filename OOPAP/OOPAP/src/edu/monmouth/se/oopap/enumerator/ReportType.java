package edu.monmouth.se.oopap.enumerator;

/**
 * Enumerator to represent the various analysis reports generated by the
 * application.
 * 
 * @author Andrew Tasso
 * @version %I% %G%
 */
public enum ReportType
{

  LineCountByPCO, PSPPhysicalLOCByPCO, PSPLogicalLOCByPCO, CommentLines, CommentedLines, SumOfCommentAndCommented, DecisionAndLoopCountByPCO, ClassOperationCountByProgram, OperationCountByPC, PublicVarCountByPC, PrivateVarCountByPC, GlobalVarCountByPC, DepthOfInheritanceTree, NumberOfChildren, LackOfCohesionInMethods, ResponseForAClass, TotalOperators, TotalLibraryCalls, Top5_20PercentOfCalculations, MeanMedStdDeviationOfCalcCount;

}
