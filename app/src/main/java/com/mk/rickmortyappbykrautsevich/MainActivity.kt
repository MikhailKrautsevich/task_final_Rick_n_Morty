package com.mk.rickmortyappbykrautsevich

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import android.util.Log
//import android.view.MenuItem
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mk.rickmortyappbykrautsevich.fragments.CharactersListFragment
import com.mk.rickmortyappbykrautsevich.fragments.EpisodeListFragment
import com.mk.rickmortyappbykrautsevich.fragments.LocationListFragment

class MainActivity : AppCompatActivity() {

    private var bottomNavigationView: BottomNavigationView? = null
    private var intTag: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().run {
                replace(
                    R.id.fragment_container,
                    CharactersListFragment.newInstance(),
                    null
                )
                commit()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bottomNavigationView?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_chars -> {
                    setFragment(CharactersListFragment.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.to_locations -> {
                    setFragment(LocationListFragment.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.to_episodes -> {
                    setFragment(EpisodeListFragment.newInstance())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        val tag = intTag.toString()
        intTag++
        supportFragmentManager.beginTransaction().run {
            replace(R.id.fragment_container, fragment, tag)
            addToBackStack(tag).commit()
        }
//        initGoBackArrow()
    }

//    private fun initGoBackArrow() {
//        val transactionCount = supportFragmentManager.backStackEntryCount
//        Log.d("11111", transactionCount.toString())
//        val trCountIsMoreThanZero = transactionCount > -1
//        supportActionBar?.setDisplayHomeAsUpEnabled(trCountIsMoreThanZero)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            supportFragmentManager.popBackStack()
//            initGoBackArrow()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
}