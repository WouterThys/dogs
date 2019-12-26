package com.wouter.dogs.view


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.wouter.dogs.R
import com.wouter.dogs.util.getProgressDrawable
import com.wouter.dogs.util.loadImage
import com.wouter.dogs.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {

    private lateinit var viewModel : DetailViewModel
    private var dogUuid = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
            viewModel.fetch(dogUuid)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogLiveData.observe(this, Observer { dog ->
            dog?.let {
                dogName.text = it.dogBreed
                dogLifespan.text = it.lifeSpan
                dogPurpose.text = it.bredFor
                dogTemperament.text = it.temperament
                dogImage.loadImage(it.imageUrl, getProgressDrawable(dogImage.context))
            }
        })
    }
}
