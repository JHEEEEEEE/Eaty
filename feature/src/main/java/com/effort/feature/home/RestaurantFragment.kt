package com.effort.feature.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.effort.feature.core.base.BaseFragment
import com.effort.feature.core.util.showLoading
import com.effort.feature.databinding.FragmentRestaurantBinding
import com.effort.presentation.UiState
import com.effort.presentation.viewmodel.home.RestaurantViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RestaurantFragment: BaseFragment<FragmentRestaurantBinding>(FragmentRestaurantBinding::inflate) {
    private val viewModel: RestaurantViewModel by viewModels()
    private val args: RestaurantFragmentArgs by navArgs() //SafeArgs로 데이터 받기
    private lateinit var restaurantListAdapter: RestaurantListAdapter

    override fun initView() {
        viewModel.fetchRestaurants(args.query)

        initRecyclerView()
        observeGetRestaurantState()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initRecyclerView() {
        restaurantListAdapter = RestaurantListAdapter()
        binding.recyclerviewRestaurant.apply {
            adapter = restaurantListAdapter
        }
    }

    //받은 category라는 args를 이제 viewmodel로 넘겨주고 받아야됨, 그거를 짜야되는데, observe와 viewmodel.()이런식으로 실행해야함.

    private fun observeGetRestaurantState() {
        val progressIndicator = binding.progressCircular.progressBar

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getRestaurantState.collectLatest { state ->
                    when (state) {
                        is UiState.Loading -> {
                            progressIndicator.showLoading(true)
                            binding.recyclerviewRestaurant.visibility = View.GONE
                        }

                        is UiState.Success -> {
                            progressIndicator.showLoading(false)
                            binding.recyclerviewRestaurant.visibility = View.VISIBLE
                            restaurantListAdapter.submitList(state.data)
                        }

                        is UiState.Error -> {
                            progressIndicator.showLoading(false)
                            binding.recyclerviewRestaurant.visibility = View.GONE
                        }

                        is UiState.Empty -> {
                            progressIndicator.showLoading(false)
                            binding.recyclerviewRestaurant.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }
}