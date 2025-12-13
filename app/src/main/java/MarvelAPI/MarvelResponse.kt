
//Aqui podremos agrego toda las data class que podemos ir necesitando, para player stats, heroes stats, etc.

//Hero Data

data class Hero(
    val id: String,
    val name: String,
    val imageUrl: String,
    val role: String,
    val attack_type: String?,
    val team: List<String>?,
    val difficulty: String?,
    val bio: String,
    val abilities: List<Abilities>,
    val costumes: List<Skin>
)

data class Abilities(

    val name: String,
    val description: String,
)

data class Skin(

    val name: String,
    val icon: String,


    )



//Player Stats
