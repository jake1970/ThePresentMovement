package com.cjras.thepresentmovement

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.cjras.thepresentmovement.databinding.ActivityHomeFeedCardBinding
import com.cjras.thepresentmovement.databinding.AnnouncementCardBinding

class home_feed_card (
    context: Context?
) : RelativeLayout(context){

    //custom card view binding
    var binding: ActivityHomeFeedCardBinding


    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Set View Binding
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    init
    {
        binding = ActivityHomeFeedCardBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}