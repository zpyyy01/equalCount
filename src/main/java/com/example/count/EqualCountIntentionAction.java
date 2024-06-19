package com.example.count;

import com.intellij.psi.*;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.psi.JavaTokenType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiFile;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.tree.IElementType;
import com.intellij.openapi.ui.Messages;

public class EqualCountIntentionAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile instanceof PsiJavaFile) {
            int equalCount = countEqualityOperators((PsiJavaFile) psiFile);
            Messages.showMessageDialog(
                    e.getProject(),
                    "Number of '==' operators: " + equalCount,
                    "Equal Count Result",
                    Messages.getInformationIcon()
            );
        } else {
            Messages.showMessageDialog(
                    e.getProject(),
                    "Please open a Java file to count '==' operators.",
                    "Equal Count Plugin",
                    Messages.getWarningIcon()
            );
        }
    }

    private int countEqualityOperators(PsiJavaFile javaFile) {
        EqualCountVisitor visitor = new EqualCountVisitor();
        javaFile.accept(visitor);
        return visitor.getEqualCount();
    }

    private static class EqualCountVisitor extends JavaRecursiveElementVisitor {
        private int equalCount = 0;

        @Override
        public void visitBinaryExpression(PsiBinaryExpression expression) {
            super.visitBinaryExpression(expression);
            IElementType operator = expression.getOperationTokenType();
            if (operator.equals(JavaTokenType.EQEQ)) {
                equalCount++;
            }
        }

        public int getEqualCount() {
            return equalCount;
        }
    }
}