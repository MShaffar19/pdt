/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.php.internal.debug.core.sourcelookup;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.sourcelookup.containers.PHPCompositeSourceContainer;
import org.eclipse.php.internal.debug.core.sourcelookup.containers.WorkspaceRootSourceContainer;

/**
 * Computes the default source lookup path for a PHP launch configuration. For
 * now just use a project name.
 */
public class PHPSourcePathComputerDelegate implements ISourcePathComputerDelegate {

	@Override
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		ISourceContainer sourceContainer;
		String projectName = configuration.getAttribute(IPHPDebugConstants.PHP_Project, (String) null);
		if (projectName != null) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			// Search in specific project only
			sourceContainer = new PHPCompositeSourceContainer(project, configuration);
		} else {
			// Search in the Workspace Root
			sourceContainer = new WorkspaceRootSourceContainer();
		}
		return new ISourceContainer[] { sourceContainer };
	}
}
