package pvp.enums

import dev.botlin.api.entities.container.Equipment
import dev.botlin.api.entities.container.Inventory
import dev.botlin.api.wrappers.name
import net.runelite.api.Item
import pvp.Pvp.Companion.interact

enum class BIS(vararg val gear: GearItem) {
    RANGED_MAINHAND(GearItem.MORRIGANS_JAVELIN, GearItem.ARMA_CBOW, GearItem.RUNE_CBOW, GearItem.HEAVY_BALLISTA),
    MELEE_MAINHAND(GearItem.VESTA_LONGSWORD, GearItem.GHRAZI_RAPIER, GearItem.ABYSSAL_WHIP),
    MAGE_MAINHAND(GearItem.KODAI_WAND, GearItem.ZURIEL_STAFF, GearItem.STAFF_OF_THE_DEAD, GearItem.AHRIM_STAFF),
    SPEC_MAINHAND(
        GearItem.VESTA_LONGSWORD,
        GearItem.DRAGON_CLAWS,
        GearItem.ARMADYL_GODSWORD,
        GearItem.STATIUS_WARHAMMER,
        GearItem.DRAGON_DAGGER
    ),

    MAIN_OFFHAND(GearItem.BLESSED_SPIRIT_SHIELD, GearItem.SPIRIT_SHIELD),

    MAIN_CHEST(GearItem.KARIL_TOP, GearItem.BLACK_DHIDE_BODY),
    MAGE_CHEST(GearItem.AHRIM_TOP, GearItem.MYSTIC_TOP),

    MAIN_LEGS(
        GearItem.BANDOS_TASSETS,
        GearItem.DHAROK_LEGS,
        GearItem.TORAG_LEGS,
        GearItem.VERAC_SKIRT,
        GearItem.RUNE_PLATELEGS
    ),
    MAGE_LEGS(GearItem.AHRIM_SKIRT, GearItem.MYSTIC_SKIRT),
    BOOTS(GearItem.ETERNAL_BOOTS, GearItem.CLIMBING_BOOTS),
    AMULET(GearItem.AMULET_OF_FURY, GearItem.AMULET_OF_GLORY),
    CAPE(GearItem.INFERNAL_CAPE, GearItem.IMBUED_GUTHIX_CAPE),
    RING(GearItem.SEERS_RING_I, GearItem.BERSERKER_RING),
    AMMO(GearItem.DIAMOND_BOLTS_E, GearItem.DRAGON_JAVELIN)

    ;

    companion object {
        fun cycle(): MutableList<Array<out GearItem>> {
            val bisItems = mutableListOf<Array<out GearItem>>()
            for (bis in values()) {
                var lowest = 69

                for (i in bis.gear.indices) {
                    var item = Inventory.getFirst(bis.gear[i].getName())

                    if (item == null) {
                        item = getFromEquipment(bis.gear[i].getName())
                    }

                    if (item == null) {
                        continue
                    }

                    if (i < lowest) {
                        lowest = i
                    }
                }

                if (lowest == 69) {
                    continue
                }

                val junk = bis.gear.copyOfRange(lowest + 1, bis.gear.size)
                val bisLmsItems = bis.gear.copyOfRange(0, lowest)
                bisItems.add(bisLmsItems)

                loop@
                for (junkItem in junk) {
                    for (equipped in Equipment.getAll()) {
                        if (equipped.name == junkItem.getName()) {
                            for (lmsItem in bis.gear) {
                                val item = Inventory.getFirst(lmsItem.getName())
                                if (item != null && lmsItem.slot.widgetInfo == equipped.widgetInfo) {
                                    interact(item, { it == "Wield" || it == "Wear" || it == "Equip" }, 5)
                                    break@loop
                                }
                            }
                        }
                    }

                    for (item in Inventory.getAll()) {
                        if (item.name == junkItem.getName()) {
                            interact(item, "Drop", 5)
                        }
                    }
                }
            }

            return bisItems
        }

        private fun getFromEquipment(name: String): Item? {
            return Equipment.getAll().firstOrNull { it.name == name }
        }
    }
}
