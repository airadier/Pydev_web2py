/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package org.python.pydev.core.tooltips.presenter;

import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Point;

/**
 * Information presenter for tooltips.
 */
public abstract class AbstractTooltipInformationPresenter extends AbstractInformationPresenter {

    public String updatePresentation(Drawable drawable, String hoverInfo, TextPresentation presentation, int maxWidth, int maxHeight) {
        if (drawable instanceof StyledText) {
            final StyledText styledText = (StyledText) drawable;
            styledText.addMouseListener(new MouseAdapter() {

                public void mouseDown(MouseEvent e) {
                    try {
                        int offset = styledText.getOffsetAtLocation(new Point(e.x, e.y));
                        StyleRange r = styledText.getStyleRangeAtOffset(offset);
                        if(r != null){
                            onHandleClick(r.data);
                        }
                    } catch (IllegalArgumentException e1) {
                    } catch (SWTException e1) {
                    }
                }
            });
        }
        
        hoverInfo = this.correctLineDelimiters(hoverInfo);
        onUpdatePresentation(hoverInfo, presentation);
        
        return hoverInfo;
    }

    protected abstract void onUpdatePresentation(String hoverInfo, TextPresentation presentation);

    protected abstract void onHandleClick(Object data);

}
