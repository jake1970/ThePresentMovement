package com.cjras.thepresentmovement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class create_announcement : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val root = inflater.inflate(R.layout.fragment_create_announcement, container, false);

        return root;
    }
}