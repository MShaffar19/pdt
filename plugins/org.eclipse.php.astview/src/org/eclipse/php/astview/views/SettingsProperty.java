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
package org.eclipse.php.astview.views;

import org.eclipse.php.core.ast.nodes.AST;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.swt.graphics.Image;

public class SettingsProperty extends ASTAttribute {

	private final Program fRoot;

	public SettingsProperty(Program root) {
		fRoot = root;
	}

	@Override
	public Object getParent() {
		return fRoot;
	}

	@Override
	public Object[] getChildren() {
		AST ast = fRoot.getAST();
		Object[] res = { new GeneralAttribute(this, "apiLevel", String.valueOf(ast.apiLevel())),
				new GeneralAttribute(this, "hasResolvedBindings", String.valueOf(ast.hasResolvedBindings())), };
		return res;
	}

	@Override
	public String getLabel() {
		return "> AST settings"; //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return 19;
	}
}
