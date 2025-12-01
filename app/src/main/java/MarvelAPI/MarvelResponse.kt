package MarvelAPI

data class MarvelResponse(
    val status : String,
    val data: MarvelData
)

data class MarvelData(
    val heroes : List<MarvelCharacter>
)

data class MarvelCharacter(
    val id : Int,
    val image : String,
    val name : String,
    val alias : String,
    val role : String,
    val abilities : Abilities
)

data class Abilities(
    val ability_name : String,
    val cooldown : Int,
    val description : String
)
