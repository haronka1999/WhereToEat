package com.e.wheretoeat.main.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.e.wheretoeat.R
import com.e.wheretoeat.databinding.FragmentSplashBinding
import com.e.wheretoeat.main.viewmodels.RestaurantViewModel
import com.e.wheretoeat.main.viewmodels.MainViewModel

/*
Here the restaurants are loaded from the dropbox link.
And also the city list is loaded forfurther usages
 */

class SplashFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var restViewModel: RestaurantViewModel
    private lateinit var sharedPref: SharedPreferences
    private lateinit var binding: FragmentSplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.favoriteRestaurants.value = mutableListOf()
        restViewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)
        sharedPref =
            context?.getSharedPreferences("credentials", Context.MODE_PRIVATE)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        fillAllRestaurants()
        return binding.root
    }


    /*
    Here I decide if the user has already logged in and here I fil the restaurants
    from the dropbox link, which is:
    https://www.dropbox.com/s/94t6su4cimnrdnt/restaurants.json?dl=0
     */
    private fun fillAllRestaurants() {
        mainViewModel.apiRestaurants.observe(viewLifecycleOwner, {
            val credentials = sharedPref.all
            val restaurantsSize = mainViewModel.apiRestaurants.value!!.size
            mainViewModel.cities.add(0, "1:Choose")
            for (i in 1 until restaurantsSize + 1) {
                mainViewModel.cities.add(i, mainViewModel.apiRestaurants.value!![i - 1].city)
            }
            //delete duplicates:
            mainViewModel.cities = mainViewModel.cities.distinct().toMutableList()

            //order by alphabetical order:
            mainViewModel.cities.sort()

            if (credentials.isEmpty()) {
                findNavController().navigate(R.id.action_splashFragment_to_registerFragment2)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            }
        })
        //get all of the restaurants
        mainViewModel.getAllRestaurantsFromDropBox()
    }
}