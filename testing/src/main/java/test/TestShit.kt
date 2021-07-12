package test

import dev.botlin.api.commons.Rand
import dev.botlin.api.game.Client
import dev.botlin.api.movement.Movement
import dev.botlin.api.provider.actor.Players
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.wrappers.distanceTo
import dev.botlin.api.wrappers.interact
import dev.botlin.api.wrappers.isReachable
import dev.botlin.api.wrappers.outline
import net.runelite.api.coords.WorldPoint
import java.awt.Color

@ScriptMeta("testshit")
class TestShit : BotScript() {

    val target = WorldPoint(3243, 3210, 0)

    override fun loop() {
        val local = Players.getLocal() ?: return
        if (Movement.isWalking()) {
            return
        }
        pathfind(target)
    }

    override fun onStart(vararg startArgs: String) {
        paint.setEnabled(true)
        paint.submit {
            for (tile in Movement.getPath(target)) {
                tile.outline(it)
            }

            Movement.pathfinder.drawDebug(it, Client.getRl())
            target.outline(it, Color.GREEN)
            getDestination()?.outline(it, Color.ORANGE)
            null
        }
    }

    fun pathfind(destination: WorldPoint) {
        val local = Players.getLocal() ?: return
        val path = Movement.getPath(destination)
        val doors = Movement.getDoorsInPath(path)
        val distance = Rand.nextInt(20, 25)
        val nextTile = if (destination.distanceTo() < 10) {
            destination
        } else {
            path
                .filter { it.distanceTo(local.worldLocation) < distance }
                .randomOrNull() ?: return
        }

        if (!nextTile.isReachable()) {
            val door = doors.minByOrNull { it.distanceTo(local.worldLocation) }
            if (door == null) {
                println("Tile $nextTile not reachable and there's no doors in our way")
                return
            }

            door.interact("Open")
            return
        }

        Movement.walk(nextTile)
    }

    fun getDestination(): WorldPoint? {
        val local = Client.getLocalDestinationLocation() ?: return null
        return WorldPoint.fromLocal(Client.getRl(), local)
    }
}
