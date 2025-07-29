package gecko10000.geckoanvils.di

import gecko10000.geckovouchers.GeckoVouchers
import gecko10000.geckovouchers.VoucherManager
import org.koin.dsl.module

fun pluginModules(plugin: GeckoVouchers) = module {
    single { plugin }
    single(createdAtStart = true) { VoucherManager() }
}
