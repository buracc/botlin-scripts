package flipper

import com.google.gson.GsonBuilder
import dev.botlin.api.events.prices.PricesUpdated
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import flipper.util.OSBuddyPrices
import flipper.util.RuneLitePrices
import net.runelite.client.eventbus.Subscribe
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@ScriptMeta("flipper")
class Flipper : BotScript() {

    companion object {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val folder = File(DATA_DIR, "prices")

        @JvmStatic
        fun main(args: Array<String>) {
            println("yo")
        }
    }

    val scheduler = Executors.newSingleThreadScheduledExecutor()

    override fun onStart(vararg startArgs: String) {
        folder.mkdirs()
        RuneLitePrices.folder.mkdirs()
        OSBuddyPrices.folder.mkdirs()

        scheduler.scheduleAtFixedRate({ checkPrices() }, 0, 10, TimeUnit.MINUTES)
    }

    override fun loop() {
    }

    fun checkPrices() {
        val filteredIds = RuneLitePrices.selectItems(
            hoursOld = 1, minPrice = 1_000_000, maxPrice = Int.MAX_VALUE
        )
        val lookup = OSBuddyPrices.getPriceHistory(*filteredIds.keys.toIntArray())

        for (i in lookup) {
            println(i)
//            println(i.key)
//            println("______")
//            val sellPrice =
//                i.value.sortedByDescending { it.overallPrice }[i.value.size - (i.value.size / 1.5).toInt()].overallPrice
//            val buyPrice =
//                i.value.sortedByDescending { it.overallPrice }[i.value.size - (i.value.size / 2.5).toInt()].overallPrice
//            val guidePrice =
//                i.value.sortedByDescending { it.overallPrice }[i.value.size - i.value.size / 2].overallPrice
//            println(sellPrice)//high
//            println(buyPrice)//low
//            // println("***")
//            // println(i.value.sortedByDescending { it.overallPrice }.size)
//            //for (k in i.value.sortedByDescending { it.overallPrice }) {
//            //    println(k.overallPrice)
//            //     println(i.value.sortedByDescending { it.overallPrice }.indexOf(k))
//            // }
//            println("______")
        }
    }

    /**
     * Dont change this
     */
    @Subscribe
    fun onPricesUpdated(e: PricesUpdated) {
        RuneLitePrices.writeToFile(e.prices)
    }

    override fun onStop() {
        scheduler.shutdown()
        scheduler.awaitTermination(1, TimeUnit.MINUTES)
    }
}
