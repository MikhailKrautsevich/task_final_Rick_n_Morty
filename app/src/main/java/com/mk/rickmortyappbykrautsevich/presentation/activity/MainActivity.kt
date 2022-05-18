package com.mk.rickmortyappbykrautsevich.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mk.rickmortyappbykrautsevich.R
import com.mk.rickmortyappbykrautsevich.data.app.App
import com.mk.rickmortyappbykrautsevich.presentation.fragments.CharactersListFragment
import com.mk.rickmortyappbykrautsevich.presentation.fragments.EpisodeListFragment
import com.mk.rickmortyappbykrautsevich.presentation.fragments.LocationListFragment

class MainActivity : AppCompatActivity(), HasBottomNavs, FragmentHost {

    private var bottomNavigationView: BottomNavigationView? = null
    private var intTag: Int = 0

    private var navBtnToCharacters: BottomNavigationItemView? = null
    private var navBtnToEpisodes: BottomNavigationItemView? = null
    private var navBtnToLocations: BottomNavigationItemView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView?.let {
            navBtnToCharacters = it.findViewById(R.id.to_chars)
            navBtnToEpisodes = it.findViewById(R.id.to_episodes)
            navBtnToLocations = it.findViewById(R.id.to_locations)
        }

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

    override fun setFragment(fragment: Fragment) {
        val tag = intTag.toString()
        intTag++
        supportFragmentManager.beginTransaction().run {
            replace(R.id.fragment_container, fragment, tag)
            addToBackStack(tag).commit()
        }
    }

    override fun setButtonsEnabled(fragment: Fragment) {
        makeAllNavButtonsEnabled()
        initGoBackArrow()
        when (fragment::class.java.simpleName) {
            CharactersListFragment::class.java.simpleName -> {
                navBtnToCharacters?.let {
                    it.isClickable = false
                }
                bottomNavigationView?.menu?.getItem(0)?.isChecked = true
            }
            LocationListFragment::class.java.simpleName -> {
                navBtnToLocations?.let {
                    it.isClickable = false
                }
                bottomNavigationView?.menu?.getItem(1)?.isChecked = true
            }
            EpisodeListFragment::class.java.simpleName -> {
                navBtnToEpisodes?.let {
                    it.isClickable = false
                }
                bottomNavigationView?.menu?.getItem(2)?.isChecked = true
            }
            else -> {
                setNoCheckedNavButtons()
            }
        }
    }

    private fun makeAllNavButtonsEnabled() {
        navBtnToCharacters?.let {
            it.isClickable = true
        }
        navBtnToLocations?.let {
            it.isClickable = true
        }
        navBtnToEpisodes?.let {
            it.isClickable = true
        }
    }

    private fun setNoCheckedNavButtons() {
        val menu = bottomNavigationView?.menu
        menu?.let {
            it.setGroupCheckable(0, true, false)
            for (i in 0 until it.size()) {
                it.getItem(i).isChecked = false
            }
            it.setGroupCheckable(0, true, true)
        }
    }

    private fun initGoBackArrow() {
        val transactionCount = supportFragmentManager.backStackEntryCount
        val trCountIsMoreThanZero = transactionCount > 0
        supportActionBar?.setDisplayHomeAsUpEnabled(trCountIsMoreThanZero)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            supportFragmentManager.popBackStack()
            initGoBackArrow()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}