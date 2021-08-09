package pvp.data

import com.openosrs.client.util.WeaponStyle
import dev.botlin.api.entities.actor.Players
import dev.botlin.api.wrappers.healthPercent
import net.runelite.api.HeadIcon
import net.runelite.api.Player
import net.runelite.api.coords.WorldPoint
import net.runelite.client.ui.overlay.OverlayUtil
import java.awt.Color.RED
import java.awt.Graphics2D
import java.time.Instant

class Enemy(private val player: Player) {
    val name: String?
        get() = player.name

    val health: Double
        get() = player.healthPercent

    val prayer: HeadIcon?
        get() = player.overheadIcon

    val moving: Boolean
        get() = player.isMoving

    val position: WorldPoint
        get() = player.worldLocation

    val chestArmor: String?
        get() {
//            val appearance = player.playerAppearance
//            if (appearance != null) {
//                val def = Definitions.getItem(appearance.getEquipmentId(KitType.TORSO))
//                if (def!!.name.toLowerCase().contains("leather") || def.name.toLowerCase().contains("hide")) {
//                    return "hides"
//                }
//
//                return "robes"
//            }

            return null
        }

    val distance: Float
        get() {
            val local = Players.getLocal() ?: return -1f
            return player.worldLocation.distanceTo2DHypotenuse(local.worldLocation)
        }

    val barrageHit: Boolean
        get() = player.graphic == 369

    var lastFreezeTime: Instant = Instant.now().minusSeconds(100)

    val frozen: Boolean
        get() = Instant.now().isBefore(lastFreezeTime)

    val weaponStyle: WeaponStyle?
        get() {
//            val appearance = player.playerAppearance
//
//            if (appearance != null) {
//                val weaponId = appearance.getEquipmentId(KitType.WEAPON)
//                return WeaponMap.StyleMap[weaponId]
//            }

            return null
        }

    fun render(g: Graphics2D) {
        val text = "$name  (F: $frozen) (W: $weaponStyle) (D: $distance) (A: ${player.animation}) (H: $health)"
        OverlayUtil.renderActorOverlay(g, player, text, RED)
    }
}
