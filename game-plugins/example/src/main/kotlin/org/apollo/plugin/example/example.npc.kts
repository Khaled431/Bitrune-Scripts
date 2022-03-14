package org.apollo.plugin.example

import org.apollo.plugin.api.table.DropTable.superiorDropTertiary
import org.apollo.utility.constants.ItemID
import org.apollo.utility.constants.NpcID

npc(NpcID.MOLANISK) {

    drops {
        always {
            add(id = ItemID.BONES)
        }
        main(105) {
            nothing(weight = 5)
            add(weight = 20, block = DropTable.rareDropCondition(1))

            add(id = ItemID.SHARK, amount = 1, weight = 80, description = "For cjay only") {
                return@add player.name == "Cjay0091"
            }
        }
        tertiary {

            add(id = ItemID.SHARK, minAmount = 1, maxAmount = 1, numerator = 1, denominator = 1)

            table("slayer") {
                add(ItemID.ABYSSAL_WHIP,  minAmount = 1, maxAmount = 1, numerator = 1, denominator = 2)
                add(ItemID.WHITE_PARTYHAT,  minAmount = 1, maxAmount = 1, numerator = 1, denominator = 2)
            }

            superiorDropTertiary(this, 50)
        }
    }
}

npc(NpcID.MOLANISK) {

    combat {

    }
}