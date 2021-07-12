package pvp.enums

import net.runelite.api.widgets.WidgetInfo

enum class EquipmentSlot(val widgetInfo: WidgetInfo) {
    MAINHAND(WidgetInfo.EQUIPMENT_WEAPON),
    OFFHAND(WidgetInfo.EQUIPMENT_SHIELD),
    LEGS(WidgetInfo.EQUIPMENT_LEGS),
    CHEST(WidgetInfo.EQUIPMENT_BODY),
    RING(WidgetInfo.EQUIPMENT_RING),
    NECK(WidgetInfo.EQUIPMENT_AMULET),
    CAPE(WidgetInfo.EQUIPMENT_CAPE),
    HEAD(WidgetInfo.EQUIPMENT_HELMET),
    FEET(WidgetInfo.EQUIPMENT_BOOTS),
    AMMO(WidgetInfo.EQUIPMENT_AMMO)
    ;
}