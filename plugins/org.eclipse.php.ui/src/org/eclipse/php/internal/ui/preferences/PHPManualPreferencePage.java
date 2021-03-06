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
package org.eclipse.php.internal.ui.preferences;

import org.eclipse.php.internal.ui.IPHPHelpContextIds;
import org.eclipse.php.internal.ui.PHPUIMessages;
import org.eclipse.wst.sse.ui.internal.preferences.OverlayPreferenceStore;

public class PHPManualPreferencePage extends AbstractConfigurationBlockPreferencePage {

	@Override
	protected IPreferenceConfigurationBlock createConfigurationBlock(OverlayPreferenceStore overlayPreferenceStore) {
		return new PHPManualConfigurationBlock(this, overlayPreferenceStore);
	}

	@Override
	protected String getHelpId() {
		return IPHPHelpContextIds.PHP_MANUAL_PREFERENCES;
	}

	@Override
	protected void setDescription() {
		setDescription(PHPUIMessages.PHPManualPreferencePage_0);
	}

	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(PreferenceConstants.getPreferenceStore());
	}
}
