package snapelooter

import dev.botlin.api.movement.web.WebBank
import dev.botlin.api.script.BotScript
import dev.botlin.api.script.ScriptMeta
import net.runelite.api.ItemID

@ScriptMeta("snapelooter")
class Snapelooter : BotScript() {
    val snapeId = ItemID.SNAPE_GRASS
    val bankLoc = WebBank.PISCARILIUS

    override fun loop() {

    }

    override fun onStart(vararg startArgs: String) {

    }
}