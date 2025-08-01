package gecko10000.geckovouchers.guis

import gecko10000.geckoanvils.di.MyKoinComponent
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.inventorygui.ItemButton
import gecko10000.geckolib.misc.ChatPrompt
import gecko10000.geckovouchers.GeckoVouchers
import gecko10000.geckovouchers.Voucher
import gecko10000.geckovouchers.VoucherManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class VoucherGUI(player: Player, private val voucher: Voucher) : GUI(player), MyKoinComponent {

    private val plugin: GeckoVouchers by inject()
    private val voucherManager: VoucherManager by inject()

    companion object {
        private const val SIZE = 18
    }

    override fun createInventory(): InventoryGUI {
        val inventory = InventoryGUI(
            Bukkit.createInventory(
                this,
                SIZE,
                MM.deserialize("<#0085e6>Editing <voucher>", Placeholder.unparsed("voucher", voucher.id))
            )
        )
        inventory.fill(0, SIZE, FILLER)
        inventory.addButton(1, ItemButton.create(voucher.voucherItem.apply {
            editMeta {
                val displayName = it.displayName()
                it.displayName(parseMM("<green>Voucher Item"))
                it.lore(
                    listOf((displayName ?: Component.translatable(this)).withDefaults()).plus(
                        it.lore().orEmpty().plus(listOf(Component.empty(), parseMM("<red>Drag and drop to replace")))
                    )
                )
            }
        }) { e ->
            val cursor = e.cursor
            if (cursor.isEmpty) return@create
            val cleaned = cursor.asOne().apply {
                editMeta {
                    it.persistentDataContainer.remove(voucherManager.voucherKey)
                }
            }
            VoucherGUI(player, voucher.copy(item = cleaned))
        })
        inventory.addButton(4, ItemButton.create(ItemStack(Material.PAPER).apply {
            amount = voucher.commands.size.coerceAtLeast(1)
            editMeta {
                it.displayName(parseMM("<green>Commands"))
                it.lore(
                    voucher.commands.map(Component::text).map(Component::withDefaults).plus(
                        listOf(
                            Component.empty(),
                            parseMM("<green>Click to add"),
                            parseMM("<red>Shift+right click to remove last"),
                        )
                    )
                )
            }
        }) { e ->
            if (e.isRightClick && e.isShiftClick) {
                VoucherGUI(player, voucher.copy(commands = voucher.commands.dropLast(1)))
            } else {
                player.closeInventory()
                ChatPrompt.prompt(
                    player,
                    Component.text("Enter a new command to run (use %player% for player name):"),
                    { s ->
                        VoucherGUI(player, voucher.copy(commands = voucher.commands.plus(s)))
                    }) {
                    if (it == ChatPrompt.CancelReason.PLAYER_CANCELLED) VoucherGUI(player, voucher)
                }
            }
        })
        inventory.addButton(
            7,
            ItemButton.create(ItemStack(if (voucher.confirmation) Material.LIME_STAINED_GLASS else Material.RED_STAINED_GLASS).apply {
                editMeta {
                    it.displayName(parseMM(if (voucher.confirmation) "<green>Confirmation Enabled" else "<red>Confirmation Disabled"))
                    it.lore(listOf(parseMM("<yellow>Click to ${if (voucher.confirmation) "<red>disable</red>" else "<green>enable</green>"}.")))
                }
            }) { _ -> VoucherGUI(player, voucher.copy(confirmation = !voucher.confirmation)) }
        )
        inventory.addButton(
            SIZE - 6,
            ItemButton.create(plugin.config.cancelButton.item) { _ -> MainVoucherGUI(player) })
        inventory.addButton(SIZE - 4, ItemButton.create(plugin.config.confirmButton.item) { _ ->
            plugin.vouchers[voucher.id] = voucher
            plugin.saveVouchers()
            MainVoucherGUI(player)
        })
        inventory.addButton(SIZE - 1, ItemButton.create(plugin.config.deleteButton.item) { e ->
            if (e.isRightClick && e.isShiftClick) {
                plugin.vouchers.remove(voucher.id)
                plugin.saveVouchers()
                MainVoucherGUI(player)
            }
        })
        return inventory
    }

}
