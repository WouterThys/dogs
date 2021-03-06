package com.wouter.dogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.wouter.dogs.model.DogBreed
import com.wouter.dogs.model.DogDatabase
import com.wouter.dogs.model.DogsApiService
import com.wouter.dogs.util.NotificationsHelper
import com.wouter.dogs.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L // 5 minutes in nano seconds

    private val dogsService = DogsApiService()
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(bypassCache :Boolean = false) {
        if (bypassCache) {
            fetchFromRemote()
        } else {
            val updateTime = prefHelper.getUpdateTime()
            if (updateTime != null && updateTime != 0L && (System.nanoTime() - updateTime) < refreshTime) {
                fetchFromDatabase()
            } else {
                fetchFromRemote()
            }
        }
    }

    private fun fetchFromDatabase() {
        checkCacheDuration()
        loading.value = true
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(), "Dogs from db", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCacheDuration() {
        val cachePreference = prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5*60
            refreshTime = cachePreferenceInt.times(1000 * 1000 * 1000).toLong()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>() {
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(getApplication(), "Dogs from web", Toast.LENGTH_SHORT).show()
                        NotificationsHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }
                })
        )
    }

    private fun dogsRetrieved(dogList: List<DogBreed>) {
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(dogList: List<DogBreed>) {
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*dogList.toTypedArray())
            var i = 0
            while (i < dogList.size) {
                dogList[i].uuid = result[i].toInt()
                ++i
            }
            dogsRetrieved(dogList)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}