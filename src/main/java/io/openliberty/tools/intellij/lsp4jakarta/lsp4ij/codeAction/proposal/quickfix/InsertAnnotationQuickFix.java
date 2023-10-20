/*******************************************************************************
 * Copyright (c) 2021, 2023 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lidia Ataupillco Ramos
 *******************************************************************************/
package io.openliberty.tools.intellij.lsp4jakarta.lsp4ij.codeAction.proposal.quickfix;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import io.openliberty.tools.intellij.lsp4jakarta.lsp4ij.Messages;
import io.openliberty.tools.intellij.lsp4jakarta.lsp4ij.codeAction.proposal.ModifyAnnotationProposal;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.java.codeaction.JavaCodeActionContext;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.java.corrections.proposal.ChangeCorrectionProposal;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Quickfix for adding new annotations with or without attributes
 *
 * @author Zijian Pei
 * @author Lidia Ataupillco Ramos
 *
 */
public class InsertAnnotationQuickFix {

    private final String[] attributes;

    private final String annotation;

    protected final boolean generateOnlyOneCodeAction;

    public InsertAnnotationQuickFix(String annotation, String... attributes) {
        this(annotation, false, attributes);
    }

    /**
     * Constructor for add missing attributes quick fix.
     *
     * @param generateOnlyOneCodeAction true if the participant must generate a
     *                                  CodeAction which add the list of attributes
     *                                  and false otherwise.
     * @param attributes                list of attributes to add.
     */
    public InsertAnnotationQuickFix(String annotation, boolean generateOnlyOneCodeAction,
                                    String... attributes) {
        this.annotation = annotation;
        this.generateOnlyOneCodeAction = generateOnlyOneCodeAction;
        this.attributes = attributes;
    }

    public List<? extends CodeAction> getCodeActions(JavaCodeActionContext context, Diagnostic diagnostic) {
        PsiElement node = context.getCoveredNode();
        PsiModifierListOwner binding = getBinding(node);
        // annotationNode is null when adding an annotation and non-null when adding attributes.
        PsiAnnotation annotationNode = PsiTreeUtil.getParentOfType(node, PsiAnnotation.class);

        List<CodeAction> codeActions = new ArrayList<>();
        addAttributes(diagnostic, context, binding, annotationNode, codeActions, this.annotation);

        return codeActions;
    }

    protected void addAttributes(Diagnostic diagnostic, JavaCodeActionContext context, PsiModifierListOwner binding,
                                 PsiAnnotation annotation, List<CodeAction> codeActions, String name) {
        if (generateOnlyOneCodeAction) {
            addAttribute(diagnostic, context, binding, annotation, codeActions, name, attributes);
        } else {
            for (String attribute : attributes) {
                addAttribute(diagnostic, context, binding, annotation, codeActions, name, attribute);
            }
        }
    }

    /**
     * use setData() API with diagnostic to pass in ElementType in diagnostic
     * collector class.
     *
     */
    private void addAttribute(Diagnostic diagnostic, JavaCodeActionContext context, PsiModifierListOwner binding,
                              PsiAnnotation annotation, List<CodeAction> codeActions, String name, String... attributes) {
        String label = getLabel(name, attributes);
        ChangeCorrectionProposal proposal = new ModifyAnnotationProposal(label, context.getSource().getCompilationUnit(),
                context.getASTRoot(), binding, annotation, 0, name, Arrays.asList(attributes));
        CodeAction codeAction = context.convertToCodeAction(proposal, diagnostic);

        if (codeAction != null) {
            codeActions.add(codeAction);
        }
    }

    protected PsiModifierListOwner getBinding(PsiElement node) {
        // handle annotation insertions for a variable declaration or a class
        PsiModifierListOwner element = PsiTreeUtil.getParentOfType(node, PsiModifierListOwner.class);
        if (element != null) {
            return element;
        }
        return PsiTreeUtil.getParentOfType(node, PsiClass.class);
    }

    protected String getLabel(String annotationName, String... attributes) {
        return Messages.getMessage("InsertItem", "@" + annotation); // uses Java syntax
    }
}
