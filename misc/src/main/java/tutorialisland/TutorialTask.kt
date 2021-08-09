package tutorialisland

import dev.botlin.api.commons.Rand
import dev.botlin.api.commons.Time
import dev.botlin.api.game.Client
import dev.botlin.api.game.GameSettings
import dev.botlin.api.input.Keyboard
import dev.botlin.api.interact.Interact
import dev.botlin.api.magic.Magic
import dev.botlin.api.magic.Regular
import dev.botlin.api.movement.Movement
import dev.botlin.api.entities.actor.NPCs
import dev.botlin.api.entities.actor.Players
import dev.botlin.api.entities.container.Inventory
import dev.botlin.api.entities.tile.TileObjects
import dev.botlin.api.entities.widget.Widgets
import dev.botlin.api.varps.Varps
import dev.botlin.api.widget.Dialog
import dev.botlin.api.widget.tab.Tab
import dev.botlin.api.widget.tab.Tabs
import dev.botlin.api.wrappers.*
import net.runelite.api.HintArrowType
import net.runelite.api.Point
import net.runelite.api.coords.WorldPoint
import net.runelite.api.widgets.WidgetID

object TutorialTask {
    val spanish = listOf(
        "Gordito",
        "Pollito",
        "Pachuco",
        "Primo",
        "Calaca",
        "Conejito",
        "Chulo",
        "Chiquito",
        "Guapo",
        "Hermosa",
        "Cielito",
        "Amado",
        "Carino"
    )

