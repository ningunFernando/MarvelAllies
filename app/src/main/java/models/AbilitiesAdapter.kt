package models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelallies.R
import Abilities

class AbilitiesAdapter(
    private val _abilities: List<Abilities>
) : RecyclerView.Adapter<AbilitiesAdapter.AbilityViewHolder>()
{
    //Se llama cada vez que el recycler necesita crear una nuevo item.
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbilityViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.abilities_item, parent, false)
        return AbilityViewHolder(view)
    }

    //Se llama para asignar los datos a cada vista
    override fun onBindViewHolder(holder: AbilityViewHolder, position: Int)
    {
        val ability = _abilities[position]
        holder.bind(ability)
    }

    //Devuelve el número total de ítems
    override fun getItemCount(): Int
    {
        return _abilities.size
    }

    class AbilityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        private val _abilityName: TextView = itemView.findViewById(R.id.Name)
        private val _abilityDescription: TextView = itemView.findViewById(R.id.Description)

        //bindear el nombre y la description
        fun bind(ability: Abilities)
        {
            _abilityName.text = ability.name
            _abilityDescription.text = ability.description
        }
    }
}
