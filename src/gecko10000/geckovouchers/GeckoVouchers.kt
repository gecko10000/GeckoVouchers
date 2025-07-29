package gecko10000.geckovouchers

import gecko10000.geckoanvils.di.MyKoinComponent
import gecko10000.geckoanvils.di.MyKoinContext
import gecko10000.geckolib.config.YamlFileManager
import gecko10000.geckovouchers.commands.CommandHandler
import gecko10000.geckovouchers.configs.Config
import gecko10000.geckovouchers.configs.VoucherHolder
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.inject

class GeckoVouchers : JavaPlugin(), MyKoinComponent {

    val voucherManager: VoucherManager by inject()

    private val configFile = YamlFileManager(
        dataFolder,
        initialValue = Config(),
        serializer = Config.serializer()
    )
    val config: Config
        get() = configFile.value

    private val voucherFile = YamlFileManager(
        dataFolder,
        configName = "vouchers.yml",
        initialValue = VoucherHolder(),
        serializer = VoucherHolder.serializer()
    )
    val vouchers: MutableMap<String, Voucher>
        get() = voucherFile.value.vouchers

    fun reloadConfigs() {
        configFile.reload()
        voucherFile.reload()
    }

    fun saveVouchers() = voucherFile.save()

    override fun onEnable() {
        MyKoinContext.init(this)
        CommandHandler().register()
    }

}
