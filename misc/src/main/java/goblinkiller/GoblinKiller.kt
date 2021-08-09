package goblinkiller

import dev.botlin.api.commons.Rand
import dev.botlin.api.commons.StopWatch
import dev.botlin.api.commons.Time
import dev.botlin.api.game.Combat
import dev.botlin.api.game.GameSettings
import dev.botlin.api.movement.Movement
import dev.botlin.api.entities.actor.NPCs
import dev.botlin.api.entities.actor.Players
import dev.botlin.api.entities.container.Inventory
import dev.botlin.api.entities.tile.TileItems
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.script.paint.tracker.PaintStatistic
import dev.botlin.api.skill.Skills
import dev.botlin.api.varps.Varps
import dev.botlin.api.wrappers.*
import net.runelite.api.Client
import net.runelite.api.Skill
import net.runelite.api.coords.WorldPoint
import javax.inject.Inject

@ScriptMeta("goblinkiller")
class GoblinKiller : BotScript() {
    val location = WorldPoint(3252, 3239, 0)
    private val timer = StopWatch.start()

    @Inject
    private lateinit var client: Client

    override fun onStart(vararg startArgs: String) {
        paint.setEnabled(true)
        paint.tracker.submit("Coins", PaintStatistic(timer, { Inventory.getFirst(995)?.quantity ?: 0 }))
        paint.tracker.trackSkill(Skill.STRENGTH)
        paint.tracker.trackSkill(Skill.ATTACK)
        paint.tracker.trackSkill(Skill.HITPOINTS)
    }

    override fun loop() {
        val local = Players.getLocal() ?: return

        if (Varps[281] < 1000) {
            TutorialTask.executeOld()
            return
        }

        if (!Movement.isRunEnabled() && Movement.getRunEnergy() > Rand.nextInt(1, 80)) {
            Movement.toggleRun()
            return
        }

        for (item in Inventory.getAll {
            it.name != "Bronze sword" && it.name != "Wooden shield" && it.id != 995
        }) {
            item.interact("Drop")
            Time.sleep(150, 350)
        }

        if (local.isMoving) {
            return
        }

        if (location.distanceTo(local) > 20) {
            Movement.walkTo(location)
            return
        }

        val gravestone = NPCs.getNearest { it.hasAction("Loot") }

        if (gravestone != null) {
            gravestone.interact("Loot")
            return
        }

        val sword = Inventory.getFirst("Bronze sword")
        val shield = Inventory.getFirst("Wooden shield")

        if (sword != null) {
            sword.interact("Wield")
            return
        }

        if (shield != null) {
            shield.interact("Wield")
            return
        }

        if (!GameSettings.Volume.isFullMuted()) {
            GameSettings.Volume.muteAll()
            return
        }

        if (Skills.getLevel(Skill.STRENGTH) > Skills.getLevel(Skill.ATTACK) &&
            Combat.getSelectedStyle() != Combat.AttackStyle.FIRST
        ) {
            Combat.selectStyle(Combat.AttackStyle.FIRST)
            return
        }

        if (Skills.getLevel(Skill.ATTACK) > Skills.getLevel(Skill.STRENGTH) &&
            Combat.getSelectedStyle() != Combat.AttackStyle.SECOND
        ) {
            Combat.selectStyle(Combat.AttackStyle.SECOND)
            return
        }

        if (local.interacting != null) {
            return
        }

        val lootItem = TileItems.getNearest { it.id == 995 && it.isInteractable() }

        if (lootItem != null) {
            lootItem.interact("Take")
            return
        }

        val goblin = Combat.getAttackableNpc {
            it.name == "Goblin" && !it.isDead && it.isInteractable()
        } ?: return

        goblin.interact("Attack")
    }
}
