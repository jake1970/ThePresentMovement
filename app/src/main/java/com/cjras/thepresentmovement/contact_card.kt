package com.cjras.thepresentmovement

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import com.cjras.thepresentmovement.databinding.AnnouncementCardBinding
import com.cjras.thepresentmovement.databinding.ContactCardBinding

class contact_card (
    context: Context? //FragmentActivity? was Context
) : RelativeLayout(context){

    var binding: ContactCardBinding

    init
    {

        binding = ContactCardBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

    }

}