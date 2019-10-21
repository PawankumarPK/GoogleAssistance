package ai.jetbrain.arya.fragments

import ai.jetbrain.arya.activitys.MainActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    lateinit var baseActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        baseActivity = activity as MainActivity
    }
}