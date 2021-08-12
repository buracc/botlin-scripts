package crabs

import dev.botlin.api.commons.StopWatch
import dev.botlin.api.coords.RectangularArea
import dev.botlin.api.entities.actor.NPCs
import dev.botlin.api.entities.actor.Players
import dev.botlin.api.entities.container.Bank
import dev.botlin.api.entities.container.Inventory
import dev.botlin.api.entities.tile.TileObjects
import dev.botlin.api.entities.widget.Widgets
import dev.botlin.api.events.ExperienceGained
import dev.botlin.api.game.Client
import dev.botlin.api.input.Keyboard
import dev.botlin.api.movement.Movement
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.script.paint.tracker.PaintStatistic
import dev.botlin.api.skill.Skills
import dev.botlin.api.widget.Dialog
import dev.botlin.api.wrappers.*
import net.runelite.api.Item
import net.runelite.api.Skill
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.eventbus.Subscribe
import kotlin.math.abs
import kotlin.math.floor

@ScriptMeta("Botlin-Crabs")
class Crabs : BotScript() {

    private val workArea = RectangularArea(1729, 3479, 1747, 3458)
    private var afkSpot = WorldPoint(1734, 3470, 0)
    private val afkSpot2 = WorldPoint(1737, 3469, 0)
    private var timer: StopWatch? = null
    private var resetTile: WorldPoint? = null
    private val tiles = mutableListOf<WorldPoint>()
    private var stopWatch = StopWatch.start()
    private val invSetup = sortedMapOf("Super strength" to 2, "Super attack" to 2, "Tuna" to 18)

    override fun onStart(vararg startArgs: String) {
        paint.setEnabled(true)
        paint.tracker.submit("Runtime", PaintStatistic { stopWatch.toElapsedString() })
    }

    @Subscribe
    fun onExperienceGained(e: ExperienceGained) {
        paint.tracker.trackSkill(e.skill)
    }

    override fun loop(): Int {
        if (tiles.isEmpty()) {
            println("loading tiles")
            loadTiles()
        }

        val local = Players.getLocal()!!
        val bank = TileObjects.getNearest("Bank chest")
        val rock = NPCs.getNearest { it.name == "Sandy rocks" && it.distanceTo(local.worldLocation) <= 2 }
        val food: Item? = Inventory.getFirst("Tuna")
        val strPot = Inventory.getFirst { it.name.contains("strength") && it.actions.contains("Drink") }
        val attkPot = Inventory.getFirst { it.name.contains("attack") && it.actions.contains("Drink") }
//        val rangePot = Inventory.getFirst { it.name.contains("Ranging") && it.actions.contains("Drink") }
        val vial = Inventory.getFirst("Vial")

        if (Dialog.canContinue()) {
            Dialog.continueSpace()
            println("continue dialog")
            return 1000
        }

        if (local.isMoving) {
            println("pathing")
            return 1000
        }

        if (food == null && afkSpot.distanceTo(local.worldLocation) < 25) {
            if (bank == null) {
                Movement.walk(afkSpot.dx(-7))
                return 1000
            }

            if (!Bank.isOpen()) {
                TileObjects.getNearest(10562)?.interact(0)
                return 1000
            }

            if (Inventory.isFull()) {
                Bank.depositInventory()
                return 1000
            }

            for ((itemName, amount) in invSetup) {
                val item = Inventory.getFirst { it.name.contains(itemName) }
                if (item != null) {
                    val rest = Inventory.getCount { it.name.contains(itemName) } - amount
                    if (rest > 0) {
                        Bank.deposit({ it.name.contains(itemName) }, rest)
                        return 1000
                    }

                    if (rest < 0) {
                        Bank.withdraw({ it.name.contains(itemName) }, abs(rest))
                    }

                    if (rest == 0) {
                        continue
                    }

                    return 1000
                }

                Bank.withdraw({ it.name.contains(itemName) }, amount)
                return 1000
            }

            return 1000
        }

        if (Skills.healthPercent <= 40) {
            Inventory.getFirst("Tuna")?.interact("Eat")
            return 1000
        }

        if (Movement.getRunEnergy() > 2 && !Movement.isRunEnabled()) {
            Movement.toggleRun()
            return 1000
        }

        if (vial != null) {
            vial.interact("Drop")
            return 1000
        }

//        val rangePotBoost = floor(5 + (Skills.getLevel(Skill.RANGED) * 0.15))
//        val currRangeBoost = Skills.getBoostedLevel(Skill.RANGED) - Skills.getLevel(Skill.RANGED)
//        val rangeBoostPercent = floor((100 / rangePotBoost) * currRangeBoost)
//
//        if (rangePot != null && (rangeBoostPercent <= 50)) {
//            rangePot.interact("Drink")
//            return 1000
//        }

        val atkpotBoost = floor(5 + (Skills.getLevel(Skill.ATTACK) * 0.15))
        val currAttkBoost = Skills.getBoostedLevel(Skill.ATTACK) - Skills.getLevel(Skill.ATTACK)
        val atkBoostPercent = floor((100 / atkpotBoost) * currAttkBoost)

        if (attkPot != null && (atkBoostPercent <= 50)) {
            attkPot.interact("Drink")
            return 1000
        }

        val strpotBoost = floor(5 + (Skills.getLevel(Skill.STRENGTH) * 0.15))
        val currStrBoost = Skills.getBoostedLevel(Skill.STRENGTH) - Skills.getLevel(Skill.STRENGTH)
        val strBoostPercent = floor((100 / strpotBoost) * currStrBoost)
        if (strPot != null && (strBoostPercent < 50)) {
            strPot.interact("Drink")
            return 1000
        }

        if (timer != null && timer!!.getElapsed().seconds > 5L) {
            if (resetTile == null) {
                resetTile = tiles.random()
                return 1000
            }

            if (local.worldLocation.distanceTo(resetTile) > 2) {
                Movement.walk(resetTile!!)
                return 1000
            }

            timer = null
            return 1000
        }

        if (local.worldLocation != afkSpot) {
            Movement.walk(afkSpot)
            return 1000
        }

        if (local.interacting != null) {
            if (timer != null) {
                timer = null
            }

            return 1000
        }
        if (rock != null) {
            if (timer == null) {
                println("timer start")
                timer = StopWatch.start()
            }
            return 1000
        }

//        val nearestRocks = NPCs.getNearest { it.name == "Sandy rocks" && workArea.contains(it) }
//        if (nearestRocks != null) {
//            afkSpot = nearestRocks.worldLocation
//        }

        if (resetTile != null) {
            resetTile = null
        }

        if (timer != null) {
            timer = null
        }

        return 1000
    }

    fun loadTiles() {
        for (x in Client.getScene().tiles[Client.getPlane()]) {
            for (y in x) {
                if (y.worldLocation.distanceTo(afkSpot) in 36..40
                    && y.worldLocation.x > afkSpot.x && y.worldLocation.y > afkSpot.y
                ) {
                    tiles.add(y.worldLocation)
                }
            }
        }
    }
}
