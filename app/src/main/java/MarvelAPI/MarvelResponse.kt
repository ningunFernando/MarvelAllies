//Aqui podremos agrego toda las data class que podemos ir necesitando, para player stats, heroes stats, etc.

//Hero Data --------------

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

//Player Stats --------------
data class Player(
    val uid: Int,
    val name: String,
    val player: PlayerDetails,
    val overall_stats: Overall_stats,
    val isPrivate: Boolean,
)

data class PlayerDetails(
    val icon: Icon,
    val rank: Rank,
)

data class Icon(
    val player_icon: String,
    val banner: String
)

data class Rank(
    val rank: String,
    val score: String
)

data class Overall_stats(
    val total_play_time: TimePlayedPlayer,
    val total_matches: Int,
    val roles_played: Roles_played,
    val overall_kda: KDA,
    val overall_kd: Float,
    val total_mvps: MVP
)
data class TimePlayedPlayer(
    val playtime: String,

    )
data class KDA(
    val kda: Float,
    )
data class MVP(
    val mvps: Int

)
data class Roles_played(
    val duelist: Role?,
    val strategist: Role?,
    val vanguard: Role?
)

data class Role(
    val total_time_played: TimePlayed?,
    val matches_played: Float?
)
data class TimePlayed(
    val time_played: Float?
)

//Obtener los datos de la leaderboard
data class Leaderboard(
    val players: List<LeaderboardPlayer>
)
//Informacion especifica de cada player
data class LeaderboardPlayer(
    val uid: String,
    val name: String,
    val score: Int,
    val icon: IconPlayer,
    val rank: RankObject
)

//Obtener el icon del player
data class IconPlayer(
    val player_icon: String
)
//Rank es un objeto que contiene diferentes datos, sacamos el objeto
data class RankObject(
    val rank: RankPlayer
)
//obtenemos en que rank esta el jugador
data class RankPlayer(
    val rank: String
)


// Noticias --------------

data class Balance(
    val total_balances: Int,
    val balances: List<FormattedPatch>?
)

data class FormattedPatch(
    val id: String,
    val title: String,
    val date: String,
    val fullContent: String,
    val imagePath: String
)
