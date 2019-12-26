package com.wouter.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.wouter.dogs.R
import com.wouter.dogs.model.DogBreed
import com.wouter.dogs.util.getProgressDrawable
import com.wouter.dogs.util.loadImage
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter(private val dogsList: ArrayList<DogBreed>) : RecyclerView.Adapter<DogsListAdapter.DogViewHolder>() {

    fun updateDogList(newDogList : List<DogBreed>) {
        dogsList.clear()
        dogsList.addAll(newDogList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun getItemCount(): Int = dogsList.size

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.name.text = dogsList[position].dogBreed
        holder.view.lifespan.text = dogsList[position].lifeSpan
        holder.view.setOnClickListener {
            Navigation.findNavController(it).navigate(ListFragmentDirections.actionDetailFragment())
        }
        holder.view.imageView.loadImage(dogsList[position].imageUrl, getProgressDrawable(holder.view.imageView.context))
    }

    class DogViewHolder(var view : View) : RecyclerView.ViewHolder(view)

}