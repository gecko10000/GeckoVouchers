package gecko10000.geckovouchers.guis

import gecko10000.geckoanvils.di.MyKoinComponent
import gecko10000.geckolib.extensions.MM
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckolib.extensions.withDefaults
import gecko10000.geckolib.inventorygui.GUI
import gecko10000.geckolib.inventorygui.InventoryGUI
import gecko10000.geckolib.inventorygui.ItemButton
import gecko10000.geckolib.inventorygui.PaginationPanel
import gecko10000.geckolib.misc.ChatPrompt
import gecko10000.geckolib.misc.ItemUtils
import gecko10000.geckovouchers.GeckoVouchers
import gecko10000.geckovouchers.Voucher
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.min

class MainVoucherGUI(player: Player) : GUI(player), MyKoinComponent {

    private val plugin: GeckoVouchers by inject()

    private fun voucherButton(voucher: Voucher): ItemButton {
        return ItemButton.create(voucher.voucherItem.apply {
            editMeta {
                val displayName = it.displayName()
                it.displayName(
                    MM.deserialize("<aqua><voucher>", Placeholder.unparsed("voucher", voucher.id)).withDefaults()
                )
                it.lore(
                    listOf((displayName ?: Component.translatable(this)).withDefaults()).plus(
                        it.lore().orEmpty()
                            .plus(
                                listOf(
                                    Component.empty(),
                                    parseMM("<red>Left click to edit."),
                                    parseMM("<red>Right click to get."),
                                    parseMM("<red>Shift+left click to move left."),
                                    parseMM("<red>Shift+right click to move right."),
                                )
                            )
                    )
                )
            }
        }) { e ->
            if (e.isShiftClick) {
                if (!e.isRightClick && !e.isLeftClick) return@create
                val left = e.isLeftClick
                val right = !left
                val entries = plugin.vouchers.toList().toMutableList()
                val index = entries.indexOfFirst { it.first == voucher.id }
                if (index == -1) return@create
                if (left && index == 0) return@create
                if (right && index == entries.size - 1) return@create
                val toMove = entries[index]
                entries.removeAt(index)
                entries.add(index + (if (left) -1 else 1), toMove)
                plugin.vouchers.clear()
                plugin.vouchers.putAll(entries)
                plugin.saveVouchers()
                MainVoucherGUI(player)
                return@create
            }
            if (e.isLeftClick) {
                VoucherGUI(player, voucher)
            }
            if (e.isRightClick) {
                ItemUtils.give(player, voucher.voucherItem)
            }
        }
    }

    override fun createInventory(): InventoryGUI {
        val vouchers = plugin.vouchers
        val slotCount = vouchers.size + 1
        val inventorySize = min(54, ItemUtils.minimumChestSize(slotCount))
        val inventory =
            InventoryGUI(Bukkit.createInventory(this, inventorySize, MM.deserialize("<#0085e6>Voucher Editor")))
        inventory.fill(0, inventorySize, FILLER)
        val paginationPanel = PaginationPanel(inventory, FILLER)
        paginationPanel.addSlots(0, if (slotCount <= 54) inventorySize else 45)
        if (slotCount > 54) {
            inventory.addButton(
                inventorySize - 6,
                ItemButton.create(plugin.config.prevButton.item) { _ -> paginationPanel.prevPage() })
            inventory.addButton(
                inventorySize - 4,
                ItemButton.create(plugin.config.nextButton.item) { _ -> paginationPanel.nextPage() })
        }
        vouchers.values.map(this::voucherButton).forEach(paginationPanel::addPagedButton)
        paginationPanel.addPagedButton(ItemButton.create(plugin.config.newButton.item) { _ ->
            player.closeInventory()
            ChatPrompt.prompt(player, Component.text("Enter a unique ID for this voucher:"), { s ->
                if (s in plugin.vouchers) {
                    player.sendRichMessage("<red>A voucher with this ID already exists.")
                    MainVoucherGUI(player)
                } else {
                    VoucherGUI(player, Voucher(id = s))
                }
            }) {
                if (it == ChatPrompt.CancelReason.PLAYER_CANCELLED) MainVoucherGUI(player)
            }
        })
        return inventory
    }
}
