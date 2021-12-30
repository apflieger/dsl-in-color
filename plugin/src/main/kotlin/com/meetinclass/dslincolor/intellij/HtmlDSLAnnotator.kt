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
import com.meetinclass.dslincolor.intellij.ColorEx.dslAttrColor
import com.meetinclass.dslincolor.intellij.ColorEx.dslTagColor
import java.awt.Color

class HtmlDSLAnnotator : Annotator {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val scheme = EditorColorsManager.getInstance().globalScheme
        if (
            element.language.displayName.toLowerCase() == "java" && element is PsiMethodCallExpression
        ) {
            val psiMethod = element.resolveMethod()
            if (psiMethod != null) {
                val annotationValue = AnnotationUtil.findAnnotation(psiMethod, NAMED_COLOR_ANNOTATION)
                    ?.findAttributeValue("value")
                if (annotationValue != null) {
                    if (annotationValue.text == HTML_DSL_TAG) {
                        colorify(holder, element, scheme.dslTagColor())
                    }
                    if (annotationValue.text == HTML_DSL_ATTR) {
                        colorify(holder, element, scheme.dslAttrColor())
                    }

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
                                    if (parameterAnnotation.text == HTML_DSL_ATTR) {
                                        colorify(holder, element, scheme.dslAttrColor())
                                    }
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
        color: Color
    ) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(element.methodExpression)
            .enforcedTextAttributes(TextAttributes(color, null, null, null, 0))
            .create()
    }

    companion object {
        const val HTML_DSL_TAG: String = "\"html-dsl-tag\""
        const val HTML_DSL_ATTR: String = "\"html-dsl-attribute\""
        val NAMED_COLOR_ANNOTATION: String = com.meetinclass.dslincolor.annotations.NamedColor::class.java.name
    }
}