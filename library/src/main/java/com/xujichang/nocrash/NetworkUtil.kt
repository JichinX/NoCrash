package com.xujichang.nocrash

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object NetworkUtil {

    /**
     * 获取网络类型
     */
    fun getNetworkState(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        return connectivityManager?.let { manager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = manager.activeNetwork
                activeNetwork?.let {
                    val linkProperties = manager.getLinkProperties(activeNetwork)
                    val networkCapabilities = manager.getNetworkCapabilities(activeNetwork)
                    """IP地址：${linkProperties?.linkAddresses}
                       |DNS：${linkProperties?.dnsServers}
                       |Routes：${linkProperties?.dnsServers}
                       |网络类型：${networkCapabilities?.getNetworkType() ?: "无法获取"}  
                    """.trimMargin()
                } ?: "无法获取"
            } else {
                ""
            }
        } ?: "无法获取"
    }
}

private val TRANSPORT_NAMES = arrayOf(
    "CELLULAR",
    "WIFI",
    "BLUETOOTH",
    "ETHERNET",
    "VPN",
    "WIFI_AWARE",
    "LOWPAN",
    "TEST"
)

private fun NetworkCapabilities.getNetworkType(): String = StringBuilder().apply {
    for (index in 0..7) {
        if (hasTransport(index)) {
            append(TRANSPORT_NAMES[index])
        }
    }
}.toString()
