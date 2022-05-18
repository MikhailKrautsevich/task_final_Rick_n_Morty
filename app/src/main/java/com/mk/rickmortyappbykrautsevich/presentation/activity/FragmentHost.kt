package com.mk.rickmortyappbykrautsevich.presentation.activity

import androidx.fragment.app.Fragment

interface FragmentHost {
    fun setFragment(fragment: Fragment)
}