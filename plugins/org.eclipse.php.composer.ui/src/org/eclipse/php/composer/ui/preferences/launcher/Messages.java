/*******************************************************************************
 * Copyright (c) 2017 Rogue Wave Software, Inc. and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Rogue Wave Software, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.php.composer.ui.preferences.launcher;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.php.composer.ui.preferences.launcher.messages"; //$NON-NLS-1$
	public static String LauncherConfigurationBlock_BrowseButton;
	public static String LauncherConfigurationBlock_CannotDetermineVersionMessage;
	public static String LauncherConfigurationBlock_DetectedVersionMessage;
	public static String LauncherConfigurationBlock_ExecutionFailedMessage;
	public static String LauncherConfigurationBlock_HelpLink;
	public static String LauncherConfigurationBlock_InvalidPHPScriptError;
	public static String LauncherConfigurationBlock_NoPHPConfiguredError;
	public static String LauncherConfigurationBlock_PhpExeLabel;
	public static String LauncherConfigurationBlock_PhpExesLink;
	public static String LauncherConfigurationBlock_ReasonMessage;
	public static String LauncherConfigurationBlock_TestButton;
	public static String LauncherConfigurationBlock_TestDialogTitle;
	public static String LauncherConfigurationBlock_TestSuccessMessage;
	public static String LauncherConfigurationBlock_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
