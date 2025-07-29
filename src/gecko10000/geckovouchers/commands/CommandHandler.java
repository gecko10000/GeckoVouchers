package gecko10000.geckovouchers.commands;

import gecko10000.geckolib.extensions.MMKt;
import gecko10000.geckolib.misc.ItemUtils;
import gecko10000.geckovouchers.GeckoVouchers;
import gecko10000.geckovouchers.Voucher;
import gecko10000.geckovouchers.guis.MainVoucherGUI;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.commands.annotations.*;
import net.strokkur.commands.annotations.arguments.CustomArg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Command("geckovouchers")
@Aliases("voucher")
@Permission("geckovouchers.command")
public class CommandHandler {

    private final GeckoVouchers plugin = JavaPlugin.getPlugin(GeckoVouchers.class);

    public void register() {
        plugin.getLifecycleManager()
                .registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event ->
                        CommandHandlerBrigadier.register(
                                event.registrar()
                        )
                ));
    }

    @Executes("give")
    @Permission("geckovouchers.give")
    void give(CommandSender sender, @CustomArg(VoucherArg.class) Voucher voucher) {
        if (!(sender instanceof Player target)) {
            sender.sendRichMessage("<red>Specify a player.");
            return;
        }
        give(sender, voucher, target);
    }

    @Executes("give")
    @Permission("geckovouchers.give")
    void give(CommandSender sender, @CustomArg(VoucherArg.class) Voucher voucher, Player target) {
        give(sender, voucher, target, 1);
    }

    @Executes("give")
    @Permission("geckovouchers.give")
    void give(CommandSender sender, @CustomArg(VoucherArg.class) Voucher voucher, Player target, int amount) {
        ItemUtils.give(target, voucher.getVoucherItem().asQuantity(amount));
        sender.sendMessage(
                MMKt.getMM().deserialize(
                        "<green>Gave <name> <amount> <voucher> vouchers.",
                        Placeholder.unparsed("name", target.getName()),
                        Placeholder.unparsed("amount", amount + ""),
                        Placeholder.unparsed("voucher", voucher.getId())
                )
        );
    }

    @Executes("edit")
    @Permission("geckovouchers.edit")
    void editCommand(CommandSender sender, @Executor Player player) {
        new MainVoucherGUI(player);
    }

    @Executes("reload")
    @Permission("geckovouchers.reload")
    void reloadCommand(CommandSender sender) {
        plugin.reloadConfigs();
        sender.sendRichMessage("<green>Configs reloaded.");
    }

    @Executes("execute")
    @Permission("geckovouchers.execute")
    void executeCommand(CommandSender sender, @CustomArg(VoucherArg.class) Voucher voucher) {
        if (!(sender instanceof Player target)) {
            sender.sendRichMessage("<red>Specify a player.");
            return;
        }
        executeCommand(sender, voucher, target);
    }

    @Executes("execute")
    @Permission("geckovouchers.execute")
    void executeCommand(CommandSender sender, @CustomArg(VoucherArg.class) Voucher voucher, Player target) {
        plugin.getVoucherManager().reward(voucher, target);
        sender.sendMessage(MMKt.getMM().deserialize(
                "<green>Rewarded <name> with the commands from <id>.",
                Placeholder.unparsed("name", target.getName()),
                Placeholder.unparsed("id", voucher.getId())
        ));
    }

}
