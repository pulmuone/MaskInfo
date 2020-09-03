package com.example.maskinfo

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maskinfo.model.Store
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
        if (map[Manifest.permission.ACCESS_FINE_LOCATION]!!
            && map[Manifest.permission.ACCESS_COARSE_LOCATION]!!) {
            viewModel.fetchStoreInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
            return
        }

         var storeAdapter = StoreAdapter()

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = storeAdapter
        }

        viewModel.apply {
            itemLiveData.observe(this@MainActivity, Observer {
                storeAdapter.updateItems(it)
            })

            loadingLiveData.observe(this@MainActivity, Observer {isLoading ->
                progressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
            })
        }

        if (savedInstanceState == null) {
            viewModel.fetchStoreInfo()
        }
    }
}