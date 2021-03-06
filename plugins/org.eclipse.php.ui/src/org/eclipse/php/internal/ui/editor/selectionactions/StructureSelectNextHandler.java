/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.php.internal.ui.editor.selectionactions;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.core.ast.visitor.ApplyAll;
import org.eclipse.php.internal.core.corext.dom.Selection;
import org.eclipse.php.internal.core.corext.dom.SelectionAnalyzer;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.xml.ui.internal.handlers.StructuredSelectNextXMLHandler;

public class StructureSelectNextHandler extends StructuredSelectNextXMLHandler {

	private ISourceModule sourceModule = null;

	private static class NextNodeAnalyzer extends ApplyAll {
		private final int fOffset;
		private ASTNode fNextNode;

		private NextNodeAnalyzer(int offset) {
			fOffset = offset;
		}

		public static ASTNode perform(int offset, ASTNode lastCoveringNode) {
			NextNodeAnalyzer analyzer = new NextNodeAnalyzer(offset);
			lastCoveringNode.accept(analyzer);
			return analyzer.fNextNode;
		}

		@Override
		protected boolean apply(ASTNode node) {
			int start = node.getStart();
			int end = start + node.getLength();
			if (start == fOffset) {
				fNextNode = node;
				return true;
			} else {
				return (start < fOffset && fOffset < end);
			}
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		sourceModule = StructureSelectUtil.getSourceModule(event);
		super.execute(event);
		sourceModule = null;

		return null;
	}

	protected Region getNewSelectionRegion(IndexedRegion indexedRegion, ITextSelection textSelection) {
		if (sourceModule != null && StructureSelectUtil.isPHP(indexedRegion)) {
			Selection selection = Selection.createFromStartLength(textSelection.getOffset(), textSelection.getLength());
			SelectionAnalyzer selAnalyzer = new SelectionAnalyzer(selection, true);
			try {
				Program ast = StructureSelectUtil.getAST(sourceModule);
				ast.accept(selAnalyzer);
				Region oldSourceRange = new Region(textSelection.getOffset(), textSelection.getLength());
				if (textSelection.getLength() == 0 && selAnalyzer.getLastCoveringNode() != null) {
					ASTNode previousNode = NextNodeAnalyzer.perform(oldSourceRange.getOffset(),
							selAnalyzer.getLastCoveringNode());
					if (previousNode != null)
						return StructureSelectUtil.getSelectedNodeSourceRange(sourceModule, previousNode);
				}
				ASTNode first = selAnalyzer.getFirstSelectedNode();
				if (first == null)
					return StructureSelectUtil.getLastCoveringNodeRange(oldSourceRange, sourceModule, selAnalyzer);

				ASTNode parent = first.getParent();
				if (parent == null)
					return StructureSelectUtil.getLastCoveringNodeRange(oldSourceRange, sourceModule, selAnalyzer);

				ASTNode lastSelectedNode = selAnalyzer.getSelectedNodes()[selAnalyzer.getSelectedNodes().length - 1];
				ASTNode nextNode = getNextNode(parent, lastSelectedNode);
				if (nextNode == parent)
					return StructureSelectUtil.getSelectedNodeSourceRange(sourceModule, first.getParent());
				int offset = oldSourceRange.getOffset();
				int end = Math.min(sourceModule.getSourceRange().getLength(),
						nextNode.getStart() + nextNode.getLength() - 1);
				return StructureSelectUtil.createSourceRange(offset, end);
			} catch (ModelException e) {
			} catch (IOException e) {
			}
		}
		return super.getNewSelectionRegion(indexedRegion, textSelection);
	}

	private ASTNode getNextNode(ASTNode parent, ASTNode node) {
		ASTNode[] siblingNodes = StructureSelectUtil.getSiblingNodes(node);
		if (siblingNodes == null || siblingNodes.length == 0)
			return parent;
		if (node == siblingNodes[siblingNodes.length - 1])
			return parent;
		else
			return siblingNodes[StructureSelectUtil.findIndex(siblingNodes, node) + 1];
	}
}
