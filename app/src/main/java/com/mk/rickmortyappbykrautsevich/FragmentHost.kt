package com.mk.rickmortyappbykrautsevich

import androidx.fragment.app.Fragment

interface FragmentHost {
    fun setFragment(fragment: Fragment)
}