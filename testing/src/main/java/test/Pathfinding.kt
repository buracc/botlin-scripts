package test

import com.google.gson.GsonBuilder
import com.google.inject.Inject
import dev.botlin.api.game.Scene
import dev.botlin.api.movement.Movement
import dev.botlin.api.movement.pathfinder.request.Coordinate
import dev.botlin.api.movement.pathfinder.request.PathRequest
import dev.botlin.api.provider.actor.Players
import dev.botlin.api.provider.tile.TileObjects
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.wrappers.hasAction
import dev.botlin.api.wrappers.isDoor
import dev.botlin.api.wrappers.outline
import net.runelite.api.Client
import net.runelite.api.Perspective
import net.runelite.api.Point
import net.runelite.api.coords.Direction
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.client.ui.overlay.OverlayUtil
import okhttp3.OkHttpClient
import java.awt.Color

@ScriptMeta("pathfinding")
class Pathfinding : BotScript() {

    @Inject
    private lateinit var client: Client

    @Inject
    private lateinit var okHttpClient: OkHttpClient

    var path: List<WorldPoint>? = null
    val gson = GsonBuilder().setPrettyPrinting().create()
    val color = Color(255, 0, 0, 128)

    val url = "https://yanick:Alooooooooo_123!@bscripts.dev:8443/pathfinder"

    override fun onStart(vararg startArgs: String) {
        paint.setEnabled(true)
        paint.submit {
            val targetTile = Scene.getMouseTile() ?: return@submit null
            val loc = targetTile.worldLocation ?: return@submit null
            val path = Movement.getPath(loc) ?: return@submit null
            for (tile in path) {
                val door = TileObjects.getAt(tile).firstOrNull { tileObject ->  tileObject.isDoor() }
                if (door != null) {
                    tile.outline(it, Color.MAGENTA)
                } else {
                    tile.outline(it, color)
                }
            }


            Movement.pathfinder.drawDebug(it, client)

            null
        }

        paused = true
    }

    val currentPos = Players.getLocal()?.worldLocation ?: WorldPoint(3060, 3390, 0)
    val start = Coordinate(currentPos.x, currentPos.y, currentPos.plane)
    val end = Coordinate(3060, 3390, 0)
    val request = PathRequest(start, end, avoidWildy = true, includeTransports = false)

    override fun loop() {
        if (path == null) {
            try {
                path = PathRequest.create(request)
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        println(path)
    }

}