@file:UseSerializers(InternalItemStackSerializer::class)

package gecko10000.geckovouchers

import gecko10000.geckoanvils.di.MyKoinComponent
import gecko10000.geckolib.config.serializers.InternalItemStackSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.inject

@Serializable
data class Voucher(
    val id: String,
    private val item: ItemStack = ItemStack(Material.BARRIER),
    val commands: List<String> = listOf(),
    val confirmation: Boolean = false,
) : MyKoinComponent {

    private val voucherManager: VoucherManager by inject()

    private val _voucherItem: ItemStack by lazy {
        return@lazy item.clone().apply {
            editMeta {
                it.persistentDataContainer.set(voucherManager.voucherKey, PersistentDataType.STRING, id)
            }
        }
    }
    val voucherItem
        get() = _voucherItem.clone()
}
