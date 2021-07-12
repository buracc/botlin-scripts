package pvp.enums;

public enum GearItem {

    // RANGE_WEP
    RUNE_CBOW("Rune crossbow", false, -1, EquipmentSlot.MAINHAND, 5, GearType.RANGE_WEP),
    ARMA_CBOW("Armadyl crossbow", false, -1, EquipmentSlot.MAINHAND, 5, GearType.RANGE_WEP),
    HEAVY_BALLISTA("Heavy ballista", true, -1, EquipmentSlot.MAINHAND, 7, GearType.RANGE_WEP),
    MORRIGANS_JAVELIN("Morrigan's javelin", false, -1, EquipmentSlot.MAINHAND, 6, GearType.RANGE_WEP),
    DARK_BOW("Dark bow", true, 50, EquipmentSlot.MAINHAND, 8, GearType.RANGE_WEP),

    // MAGE_WEP
    KODAI_WAND("Kodai wand", false, -1, EquipmentSlot.MAINHAND, 5, GearType.MAGE_WEP),
    ZURIEL_STAFF("Zuriel's staff", false, -1, EquipmentSlot.MAINHAND, 5, GearType.MAGE_WEP),
    STAFF_OF_THE_DEAD("Staff of the dead", false, -1, EquipmentSlot.MAINHAND, 5, GearType.MAGE_WEP),
    AHRIM_STAFF("Ahrim's staff", false, -1, EquipmentSlot.MAINHAND, 5, GearType.MAGE_WEP),
    ANCIENT_STAFF("Ancient staff", false, -1, EquipmentSlot.MAINHAND, 5, GearType.MAGE_WEP),

    // MELEE_WEP
    DRAGON_DAGGER("Dragon dagger", false, 25, EquipmentSlot.MAINHAND, 4, GearType.MELEE_WEP),
    ABYSSAL_WHIP("Abyssal whip", false, -1, EquipmentSlot.MAINHAND, 4, GearType.MELEE_WEP),
    ELDER_MAUL("Elder maul", true, -1, EquipmentSlot.MAINHAND, 6, GearType.MELEE_WEP),
    GHRAZI_RAPIER("Ghrazi rapier", false, -1, EquipmentSlot.MAINHAND, 4, GearType.MELEE_WEP),
    VESTA_LONGSWORD("Vesta's longsword", false, 25, EquipmentSlot.MAINHAND, 5, GearType.MELEE_WEP),
    STATIUS_WARHAMMER("Statius's warhammer", false, 35, EquipmentSlot.MAINHAND, 5, GearType.MELEE_WEP),
    DRAGON_CLAWS("Dragon claws", true, 50, EquipmentSlot.MAINHAND, 4, GearType.MELEE_WEP),
    ARMADYL_GODSWORD("Armadyl godsword", true, 50, EquipmentSlot.MAINHAND, 6, GearType.MELEE_WEP),

    // AMULET
    AMULET_OF_FURY("Amulet of fury", EquipmentSlot.NECK, GearType.RANGE_GEAR, GearType.MELEE_GEAR),
    AMULET_OF_GLORY("Amulet of glory", EquipmentSlot.NECK, GearType.RANGE_GEAR, GearType.MELEE_GEAR),
    OCCULT_NECKLACE("Occult necklace", EquipmentSlot.NECK, GearType.MAGE_GEAR),

    // RING
    SEERS_RING_I("Seers ring (i)", EquipmentSlot.RING, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),
    BERSERKER_RING("Berserker ring", EquipmentSlot.RING, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),

    // CAPE
    INFERNAL_CAPE("Infernal cape", EquipmentSlot.CAPE, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),
    IMBUED_GUTHIX_CAPE("Imbued guthix cape", EquipmentSlot.CAPE, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),

