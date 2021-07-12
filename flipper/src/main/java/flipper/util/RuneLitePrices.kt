package flipper.util

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dev.botlin.api.events.prices.Price
import dev.botlin.api.script.BotScript
import flipper.Flipper
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

object RuneLitePrices {
    val type = object : TypeToken<Map<Int, Price>>() {}.type
    val folder = File(Flipper.folder, "rl")

    /*

    need 2 cache properly

     */

    fun writeToFile(prices: Map<Int, Price>) {
        val time = Instant.now().toEpochMilli()

        val file = File(folder, "$time.json")
        val fileWriter = FileWriter(file)

        Flipper.gson.toJson(prices, fileWriter)

        fileWriter.flush()
        fileWriter.close()
    }

    fun getPricesFromFile(fileName: String): Map<Int, Price> {
        return getPricesFromFile(File(folder, fileName))
    }

    fun getPricesFromFile(file: File): Map<Int, Price> {
        val reader = FileReader(file)

        return Flipper.gson.fromJson<Map<Int, Price>>(reader, type).also {
            reader.close()
        }
    }

    /**
     * U prob dont need 2 use this method
     */
    fun getPricesFiles(hoursOld: Int): List<File> {
        return folder.listFiles()?.filter {
            val epoch = it.name.substringBefore(".").toLong()
            val timestamp = Instant.ofEpochMilli(epoch)

            timestamp.plus(hoursOld.toLong(), ChronoUnit.HOURS).isAfter(Instant.now())
        } ?: emptyList()
    }

    /**
     * U dont need to use this
     */
    fun loadPricesFromFiles(hoursOld: Int): TreeMap<Long, List<Price>> {
        val prices = TreeMap<Long, List<Price>>()
        for (file in getPricesFiles(hoursOld)) {
            prices[file.name.substringBefore(".").toLong()] = getPricesFromFile(file).values.toList()
        }

        return prices
    }

    /**
     * U dont need to use this
     */
    fun loadPriceHistory(hoursOld: Int): Map<Int, Set<Price>> {
        val out = mutableMapOf<Int, MutableSet<Price>>()

        for (prices in loadPricesFromFiles(hoursOld).values) {
            for (price in prices) {
                if (!out.containsKey(price.id)) {
                    out[price.id] = mutableSetOf()
                }

                out[price.id]?.add(price) ?: println("yo wtf null")
            }
        }

        return out
    }

    /**
     * Select by min max price
     */
    fun selectItems(hoursOld: Int = 36, minPrice: Int, maxPrice: Int): Map<Int, Set<Price>> {
        return loadPriceHistory(hoursOld).filter { it.value.all { item -> item.price in minPrice..maxPrice } }
    }

    /**
     * Select by ids
     */
    fun selectItems(hoursOld: Int = 36, vararg ids: Int): Map<Int, Set<Price>> {
        return loadPriceHistory(hoursOld).filter { it.key in ids }
    }
}