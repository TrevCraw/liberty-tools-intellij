/*******************************************************************************
 * Copyright (c) 2020, 2023 Red Hat Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *     IBM Corp. - IntelliJ support
 *******************************************************************************/
package io.openliberty.tools.intellij.lsp4jakarta.lsp4ij.servlet;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.java.codeaction.JavaCodeActionContext;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.java.corrections.proposal.ChangeCorrectionProposal;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.java.corrections.proposal.ImplementInterfaceProposal;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;

import java.util.ArrayList;
import java.util.List;

/**
 * QuickFix for fixing HttpServlet extension error by providing the code actions
 * which implements IJavaCodeActionParticipant
 *
 * Adapted from
 * https://github.com/eclipse/lsp4mp/blob/master/microprofile.jdt/org.eclipse.lsp4mp.jdt.core/src/main/java/org/eclipse/lsp4mp/jdt/internal/health/java/ImplementHealthCheckQuickFix.java
 *
 * @author Credit to Angelo ZERR
 *
 */
public class FilterImplementationQuickFix {

    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic) {
        List<CodeAction> codeActions = new ArrayList<>();
        PsiElement node = context.getCoveredNode();
        PsiClass parentType = getBinding(node);
        if (parentType != null) {
            // Create code action
            // interface
            ChangeCorrectionProposal proposal = new ImplementInterfaceProposal(
                    null, parentType, context.getASTRoot(),
                    "jakarta.servlet.Filter", 0, context.getSource().getCompilationUnit());
            CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);

            if (codeAction != null) {
                codeActions.add(codeAction);
            }
        }
        return codeActions;
    }

    private PsiClass getBinding(PsiElement node) {
        return PsiTreeUtil.getParentOfType(node, PsiClass.class);
    }
}
