package gecko10000.geckovouchers

import org.koin.dsl.module

fun pluginModules(plugin: GeckoVouchers) = module {
    single { plugin }
    single(createdAtStart = true) { VoucherManager() }
}
