/*
 * Author: atotic
 * Created on Apr 8, 2004
 * License: Common Public License v1.0
 */
package org.python.pydev.editor.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.runtime.IPath;


/**
 * Scope is where definitions of locals/classes/functions go in a namespace.
 * Every AbstractNode has a scope.
 * 
 * There is a scope hierarchy:
 * ModuleNode scope
 *   Function scope
 *   Class scope
 *      Function scope
 */
public class Scope {
	private ArrayList locals;
	private AbstractNode start;
	private AbstractNode end;
	private Scope parent;
	
	// Lists of elements defined inside this this scope.
	private ArrayList children;		// array of LocalNodes
	private ArrayList functions;	// array of FunctionCallNodes
	
	public Scope(AbstractNode start) {
		this.start = start;
		AbstractNode startParent = start.getParent();
		if (startParent != null) {
			parent = startParent.getScope();
			parent.addChild(this);
		}
		children = new ArrayList();
	}
	
	private void addChild(Scope scope) {
		children.add(scope);
	}
	
	public ArrayList getChildren() {
		return children;
	}

	public LocalNode getLocalByName(String name) {
		if (locals == null)
			return null;
		Iterator i = locals.iterator();
		while (i.hasNext()) {
			LocalNode l = (LocalNode)i.next();
			if (l.toString().equals(name))
				return l;
		}
		return null;
	}

	void addLocalDefinition(LocalNode newLocal) {
		if (locals == null)
			locals =  new ArrayList();
		if (getLocalByName(newLocal.toString()) == null)
			locals.add(newLocal);
	}

	
	void addFunctionDefinition(FunctionNode newDef) {
		if (getFunctions() == null)
			setFunctions(new ArrayList());
		getFunctions().add(newDef);
	}
	
	public Location getStart() {
		return start.getStart();
	}
	
	public AbstractNode getStartNode() {
		return start;
	}
	
	public Scope getParent() {
		return parent;
	}

	public Location getEnd() {
		return end.getEnd();
	}
	/**
	 * Computes where the last child of the node ends
	 */
	public void setEnd(AbstractNode end) {
		ArrayList children = end.getChildren();
		int size = children.size();
		AbstractNode trueEndNode = size > 0 ? (AbstractNode)children.get(size-1) : end;	
		this.end = trueEndNode;
	}
	
	/**
	 * @param token : function name
	 * @param c : comparator to test for.
	 * @return an ArrayList of AbstractNode to the function/class definitions. 
	 * each returned item will test as equal in c.compare(token, item);
	 * null is never returned, there will be an empty array if none were found.
	 * 
	 * Usage:
	   scope.findFunctionCalls("FunctionName", true, 
			new Comparator() {
				public int compare(Object token, Object funcCall) {
					return ((String)token).compareTo(((AbstractNode)funcCall).getName());

	 */
	
	public ArrayList findFunctionCalls(Object token, boolean recursive, Comparator c) {
		ArrayList retVal = new ArrayList();
		
		if (start != null){
			if (start instanceof ClassNode && c.compare(token, start) == 0){
			
				// class name can also be a function call
				retVal.add(start);
				// now traverse parents
			}
			for( Iterator itChildren = start.children.iterator(); itChildren.hasNext();){
				Object item = itChildren.next();
				if(item instanceof FunctionNode || item instanceof ClassNode){
					if (c.compare(token, item) == 0){
						retVal.add(item);
					}
				}
			}
		}
			
		ArrayList ancestors = null;
		if (recursive)
			if (parent != null) 
				ancestors = parent.findFunctionCalls(token, recursive, c);
		if (ancestors != null)
			retVal.addAll(ancestors);
		return retVal;
	}
	
	/**
	 * Find a file "name.py", searching the import include path
	 * @return ArrayList of File objects that match.
	 */
	public File findImport(String name, IPath startAt) {
        return null;
	}
	
	public Scope findContainingClass() {
		if (this.start instanceof ClassNode)
			return this;
		else
			return parent != null ? parent.findContainingClass() : null; 
	}

    /**
     * @param functions The functions to set.
     */
    public void setFunctions(ArrayList functions) {
        this.functions = functions;
    }

    /**
     * @return Returns the functions.
     */
    public ArrayList getFunctions() {
        return functions;
    }

}
