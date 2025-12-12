//Aqui podremos agrego toda las data class que podemos ir necesitando, para player stats, heroes stats, etc.

//Hero Data

data class Hero(
    val id: String,
    val name: String,
    val real_name: String?,
    val imageUrl: String,
    val role: String,
    val attack_type: String?,
    val team: List<String>?,
    val difficulty: String?,
    val bio: String?,
    val abilities: List<abilities>
)

data class abilities(

    val id: Int,
    val icon: String,
    val name: String,
    val type : String,
    val isCollab: Boolean,
    val description: String,
    val transformation_id: String
)


//Player Stats
