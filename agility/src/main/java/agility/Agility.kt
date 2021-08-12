package agility

import dev.botlin.api.commons.Rand
import dev.botlin.api.commons.Time
import dev.botlin.api.movement.Movement
import dev.botlin.api.entities.actor.Players
import dev.botlin.api.entities.container.Inventory
import dev.botlin.api.entities.tile.TileItems
import dev.botlin.api.entities.tile.TileObjects
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.widget.Dialog
import dev.botlin.api.wrappers.actions
import dev.botlin.api.wrappers.hasAction
import dev.botlin.api.wrappers.interact
import dev.botlin.api.wrappers.name
import net.runelite.api.TileObject

@ScriptMeta("agility")
class Agility : BotScript() {

    var course = Course.GNOME_COURSE
    var obstacle: Obstacle? = null

    override fun onStart(vararg startArgs: String) {
        course = Course.getNearest()
        println("detected course $course")
    }

    override fun loop(): Int {
        val food = Inventory.getFirst { it.actions.contains("Eat") }

        if (Dialog.canContinue()) {
            Dialog.continueSpace()
        }

        obstacle = course.getNext(Players.getLocal())
        if (obstacle == null) {
            println("obstacle cannot be detected")
            return 1000
        }

        val obs: TileObject? =
            if (obstacle!!.id != 0) {
                println("id: ${obstacle!!.id}")
                TileObjects.getNearest(obstacle!!.id)
            } else {
                println("id: ${obstacle!!.name}")
                TileObjects.getNearest { it.hasAction(obstacle!!.action) && it.name == obstacle!!.name }
            }

        val mark = TileItems.getNearest("Mark of grace")

        if (Movement.getRunEnergy() > Rand.nextInt(5, 55) && !Movement.isRunEnabled()) {
            Movement.toggleRun()
            return 1000
        }

        if (mark != null && obstacle!!.area.contains(mark.tile)) {
            mark.interact("Take")
            return 1000
        }

        if (obs != null) {
            val local = Players.getLocal()!!

            if (local.animation != -1 || local.isMoving) {
                return 1000
            }

            for (i in 0..Rand.nextInt(3, 5)) {
                obs.interact(obstacle!!.action)
                Time.sleep(235, 275)
            }

            Time.sleepUntil({ local.animation != -1 }, 5000)
        } else {
            println("Obstacle null")
        }

        return 1000
    }
}
