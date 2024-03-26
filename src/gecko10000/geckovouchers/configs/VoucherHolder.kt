package gecko10000.geckovouchers.configs

import gecko10000.geckovouchers.Voucher
import kotlinx.serialization.Serializable

@Serializable
data class VoucherHolder(
    val vouchers: MutableMap<String, Voucher> = linkedMapOf(),
)
