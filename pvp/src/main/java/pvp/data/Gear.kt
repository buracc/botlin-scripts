package pvp.data

import dev.botlin.api.game.Combat
import dev.botlin.api.provider.container.Equipment
import dev.botlin.api.provider.container.Inventory
import dev.botlin.api.widget.Prayer
import dev.botlin.api.wrappers.name
import pvp.Pvp.Companion.interact
import pvp.enums.EquipmentSlot
import pvp.enums.GearItem
import pvp.enums.GearType

class Gear(var gears: MutableList<GearItem>, val offensivePrayer: Prayer) {

    fun switchGear(target: Enemy?) {
        for (i in Inventory.getAll()) {
            for (s in gears) {
                if (!enoughSpec(target) && s.hasSpec() && s.isWeapon && haveSeparateSpecWeapon()) {
                    continue
                }

                if (enoughSpec(target) && !s.hasSpec() && s.isWeapon && haveSeparateSpecWeapon()) {
                    continue
                }

                if (getWeapon() != null && getWeapon()!!.isTwoHanded && s.slot == EquipmentSlot.OFFHAND) {
                    continue
                }

                if (enoughSpec(target) && getSpecWeapon()!!.isTwoHanded && s.slot == EquipmentSlot.OFFHAND) {
                    continue
                }

                if (i.name == s.getName()) {
                    interact(i, 1, 5)
                }
            }
        }
    }

    fun allEquipped(target: Enemy): Boolean {
        return !anyUnequipped(target)
    }

    fun anyUnequipped(target: Enemy?): Boolean {
        for (item in gears) {
            if (!enoughSpec(target) && item.hasSpec() && item.isWeapon) {
                continue
            }

            if (enoughSpec(target) && !item.hasSpec() && item.isWeapon) {
                continue
            }

            if (getWeapon() != null && getWeapon()!!.isTwoHanded && item.slot == EquipmentSlot.OFFHAND) {
                continue
            }

            if (enoughSpec(target) && getSpecWeapon()!!.isTwoHanded && item.slot == EquipmentSlot.OFFHAND) {
                continue
            }

            if (Inventory.getFirst(item.getName()) != null) {
                return true
            }
        }

        return false
    }

    fun hasItem(name: String): Boolean {
        return gears.any { it.getName() == name }
    }

    fun addItem(item: GearItem) {
        gears.add(item)
    }

    fun removeItem(name: String) {
        gears.removeIf { it.getName() == name }
    }

    fun getSpecWeapon(): GearItem? {
        return gears.firstOrNull { it.hasSpec() && it.isWeapon }
    }

    fun getWeapon(): GearItem? {
        return gears.firstOrNull { !it.hasSpec() && it.isWeapon }
    }

    fun haveSeparateSpecWeapon(): Boolean {
        var amount = 0

        for (i in GearItem.values()) {
            for (item in Inventory.getAll()) {
                if (item.name == i.getName()) {
                    for (g in i.types) {
                        if (g == GearType.MELEE_WEP) {
                            amount++
                        }
                    }
                }
            }

            for (item in Equipment.getAll()) {
                if (item.name == i.getName()) {
                    for (g in i.types) {
                        if (g === GearType.MELEE_WEP) {
                            amount++
                        }
                    }
                }
            }
        }

        return amount == 2
    }

    fun enoughSpec(target: Enemy?): Boolean {
        if (target == null) {
            return false
        }

        return getSpecWeapon() != null && target.health > 0 && target.health <= 70 && Combat.getSpecEnergy() >= getSpecWeapon()!!.specBar
    }

    fun clear() {
        gears.clear()
    }
}
