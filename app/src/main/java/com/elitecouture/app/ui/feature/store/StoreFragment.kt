package com.elitecouture.app.ui.feature.store

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.elitecouture.app.R
import com.elitecouture.app.di.ServiceLocator
import com.elitecouture.app.ui.common.EliteCoutureDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

class StoreFragment : Fragment() {
    private val sessionManager by lazy { ServiceLocator.provideSessionManager(requireContext()) }
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var filterSummary: TextView
    private lateinit var filterHint: TextView
    private val storeViewModel: StoreViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feature_store, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    bottomNavigation = view.findViewById(R.id.bottom_navigation)
    bottomNavigation.selectedItemId = R.id.navigation_store
        filterSummary = view.findViewById(R.id.store_filter_title)
        filterHint = view.findViewById(R.id.store_filter_hint)

        filterHint.text = getString(R.string.drawer_subtitle)
        
        // Configurar restricciones para modo invitado
        configureGuestModeRestrictions()
        
        // Configurar interceptor de botón back
        setupBackPressHandler()
        
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_store -> {
                    // Ya estamos en la tienda
                    true
                }
                R.id.navigation_cart -> {
                    if (sessionManager.isGuestMode()) {
                        Toast.makeText(requireContext(), getString(R.string.toast_guest_restricted_feature), Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.toast_cart_under_construction), Toast.LENGTH_SHORT).show()
                        true
                    }
                }
                R.id.navigation_profile -> {
                    if (sessionManager.isGuestMode()) {
                        Toast.makeText(requireContext(), getString(R.string.toast_guest_restricted_feature), Toast.LENGTH_SHORT).show()
                        false
                    } else {
                        findNavController().navigate(R.id.action_storeFragment_to_profileFragment)
                        true
                    }
                }
                else -> false
            }
        }

        storeViewModel.selectedFilter.observe(viewLifecycleOwner) { filter ->
            renderFilter(filter)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Reconfigurar restricciones cada vez que el fragment se vuelve visible
        configureGuestModeRestrictions()
    }
    
    private fun configureGuestModeRestrictions() {
        val isGuest = sessionManager.isGuestMode()
        android.util.Log.d("StoreFragment", "=== CONFIGURANDO RESTRICCIONES ===")
        android.util.Log.d("StoreFragment", "isGuestMode: $isGuest")
        
        // Colores para íconos y texto
        val disabledColor = resources.getColor(R.color.text_disabled_pink, null) // Gris rosado
        val normalColor = resources.getColor(R.color.color_primary, null)
        
        android.util.Log.d("StoreFragment", "Colores - Normal: $normalColor, Disabled: $disabledColor")
        
        if (isGuest) {
            // MODO INVITADO: Aplicar ColorStateList personalizado para íconos y texto
            
            // ColorStateList para íconos (gris rosado cuando no seleccionado)
            val guestColorStateList = android.content.res.ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_checked), // Estado seleccionado
                    intArrayOf() // Estado normal (no seleccionado)
                ),
                intArrayOf(
                    normalColor,    // Ítem seleccionado: color normal (rojo)
                    disabledColor   // Ítems no seleccionados: gris rosado
                )
            )
            
            // Aplicar el mismo ColorStateList a íconos Y texto
            bottomNavigation.itemIconTintList = guestColorStateList
            bottomNavigation.itemTextColor = guestColorStateList
            
            android.util.Log.d("StoreFragment", "Modo invitado - ColorStateList aplicado a íconos y texto")
            
        } else {
            // MODO USUARIO NORMAL: ColorStateList normal para todos
            val normalColorStateList = android.content.res.ColorStateList.valueOf(normalColor)
            bottomNavigation.itemIconTintList = normalColorStateList
            bottomNavigation.itemTextColor = normalColorStateList
            
            android.util.Log.d("StoreFragment", "Usuario normal - ColorStateList aplicado")
        }
    }

    private fun renderFilter(filter: StoreFilter) {
        val genderText = when (filter.gender) {
            Gender.ALL -> getString(R.string.store_filter_gender_all)
            Gender.MEN -> getString(R.string.store_filter_gender_men)
            Gender.WOMEN -> getString(R.string.store_filter_gender_women)
        }

        val categoryText = when (filter.category) {
            Category.ALL -> getString(R.string.store_filter_category_all)
            Category.PANTS -> getString(R.string.store_filter_category_pants)
            Category.JACKETS -> getString(R.string.store_filter_category_jackets)
            Category.COATS -> getString(R.string.store_filter_category_coats)
            Category.SKIRTS -> getString(R.string.store_filter_category_skirts)
            Category.ACCESSORIES -> getString(R.string.store_filter_category_accessories)
        }

        filterSummary.text = getString(R.string.store_filter_summary, genderText, categoryText)
    }

    private fun setupBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                EliteCoutureDialog.create(requireContext())
                    .setTitle(R.string.store_exit_dialog_title)
                    .setMessage(R.string.store_exit_dialog_message)
                    .setPositiveButton(R.string.store_exit_dialog_confirm) {
                        requireActivity().finish()
                    }
                    .setNegativeButton(R.string.store_exit_dialog_cancel)
                    .setCancelable(true)
                    .show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}
