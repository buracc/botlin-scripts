package runelooter

import com.google.inject.Inject
import dev.botlin.api.commons.StopWatch
import dev.botlin.api.commons.Time
import dev.botlin.api.definitions.Definitions
import dev.botlin.api.events.inventory.InventoryChanged
import dev.botlin.api.entities.actor.Players
import dev.botlin.api.entities.tile.TileItems
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.script.paint.tracker.PaintStatistic
import dev.botlin.api.wrappers.distanceTo
import dev.botlin.api.wrappers.interact
import dev.botlin.api.wrappers.name
import net.runelite.api.events.GameTick
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.game.ItemManager

@ScriptMeta("runelooter")
class Runelooter : BotScript() {

    @Inject
    private lateinit var itemManager: ItemManager

    private val loot = mutableMapOf<Int, Int>()
    private val timer = StopWatch.start()

    override fun loop() {
        nextReturn = 150
        if (Players.getLocal()!!.isMoving) {
            nextReturn = 2000
            return
        }

        val loot = TileItems.getAll { it.tile.distanceTo(Players.getLocal()!!) < 4 && it.name.contains("rune") }
        loot.forEach {
            it.interact("Take")
            Time.sleep(100)
        }
    }

    @Subscribe
    fun onInventoryChanged(e: InventoryChanged) {
        if (loot.containsKey(e.itemId)) {
            loot.replace(e.itemId, loot[e.itemId]!! + e.amount)
        } else {
            loot[e.itemId] = e.amount
        }
    }

    @Subscribe
    fun onGameTick(e: GameTick) {
        for ((id, amount) in loot) {
            val price = itemManager.getItemPrice(id) * amount
            val name = Definitions.getItem(id)!!.name

            if (!paint.tracker.contains(name)) {
                paint.tracker.submit(
                    name,
                    PaintStatistic(
                        timer,
                        { price }
                    )
                )
            }
        }

        if (!paint.tracker.contains("Total")) {
            paint.tracker.submit("Total", PaintStatistic(
                timer, {
                    var total = 0
                    for ((id, amount) in loot) {
                        total += (itemManager.getItemPrice(id) * amount)
                    }

                    total
                }
            ))
        }
    }

    override fun onStart(vararg startArgs: String) {
        paint.setEnabled(true)
    }
}
