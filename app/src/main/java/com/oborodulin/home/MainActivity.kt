package com.oborodulin.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.oborodulin.home.controller.payer.PayerFragment
import com.oborodulin.home.controller.payer.PayerListFragment
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), PayerListFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)
        //val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        /*
        val provider: ViewModelProvider = ViewModelProvider(this)
        val homeViewModel = provider.get(HomeViewModel::class.java)
        Log.d(TAG, "Got a HomeViewModel: $homeViewModel")
        */
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = PayerListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        //savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    override fun onPayerEditClick(payerId: UUID) {
        Log.d(TAG, "MainActivity.onPayerEditClick: $payerId")
        val fragment = PayerFragment.newInstance(payerId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onPayerListClick(payerId: UUID) {
        Log.d(TAG, "MainActivity.onPayerListClick: $payerId")
        /* val fragment = ReceiptListFragment.newInstance(payerId)
         supportFragmentManager
             .beginTransaction()
             .replace(R.id.fragment_container, fragment)
             .addToBackStack(null)
             .commit()
         */
    }
}