/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.python.pydev.analysis.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.python.pydev.core.ICodeCompletionASTManager;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.core.IPythonNature;
import org.python.pydev.core.MisconfigurationException;
import org.python.pydev.core.Tuple;
import org.python.pydev.core.docutils.PySelection;
import org.python.pydev.core.log.Log;
import org.python.pydev.editor.actions.PyAction;
import org.python.pydev.editor.actions.PyOpenAction;
import org.python.pydev.editor.codecompletion.revisited.CompletionCache;
import org.python.pydev.editor.model.ItemPointer;
import org.python.pydev.plugin.PydevPlugin;
import org.python.pydev.plugin.nature.PythonNature;
import org.python.pydev.plugin.nature.SystemPythonNature;
import org.python.pydev.ui.interpreters.ChooseInterpreterManager;

import com.python.pydev.analysis.AnalysisPlugin;
import com.python.pydev.analysis.additionalinfo.AbstractAdditionalDependencyInfo;
import com.python.pydev.analysis.additionalinfo.AbstractAdditionalInterpreterInfo;
import com.python.pydev.analysis.additionalinfo.AdditionalProjectInterpreterInfo;
import com.python.pydev.analysis.additionalinfo.AdditionalSystemInterpreterInfo;
import com.python.pydev.analysis.additionalinfo.IInfo;

public class PyGlobalsBrowser extends PyAction{

    public void run(IAction action) {
        IPythonNature pythonNature;
        try{
            pythonNature = getPyEdit().getPythonNature();
        }catch(MisconfigurationException e1){
            handle(e1);
            return;
        }
        PySelection ps = new PySelection(this.getPyEdit());
        String selectedText = ps.getSelectedText();
        if(selectedText == null || selectedText.length() == 0){
        	try {
				selectedText = ps.getCurrToken().o1;
			} catch (BadLocationException e) {
				//ignore
			}
        }

        if(pythonNature != null){
            IInterpreterManager manager = pythonNature.getRelatedInterpreterManager();
            getFromManagerAndRelatedNatures(selectedText, manager);
        }else{
            getFromSystemManager(selectedText);
        }
        

    }

    /**
     * @param selectedText the text that should be selected in the beginning (may be null)
     */
    private void getFromSystemManager(String selectedText) {
        //is null
        Tuple<SystemPythonNature, String> infoForFile = PydevPlugin.getInfoForFile(getPyEdit().getEditorFile());
        if(infoForFile != null){
            IPythonNature systemPythonNature = infoForFile.o1;
            if(systemPythonNature == null){
                getFromWorkspace(selectedText);
                return;
            }
            
            IInterpreterManager manager = infoForFile.o1.getRelatedInterpreterManager();
            getFromManagerAndRelatedNatures(selectedText, manager);
            
        }else{
            getFromWorkspace(selectedText);
        }
    }

    /**
     * This method will check if the user has python and/or the jython interpreter configured. If it has only
     * one of those, it will get the info for it and the related projects.
     * 
     * If both are configured, default is python
     * 
     * If none is configured, it will show an error saying so.
     *  
     * @param selectedText the text that should be initially set as the filter
     */
    public static void getFromWorkspace(String selectedText) {
        IInterpreterManager useManager = ChooseInterpreterManager.chooseInterpreterManager();
        if(useManager == null){
            return;
        }
        
        getFromManagerAndRelatedNatures(selectedText, useManager);
        
    }

    private static void handle(MisconfigurationException e){
        Log.log(e);
    }

    /**
     * Gets it using all the natures that match a given interpreter manager.
     * @throws MisconfigurationException 
     */
    private static void getFromManagerAndRelatedNatures(String selectedText, IInterpreterManager useManager){
        AbstractAdditionalInterpreterInfo additionalSystemInfo;
		try {
			additionalSystemInfo = AdditionalSystemInterpreterInfo.getAdditionalSystemInfo(
			        useManager, useManager.getDefaultInterpreter());
		} catch (MisconfigurationException e) {
			MessageDialog.openError(getShell(), "Error", "Additional info is not available (default interpreter not configured).");
			handle(e);
			return;
		}
        
        List<AbstractAdditionalInterpreterInfo> additionalInfo = new ArrayList<AbstractAdditionalInterpreterInfo>();
        additionalInfo.add(additionalSystemInfo);
        
        List<IPythonNature> natures = PythonNature.getPythonNaturesRelatedTo(useManager.getInterpreterType());
        for (IPythonNature nature : natures) {
            AbstractAdditionalDependencyInfo info;
			try {
				info = AdditionalProjectInterpreterInfo.getAdditionalInfoForProject(nature);
				if(info != null){
					additionalInfo.add(info);
				}
			} catch (MisconfigurationException e) {
				//just go on to the next nature if one is not properly configured.
				handle(e);
			}
        }
        doSelect(natures, additionalInfo, selectedText);
    }
    

    /**
     * @param pythonNatures the natures from were we can get info
     * @param additionalInfo the additional informations 
     * @param selectedText the text that should be initially set as a filter
     */
    public static void doSelect(List<IPythonNature> pythonNatures, List<AbstractAdditionalInterpreterInfo> additionalInfo, 
            String selectedText) {
        
        SelectionDialog dialog = GlobalsDialogFactory.create(getShell(), additionalInfo, selectedText, pythonNatures);

        dialog.open();
        Object[] result = dialog.getResult();
        if(result != null && result.length > 0){
            for(Object obj:result){
                IInfo entry;
                if(obj instanceof AdditionalInfoAndIInfo){
                    entry = ((AdditionalInfoAndIInfo)obj).info;
                }else{
                    entry = (IInfo) obj;
                }
                List<ItemPointer> pointers = new ArrayList<ItemPointer>();
                
                CompletionCache completionCache = new CompletionCache();
                for(IPythonNature pythonNature:pythonNatures){
                    //try to find in one of the natures...
                    ICodeCompletionASTManager astManager = pythonNature.getAstManager();
                    if(astManager == null){
                        continue;
                    }
                    AnalysisPlugin.getDefinitionFromIInfo(pointers, astManager, pythonNature, entry, completionCache);
                    if(pointers.size() > 0){
                        new PyOpenAction().run(pointers.get(0));
                        break; //don't check the other natures
                    }
                }
            }
        }
    }

}