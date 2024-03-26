@file:UseSerializers(MMComponentSerializer::class)

package gecko10000.geckovouchers.configs

import com.charleskorn.kaml.YamlComment
import gecko10000.geckoconfig.objects.DisplayItem
import gecko10000.geckoconfig.serializers.MMComponentSerializer
import gecko10000.geckolib.extensions.MM
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseSerializers
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Serializable
data class Config(
    val confirmationMessage: Component = MM.deserialize("<gray>Right click again to use this voucher."),
    @YamlComment("In ticks")
    val confirmTimer: Long = 200,
    private val filler: Material = Material.BLACK_STAINED_GLASS_PANE,
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
) {
    @Transient
    private val _fillerItem = ItemStack(filler)
    val fillerItem
        get() = _fillerItem.clone()
}
