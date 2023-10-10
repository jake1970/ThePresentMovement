package com.cjras.thepresentmovement

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.cjras.thepresentmovement.databinding.ActivityHomeFeedCardBinding
import com.cjras.thepresentmovement.databinding.AnnouncementCardBinding

class home_feed_card (
    context: Context? //FragmentActivity? was Context
) : RelativeLayout(context){

    var binding: ActivityHomeFeedCardBinding

    init
    {

        binding = ActivityHomeFeedCardBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

    }
}