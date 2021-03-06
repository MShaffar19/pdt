/*******************************************************************************
 * Copyright (c) 2012, 2016, 2017 PDT Extension Group and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     PDT Extension Group - initial API and implementation
 *     Kaloyan Raev - [501269] externalize strings
 *     Kaloyan Raev - [511744] Wizard freezes if no PHP executable is configured
 *******************************************************************************/
package org.eclipse.php.composer.ui.job.runner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.php.composer.core.log.Logger;
import org.eclipse.php.composer.ui.ComposerUIPlugin;
import org.eclipse.php.composer.ui.dialogs.MissingExecutableDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class MissingExecutableRunner implements Runnable {

	private int returnCode;

	@Override
	public void run() {
		try {
			Status status = new Status(IStatus.WARNING, ComposerUIPlugin.PLUGIN_ID,
					Messages.MissingExecutableRunner_ErrorMessage);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MissingExecutableDialog dialog = new MissingExecutableDialog(shell, status);
			returnCode = dialog.open();
		} catch (Exception e) {
			Logger.logException(e);
		}
	}

	public int getReturnCode() {
		return returnCode;
	}
}