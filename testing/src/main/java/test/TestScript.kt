package test

import dev.botlin.api.entities.actor.NPCs
import dev.botlin.api.entities.actor.Players
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import dev.botlin.api.wrappers.distanceTo
import dev.botlin.api.wrappers.interact
import dev.botlin.api.wrappers.isInteractable

@ScriptMeta("chickenkiller")
class TestScript : BotScript() {

    override fun loop() {
        val local = Players.getLocal() ?: return

        if (local.interacting != null) {
            return
        }

        val chicken = NPCs.getNearest {
            (it.name == "Chicken" || it.name == "Goblin")
                    && it.distanceTo(local.worldLocation) < 10
                    && !it.isDead
                    && it.isInteractable()
        } ?: return

        chicken.interact("Attack")
    }

    override fun onStart(vararg startArgs: String) {

    }
}
