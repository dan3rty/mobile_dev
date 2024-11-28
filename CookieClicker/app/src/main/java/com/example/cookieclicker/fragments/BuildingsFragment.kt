package com.example.cookieclicker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookieclicker.BuildingsAdapter
import com.example.cookieclicker.R
import com.example.cookieclicker.databinding.FragmentBuildingsBinding
import com.example.cookieclicker.GameViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BuildingsFragment : Fragment(R.layout.fragment_buildings) {
    private var _binding: FragmentBuildingsBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBuildingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BuildingsAdapter { building ->
            gameViewModel.buyBuilding(building)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        gameViewModel.clickerUiState
            .onEach {
                gameViewModel.buildings.collect { buildings ->
                    adapter.submitList(buildings)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        gameViewModel.clickerUiState
            .onEach {
                gameViewModel.clickerUiState.collect {
                    gameViewModel.updateBuildingsAvailability()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        gameViewModel.clickerUiState
            .onEach {
                gameViewModel.toastMessage.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
