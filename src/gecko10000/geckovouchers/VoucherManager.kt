package gecko10000.geckovouchers

import gecko10000.geckoanvils.di.MyKoinComponent
import gecko10000.geckolib.misc.EventListener
import gecko10000.geckolib.misc.Task
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

class VoucherManager : MyKoinComponent {

    private val plugin: GeckoVouchers by inject()
    val voucherKey by lazy { NamespacedKey(plugin, "voucher") }

    fun ItemStack.getVoucherId(): String? =
        this.itemMeta.persistentDataContainer.get(voucherKey, PersistentDataType.STRING)

    private val confirmations = mutableMapOf<UUID, Pair<String, Task>>()

    private fun execute(command: String, player: Player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.name))
    }

    fun reward(voucher: Voucher, player: Player) {
        voucher.commands.forEach { execute(it, player) }
    }

    init {
        EventListener(PlayerInteractEvent::class.java) { e ->
            if (e.useItemInHand() == Event.Result.DENY) return@EventListener
            if (e.action != Action.RIGHT_CLICK_BLOCK && e.action != Action.RIGHT_CLICK_AIR) {
                return@EventListener
            }
            val item = e.item ?: return@EventListener
            if (item.isEmpty || !item.hasItemMeta()) return@EventListener
            val voucherId = item.getVoucherId() ?: return@EventListener
            e.isCancelled = true
            val voucher = plugin.vouchers[voucherId] ?: return@EventListener
            val player = e.player
            val uuid = player.uniqueId
            if (!voucher.confirmation || confirmations[uuid]?.first == voucherId) {
                confirmations.remove(uuid)?.second?.cancel()
                reward(voucher, player)
                item.amount--
                return@EventListener
            }
            player.sendMessage(plugin.config.confirmationMessage)
            val task = Task.syncDelayed({ task ->
                confirmations.remove(uuid, voucherId to task)
            }, plugin.config.confirmTimer)
            confirmations[uuid] = voucherId to task
        }
    }

}
