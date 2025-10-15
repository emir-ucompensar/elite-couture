package com.elitecouture.app.ui.store
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.elitecouture.app.R

class StoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout de la tienda
        return inflater.inflate(R.layout.fragment_store, container, false)
    }
}
