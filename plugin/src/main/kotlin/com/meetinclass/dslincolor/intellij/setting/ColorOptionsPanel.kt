package com.meetinclass.dslincolor.intellij.setting

import com.intellij.application.options.colors.*
import com.intellij.ide.util.PropertiesComponent
import com.intellij.ui.ColorPanel
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.EventDispatcher
import com.meetinclass.dslincolor.intellij.HtmlDSLAnnotator
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode
import java.awt.Color
import java.awt.event.ActionListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath
import javax.swing.tree.TreeSelectionModel


class ColorOptionsPanel(
    private val options: ColorAndFontOptions,
    private val schemesProvider: SchemesPanel,
    private val category: String
) : OptionsPanel {

    private lateinit var rootPanel: JPanel
    private lateinit var optionsTree: Tree

    private lateinit var colorLabel: JLabel

    private lateinit var color: ColorPanel

    private lateinit var gradientLabel: JLabel

    private val properties: PropertiesComponent = PropertiesComponent.getInstance()
    private val eventDispatcher: EventDispatcher<ColorAndFontSettingsListener> =
        EventDispatcher.create(ColorAndFontSettingsListener::class.java)

    init {

        val actionListener = ActionListener {
            eventDispatcher.multicaster.settingsChanged()
            options.stateChanged()
        }
        color.addActionListener(actionListener)

        options.addListener(object : ColorAndFontSettingsListener.Abstract() {
            override fun settingsChanged() {
                if (!schemesProvider.areSchemesLoaded()) return
                if (optionsTree.selectedValue != null) {
                    // update options after global state change
                    processListValueChanged()
                }
            }
        })

        optionsTree.apply {
            isRootVisible = false
            model = DefaultTreeModel(DefaultMutableTreeTableNode())
            selectionModel.selectionMode = TreeSelectionModel.SINGLE_TREE_SELECTION
            addTreeSelectionListener {
                if (schemesProvider.areSchemesLoaded()) {
                    processListValueChanged()
                }
            }
        }
    }

    override fun getPanel(): JPanel = rootPanel

    override fun addListener(listener: ColorAndFontSettingsListener) {
        eventDispatcher.addListener(listener)
    }

    override fun updateOptionsList() {
        fillOptionsList()
        processListValueChanged()
    }

    private data class DescriptionsNode(val optionName: String, val descriptions: List<TextAttributesDescription>) {
        override fun toString(): String = optionName
    }

    private fun fillOptionsList() {
        val nodes = options.currentDescriptions.asSequence()
            .filter { it is TextAttributesDescription && it.group == category }
            .map {
                val description = it as TextAttributesDescription
                val rainbowName = description.toString().split(":")[0]
                rainbowName to description
            }
            .groupBy { it.first }
            .map { (rainbowName, descriptions) ->
                DefaultMutableTreeNode(
                    DescriptionsNode(rainbowName,
                        descriptions.asSequence().map { it.second }.toList().sortedBy { it.toString() })
                )
            }
        val root = DefaultMutableTreeNode()
        for (node in nodes) {
            root.add(node)
        }

        (optionsTree.model as DefaultTreeModel).setRoot(root)
    }

    private fun processListValueChanged() {
        var descriptionsNode = optionsTree.selectedDescriptions
        if (descriptionsNode == null) {
            properties.getValue(SELECTED_COLOR_OPTION_PROPERTY)?.let { preselected ->
                optionsTree.selectOptionByRainbowName(preselected)
                descriptionsNode = optionsTree.selectedDescriptions
            }
        }

        descriptionsNode?.run {
            properties.setValue(SELECTED_COLOR_OPTION_PROPERTY, optionName)
            reset(optionName, descriptions)
        } ?: resetDefault()
    }

    private fun resetDefault() {
        gradientLabel.text = "Assign color from the spectrum below:"

        color.isEnabled = false
        color.selectedColor = null
        colorLabel.isEnabled = false
    }

    private fun reset(name: String, descriptions: List<TextAttributesDescription>) {
        gradientLabel.text = "Assign each ${name.toLowerCase()} its own color from the spectrum below:"

        color.isEnabled = true
        colorLabel.isEnabled = true
        color.selectedColor = descriptions.firstOrNull()?.rainbowColor
        descriptions.firstOrNull()?.let { eventDispatcher.multicaster.selectedOptionChanged(it) }
    }


    override fun applyChangesToScheme() {
        val scheme = options.selectedScheme
        val (_, descriptions) = optionsTree.selectedDescriptions ?: return
        color.selectedColor?.let { color ->
            descriptions[0].rainbowColor = color
            descriptions[0].apply(scheme)
        }
    }

    override fun processListOptions(): MutableSet<String> = mutableSetOf()

    override fun showOption(option: String): Runnable = Runnable {
        optionsTree.selectOptionByRainbowName(option)
    }

    override fun selectOption(typeToSelect: String) {
        optionsTree.selectOptionByType(typeToSelect)
    }

    companion object {
        private const val SELECTED_COLOR_OPTION_PROPERTY = "dsl.in.color.selected.color.option.name"

        private var TextAttributesDescription.rainbowColor: Color?
            get() = externalForeground
            set(value) {
                externalForeground = value
            }

        private val Tree.selectedValue: Any?
            get() = (lastSelectedPathComponent as? DefaultMutableTreeNode)?.userObject

        private val Tree.selectedDescriptions: DescriptionsNode?
            get() = selectedValue as? DescriptionsNode

        private fun Tree.findOption(nodeObject: Any, matcher: (Any) -> Boolean): TreePath? {
            val model = model as DefaultTreeModel
            for (i in 0 until model.getChildCount(nodeObject)) {
                val childObject = model.getChild(nodeObject, i)
                if (childObject is DefaultMutableTreeNode) {
                    val data = childObject.userObject
                    if (matcher(data)) {
                        return TreePath(model.getPathToRoot(childObject))
                    }
                }

                val pathInChild = findOption(childObject, matcher)
                if (pathInChild != null) return pathInChild
            }

            return null
        }

        private fun Tree.selectOptionByRainbowName(rainbowName: String) {
            selectPath(findOption(model.root) { data ->
                data is DescriptionsNode
                        && rainbowName.isNotBlank()
                        && data.optionName.contains(rainbowName, ignoreCase = true)
            })
        }

        private fun Tree.selectOptionByType(attributeType: String) {
            selectPath(findOption(model.root) { data ->
                data is DescriptionsNode && data.descriptions.any { it.type == attributeType }
            })
        }

        private fun Tree.selectPath(path: TreePath?) {
            if (path != null) {
                selectionPath = path
                scrollPathToVisible(path)
            }
        }
    }
}