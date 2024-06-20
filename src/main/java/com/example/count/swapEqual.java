package com.example.count;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiBinaryExpression;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTypesUtil;
import org.jetbrains.annotations.NotNull;
final class swapEqual extends AbstractBaseJavaLocalInspectionTool {
    private final ReplaceWithEqualsswapped myQuickFix = new ReplaceWithEqualsswapped();

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {
            @Override
            public void visitBinaryExpression(@NotNull PsiBinaryExpression expression) {
                super.visitBinaryExpression(expression);

                IElementType opSign = expression.getOperationTokenType();
                if (opSign == JavaTokenType.EQEQ) {
                    PsiExpression lOperand = expression.getLOperand();
                    PsiExpression rOperand = expression.getROperand();
                    if (rOperand == null) {
                        return;
                    }
                    PsiType lType = lOperand.getType();
                    PsiType rType = rOperand.getType();
                    if (lType == null || rType == null) {
                        return;
                    }
                    holder.registerProblem(expression, InspectionBundle.message("inspection.comparing.string.references.problem.descriptor"), myQuickFix);
                }
            }
        };
    }

    private static class ReplaceWithEqualsswapped implements LocalQuickFix {

        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.message("inspection.comparing.string.references.use.quickfix");
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            // binaryExpression holds a PSI expression of the form "x == y",
            // which needs to be swapped to "y == x"
            PsiBinaryExpression binaryExpression = (PsiBinaryExpression) descriptor.getPsiElement();
            IElementType opSign = binaryExpression.getOperationTokenType();
            PsiExpression lExpr = binaryExpression.getLOperand();
            PsiExpression rExpr = binaryExpression.getROperand();
            if (rExpr == null) {
                return;
            }
            PsiExpression newLExpr, newRExpr;
            if (opSign == JavaTokenType.EQEQ) {
                newLExpr = rExpr;
                newRExpr = lExpr;
            } else {
                newLExpr = lExpr;
                newRExpr = rExpr;
            }

            PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
            PsiBinaryExpression newBinaryExpression = (PsiBinaryExpression) factory.createExpressionFromText(
                    newLExpr.getText() + opSign.toString() + newRExpr.getText(), binaryExpression
            );
            binaryExpression.replace(newBinaryExpression);
        }

        @NotNull
        public String getFamilyName() {
            return getName();
        }

    }
}
