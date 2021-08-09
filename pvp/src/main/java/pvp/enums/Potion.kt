package pvp.enums

import dev.botlin.api.entities.container.Inventory
import net.runelite.api.Skill
import pvp.Pvp.Companion.interact
import dev.botlin.api.skill.Skills
import dev.botlin.api.wrappers.name

enum class Potion(val potionName: String, val consume: () -> Boolean) {
    COMBAT("Super combat potion", { Skills.getBoostedLevel(Skill.STRENGTH) < 116 }),
    RESTORE("Super restore", { Skills.getBoostedLevel(Skill.MAGIC) < 99 }),
    RANGING("Ranging potion", { Skills.getBoostedLevel(Skill.RANGED) < 110 })

    ;

    companion object {
        fun consume() {
            for (potion in values()) {
                if (potion.consume()) {
                    val pot = Inventory.getFirst { it.name.contains(potion.potionName) }
                    if (pot != null) {
                        interact(pot, "Drink", 5)
                        break
                    }
                }
            }
        }
    }
}
