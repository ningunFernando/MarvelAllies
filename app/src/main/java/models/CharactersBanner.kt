package models

//Lista de item de characters
data class CharactersBanner(
    val characters: List<CharactersItem>
)

//datos que se van a tomar de cada Item
data class CharactersItem(
    val name: String,
    val imageUrl: String

)



