package com.relateddigital.relateddigital_android.inapp

import android.app.Fragment
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.databinding.FragmentHalfScreenBinding
import com.relateddigital.relateddigital_android.util.StringUtils
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 * Use the [HalfScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HalfScreenFragment : Fragment() {
    private var mStateId = 0
    private var mInAppState: InAppNotificationState? = null
    private var mIsTop = false
    private lateinit var binding: FragmentHalfScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStateId = arguments.getInt(ARG_PARAM1)
        mInAppState = arguments.getParcelable(ARG_PARAM2)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentHalfScreenBinding.inflate(inflater, container, false)
        val view: View = binding.root
        if (savedInstanceState != null) {
            //TODO: get the json string here
        } else {
            //TODO: get the json string here
        }
        setupInitialView()
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //TODO: save the json string here
    }

    private fun setupInitialView() {
        //Check the position and assign it to mIsTop
        mIsTop = true
        if (mIsTop) {
            adjustTop()
        } else {
            adjustBottom()
        }
        setupCloseButton()
    }

    private fun adjustTop() {
        //TODO remove the code below when the actual data gets ready
        //TODO check if title is on
        if (true) {
            binding.halfScreenContainerTop.setBackgroundColor(resources.getColor(R.color.black))
            binding.topTitleView.text = "30 Ağustos Zafer Bayramı"
            binding.topTitleView.setTextColor(Color.parseColor("#E02B19"))
            binding.topTitleView.textSize = 20f
            binding.topTitleView.setTypeface(Typeface.SANS_SERIF)
        } else {
            binding.topTitleView.visibility = View.GONE
        }
        Picasso.get().load("https://brtk.net/wp-content/uploads/2021/08/28/30agustossss.jpg?ver=cf14dae8e18a0da9aee40b2c8f3f2b39")
                .into(binding.topImageView)
        binding.topImageView.setOnClickListener { //TODO click report here
            //TODO Check if there is buttonCallback
            try {
                val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString("https://www.relateddigital.com/"))
                startActivity(viewIntent)
            } catch (e: ActivityNotFoundException) {
                Log.i("Visilabs", "User doesn't have an activity for notification URI")
            }
            endFragment()
        }
        binding.halfScreenContainerBot.visibility = View.GONE
    }

    private fun adjustBottom() {
        //TODO remove the code below when the actual data gets ready
        //TODO check if title is on
        if (true) {
            binding.halfScreenContainerBot.setBackgroundColor(resources.getColor(R.color.black))
            binding.botTitleView.text = "30 Ağustos Zafer Bayramı"
            binding.botTitleView.setTextColor(Color.parseColor("#E02B19"))
            binding.botTitleView.textSize = 20f
            binding.botTitleView.typeface = Typeface.SANS_SERIF
        } else {
            binding.botTitleView.visibility = View.GONE
        }
        Picasso.get().load("https://brtk.net/wp-content/uploads/2021/08/28/30agustossss.jpg?ver=cf14dae8e18a0da9aee40b2c8f3f2b39")
                .into(binding.botImageView)
        binding.botImageView.setOnClickListener { //TODO click report here
            //TODO Check if there is buttonCallback
            try {
                val viewIntent = Intent(Intent.ACTION_VIEW, StringUtils.getURIfromUrlString("https://www.relateddigital.com/"))
                startActivity(viewIntent)
            } catch (e: ActivityNotFoundException) {
                Log.i("Visilabs", "User doesn't have an activity for notification URI")
            }
            endFragment()
        }
        binding.halfScreenContainerTop.visibility = View.GONE
    }

    private fun setupCloseButton() {
        //TODO check if close button will be displayed first
        if (mIsTop) {
            binding.topCloseButton.setBackgroundResource(closeIcon)
            binding.topCloseButton.setOnClickListener { endFragment() }
        } else {
            binding.botCloseButton.setBackgroundResource(closeIcon)
            binding.botCloseButton.setOnClickListener { endFragment() }
        }
    }

    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/
    private val closeIcon: Int
        get() = R.drawable.ic_close_white_24dp
    //TODO when real data comes:
    /* switch (mInAppMessage.getActionData().getCloseButtonColor()) {

         case "white":
             return R.drawable.ic_close_white_24dp;

         case "black":
             return R.drawable.ic_close_black_24dp;
     }
     return R.drawable.ic_close_black_24dp;*/

    private fun endFragment() {
        //TODO Release display state here
        if (activity != null) {
            activity.fragmentManager.beginTransaction().remove(this@HalfScreenFragment).commit()
        }
    }

    companion object {
        private const val LOG_TAG = "HalfScreenFragment"

        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "stateIdKey"
        private const val ARG_PARAM2 = "inAppStateKey"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param stateId Parameter 1.
         * @param inAppState Parameter 2.
         * @return A new instance of fragment SocialProofFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(stateId: Int, inAppState: InAppNotificationState?): HalfScreenFragment {
            val fragment = HalfScreenFragment()
            val args = Bundle()
            args.putInt(ARG_PARAM1, stateId)
            args.putParcelable(ARG_PARAM2, inAppState)
            fragment.arguments = args
            return fragment
        }
    }
}