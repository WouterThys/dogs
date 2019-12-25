package com.wouter.dogs.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wouter.dogs.model.DogBreed

class DetailViewModel : ViewModel() {

    val dogLiveData = MutableLiveData<DogBreed>()

    fun fetch() {
        val dog = DogBreed("3", "Fish dog", "10 years", "group", "", "Boring", "")

        dogLiveData.value = dog
    }

}