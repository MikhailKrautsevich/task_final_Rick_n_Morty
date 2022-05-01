package com.mk.rickmortyappbykrautsevich

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        supportFragmentManager.beginTransaction().run {
            replace(
                R.id.fragment_container,
                CharactersListFragment.newInstance(),
                null
            )
            commit()
        }
    }

    override fun onStart() {
        super.onStart()
        bottomNavigationView?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.to_chars -> {
                    setFragment(CharactersListFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.to_locations -> {
                    setFragment(LocationListFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.to_episodes -> {
                    setFragment(EpisodeListFragment())
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
    }
}