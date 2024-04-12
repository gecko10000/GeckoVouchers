package gecko10000.geckovouchers

import gecko10000.geckolib.extensions.MM
import gecko10000.geckovouchers.guis.MainVoucherGUI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redempt.redlib.commandmanager.ArgType
import redempt.redlib.commandmanager.CommandHook
import redempt.redlib.commandmanager.CommandParser
import redempt.redlib.itemutils.ItemUtils

class CommandHandler : KoinComponent {

    private val plugin: GeckoVouchers by inject()
    private val voucherManager: VoucherManager by inject()

    init {
        CommandParser(plugin.getResource("command.rdcml"))
            .setArgTypes(ArgType("voucher") { s -> plugin.vouchers[s] }.setTab { _ -> plugin.vouchers.keys.toList() })
            .parse().register("vowchurs", this)
    }

    @CommandHook("give")
    fun giveCommand(sender: CommandSender, voucher: Voucher, target: Player, amount: Int) {
        ItemUtils.give(target, voucher.voucherItem.asQuantity(amount))
        sender.sendMessage(
            MM.deserialize(
                "<green>Gave ${target.name} ${if (amount == 1) "a <voucher> voucher" else "$amount <voucher> vouchers"}.",
                Placeholder.unparsed("voucher", voucher.id)
            )
        )
    }

    @CommandHook("edit")
    fun editCommand(player: Player) = MainVoucherGUI(player)

    @CommandHook("reload")
    fun reloadCommand(sender: CommandSender) {
        plugin.reloadConfigs()
        sender.sendMessage(MM.deserialize("<green>Configs reloaded."))
    }

    @CommandHook("execute")
    fun executeCommand(sender: CommandSender, voucher: Voucher, target: Player) {
        voucherManager.reward(voucher, target)
        sender.sendMessage(MM.deserialize("<green>Rewarded ${target.name} with the commands from ${voucher.id}."))
    }

}
