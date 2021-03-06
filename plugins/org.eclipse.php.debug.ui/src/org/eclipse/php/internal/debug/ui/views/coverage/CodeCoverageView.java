/*******************************************************************************
 * Copyright (c) 2017 Rogue Wave Software Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Rogue Wave Software Inc. - initial implementation
 *******************************************************************************/
package org.eclipse.php.internal.debug.ui.views.coverage;

import java.io.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.php.internal.debug.core.zend.communication.IRemoteFileContentRequestor;
import org.eclipse.php.internal.debug.core.zend.debugger.CodeCoverageData;
import org.eclipse.php.internal.debug.core.zend.debugger.RemoteDebugger;
import org.eclipse.php.internal.ui.IPHPHelpContextIds;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Code coverage view.
 */
public class CodeCoverageView extends ViewPart {

	private CodeCoverageTextViewer fSourceViewer;
	private CodeCoverageData fCodeCoverageData;
	private CodeCoverageViewActionGroup fActionGroup;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.
	 * widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		fSourceViewer = new CodeCoverageTextViewer(parent, SWT.NONE);
		fActionGroup = new CodeCoverageViewActionGroup(this);
		fActionGroup.fillActionBars(getViewSite().getActionBars());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IPHPHelpContextIds.CODE_COVERAGE_VIEW);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	public void setInput(CodeCoverageData codeCoverageData) {
		if (fSourceViewer != null && fCodeCoverageData != codeCoverageData) {
			fCodeCoverageData = codeCoverageData;
			fSourceViewer.setCoverageBitmask(codeCoverageData.getCoverageBitmask());
			fSourceViewer.setSingificanceBitmask(codeCoverageData.getSignificanceBitmask());
			fSourceViewer.applyStyles();
			String fileName = codeCoverageData.getLocalFileName();
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			InputStream inputStream = null;
			try {
				Path path = new Path(fileName);
				IFile file = root.getFile(path);
				if (file != null && file.exists()) { // full path specified
					inputStream = file.getContents();
				} else {
					file = root.getFileForLocation(path);
					if (file != null && file.exists()) { // full path specified
						inputStream = file.getContents();
					}
				}
			} catch (Exception e) {
			}
			try {
				if (inputStream == null) {
					File localFile = new File(fileName);
					if (localFile.exists()) {
						inputStream = new FileInputStream(localFile);
					}
				}
			} catch (IOException e) {
				DebugUIPlugin.log(e);
			}
			if (inputStream != null) {
				try {
					BufferedReader is = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder fileContents = new StringBuilder();
					String line = null;
					while ((line = is.readLine()) != null) {
						fileContents.append(line);
						fileContents.append("\n"); //$NON-NLS-1$
					}
					fSourceViewer.setText(fileContents.toString());
				} catch (IOException e) {
					DebugUIPlugin.log(e);
				}
			} else {
				RemoteDebugger.requestRemoteFile(new IRemoteFileContentRequestor() {
					@Override
					public void fileContentReceived(final byte[] content, String serverAddress, String originalURL,
							String fileName, int lineNumber) {
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								if (fSourceViewer != null && !fSourceViewer.isDisposed()) {
									fSourceViewer.setText(new String(content));
								}
							}
						});
					}

					@Override
					public void requestCompleted(Exception e) {
					}
				}, fileName, 1, codeCoverageData.getURL());
			}
			setTitleToolTip(fileName);
			int idx = Math.max(fileName.lastIndexOf('\\'), fileName.lastIndexOf('/'));
			String lastSegment = idx == -1 ? fileName : fileName.substring(idx + 1);
			setPartName(lastSegment);
		}
	}

	public CodeCoverageTextViewer getViewer() {
		return fSourceViewer;
	}
}
