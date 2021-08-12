package pvp

import com.openosrs.client.util.WeaponStyle
import dev.botlin.api.Interactable
import dev.botlin.api.commons.Rand
import dev.botlin.api.definitions.Definitions
import dev.botlin.api.events.inventory.ChangeType
import dev.botlin.api.events.inventory.InventoryChanged
import dev.botlin.api.game.Combat
import dev.botlin.api.magic.Ancient
import dev.botlin.api.magic.Magic
import dev.botlin.api.magic.Spell
import dev.botlin.api.movement.Movement
import dev.botlin.api.entities.actor.Players
import dev.botlin.api.entities.container.Equipment
import dev.botlin.api.entities.container.Inventory
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.skill.Skills
import dev.botlin.api.varps.Varps
import dev.botlin.api.widget.Prayer
import dev.botlin.api.widget.Prayers
import dev.botlin.api.wrappers.hasAction
import dev.botlin.api.wrappers.healthPercent
import dev.botlin.api.wrappers.interact
import dev.botlin.api.wrappers.name
import net.runelite.api.HeadIcon
import net.runelite.api.Item
import net.runelite.api.Player
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.*
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.ui.overlay.OverlayUtil
import pvp.data.Constants
import pvp.data.Enemy
import pvp.data.Gear
import pvp.enums.BIS
import pvp.enums.GearItem
import pvp.enums.GearType
import pvp.enums.Potion
import java.awt.Color
import java.time.Instant
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@ScriptMeta("Pvp")
class Pvp : BotScript() {

    val barrageAnimId = 1979
    val sotdMeleeAnimId = 440
    val ahrimStaffMeleeId = 2078
    val rankVarp = 1380
    val eatAnimId = 829

    val meleeSetup = Gear(CopyOnWriteArrayList(), Prayer.PIETY)
    val mageSetup = Gear(CopyOnWriteArrayList(), Prayer.AUGURY)
    val rangeSetup = Gear(CopyOnWriteArrayList(), Prayer.RIGOUR)

    val primarySpell = Ancient.ICE_BARRAGE
    val secondarySpell = Ancient.BLOOD_BARRAGE
    var enemy: Enemy? = null

    var enemyAnims = mutableListOf<Int>()

    var freezeTimer: Instant = Instant.now().minusSeconds(100)
    val frozen: Boolean
        get() = Instant.now().isBefore(freezeTimer)

    var eatTick = Instant.now()

    @Subscribe
    fun onInventoryChanged(e: InventoryChanged) {
        val def = Definitions.getItem(e.itemId)
        if (def!!.name == "Shark" && e.changeType == ChangeType.ITEM_REMOVED) {
            eatTick = Instant.now().plusMillis(1800)
            println("ate, delaying next attack")
        }
    }

    @Subscribe
    fun onAnimationChanged(e: AnimationChanged) {
        if (enemy != null) {
            val target = enemy!!
            if (e.actor is Player && e.actor.name == target.name) {
                if (target.weaponStyle == WeaponStyle.MAGIC) {
                    enemyAnims.add(e.actor.animation)
                }
            }
        }
    }

    @Subscribe
    fun onPlayerDeath(e: ActorDeath) {
        if (e.actor == Players.getLocal()!!) {
            stopLooping()
        }
    }

    @Subscribe
    fun onChatMessage(e: ChatMessage) {
        val msg = e.message
        if (msg.contains("have been frozen")) {
            freezeTimer = Instant.now().plusSeconds(25)
        }
    }

    @Subscribe
    fun onInteractingChanged(e: InteractingChanged) {
        if (e.source == Players.getLocal()!!) {
            if (e.target != null && e.target is Player) {
                if (enemy != null && enemy!!.name == e.target.name) {
                    return
                }

                enemy = Enemy(e.target as Player)
                println("target changed to: ${e.target.name}")
            }

            return
        }

        if (e.source.interacting != null && e.source.interacting == Players.getLocal()!! && e.source is Player) {
            if (enemy != null && enemy!!.name == e.source.name) {
                return
            }

            enemy = Enemy(e.source as Player)
        }
    }

