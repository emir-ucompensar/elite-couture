package com.elitecouture.app.ui.feature.store

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elitecouture.app.R
import com.elitecouture.app.data.repository.StoreRepository
import com.elitecouture.app.domain.model.Store
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

/**
 * Fragment que muestra un mapa con las tiendas físicas de Elite Couture.
 * 
 * Soporta dos proveedores de mapas:
 * - OSMDroid (OpenStreetMap) - Por defecto, no requiere API key
 * - Google Maps - Opcional, requiere configuración de API key
 * 
 * Para cambiar el proveedor, modifica la constante USE_GOOGLE_MAPS
 */
class StoreMapFragment : Fragment() {
    
    companion object {
        /**
         * Define qué proveedor de mapas usar:
         * - false: OSMDroid (OpenStreetMap) - Recomendado, sin dependencias de Google
         * - true: Google Maps - Requiere API key configurada
         */
        private const val USE_GOOGLE_MAPS = false
    }
    
    // Variables para OSMDroid
    private var osmMapView: MapView? = null
    private var btnCenterNearest: View? = null
    private var btnZoomIn: View? = null
    private var btnZoomOut: View? = null
    
    // Variables comunes
    private lateinit var toolbar: com.google.android.material.appbar.MaterialToolbar
    private lateinit var storeRecyclerView: RecyclerView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var storeAdapter: StoreAdapter
    
    private var stores: List<Store> = emptyList()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Elegir el layout según el proveedor de mapas
        val layoutId = if (USE_GOOGLE_MAPS) {
            R.layout.fragment_store_map_google
        } else {
            // Configurar OSMDroid
            Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
            )
            R.layout.fragment_store_map_osm
        }
        
        return inflater.inflate(layoutId, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupBottomSheet(view)

        // Cargar tiendas y luego inicializar mapa y recycler
        loadStoresAndInitUI(view)
    }

    private fun loadStoresAndInitUI(view: View) {
        // Usar lifecycleScope para lanzar corutina en el fragmento
        viewLifecycleOwner.lifecycleScope.launch {
            stores = StoreRepository.getStores()
            setupMap(view)
            setupRecyclerView()
        }
    }
    
    private fun initializeViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        storeRecyclerView = view.findViewById(R.id.store_recycler_view)
        btnCenterNearest = view.findViewById(R.id.btn_center_nearest_store)
        btnZoomIn = view.findViewById(R.id.btn_zoom_in)
        btnZoomOut = view.findViewById(R.id.btn_zoom_out)

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        btnCenterNearest?.setOnClickListener {
            centerOnNearestStore()
        }
        btnZoomIn?.setOnClickListener {
            osmMapView?.controller?.zoomIn()
        }
        btnZoomOut?.setOnClickListener {
            osmMapView?.controller?.zoomOut()
        }
    }

    private fun centerOnNearestStore() {
        // Obtener ubicación actual (mock: centro de Bogotá si no hay permisos)
        // En producción, usar FusedLocationProviderClient
        val userLat = 4.648283
        val userLng = -74.247894
        val userPoint = GeoPoint(userLat, userLng)

        // Buscar tienda más cercana
        val nearest = stores.minByOrNull {
            val dLat = it.latitude - userLat
            val dLng = it.longitude - userLng
            dLat * dLat + dLng * dLng
        }
        if (nearest != null) {
            osmMapView?.controller?.animateTo(GeoPoint(nearest.latitude, nearest.longitude))
            osmMapView?.controller?.setZoom(16.0)
        }
    }
    
    private fun setupMap(view: View) {
        if (USE_GOOGLE_MAPS) {
            setupGoogleMap()
        } else {
            setupOSMMap(view)
        }
    }
    
    /**
     * Configura OpenStreetMap (OSMDroid)
     */
    private fun setupOSMMap(view: View) {
        osmMapView = view.findViewById(R.id.osm_map_view)

        viewLifecycleOwner.lifecycleScope.launch {
            osmMapView?.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                // Configurar zoom inicial y centro
                val centerLocation = StoreRepository.getCenterLocation()
                controller.setZoom(12.0)
                controller.setCenter(GeoPoint(centerLocation.first, centerLocation.second))

                // Agregar marcadores
                addOSMMarkers()
            }
        }
    }
    
    /**
     * Agrega marcadores en OSMDroid
     */
    private fun addOSMMarkers() {
    stores.forEach { store ->
            val marker = Marker(osmMapView)
            marker.position = GeoPoint(store.latitude, store.longitude)
            marker.title = store.name
            marker.snippet = "${store.address}\n${store.phone}\n${store.hours}"
            
            // Personalizar el ícono del marcador
            marker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.store_marker)
            
            // Al hacer click en el marcador
            marker.setOnMarkerClickListener { clickedMarker, _ ->
                // Centrar el mapa en el marcador
                osmMapView?.controller?.animateTo(clickedMarker.position)
                
                // Mostrar el InfoWindow
                clickedMarker.showInfoWindow()
                
                true
            }
            
            osmMapView?.overlays?.add(marker)
        }
        
        osmMapView?.invalidate()
    }
    
    /**
     * Configura Google Maps (requiere API key)
     */
    private fun setupGoogleMap() {
        // TODO: Implementar cuando se tenga API key configurada
        // Por ahora, esta funcionalidad está deshabilitada
    }
    
    private fun setupBottomSheet(view: View) {
        val bottomSheet = view.findViewById<View>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        
        // Configurar el comportamiento del BottomSheet
        bottomSheetBehavior.apply {
            peekHeight = 350 // Altura cuando está colapsado
            isHideable = false
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }
    
    private fun setupRecyclerView() {
        storeAdapter = StoreAdapter(
            stores = stores,
            onDirectionsClick = { store ->
                openDirections(store)
            },
            onStoreClick = { store ->
                // Centrar el mapa en la tienda seleccionada
                if (USE_GOOGLE_MAPS) {
                    // TODO: Centrar Google Map
                } else {
                    osmMapView?.controller?.animateTo(
                        GeoPoint(store.latitude, store.longitude)
                    )
                    osmMapView?.controller?.setZoom(16.0)
                }

                // Colapsar el BottomSheet
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        )

        storeRecyclerView.apply {
            adapter = storeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    
    private fun openDirections(store: Store) {
        val uri = Uri.parse("google.navigation:q=${store.latitude},${store.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            // Si Google Maps no está instalado, abrir en el navegador
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${store.latitude},${store.longitude}")
            )
            startActivity(browserIntent)
        }
    }
    
    // Lifecycle methods para OSMDroid
    override fun onResume() {
        super.onResume()
        osmMapView?.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        osmMapView?.onPause()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        osmMapView?.onDetach()
        osmMapView = null
    }
}
