package models

data class CharactersBanner(
    val characters: List<CharactersItem>
)
data class CharactersItem(
    val name: String,
    val imageUrl: String
)