    @Subscribe
    fun onGameTick(e: GameTick) {
        val local = Players.getLocal()
        if (local == null || Prayers.getPoints() == 0) {
            return
        }

        BIS.cycle()
        Potion.consume()

        for (item in Inventory.getAll()) {
            val lower = item.name.toLowerCase()

            if ((item.hasAction("Wear") || item.hasAction("Wield") || item.hasAction("Equip")) && Inventory.getCount { it.id == item.id } > 1) {
                interact(item, "Drop", 5)
                break
            }

            for (junk in Constants.JUNK) {
                if (lower.contains(junk)) {
                    interact(item, "Drop", 5)
                }
            }

            updateGearSetup(item)
        }

        val food = Inventory.getFirst { it.hasAction("Eat") }

        if (food != null && Skills.health <= 40) {
            interact(food, "Eat", 5)
        }

        if (enemy == null) {
            if (mageSetup.anyUnequipped(null)) {
                mageSetup.switchGear(null)
            }

            println("no enemy")
            return
        }

        if (enemy!!.health == 0.0) {
            println("enemy died")
            enemy = null
            return
        }

        val target = enemy!!
        if (target.moving && target.frozen) {
            println("reset timer")
            target.lastFreezeTime = Instant.now().minusSeconds(26)
        }

        if (target.barrageHit && !target.frozen) {
            println("froze target")
            target.lastFreezeTime = Instant.now().plusSeconds(25)
        }

        if (target.distance == 0.0f && frozen && food != null && Skills.health <= 90) {
            println("eating, cuz dd")
            interact(food, "Eat", 5)
        }

        val style = target.weaponStyle

        if (style != null) {
            if (style == WeaponStyle.MAGIC
                && (enemyAnims.contains(sotdMeleeAnimId) || enemyAnims.contains(ahrimStaffMeleeId))
            ) {
                pray(Prayer.PROTECT_FROM_MELEE)
            } else {
                when (style) {
                    WeaponStyle.MAGIC -> {
                        if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC)) {
                            println("enable mage pray")
                            pray(Prayer.PROTECT_FROM_MAGIC)
                        }
                    }

                    WeaponStyle.MELEE -> {
                        if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE)) {
                            println("enable melee pray")
                            pray(Prayer.PROTECT_FROM_MELEE)
                        }
                    }

