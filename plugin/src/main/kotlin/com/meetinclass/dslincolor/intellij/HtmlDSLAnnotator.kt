package com.meetinclass.dslincolor.intellij

import com.intellij.codeInsight.AnnotationUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiMethodCallExpression
import com.meetinclass.dslincolor.intellij.ColorEx.getColorByAnnotationValue

class HtmlDSLAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (
            element.language.displayName.toLowerCase() == "java" && element is PsiMethodCallExpression
        ) {
            val psiMethod = element.resolveMethod()
            if (psiMethod != null) {
                val annotationValue = AnnotationUtil.findAnnotation(psiMethod, NAMED_COLOR_ANNOTATION)
                    ?.findAttributeValue("value")
                if (annotationValue != null) {
                    colorify(holder, element, annotationValue.text)
                    element.argumentList.expressions
                        .forEachIndexed { idx, it ->
                            if (it is PsiLiteralExpression) {
                                val parameterAnnotation =
                                    AnnotationUtil.findAnnotation(
                                        psiMethod.parameterList.parameters[idx],
                                        NAMED_COLOR_ANNOTATION
                                    )
                                        ?.findAttributeValue("value")
                                if (parameterAnnotation != null) {
                                    colorify(holder, element, parameterAnnotation.text)
                                }
                            }
                        }
                }
            }
        }
    }

    private fun colorify(
        holder: AnnotationHolder,
        element: PsiMethodCallExpression,
        annotationValue: String
    ) {
        val scheme = EditorColorsManager.getInstance().globalScheme
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.methodExpression)
            .enforcedTextAttributes(
                TextAttributes(
                    scheme.getColorByAnnotationValue(annotationValue),
                    null,
                    null,
                    null,
                    0
                )
            )
            .create()
    }

    companion object {
        val NAMED_COLOR_ANNOTATION: String = com.meetinclass.dslincolor.annotations.NamedColor::class.java.name
    }
}