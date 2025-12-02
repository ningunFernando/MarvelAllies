data class MarvelCharacter(
    val id: Int,
    val image: String,
    val name: String,
    val alias: String,
    val role: String,
    val abilities: List<Ability>
)

data class Ability(
    val ability_name: String,
    val cooldown: Int,
    val description: String
)