                    WeaponStyle.RANGE -> {
                        if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES)) {
                            println("enable range pray")
                            pray(Prayer.PROTECT_FROM_MISSILES)
                        }
                    }
                }
            }
        }

        when (target.prayer) {
            HeadIcon.MAGIC -> {
                if (frozen && target.distance > 1.0f) {
                    if (rangeSetup.anyUnequipped(target)) {
                        println("1A. protecting mage and we're frozen, switching to range")
                        rangeSetup.switchGear(target)
                        return
                    }
                } else {
                    if (meleeSetup.anyUnequipped(target)) {
                        println("1B. protecting mage, switching to melee")
                        meleeSetup.switchGear(target)
                        return
                    }
                }
            }

            HeadIcon.MELEE -> {
                val chestArmor = target.chestArmor
                if (chestArmor != null) {
                    if (chestArmor == "hides") {
                        if (!target.frozen) {
                            if (mageSetup.anyUnequipped(target)) {
                                println("2A. protecting melee, wearing hides, not frozen, switching to mage")
                                mageSetup.switchGear(target)
                                return
                            }
                        } else {
                            if (rangeSetup.anyUnequipped(target)) {
                                println("2B. protecting melee, wearing hides, switching to range")
                                rangeSetup.switchGear(target)
                                return
                            }
                        }
                    } else if (chestArmor == "robes") {
                        if (rangeSetup.anyUnequipped(target)) {
                            println("2C. protecting melee, wearing robes, switching to range")
                            rangeSetup.switchGear(target)
                            return
                        }
                    }
                } else {
                    if (!target.frozen) {
                        if (mageSetup.anyUnequipped(target)) {
                            println("2D. protecting melee, no chest armor, not frozen, switching to mage")
                            mageSetup.switchGear(target)
                            return
                        }
                    } else {
                        if (rangeSetup.anyUnequipped(target)) {
                            println("2E. protecting melee, no chest armor, switching to range")
                            rangeSetup.switchGear(target)
                            return
                        }
                    }
                }
            }

            HeadIcon.RANGED -> {
                if (frozen && target.distance > 1.0f) {
                    if (mageSetup.anyUnequipped(target)) {
                        println("3A. protecting range and we're frozen, switching to mage")
                        mageSetup.switchGear(target)
                        return
                    }
                } else {
                    val chestArmor = target.chestArmor
                    if (chestArmor != null) {
                        if (chestArmor == "hides") {
                            if (!target.frozen) {
                                if (mageSetup.anyUnequipped(target)) {
                                    println("3B. protecting ranged, target not frozen, wearing hides, switching to mage")
                                    mageSetup.switchGear(target)
                                    return
                                }
                            } else {
                                if (meleeSetup.anyUnequipped(target)) {
                                    println("3C. protecting ranged, target frozen, wearing hides, switching to melee")
                                    meleeSetup.switchGear(target)
                                    return
                                }
                            }
                        } else if (chestArmor == "robes") {
                            if (meleeSetup.anyUnequipped(target)) {
                                println("3D. protecting ranged, target not frozen, wearing robes, switching to mage")
                                meleeSetup.switchGear(target)
                                return
                            }
                        }
                    } else {
                        if (!target.frozen) {
                            if (mageSetup.anyUnequipped(target)) {
                                println("3E. protecting ranged, target not frozen, no chest armor, switching to mage")
                                mageSetup.switchGear(target)
                                return
                            }
                        } else {
                            if (meleeSetup.anyUnequipped(target)) {
                                println("3F. protecting ranged, target frozen, no chestr armor, switching to melee")
                                meleeSetup.switchGear(target)
                                return
                            }
                        }
                    }
                }
            }

            else -> {
                val chestArmor = target.chestArmor
                if (frozen && target.distance > 1.0f) {
                    if (chestArmor != null) {
                        if (chestArmor == "hides") {
                            if (!target.frozen) {
                                if (mageSetup.anyUnequipped(target)) {
                                    println("4A. not protecting, we are frozen, target not frozen, wearing hides, switching to mage")
                                    mageSetup.switchGear(target)
                                    return
                                }
                            } else {
                                if (rangeSetup.anyUnequipped(target)) {
                                    println("4B. not protecting, we are frozen, target frozen, wearing hides, switching to range")
                                    rangeSetup.switchGear(target)
                                    return
                                }
                            }
                        } else if (chestArmor == "robes") {
                            if (!target.frozen) {
                                if (mageSetup.anyUnequipped(target)) {
                                    println("4C. not protecting, we are frozen, target not frozen, wearing robes, switching to mage")
                                    mageSetup.switchGear(target)
                                    return
                                }
                            } else {
                                if (rangeSetup.anyUnequipped(target)) {
                                    println("4D. not protecting, we are frozen, target frozen, wearing robes, switching to range")
                                    rangeSetup.switchGear(target)
                                    return
                                }
                            }
                        }
                    } else {
                        if (!target.frozen) {
                            if (mageSetup.anyUnequipped(target)) {
                                println("4E. not protecting, we are frozen, target not frozen, no chest armor, switching to mage")
                                mageSetup.switchGear(target)
                                return
                            }
                        } else {
                            if (rangeSetup.anyUnequipped(target)) {
                                println("4F. not protecting, we are frozen, target frozen, wearing robes, switching to range")
                                rangeSetup.switchGear(target)
                                return
                            }
                        }
                    }
                } else {
                    if (chestArmor != null) {
                        if (chestArmor == "hides") {
                            if (target.distance > 5.0f && target.health > 50.0) {
                                if (!target.frozen) {
                                    if (mageSetup.anyUnequipped(target)) {
                                        println("4G. not protecting, we are not frozen, target not frozen, wearing hides, far away, switching to mage")
                                        mageSetup.switchGear(target)
                                        return
                                    }
                                } else {
                                    if (rangeSetup.anyUnequipped(target)) {
                                        println("4H. not protecting, we are not frozen, target frozen, wearing hides, far away, switching to range")
                                        rangeSetup.switchGear(target)
                                        return
                                    }
                                }
                            } else {
                                if (!target.frozen) {
                                    if (mageSetup.anyUnequipped(target)) {
                                        println("4I. not protecting, we are not frozen, target not frozen, wearing hides, close, switching to mage")
                                        mageSetup.switchGear(target)
                                        return
                                    }
                                } else {
                                    if (meleeSetup.anyUnequipped(target)) {
                                        println("4J. not protecting, we are not frozen, target frozen, wearing hides, close, switching to melee")
                                        meleeSetup.switchGear(target)
                                        return
                                    }
                                }
                            }
                        } else if (chestArmor == "robes") {
                            if (target.distance > 5.0f && target.health > 50.0) {
                                if (!target.frozen) {
                                    if (mageSetup.anyUnequipped(target)) {
                                        println("4K. not protecting, we are not frozen, target not frozen, wearing robes, far away, switching to mage")
                                        mageSetup.switchGear(target)
                                        return
                                    }
                                } else {
                                    if (rangeSetup.anyUnequipped(target)) {
                                        println("4L. not protecting, we are not frozen, target frozen, wearing robes, far away, switching to range")
                                        rangeSetup.switchGear(target)
                                        return
                                    }
                                }
                            } else {
                                if (!target.frozen) {
                                    if (mageSetup.anyUnequipped(target)) {
                                        println("4M. not protecting, we are not frozen, target not frozen, wearing robes, close, switching to mage")
                                        mageSetup.switchGear(target)
                                        return
                                    }
                                } else {
                                    if (meleeSetup.anyUnequipped(target)) {
                                        println("4N. not protecting, we are not frozen, target frozen, wearing robes, close, switching to melee")
                                        meleeSetup.switchGear(target)
                                        return
                                    }
                                }
                            }
                        }
                    } else {
                        if (target.distance > 5.0f && target.health > 50.0) {
                            if (!target.frozen) {
                                if (mageSetup.anyUnequipped(target)) {
                                    println("4O. not protecting, we are not frozen, target not frozen, no chest armor, far away, switching to mage")
                                    mageSetup.switchGear(target)
                                    return
                                }
                            } else {
                                if (rangeSetup.anyUnequipped(target)) {
                                    println("4P. not protecting, we are not frozen, target frozen, no chest armor, far away, switching to range")
                                    rangeSetup.switchGear(target)
                                    return
                                }
                            }
                        } else {
                            if (!target.frozen) {
                                if (mageSetup.anyUnequipped(target)) {
                                    println("4Q. not protecting, we are not frozen, target not frozen, no chest armor, close, switching to mage")
                                    mageSetup.switchGear(target)
                                    return
                                }
                            } else {
                                if (meleeSetup.anyUnequipped(target)) {
                                    println("4R. not protecting, we are not frozen, target frozen, no chest armor, close, switching to melee")
                                    meleeSetup.switchGear(target)
                                    return
                                }
                            }
                        }
                    }
                }
            }
        }

        enemyAnims.clear()

        val specWeapon = Equipment.getFirst(meleeSetup.getSpecWeapon()!!.getName())

        if (target.frozen && !frozen && local.worldLocation != target.position) {
            if (specWeapon != null && target.distance > 1.0f) {
                println("specing, not going to walk under yet")
            } else {
                println("walking under")
                walk(target.position, 15)
                return
            }
        }

        val myWeapon = Equipment.getFirst { it.widgetInfo == WidgetInfo.EQUIPMENT_WEAPON }

        if (myWeapon != null) {
            val myStyle = WeaponMap.StyleMap[myWeapon.id]
            if (myStyle != null) {
                when (myStyle) {
                    WeaponStyle.MELEE -> {
                        if (!Prayers.isEnabled(meleeSetup.offensivePrayer)) {
                            println("our style is melee, toggling ${meleeSetup.offensivePrayer}")
                            pray(meleeSetup.offensivePrayer)
                        }

                        if (meleeSetup.enoughSpec(target)) {
                            println("we have spec")
                            if (specWeapon != null) {
                                println("spec weapon is ${specWeapon.name}, enabling spec")
                                spec(50)
                            }
                        }

                        if (local.worldLocation == target.position && Instant.now().isBefore(eatTick)) {
                            println("not going to melee yet, waiting for eat delay")
                            return
                        }

                        if (local.interacting == null && !local.isMoving) {
                            interact(Players.getNearest(target.name!!)!!, "Attack", 150)
                            println("meleeing target")
                        }
                    }

                    WeaponStyle.RANGE -> {
                        if (!Prayers.isEnabled(rangeSetup.offensivePrayer)) {
                            println("our style is range, toggling ${rangeSetup.offensivePrayer}")
                            pray(rangeSetup.offensivePrayer)
                        }

                        if (local.worldLocation == target.position && Instant.now().isBefore(eatTick)) {
                            println("not going to range yet, waiting for eat delay")
                            return
                        }

                        if (!local.isMoving && local.interacting == null) {
                            println("ranging target")
                            interact(Players.getNearest(target.name!!)!!, "Attack", 150)
                        }
                    }

                    WeaponStyle.MAGIC -> {
                        if (!Prayers.isEnabled(mageSetup.offensivePrayer)) {
                            println("our style is magic, toggling ${mageSetup.offensivePrayer}")
                            pray(mageSetup.offensivePrayer)
                        }

                        if (local.worldLocation == target.position && Instant.now().isBefore(eatTick)) {
                            println("not going to barrage yet, waiting for eat delay")
                            return
                        }

                        if (!local.isMoving) {
                            if (target.frozen && Skills.healthPercent <= 70) {
                                cast(Players.getNearest(target.name!!)!!, secondarySpell, 150)
                            } else {
                                cast(Players.getNearest(target.name!!)!!, primarySpell, 150)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStart(vararg startArgs: String) {
        paint.setEnabled(true)
        paint.submit {
            val text = "(F: $frozen) (A: ${Players.getLocal()!!.animation}) (H: ${Players.getLocal()!!.healthPercent})"
            OverlayUtil.renderActorOverlay(it, Players.getLocal()!!, text, Color.GREEN)

            if (enemy != null) {
                enemy!!.render(it)
            }

            null
        }
    }

    override fun loop(): Int {
        return 50
    }

    companion object {
        fun walk(point: WorldPoint, delay: Int) {
            val service = Executors.newSingleThreadScheduledExecutor()
            service.schedule({ Movement.walk(point) }, delay.toLong(), TimeUnit.MILLISECONDS)
            service.shutdown()
        }

        fun interact(interactable: Interactable, action: String, delay: Int) {
            val service = Executors.newSingleThreadScheduledExecutor()
            service.schedule({ interactable.interact(action) }, delay.toLong(), TimeUnit.MILLISECONDS)
            service.shutdown()
        }

        fun interact(interactable: Interactable, index: Int, delay: Int) {
            val service = Executors.newSingleThreadScheduledExecutor()
            service.schedule({ interactable.interact(index) }, delay.toLong(), TimeUnit.MILLISECONDS)
            service.shutdown()
        }

        fun interact(interactable: Interactable, filter: (String?) -> Boolean, delay: Int) {
            val service = Executors.newSingleThreadScheduledExecutor()
            service.schedule({ interactable.interact(filter) }, delay.toLong(), TimeUnit.MILLISECONDS)
            service.shutdown()
        }

        fun attack(interactable: Player, delay: Int) {
            val service = Executors.newSingleThreadScheduledExecutor()
            service.schedule({
                if (Players.getLocal()!!.interacting == null) {
                    interactable.interact("Attack")
                }
            }, delay.toLong(), TimeUnit.MILLISECONDS)
            service.shutdown()
        }

        fun cast(interactable: Player, spell: Spell, delay: Int) {
            val service = Executors.newSingleThreadScheduledExecutor()
            service.schedule({
                Magic.cast(spell, interactable)
            }, delay.toLong(), TimeUnit.MILLISECONDS)
            service.shutdown()
        }

        fun spec(delay: Int) {
            val service = Executors.newSingleThreadScheduledExecutor()
            service.schedule({ Combat.enableSpec() }, delay.toLong(), TimeUnit.MILLISECONDS)
            service.shutdown()
        }

        fun pray(prayer: Prayer) {
            val service = Executors.newSingleThreadScheduledExecutor()
            service.schedule({
//                if (Rand.nextInt(0, 6) != 0) {
                Prayers.togglePrayer(prayer)
//                }

            }, Rand.nextInt(1, 9).toLong(), TimeUnit.MILLISECONDS)
            service.shutdown()
        }
    }

    private fun updateGearSetup(i: Item) {
        for (lmsItem in GearItem.values()) {
            val name: String = i.name
            val inventoryItem = Inventory.getFirst(lmsItem.getName())
            val equipmentItem = Equipment.getFirst(lmsItem.getName())

            if (inventoryItem == null && equipmentItem == null && mageSetup.hasItem(lmsItem.getName())) {
                mageSetup.removeItem(lmsItem.getName())
            }
            if (inventoryItem == null && equipmentItem == null && rangeSetup.hasItem(lmsItem.getName())) {
                rangeSetup.removeItem(lmsItem.getName())
            }
            if (inventoryItem == null && equipmentItem == null && meleeSetup.hasItem(lmsItem.getName())) {
                meleeSetup.removeItem(lmsItem.getName())
            }

            if (name != lmsItem.getName()) {
                continue
            }

            for (type in lmsItem.types) {
                if (type == GearType.MAGE_GEAR) {
                    if (!mageSetup.hasItem(name)) {
                        mageSetup.addItem(lmsItem)
                    }
                }

                if (type === GearType.MAGE_WEP) {
                    if (!mageSetup.hasItem(name)) {
                        mageSetup.addItem(lmsItem)
                    }
                }

                if (type === GearType.RANGE_GEAR) {
                    if (!rangeSetup.hasItem(name)) {
                        rangeSetup.addItem(lmsItem)
                    }
                }

                if (type === GearType.RANGE_WEP) {
                    if (!rangeSetup.hasItem(name)) {
                        rangeSetup.addItem(lmsItem)
                    }
                }

                if (type === GearType.MELEE_GEAR) {
                    if (!meleeSetup.hasItem(name)) {
                        meleeSetup.addItem(lmsItem)
                    }
                }

                if (type === GearType.MELEE_WEP) {
                    if (!meleeSetup.hasItem(name)) {
                        meleeSetup.addItem(lmsItem)
                    }
                }
            }
        }
    }

    fun getRank(): Int {
        return Varps[rankVarp]
    }
}
