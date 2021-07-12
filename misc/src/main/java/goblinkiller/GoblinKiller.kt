package goblinkiller

import dev.botlin.api.commons.Rand
import dev.botlin.api.commons.StopWatch
import dev.botlin.api.commons.Time
import dev.botlin.api.definitions.Definitions
import dev.botlin.api.events.inventory.InventoryChanged
import dev.botlin.api.game.Combat
import dev.botlin.api.game.Game
import dev.botlin.api.game.GameSettings
import dev.botlin.api.movement.Movement
import dev.botlin.api.provider.actor.NPCs
import dev.botlin.api.provider.actor.Players
import dev.botlin.api.provider.container.Inventory
import dev.botlin.api.provider.tile.TileItems
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.script.blocking.events.LoginEvent
import dev.botlin.api.script.paint.tracker.PaintStatistic
import dev.botlin.api.skill.Skills
import dev.botlin.api.varps.Varps
import dev.botlin.api.wrappers.distanceTo
import dev.botlin.api.wrappers.hasAction
import dev.botlin.api.wrappers.interact
import dev.botlin.api.wrappers.name
import net.runelite.api.Client
import net.runelite.api.Skill
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.GameTick
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.game.ItemManager
import tutorialisland.TutorialTask
import javax.inject.Inject

@ScriptMeta("goblinkiller")
class GoblinKiller : BotScript() {
    val webhook =
        DiscordWebhook("https://discordapp.com/api/webhooks/709402272194494508/np386cW7G8eMYkNZ3f2aGGdGVYRodLRiKy0omSKuCGBBKYE1gaIWchEHjA43X4Ju1RWD")
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
        blockingEventManager.addLoginResponseHandler(LoginEvent.Response.ACCOUNT_DISABLED) {
            sendDiscordMsg(true, "Banned", "Banned nigga")
            -1
        }
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
            Movement.webWalk(location, 3)
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

        val lootItem = TileItems.getNearest { it.id == 995 && Movement.isReachable(it.tile.worldLocation) }

        if (lootItem != null) {
            lootItem.interact("Take")
            return
        }

        val goblin = NPCs.getNearest {
            it.hasAction("Attack")
                    && it.distanceTo(location) < 15
                    && Movement.isReachable(it.worldLocation)
                    && !it.isDead
            if (!it.hasAction("Attack") || it.distanceTo(location) > 15 || !Movement.isReachable(it.worldLocation) || it.isDead) {
                return@getNearest false
            }

            if (it.interacting != null && it.interacting == local) {
                return@getNearest true
            }

            return@getNearest it.interacting == null
        } ?: return

        goblin.interact("Attack")
    }

    fun sendDiscordMsg(notify: Boolean = false, title: String, desc: String, vararg fields: Pair<String, String>) {
        val embed = DiscordWebhook.EmbedObject()
        webhook.setContent("")
        webhook.clearEmbeds()
        if (notify) {
            webhook.setContent("<@135851218546327562>")
        }

        if (title.isNotBlank()) {
            embed.title = title
        }

        if (desc.isNotBlank()) {
            embed.description = desc
        }

        for (field in fields) {
            embed.addField(field.first, field.second, false)
        }

        webhook.addEmbed(embed)
        webhook.execute()
    }
}