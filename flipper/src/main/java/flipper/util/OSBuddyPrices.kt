package flipper.util

import com.google.gson.reflect.TypeToken
import flipper.Flipper
import net.runelite.api.ItemID
import java.io.File
import java.net.URL

object OSBuddyPrices {
    data class PriceHistory(
        val ts: Long, // timestamp in milliseconds
        val overallPrice: Int,
        val overallQuantity: Int,
        val buyingPrice: Int,
        val buyingQuantity: Int,
        val sellingPrice: Int,
        val sellingQuantity: Int
    )

    val priceHistoryType = object : TypeToken<List<PriceHistory>>() {}.type
    val baseUrl = "https://rsbuddy.com/exchange/graphs/180/"
    val folder = File(Flipper.folder, "osb")

    val items = listOf(
        ItemID.ABYSSAL_WHIP,
        ItemID.AHRIMS_HOOD,
        ItemID.DHAROKS_GREATAXE,
        ItemID.BANDOS_CHESTPLATE,
        ItemID.DRAGON_HUNTER_CROSSBOW,
        ItemID.DRACONIC_VISAGE,
        ItemID.DRAGON_HUNTER_LANCE
    )

    /**
     * This gets full price history, the one used in the graphs
     */
    fun getPriceHistory(vararg ids: Int): Map<Int, List<PriceHistory>> {
        val out = mutableMapOf<Int, List<PriceHistory>>()

        println("Fetching prices..")

        for (id in ids) {
            try {
                val itemUrl = "$baseUrl$id.json"
                val json = URL(itemUrl).readText()
                val timestamps = Flipper.gson.fromJson<List<PriceHistory>>(json, priceHistoryType).sortedByDescending { it.ts }
                out[id] = timestamps
            } catch (e: Exception) {
                continue
            }
        }

        println("Finished fetching prices")

        return out
    }
}