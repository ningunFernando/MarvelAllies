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

//Datos que queremos de cada jugador en el adapter
data class PlayersItem(
    val query: String,
    val name: String,
    val player_icon: String,
    val rank: String,
    val score: Int
    )

data class NewsItem(
    val title: String,
    val description: String,
    val imageUrl: String
)




