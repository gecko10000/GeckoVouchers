package gecko10000.geckovouchers

import gecko10000.geckoconfig.YamlFileManager
import gecko10000.geckovouchers.configs.Config
import gecko10000.geckovouchers.configs.VoucherHolder
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.context.GlobalContext.startKoin

class GeckoVouchers : JavaPlugin() {

    private lateinit var configFile: YamlFileManager<Config>
    val config: Config
        get() = configFile.value

    private lateinit var voucherFile: YamlFileManager<VoucherHolder>
    val vouchers: MutableMap<String, Voucher>
        get() = voucherFile.value.vouchers

    fun reloadConfigs() {
        configFile.reload()
        voucherFile.reload()
    }

    fun saveVouchers() = voucherFile.save()

    override fun onEnable() {
        configFile = YamlFileManager(dataFolder, initialValue = Config(), serializer = Config.serializer())
        voucherFile = YamlFileManager(
            dataFolder,
            configName = "vouchers.yml",
            initialValue = VoucherHolder(),
            serializer = VoucherHolder.serializer()
        )
        startKoin {
            modules(pluginModules(this@GeckoVouchers))
        }
        CommandHandler()
    }

}
