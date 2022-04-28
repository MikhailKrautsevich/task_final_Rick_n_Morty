package com.mk.rickmortyappbykrautsevich

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mk.rickmortyappbykrautsevich.fragments.CharactersListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

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