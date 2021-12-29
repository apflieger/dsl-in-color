package com.meetinclass.dslincolor.intellij

import com.intellij.codeInsight.AnnotationUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.psi.*
import com.meetinclass.dslincolor.intellij.ColorEx.GREEN
import com.meetinclass.dslincolor.intellij.ColorEx.YELLOW

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
                    if (annotationValue.text == "\"html-dsl-tag\"") {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(element.methodExpression)
                            .enforcedTextAttributes(TextAttributes(YELLOW, null, null, null, 0))
                            .create()
                    }
                    if (annotationValue.text == "\"html-dsl-attribute\"") {
                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                            .range(element.methodExpression)
                            .enforcedTextAttributes(TextAttributes(GREEN, null, null, null, 0))
                            .create()
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
                                    if (parameterAnnotation.text == "\"html-dsl-attribute\"") {
                                        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                                            .range(it)
                                            .enforcedTextAttributes(TextAttributes(GREEN, null, null, null, 0))
                                            .create()
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    companion object {
        val NAMED_COLOR_ANNOTATION: String = com.meetinclass.dslincolor.annotations.NamedColor::class.java.name
    }
}