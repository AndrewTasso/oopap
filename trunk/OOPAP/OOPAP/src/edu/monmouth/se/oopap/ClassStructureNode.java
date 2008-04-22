package edu.monmouth.se.oopap;

import java.util.ArrayList;

/**
 * This class defines a node structure that stores a class name and a 
 * list of its children.
 * 
 * @author Jason Schramm
 * @version %I% %G% 
 */
public class ClassStructureNode
{

	//String to hold the name of the class
	String className;
	//ArrayList of Strings to hold the names of every child
	ArrayList<String> children;

	/**
	 * Constructor that initializes an empty child list.
	 */
	public ClassStructureNode()
	{
	    this.children = new ArrayList<String>();
	}

	/**
	 * Constructor that initializes an empty child list,
	 * taking a class name to set for the node.
	 * 
	 * @param parentName: string of parent class name (superclass)
	 */
	public ClassStructureNode(String parentName)
	{
		this.className = parentName;
	    this.children = new ArrayList<String>();
	}

	/**
	 *
	 * Constructor that initializes an empty children list,
	 * taking a class name and child name to set for the node.
	 * Adds the child class name to the children list.
	 * 
	 * @param parentName: string of parent class name (superclass)
	 * @param childName: string of child class name (subclass)
	 */
	public ClassStructureNode(String parentName, String childName)
	{
	  this.children = new ArrayList<String>();
		this.className = parentName;
		this.children.add(childName);
	}
	
	/**
	 * Method to add a child's class name to the children list.
	 * 
	 * @param theChildName: string of child class name
	 */
	public void addChild (String theChildName)
	{
		this.children.add(theChildName);
	}

	/**
	 * Method responsible for returning the children nodes (subclasses).
	 * 
	 * @return String list of children.
	 */
	public ArrayList<String> getChildren()
	{
		return this.children;
	}
	
	/**
	 * Method responsible for returning the number of children (subclasses) of the node.
	 * 
	 * @return Integer representing number of children in children list.
	 */
	public int getNumberChildren()
	{
		return this.children.size();
	}

	/**
	 * Method responsible for returning children class names (subclasses) of the node.
	 * 
	 * @return String array of children class names
	 */
	public String[] getChildrenArray()
	{
		return (String[])this.children.toArray();
	}
	
	/**
	 * Method responsible for setting the parent class name (superclass).
	 * 
	 * @param parentName: Class name string to set className to.
	 */
	public void setParent(String parentName)
	{
		this.className = parentName;
	}
	
	/**
	 * Method responsible for returning then name of the parent class (superclass).
	 * 
	 * @return String containing parent class name.
	 */
	public String getParent()
	{
		return this.className;
	}
}
