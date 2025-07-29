@file:UseSerializers(MMComponentSerializer::class)

package gecko10000.geckovouchers.configs

import com.charleskorn.kaml.YamlComment
import gecko10000.geckolib.config.objects.DisplayItem
import gecko10000.geckolib.config.serializers.MMComponentSerializer
import gecko10000.geckolib.extensions.MM
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import org.bukkit.Material

@Serializable
data class Config(
    val confirmationMessage: Component = MM.deserialize("<gray>Right click again to use this voucher."),
    @YamlComment("In ticks")
    val confirmTimer: Long = 200,
    val prevButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<red>Previous"),
        material = Material.RED_STAINED_GLASS_PANE,
    ),
    val nextButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<green>Next"),
        material = Material.LIME_STAINED_GLASS_PANE,
    ),
    val newButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<white>(<green><bold>+</bold></green>)"),
        material = Material.GREEN_STAINED_GLASS_PANE,
    ),
    val cancelButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<red>Cancel"),
        material = Material.RED_STAINED_GLASS_PANE,
    ),
    val confirmButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<green>Confirm"),
        material = Material.LIME_STAINED_GLASS_PANE,
    ),
    val deleteButton: DisplayItem = DisplayItem(
        name = MM.deserialize("<red>Delete"),
        lore = listOf(
            MM.deserialize("<red><bold>Dangerous operation."),
            MM.deserialize("<red>Shift+right click if you're sure.")
        ),
        material = Material.RED_STAINED_GLASS_PANE,
    ),
)
