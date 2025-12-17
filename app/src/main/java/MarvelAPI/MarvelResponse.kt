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
    val abilities: List<Abilities>,
    val costumes: List<Skin>
)

data class Abilities(

    val id: Int,
    val icon: String,
    val name: String,
    val type : String,
    val isCollab: Boolean,
    val description: String,
    val transformation_id: String
)

data class Skin(
    val name: String,
    val icon: String
)

//Player Stats
data class Player(
    val uid: Int,
    val name: String,
    val icon: Icon,
    val rank: Rank,
    val overall_stats: List<Overall_stats>,
    val roles_played: List<Roles_played>,
)

data class Icon(
    val player_icon_id: String,
    val player_icon: String,
    val banner: String
)

data class Rank(
    val rank: String,
    val Score: String
)

data class Overall_stats(
    val time_played: Int,
    val total_matches: Int,
    val kda: Int,
    val overall_kd: Int,
    val mvps: Int
)

data class Roles_played(
    val duelist: List<Role>,
    val strategist: List<Role>,
    val vanguard: List<Role>
)

data class Role(
    val time_played: Int,
    val matches_played: Int
)

data class Leaderboard(
    val page: Int,
    val limit: Int,
    val players: List<LeaderboardPlayer>
)
data class LeaderboardPlayer(
    val uid: String,
    val name: String,
    val score: Int,
    val icon: IconResponse,
    val rank: RankResponse
)
data class IconResponse(
    val player_icon: String
)
data class RankResponse(
    val rank: String
)
// Noticias