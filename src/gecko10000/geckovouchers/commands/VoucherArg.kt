package gecko10000.geckovouchers.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import gecko10000.geckoanvils.di.MyKoinComponent
import gecko10000.geckolib.extensions.parseMM
import gecko10000.geckovouchers.GeckoVouchers
import gecko10000.geckovouchers.Voucher
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import io.papermc.paper.command.brigadier.argument.CustomArgumentType
import org.koin.core.component.inject
import java.util.concurrent.CompletableFuture

class VoucherArg : CustomArgumentType<Voucher, String>, MyKoinComponent {

    private val plugin: GeckoVouchers by inject()

    override fun parse(reader: StringReader): Voucher {
        val voucherName = reader.readUnquotedString()
        val voucher = plugin.vouchers[voucherName] ?: throw SimpleCommandExceptionType(
            MessageComponentSerializer
                .message().serialize(parseMM("<red>Invalid voucher $voucherName."))
        ).create()
        return voucher
    }

    override fun getNativeType(): ArgumentType<String> {
        return StringArgumentType.string()
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        plugin.vouchers.keys
            .filter { it.lowercase().startsWith(builder.remainingLowerCase) }
            .forEach(builder::suggest)
        return builder.buildFuture()
    }

}
