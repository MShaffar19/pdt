/*******************************************************************************
 * Copyright (c) 2015 Dawid Pakuła and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Dawid Pakuła - initial API and implementation
 *******************************************************************************/
package org.eclipse.php.internal.core.typeinference;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.evaluation.types.IClassType;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.core.compiler.ast.nodes.AnonymousClassDeclaration;
import org.eclipse.php.internal.core.Logger;

public class AnonymousClassInstanceType implements IClassType {

	private AnonymousClassDeclaration clazz;
	private ISourceModule module;

	public AnonymousClassInstanceType(ISourceModule module, AnonymousClassDeclaration clazz) {
		this.clazz = clazz;
		this.module = module;
	}

	public AnonymousClassDeclaration getTypeDeclaration() {
		return this.clazz;
	}

	@Override
	public String getTypeName() {
		if (clazz != null) {
			return "anonymous class instance"; //$NON-NLS-1$
		}
		return "class instance: !!unknown!!"; //$NON-NLS-1$
	}

	public ISourceModule getModule() {
		return module;
	}

	public IType getType() {
		try {
			IModelElement element = module.getElementAt(this.clazz.sourceStart());
			if (element instanceof IType) {
				return (IType) element;
			}
		} catch (ModelException e) {
			Logger.logException(e);
		}

		return null;
	}

	@Override
	public boolean subtypeOf(IEvaluatedType type) {
		// TODO detect by superclass/interface list
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 45;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AnonymousClassInstanceType other = (AnonymousClassInstanceType) obj;
		if (clazz == null) {
			if (other.clazz != null) {
				return false;
			}
		} else if (!clazz.equals(other.clazz)) {
			return false;
		}
		if (module == null) {
			if (other.module != null) {
				return false;
			}
		} else if (!module.equals(other.module)) {
			return false;
		}
		return true;
	}
}
