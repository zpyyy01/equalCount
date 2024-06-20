package com.example.count;
// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implements an intention action to replace a ternary statement with if-then-else.
 */
@NonNls
final class swapEqual extends PsiElementBaseIntentionAction implements IntentionAction {

    /**
     * Checks whether this intention is available at the caret offset in file - the caret must sit just before a "?"
     * character in a ternary statement. If this condition is met, this intention's entry is shown in the available
     * intentions list.
     *
     * <p>Note: this method must do its checks quickly and return.</p>
     *
     * @param project a reference to the Project object being edited.
     * @param editor  a reference to the object editing the project source
     * @param element a reference to the PSI element currently under the caret
     * @return {@code true} if the caret is in a literal string element, so this functionality should be added to the
     * intention menu or {@code false} for all other types of caret positions
     */
    public boolean isAvailable(@NotNull Project project, Editor editor, @Nullable PsiElement element) {
        // Quick sanity check
        if (element == null) {
            return false;
        }

        // Is this a token of type representing an "==" character?
        if (element instanceof PsiJavaToken token) {
            if (token.getTokenType() != JavaTokenType.EQEQ) {
                return false;
            }
            return token.getParent() instanceof PsiBinaryExpression;
        }
        return false;
    }

    /**
     * @param project a reference to the Project object being edited.
     * @param editor  a reference to the object editing the project source
     * @param element a reference to the PSI element currently under the caret
     * @throws IncorrectOperationException Thrown by underlying (PSI model) write action context
     *                                     when manipulation of the PSI tree fails.
     */
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element)
            throws IncorrectOperationException {
        //swapExpressionSides of ==
        PsiBinaryExpression binaryExpression = (PsiBinaryExpression) element.getParent();
        PsiExpression lhs = binaryExpression.getLOperand();
        PsiExpression rhs = binaryExpression.getROperand();
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);
        if(rhs == null) return;
        PsiExpression newExpression = factory.createExpressionFromText(rhs.getText() + " == " + lhs.getText(), binaryExpression);
        binaryExpression.replace(newExpression);

    }


    @NotNull
    public String getText() {
        return getFamilyName();
    }


    @NotNull
    public String getFamilyName() {
        return "By zpyyy: flip ==";
    }

}
