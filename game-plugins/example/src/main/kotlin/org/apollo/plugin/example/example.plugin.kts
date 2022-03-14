package org.apollo.plugin.example

import org.apollo.plugin.api.entity.mob.player.DialogueOptionApi
import org.apollo.plugin.api.entity.mob.player.PrivilegeApi
import org.apollo.plugin.api.entity.mob.player.SkillDialogueTypeApi
import org.apollo.plugin.api.entity.mob.player.SkillType
import org.apollo.plugin.api.entity.mob.player.dialogue.DialogueAnimationApi
import org.apollo.plugin.api.entity.obj.ObjectApi
import org.apollo.utility.constants.ItemID
import org.apollo.utility.constants.NpcID
import org.apollo.utility.constants.ObjectID
import org.apollo.utility.constants.generated.IfConstants
import org.apollo.utility.constants.generated.NightmareComponents
import java.util.*

onLogin { tick ->
    messageGame("Hello, we logged in $tick")
    dialogue {
        player("My name is cjay")
        npc(NpcID.ORACLE, "Hello there cjay", DialogueAnimationApi.NONE)
        itemSingle(ItemID.ABYSSAL_WHIP, "You given the whip.")
        itemDouble(ItemID.ABYSSAL_WHIP, ItemID.ABYSSAL_TENTACLE, "And the oracle makes it an abyssal tentacle.")
        message("You lost the whip sadly.")
        options {
            option("Option one") {
                player("Option one")
            }
            option("Option two") {
                player("Option two")
                final_action {
                    player("Option two action")
                    messageGame("We got to option two, but stopped dialogues")
                    playAnimation(424)
                }
            }
        }
    }

    dialogue {
        val options = options("title", "my name", "is khaled", "i select")
        when (options) {
            DialogueOptionApi.FIRST -> message("Hi")
        }
        message("You picked option $options")
    }
    messageGame("DSKLFJSLDKKJDSFLLKSJDFLKDSF 22")
}

onLogout { tick ->
    println("Logged out on $tick")
}

onCommand(name = "test", PrivilegeApi.OWNER) { args ->
    val attack = this[SkillType.ATTACK]
    val level = attack.level
    val base = attack.baseLevel
    val exp = attack.exp

    attack.exp += 10 // Adds 10 exp (with multiplier)
    attack.level += 10 // Boosts attack by ten levels

    val level2 = getLevel(SkillType.ATTACK)
    val base2 = getBaseLevel(SkillType.ATTACK)
    addXp(SkillType.ATTACK, 10.0)

    val index = args.selectIntOptional(0, 0)
    dialogue {
        val result = skill(SkillDialogueTypeApi.MAKE, ItemID.CHOCOLATE_BAR)
        if (result == null) {
            println("Null, no option selected")
        } else {
            println("Selected action: ${result.id} - ${result.amount}")
        }
    }
}

onCommand(name = "transaction", PrivilegeApi.OWNER) { args ->
    val autoTransaction = inventory.transaction {
        val moved = moveSlot(id = ItemID.ABYSSAL_WHIP, amount = 1, to = equipment, slot = 3, auto = false)

        add(id = ItemID.ABYSSAL_WHIP, amount = 1)
        add(id = ItemID.ABYSSAL_WHIP, amount = 20)
    }

    val noAutoFail = inventory.transaction {
        val removed = remove(id = ItemID.ABYSSAL_WHIP, amount = 19, auto = false) // This will try to remove 19 whips
        if (removed == 0) {
            fail() // If we have none, fail
            return@transaction
        }
        add(id = ItemID.ABYSSAL_TENTACLE, amount = removed) // And adds however many we removed
    }

    messageGame("$autoTransaction $noAutoFail")
}

