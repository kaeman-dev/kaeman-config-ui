package cc.me0wo.kaeman.config.ui

import io.wispforest.owo.ui.component.*
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import io.wispforest.owo.ui.util.NinePatchTexture
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import java.util.Collections
import java.util.WeakHashMap
import kotlin.math.roundToInt

data class ConfigTheme(
    val background: Int = 0xF0181D27.toInt(),
    val control: Int = 0xFF222A36.toInt(),
    val border: Int = 0xFF354051.toInt(),
    val accent: Int = 0xFF8BDCCB.toInt(),
    val text: Int = 0xFFE8EDF4.toInt(),
    val muted: Int = 0xFF8693A5.toInt(),
    val panelRadius: Int = 10,
    val controlRadius: Int = 5
) {
    init { require(panelRadius > 0 && controlRadius > 0) }
    private val styled = Collections.newSetFromMap(WeakHashMap<UIComponent, Boolean>())
    internal val panel = Surface { graphics, c ->
        box(graphics, c.x(), c.y(), c.width(), c.height(), background, border, panelRadius)
    }
    private val buttons = ButtonComponent.Renderer { g, b, _ ->
        val focused = b.active && (b.isHovered || b.isFocused)
        box(g, b.x, b.y, b.width, b.height, if (b.active) control else background, if (focused) accent else border)
    }
    private val fields = Surface { g, parent ->
        parent.children().forEach { c ->
            when (c) {
                is TextBoxComponent -> box(g, c.x, c.y - 3, c.width, c.height + 6, control, if (c.isFocused) accent else border)
                is SliderComponent -> {
                    val color = if (c.active) accent else muted
                    box(g, c.x, c.y, c.width, c.height, control, if (c.isFocused) accent else border)
                    val track = c.width - 12
                    val progress = (track * c.value()).roundToInt()
                    rounded(g, c.x + 6, c.y + c.height - 5, track, 2, border, 1)
                    rounded(g, c.x + 6, c.y + c.height - 5, progress, 2, color, 1)
                    rounded(g, c.x + 4 + progress, c.y + c.height - 6, 4, 4, color, 2)
                    val font = Minecraft.getInstance().font
                    g.text(font, c.message, c.x + (c.width - font.width(c.message)) / 2, c.y + 3, text, false)
                }
            }
        }
    }

    internal fun apply(root: FlowLayout) {
        root.forEachDescendant { c ->
            if (styled.add(c)) when (c) {
                is ButtonComponent -> c.renderer(buttons).textShadow(false)
                is TextBoxComponent -> {
                    c.setBordered(false)
                    c.parent()?.surface(fields)
                }
                is SliderComponent -> {
                    c.setAlpha(0f)
                    c.parent()?.surface(fields)
                }
                is LabelComponent -> {
                    c.shadow(false).color(Color.ofArgb(text))
                    if (c.id() == "header") c.text(c.text().copy().withStyle { it.withColor(accent and 0xFFFFFF).withBold(true) })
                }
                is BoxComponent -> c.color(Color.ofArgb(border))
            }
        }
    }

    private fun box(g: OwoUIGraphics, x: Int, y: Int, w: Int, h: Int, fill: Int, edge: Int, radius: Int = controlRadius) {
        rounded(g, x, y, w, h, edge, radius)
        rounded(g, x + 1, y + 1, w - 2, h - 2, fill, (radius - 1).coerceAtLeast(1))
    }

    private fun rounded(g: OwoUIGraphics, x: Int, y: Int, w: Int, h: Int, color: Int, radius: Int) {
        if (w <= 0 || h <= 0) return
        val scale = minOf(radius.toFloat(), w / 2f, h / 2f) / 16f
        g.pose().pushMatrix()
        g.pose().translate(x.toFloat(), y.toFloat()).scale(scale, scale)
        texture.draw(g, 0, 0, (w / scale).roundToInt(), (h / scale).roundToInt(), Color.ofArgb(color))
        g.pose().popMatrix()
    }

    private companion object {
        val texture = NinePatchTexture(
            Identifier.fromNamespaceAndPath("kaeman_config_ui", "textures/gui/rounded.png"),
            0, 0, Size.square(16), Size.square(32), Size.square(64), false
        )
    }
}
