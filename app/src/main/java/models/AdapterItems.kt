package models

//Lista de item de characters
data class CharactersBanner(
    val characters: List<CharactersItem>
)

//datos que se van a tomar de cada Item
data class CharactersItem(
    val query: String,
    val name: String,
    val imageUrl: String,

    )
data class PlayersItem(
    val uid: Int,
    val name: String,
    val player_icon: String,
    val rank: String,
    val Score: String
    )




