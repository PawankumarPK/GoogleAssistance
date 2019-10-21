package ai.jetbrain.arya.fragments


import ai.jetbrain.arya.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mWelcomeScreenFragment.setOnClickListener { welcomeScreen() }
        mEyeFragment.setOnClickListener { eyeScreen() }
    }

    private fun welcomeScreen() {
        fragmentManager!!.beginTransaction()
            .replace(R.id.mFrameContainer, WelcomeScreen())
            .addToBackStack(null)
            .commit()
    }


    private fun eyeScreen() {
        fragmentManager!!.beginTransaction()
            .replace(R.id.mFrameContainer, EyeFragment())
            .addToBackStack(null)
            .commit()
    }

}
