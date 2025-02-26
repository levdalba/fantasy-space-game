package com.motycka.edu.game.character

data class Character(
    val id: Long = 0,
    val accountId: Long,
    val name: String,
    val health: Int,
    val attackPower: Int,
    val stamina: Int? = null,
    val defensePower: Int? = null,
    val mana: Int? = null,
    val healingPower: Int? = null,
    val characterClass: CharacterClass,
    val level: Int = 1,
    val experience: Int = 0,
    val shouldLevelUp: Boolean = false
) {
    enum class CharacterClass {
        WARRIOR, SORCERER
    }

    init {
        validate()
    }

    private fun validate() {
        require(health > 0) { "Health must be greater than 0" }
        require(attackPower >= 0) { "Attack power must be non-negative" }
        require(level >= 1) { "Level must be at least 1" }
        require(experience >= 0) { "Experience must be non-negative" }

        when (characterClass) {
            CharacterClass.WARRIOR -> {
                requireNotNull(stamina) { "Stamina is required for Warriors" }
                requireNotNull(defensePower) { "Defense power is required for Warriors" }
                require(mana == null && healingPower == null) { "Warriors cannot have mana or healing power" }
                require(stamina > 0 && defensePower >= 0) { "Invalid Warrior attributes" }
            }
            CharacterClass.SORCERER -> {
                requireNotNull(mana) { "Mana is required for Sorcerers" }
                requireNotNull(healingPower) { "Healing power is required for Sorcerers" }
                require(stamina == null && defensePower == null) { "Sorcerers cannot have stamina or defense power" }
                require(mana > 0 && healingPower >= 0) { "Invalid Sorcerer attributes" }
            }
        }

        val totalPoints = health + attackPower + (stamina ?: 0) + (defensePower ?: 0) + (mana ?: 0) + (healingPower ?: 0)
        require(totalPoints <= 200) { "Total points cannot exceed 200" }
    }

    fun levelUp(): Character {
        return copy(level = level + 1, experience = 0, shouldLevelUp = false)
    }
}