    // OFF_HAND
    BLESSED_SPIRIT_SHIELD("Blessed spirit shield", EquipmentSlot.OFFHAND, GearType.RANGE_GEAR, GearType.MAGE_GEAR),
    SPIRIT_SHIELD("Spirit shield", EquipmentSlot.OFFHAND, GearType.RANGE_GEAR, GearType.MAGE_GEAR),
    DRAGON_DEFENDER("Dragon defender", EquipmentSlot.OFFHAND, GearType.MELEE_GEAR),
    MAGES_BOOK("Mage's book", EquipmentSlot.OFFHAND, GearType.MAGE_GEAR),

    // HELM
    VERAC_HELM("Verac's helm", EquipmentSlot.HEAD, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),
    DHAROK_HELM("Dharok's helm", EquipmentSlot.HEAD, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),
    GUTHAN_HELM("Guthan's helm", EquipmentSlot.HEAD, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),
    TORAG_HELM("Torag's helm", EquipmentSlot.HEAD, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),

    // CHEST
    KARIL_TOP("Karil's leathertop", EquipmentSlot.CHEST, GearType.RANGE_GEAR, GearType.MELEE_GEAR),
    BLACK_DHIDE_BODY("Black d'hide body", EquipmentSlot.CHEST, GearType.MELEE_GEAR, GearType.RANGE_GEAR),
    AHRIM_TOP("Ahrim's robetop", EquipmentSlot.CHEST, GearType.MAGE_GEAR),
    MYSTIC_TOP("Mystic robe top", EquipmentSlot.CHEST, GearType.MAGE_GEAR),

    // LEGS
    BANDOS_TASSETS("Bandos tassets", EquipmentSlot.LEGS, GearType.RANGE_GEAR, GearType.MELEE_GEAR),
    DHAROK_LEGS("Dharok's platelegs", EquipmentSlot.LEGS, GearType.RANGE_GEAR, GearType.MELEE_GEAR),
    TORAG_LEGS("Torag's platelegs", EquipmentSlot.LEGS, GearType.RANGE_GEAR, GearType.MELEE_GEAR),
    VERAC_SKIRT("Verac's plateskirt", EquipmentSlot.LEGS, GearType.RANGE_GEAR, GearType.MELEE_GEAR),
    RUNE_PLATELEGS("Rune platelegs", EquipmentSlot.LEGS, GearType.RANGE_GEAR, GearType.MELEE_GEAR),
    AHRIM_SKIRT("Ahrim's robeskirt", EquipmentSlot.LEGS, GearType.MAGE_GEAR),
    MYSTIC_SKIRT("Mystic robe bottom", EquipmentSlot.LEGS, GearType.MAGE_GEAR),

    // BOOTS
    ETERNAL_BOOTS("Eternal boots", EquipmentSlot.FEET, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),
    CLIMBING_BOOTS("Climbing boots", EquipmentSlot.FEET, GearType.RANGE_GEAR, GearType.MELEE_GEAR, GearType.MAGE_GEAR),

    // AMMO
    DRAGON_JAVELIN("Dragon javelin", EquipmentSlot.AMMO, GearType.RANGE_GEAR),
    DIAMOND_BOLTS_E("Diamond bolts (e)", EquipmentSlot.AMMO, GearType.RANGE_GEAR);

    private String name;
    private boolean twoHanded;
    private int specBar;
    private EquipmentSlot slot;
    private int speed;
    private GearType[] types;

    GearItem(String name, EquipmentSlot slot, GearType... types) {
        this.name = name;
        this.slot = slot;
        this.types = types;
    }

    GearItem(String name, boolean twoHanded, int specBar, EquipmentSlot slot, int speed, GearType... types) {
        this.name = name;
        this.twoHanded = twoHanded;
        this.specBar = specBar;
        this.slot = slot;
        this.speed = speed;
        this.types = types;
    }

    public String getName() {
        return name;
    }

    public boolean isTwoHanded() {
        return twoHanded;
    }

    public int getSpecBar() {
        return specBar;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public GearType[] getTypes() {
        return types;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean hasSpec() {
        return specBar > -1;
    }

    public boolean isWeapon() {
        return slot == EquipmentSlot.MAINHAND;
    }

    public static GearItem getItemByName(String name) {
        for (GearItem i : GearItem.values()) {
            if (i.getName().equals(name)) {
                return i;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
