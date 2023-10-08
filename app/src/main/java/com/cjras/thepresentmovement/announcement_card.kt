package com.cjras.thepresentmovement

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import com.cjras.thepresentmovement.databinding.AnnouncementCardBinding

class announcement_card (
    context: Context? //FragmentActivity? was Context
) : RelativeLayout(context){


    var binding: AnnouncementCardBinding

    init
    {

        binding = AnnouncementCardBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

    }
}

//
/*
        val activityLayout = binding.llff;
        var newAnnouncement = announcement_card(this)
        //add the new view
        activityLayout.addView(newAnnouncement)
 */
//