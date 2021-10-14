package com.relateddigital.androidexampleapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.relateddigital.androidexampleapp.databinding.ActivityStoryDemoBinding
import com.relateddigital.relateddigital_android.inapp.story.StoryItemClickListener
import com.relateddigital.relateddigital_android.inapp.story.StoryRequestListener
import com.relateddigital.relateddigital_android.util.PersistentTargetManager

class StoryDemoActivity : AppCompatActivity() {
    private var binding: ActivityStoryDemoBinding? = null
    private var storyItemClickListener: StoryItemClickListener? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDemoBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        storyItemClickListener = object : StoryItemClickListener {
            override fun storyItemClicked(storyLink: String?) {
                Toast.makeText(applicationContext, storyLink, Toast.LENGTH_SHORT).show()
                Log.i("link story", storyLink!!)
                try {
                    val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(storyLink))
                    startActivity(viewIntent)
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "The link is not formatted properly!")
                }
            }
        }
        binding!!.btnShowStory.setOnClickListener {
            hideKeyboard(this)
            binding!!.etStoryId.clearFocus()
            showStory()
        }
        binding!!.btnClearStoryCache.setOnClickListener {
            hideKeyboard(this)
            binding!!.etStoryId.clearFocus()
            clearStoryCache()
        }
        binding!!.sw.setOnTouchListener { _, _ ->
            hideKeyboard(this)
            binding!!.etStoryId.clearFocus()
            false
        }
    }

    private fun showStory() {
        val storyId = binding!!.etStoryId.text.toString().trim()
        val storyRequestListener = object : StoryRequestListener {
            override fun onRequestResult(isAvailable: Boolean) {
                if (!isAvailable) {
                    binding!!.vrvStory.visibility = View.GONE
                }
            }
        }
        if (storyId.isEmpty()) {
            binding!!.vrvStory.setStoryActionWithRequestCallback(applicationContext,
                    storyItemClickListener, storyRequestListener)
        } else {
            binding!!.vrvStory.setStoryActionIdWithRequestCallback(applicationContext, storyId,
                    storyItemClickListener, storyRequestListener)
        }
    }

    private fun clearStoryCache() {
        PersistentTargetManager.clearStoryCache(applicationContext)
    }

    companion object {
        private const val LOG_TAG = "StoryActivity"
        private fun hideKeyboard(activity: AppCompatActivity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}