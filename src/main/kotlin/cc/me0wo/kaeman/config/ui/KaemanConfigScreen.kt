package cc.me0wo.kaeman.config.ui

import io.wispforest.owo.config.ConfigWrapper
import io.wispforest.owo.config.ui.ConfigScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.TextBoxComponent
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.Insets
import io.wispforest.owo.ui.core.Sizing
import io.wispforest.owo.ui.core.Surface
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen

class KaemanConfigScreen @JvmOverloads constructor(
    config: ConfigWrapper<*>, parent: Screen? = null, private val theme: ConfigTheme = ConfigTheme()
) : ConfigScreen(DEFAULT_MODEL_ID, config, parent) {
    override fun build(root: FlowLayout) {
        super.build(root)
        val contentWidth = (width - 24).coerceIn(1, 720)
        root.surface(Surface.vanillaPanorama(false).and(Surface.blur(8f, 12f)).and(Surface.flat(0xA0090D14.toInt())))
        root.childById(FlowLayout::class.java, "main-panel").apply {
            padding(Insets.of(12))
            surface(theme.panel)
            parent()?.apply {
                surface(Surface.BLANK)
                horizontalSizing(Sizing.fixed(contentWidth))
            }
        }
        root.childById(FlowLayout::class.java, "option-panel").padding(Insets.of(2))
        (root.children().last() as? FlowLayout)?.apply {
            padding(Insets.horizontal(4))
            horizontalSizing(Sizing.fixed(contentWidth))
        }
        listOf("done-button", "reload-button").forEach {
            root.childById(ButtonComponent::class.java, it).horizontalSizing(Sizing.fixed(60))
        }
        root.childById(TextBoxComponent::class.java, "search-field")
            .horizontalSizing(Sizing.fixed((contentWidth - 210).coerceAtLeast(40)))
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        uiAdapter?.rootComponent?.let(theme::apply)
        super.extractRenderState(graphics, mouseX, mouseY, delta)
    }
}
