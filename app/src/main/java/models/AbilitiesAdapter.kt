package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelallies.R
import Abilities

class AbilitiesAdapter(
    private val abilities: List<Abilities>
) : RecyclerView.Adapter<AbilitiesAdapter.CharacterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CharacterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.abilities_item, parent, false)
        return CharacterViewHolder(view)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val ability = abilities[position]
        holder.bind(ability)
    }

    override fun getItemCount(): Int {
        return abilities.size
    }

    class CharacterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val abilityName: TextView = itemView.findViewById(R.id.Name)
        private val abilityDescription: TextView = itemView.findViewById(R.id.Description)


        fun bind(ability: Abilities) {
            abilityName.text = ability.name
            abilityDescription.text = ability.description
        }
    }
}