    fun executeOld() {
        val config = Varps[281]
        var doDefault = false
        val local = Players.getLocal() ?: return

        println(config)

        if (local.isAnimating) {
            return
        }

        if (local.isMoving) {
            return
        }

        if (Dialog.canContinue()) {
            Dialog.continueSpace()
            Dialog.continueTutorial()
            return
        }

        when (config) {
            0 -> return

            1 -> {
                val nameStatus = Widgets.get(558, 12)

                if (nameStatus != null && nameStatus.visible) {
                    val statusText: String = nameStatus.text.toLowerCase()
                    if (statusText.contains("sorry, this display name")) {
                        Interact.interact(1, 57, -1, 36569103, Point(-1, -1))
                        Time.sleepUntil({ statusText.contains("great!") }, 5000)
                        return
                    }

                    if (statusText.contains("great! this display name")) {
                        println("setting name")
                        Widgets.get(558, 18)?.interact("Set name")
                    } else {
                        if (Dialog.isEnterAmountOpen()) {
                            Keyboard.type(spanish.random() + Rand.nextInt(20000, 2000000), true)
                            return
                        }

                        println("lookup name")
                        Widgets.get(558, 17)?.interact("Look up name")
                        return
                    }

                    return
                }

                val confirmSkin = Widgets.get(679, 68)
                confirmSkin?.interact("Confirm")
            }

            2 -> {
                if (Dialog.isViewingOptions()) {
                    Dialog.chooseOption(1)
                    println("selecting")
                    return
                }

                val guide = NPCs.getHintArrowed()
                guide?.interact("Talk-to")
            }

            3 -> {
                val optionsResizable = Widgets.get(164, 41)
                if (optionsResizable != null && optionsResizable.visible) {
                    println("click options resizable")
                    optionsResizable.interact("Settings")
                    return
                }

                val optionsFixed = Widgets.get(548, 38)
                if (optionsFixed != null && optionsFixed.visible) {
                    println("click options fixed")
                    optionsFixed.interact("Settings")
                    return
                }
            }

            7 -> {
                if (!GameSettings.Volume.isFullMuted()) {
                    GameSettings.Volume.muteAll()
                }

                if (GameSettings.Display.getMode() != GameSettings.Display.Mode.FIXED) {
                    GameSettings.Display.setMode(GameSettings.Display.Mode.FIXED)
                }

                doDefault = true
            }

            30 -> {
                Tabs.open(Tab.INVENTORY);
            }

            50 -> {
                Tabs.open(Tab.SKILLS)
            }

            80 -> {
                val fire = TileObjects.getNearest { it.worldLocation == local.worldLocation }
                if (fire != null) {
                    Movement.walk(local.worldLocation.dx(1))
                    return
                }

                Inventory.getFirst("Tinderbox")?.useOn(Inventory.getFirst("Logs")!!)
            }

            90 -> {
                if (Dialog.canContinue()) {
                    Dialog.continueTutorial()
                    Dialog.continueSpace()
                    return
                }

                Inventory.getFirst("Raw shrimps")?.useOn(TileObjects.getNearest("Fire")!!)
            }

            150 -> {
                Inventory.getFirst("Pot of flour")?.useOn(Inventory.getFirst("Bucket of water")!!)
            }

            230 -> {
                Tabs.open(Tab.QUESTS)
            }

            260 -> {
                if (NPCs.getHintArrowed() == null) {
                    Movement.walk(3080, 9503)
                    return
                }

                doDefault = true
            }

            340 -> {
                val smithDagger =
                    Widgets.get(WidgetID.SMITHING_GROUP_ID) { it.name.toLowerCase().contains("bronze dagger") }
                if (smithDagger == null || !smithDagger.visible) {
                    TileObjects.getNearest("Anvil")?.interact("Smith")
                    return
                }

                smithDagger.interact("Smith")
                Time.sleep(2000)
            }

            350 -> {
                val smithDagger =
                    Widgets.get(WidgetID.SMITHING_GROUP_ID) { it.name.toLowerCase().contains("bronze dagger") }
                if (smithDagger == null || !smithDagger.visible) {
                    TileObjects.getNearest("Anvil")?.interact("Smith")
                    return
                }

                smithDagger.interact("Smith")
                Time.sleep(2000)
            }

            390 -> {
                Tabs.open(Tab.EQUIPMENT)
            }

            400 -> {
                Widgets.get(WidgetID.EQUIPMENT_GROUP_ID, 1)?.interact { true }
            }

            405 -> {
                Inventory.getFirst("Bronze dagger")?.interact("Wield")
            }

            420 -> {
                val sword = Inventory.getFirst("Bronze sword")
                val shield = Inventory.getFirst("Wooden shield")

                sword?.interact("Wield")
                shield?.interact("Wield")
            }

            430 -> {
                Tabs.open(Tab.COMBAT)
            }

            440 -> {
                TileObjects.getNearest {
                    it.name == "Gate" && it.worldLocation == WorldPoint(
                        3111,
                        9518,
                        Client.getPlane()
                    )
                }?.interact("Open")
            }

            470 -> {
                TileObjects.getNearest {
                    it.hasAction("Open") && it.worldLocation == WorldPoint(
                        3111,
                        9518,
                        Client.getPlane()
                    )
                }?.interact("Open")
                Time.sleep(10 * 1000)

                NPCs.getHintArrowed()?.interact("Talk-to")
                Time.sleep(10 * 1000)
                doDefault = true
            }

            490 -> {
                val bow = Inventory.getFirst("Shortbow")
                if (bow != null) {
                    bow.interact("Wield")
                    return
                }

                val arrows = Inventory.getFirst("Bronze arrow")
                if (arrows != null) {
                    arrows.interact("Wield")
                    return
                }

                if (NPCs.getHintArrowed() == null) {
                    NPCs.getNearest("Giant rat")?.interact("Attack")
                    return
                }

                doDefault = true
            }

            510 -> {
                val booth = TileObjects.getNearest("Bank booth")
                booth?.interact { true }
            }

            520 -> {
                val pbooth = TileObjects.getNearest(26815)
                pbooth?.interact(0)
            }

            531 -> {
                Tabs.open(Tab.ACCOUNT)
            }

            550 -> {
                if (local.worldLocation.y > 3116) {
                    Movement.walk(WorldPoint(3134, 3116, 0))
                    return
                }

                doDefault = true
            }

            560 -> {
                Tabs.open(Tab.PRAYER)
            }

            580 -> {
                if (Dialog.canContinue()) {
                    doDefault = true
                    Dialog.continueTutorial()
                    Dialog.continueSpace()
                }

                Tabs.open(Tab.FRIENDS)
            }

            620 -> {
                if (local.worldLocation.y > 3100) {
                    Movement.walk(WorldPoint(3131, 3088, 0))
                    return
                }

                doDefault = true
            }

            630 -> {
                Tabs.open(Tab.MAGIC)
            }

            650 -> {
                if (Dialog.canContinue()) {
                    Dialog.continueTutorial()
                    Dialog.continueSpace()
                    return
                }

                val strikeLoc = WorldPoint(3139, 3091, Client.getPlane())
                if (strikeLoc == local.worldLocation) {
                    Magic.cast(Regular.WIND_STRIKE, NPCs.getNearest("Chicken")!!)
                    return
                }

                Movement.walk(strikeLoc)
            }

            670 -> {
                if (Dialog.isViewingOptions()) {
                    if (Dialog.getOptions()[0].text.contains("Iron Man")) {
                        Dialog.chooseOption(3)
                        return
                    }

                    Dialog.chooseOption("Yes.")
                    return
                }

                doDefault = true
            }

            1000 -> {

            }

            else -> {
                doDefault = true
            }
        }

        if (!doDefault) {
            return
        }

        if (Dialog.isViewingOptions()) {
            Dialog.chooseOption(1)
            return
        }

        val hinted = when (Client.getHintArrowType()) {
            HintArrowType.NPC -> {
                NPCs.getHintArrowed()
            }

            HintArrowType.WORLD_POSITION -> {
                val point = Client.getHintArrowPoint()
                TileObjects.getNearest {
                    if (it.actions.firstOrNull { true } == null) {
                        return@getNearest false
                    }

                    if (it.worldLocation == point) {
                        return@getNearest true
                    }

                    if (it.worldLocation.distanceTo(point) < 2) {
                        return@getNearest true
                    }

                    return@getNearest false
                }
            }

            else -> {
                null
            }
        } ?: return

        if (hinted.hasAction("Attack") && local.interacting == null) {
            println("interacting attack")
            hinted.interact("Attack")
            return
        }

        println("interacting arrow")
        hinted.interact(0)
    }
}
