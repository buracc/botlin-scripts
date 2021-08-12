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

    override fun loop(): Int {
        val local = Players.getLocal() ?: return 1000

        if (local.interacting != null) {
            return 1000
        }

        val chicken = NPCs.getNearest {
            (it.name == "Chicken" || it.name == "Goblin")
                    && it.distanceTo(local.worldLocation) < 10
                    && !it.isDead
                    && it.isInteractable()
        } ?: return 1000

        chicken.interact("Attack")
        return 1000
    }

    override fun onStart(vararg startArgs: String) {

    }
}
