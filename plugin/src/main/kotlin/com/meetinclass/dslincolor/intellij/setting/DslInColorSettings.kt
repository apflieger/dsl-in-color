package com.meetinclass.dslincolor.intellij.setting

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil.copyBean
import org.jetbrains.annotations.Nullable


@State(name = "DslInColorSettings", storages = [(Storage("dsl_in_color.xml"))])
class DslInColorSettings : PersistentStateComponent<DslInColorSettings> {
    var annotationList: Set<String> = setOf("html-dsl-tag", "html-dsl-attribute")

    @Nullable
    override fun getState() = this

    override fun loadState(state: DslInColorSettings) {
        copyBean(state, this)
    }

    companion object {
        val instance: DslInColorSettings
            get() = ApplicationManager.getApplication().getService(DslInColorSettings::class.java)
    }
}