onInterface(IfConstants.NIGHTMARE_PROPERTY) {
    onOpen { builder ->
        println("OPEN")
    }

    onClose {
        println("CLOSE")
    }

    onButton(NightmareComponents.PILLARS_COMPONENT_KEY) { builder, slot, item ->

    }

    onInterface(
        NightmareComponents.PILLARS_COMPONENT_KEY,
        IfConstants.NIGHTMARE_PROPERTY,
        NightmareComponents.PORTAL_COMPONENT_KEY
    ) { builder, fromSlot, fromItem, toSlot, toItem ->

    }

    onGroundItem(NightmareComponents.PILLARS_COMPONENT_KEY, ItemID.ABYSSAL_WHIP) {
        onOp { builder, ground, item, slot ->

        }
    }

    onNpc(NightmareComponents.PILLARS_COMPONENT_KEY, NpcID.MOLANISK) {
        onOp { builder, npc, item, slot ->

        }
    }

    onObj(NightmareComponents.PILLARS_COMPONENT_KEY, ObjectID.NOTICE_BOARD_32655) {
        onOp { builder, obj, slot, item ->

        }
    }

    onObj(NightmareComponents.PILLARS_COMPONENT_KEY) {
        onOp { builder, obj, slot, item ->

        }


    }

    onPlayer(NightmareComponents.PILLARS_COMPONENT_KEY) {
        onOp { builder, other, slot, item ->

        }
    }
}

onNpc(InteractionOrder.FIRST, 6764) {
    onAp(5)

    onOp { npc ->
        messageGame("We talked to the base npc")
    }

    onInventoryItem {
        onOp { npc, item ->
            messageGame("Used ${item.name} on Khaled's varbit")
        }
    }
}


onNpc("Talk-To", NpcID.CAPTAIN_KHALED_6972) {
    onAp(5)

    onOp { npc ->
        messageGame("We talked to the specific npc")
    }

    onInventoryItem {
        onOp { npc, item ->
            messageGame("Used ${item.name} on Khaled")
        }
    }

    onInventoryItem(ItemID.ABYSSAL_WHIP) {
        onOp { npc, item ->
            messageGame("Used whip on Khaled")
        }
    }
}

onObj("Read", ObjectID.NOTICE_BOARD_32655) {
    onAp { obj ->
        if (!obj.position.isWithinDistance(position, 5)) {
            dialogue { message("You are too far to interact with the notice board.") }
            return@onAp OptionalInt.empty()
        }
        return@onAp OptionalInt.of(3)
    }

    onOp { obj ->
        messageGame("Hello there, we clicked an object")
        dialogue {
            message("Yeet")
        }

        ObjectApi(ObjectID.CRATE, position, 10, 0)
    }

    onInventoryItem {
        onOp { obj, item ->

        }
    }

    onInventoryItem(ItemID.ABYSSAL_WHIP, ItemID.GOLDEN_TINDERBOX) {
        onOp { obj, item ->

        }
    }
}

onGroundItem("Light", ItemID.LOGS) {
    onAp(5)

    onOp {
        messageGame("We lit out here.")
    }

    onInventoryItem {
        onOp { ground, item ->
            messageGame("We are lighting the logs now")
        }
    }
}

onContainerItem(ItemID.TOXIC_BLOWPIPE) {

    onClickInventory("Unload") { item ->
        item.id = ItemID.ASHES
    }

    onClickEquipment("Remove") { item ->
        messageGame("We blocked the remove")
    }

    onClickBoth("Check") { item ->
        messageGame("We checked it")
    }

    onInventoryItem { primary, secondary ->
        messageGame("We are NOT lit rn.")
    }

    onInventoryItem(ItemID.TINDERBOX, ItemID.GOLDEN_TINDERBOX) { primary, secondary ->
        messageGame("We lit out here.")
    }

    onObj {
        onOp { obj, item ->

        }
    }

    onGroundItem(ItemID.LOGS) {
        this onAp 5

        onOp { ground, item ->
            messageGame("We touched the ${ground.item.name}.")
        }
    }

    onGroundItem {
        onOp { ground, item ->

        }
    }
}