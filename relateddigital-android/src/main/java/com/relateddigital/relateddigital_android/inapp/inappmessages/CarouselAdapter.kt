package com.relateddigital.relateddigital_android.inapp.inappmessages

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.RelatedDigital
import com.relateddigital.relateddigital_android.model.InAppCarouselItem
import com.relateddigital.relateddigital_android.model.InAppMessage
import com.relateddigital.relateddigital_android.network.RequestHandler
import com.relateddigital.relateddigital_android.util.AppUtils
import com.squareup.picasso.Picasso

class CarouselAdapter(
    private val mContext: Context,
    private val finishCallback: CarouselFinishInterface,
    private val carouselButtonInterface: CarouselButtonInterface
) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {
    private var message: InAppMessage? = null
    private var mCarouselItems: List<InAppCarouselItem>? = null
    private var player0: ExoPlayer? = null
    private var player1: ExoPlayer? = null
    private var player2: ExoPlayer? = null
    private var player3: ExoPlayer? = null
    private var player4: ExoPlayer? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarouselViewHolder {
        val view: View =
            LayoutInflater.from(mContext).inflate(R.layout.in_app_carousel_item, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CarouselViewHolder,
        position: Int
    ) {
        holder.carouselContainer.visibility = View.VISIBLE
        holder.backgroundImage.visibility = View.VISIBLE
        holder.carouselImage.visibility = View.VISIBLE
        holder.carouselTitle.visibility = View.VISIBLE
        holder.carouselBodyText.visibility = View.VISIBLE
        holder.couponContainer.visibility = View.VISIBLE
        holder.couponCode.visibility = View.VISIBLE
        holder.carouselButton.visibility = View.VISIBLE
        holder.dotIndicator.visibility = View.VISIBLE
        holder.closeButton.visibility = View.VISIBLE

        if (message!!.mActionData!!.mCloseEventTrigger.isNullOrEmpty()) {
            holder.closeButton.setBackgroundResource(getCloseIcon())
            holder.closeButton.setOnClickListener {
                finishCallback.onFinish()
            }
        } else {
            if (message!!.mActionData!!.mCloseEventTrigger.equals("backgroundclick")) {
                holder.closeButton.visibility = View.GONE
            } else {
                holder.closeButton.setBackgroundResource(getCloseIcon())
                holder.closeButton.setOnClickListener {
                    finishCallback.onFinish()
                }
            }
        }

        holder.dotIndicator.removeAllViews()

        for (i in mCarouselItems!!.indices) {
            val view = View(mContext)
            view.setBackgroundResource(R.drawable.dot_indicator_default)
            val layoutParams = LinearLayout.LayoutParams(
                40, 40
            )
            layoutParams.setMargins(10, 0, 10, 0)
            view.layoutParams = layoutParams
            holder.dotIndicator.addView(view)
        }


        for (i in mCarouselItems!!.indices) {
            if (i == position) {
                holder.dotIndicator.getChildAt(i)
                    .setBackgroundResource(R.drawable.dot_indicator_selected)
            } else {
                holder.dotIndicator.getChildAt(i)
                    .setBackgroundResource(R.drawable.dot_indicator_default)
            }
        }

        if (!mCarouselItems!![position].backgroundImage.isNullOrEmpty()) {
            Picasso.get().load(mCarouselItems!![position].backgroundImage)
                .into(holder.backgroundImage)
        } else {
            holder.backgroundImage.visibility = View.GONE
            if (!mCarouselItems!![position].backgroundColor.isNullOrEmpty()) {
                holder.backgroundImage.visibility = View.GONE
                holder.carouselContainer.setBackgroundColor(
                    Color.parseColor(
                        mCarouselItems!![position].backgroundColor
                    )
                )
            }
        }

        if (!mCarouselItems!![position].image.isNullOrEmpty()) {
            holder.carouselImage.visibility = View.VISIBLE
            holder.carouselVideo.visibility = View.GONE
            if (AppUtils.isAnImage(mCarouselItems!![position].image)) {
                Picasso.get().load(mCarouselItems!![position].image)
                    .into(holder.carouselImage)
            } else {
                Glide.with(mContext)
                    .load(mCarouselItems!![position].image)
                    .into(holder.carouselImage)
            }
        } else {
            holder.carouselImage.visibility = View.GONE
            if (false) { // TODO : if !video.isNullOrEmpty():
                holder.carouselVideo.visibility = View.VISIBLE
                when (position) {
                    0 -> {
                        holder.carouselVideo.player = player0
                        val mediaItem =
                            MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4") // TODO : real url here
                        player0!!.setMediaItem(mediaItem)
                        player0!!.prepare()
                        holder.carouselVideo.addOnAttachStateChangeListener(object :
                            View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: View) {
                                holder.carouselVideo.player!!.playWhenReady = true
                            }

                            override fun onViewDetachedFromWindow(v: View) {
                                if (holder.carouselVideo.player != null) {
                                    holder.carouselVideo.player!!.pause()
                                }
                            }
                        })
                    }
                    1 -> {
                        holder.carouselVideo.player = player1
                        val mediaItem =
                            MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4") // TODO : real url here
                        player1!!.setMediaItem(mediaItem)
                        player1!!.prepare()
                        holder.carouselVideo.addOnAttachStateChangeListener(object :
                            View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: View) {
                                holder.carouselVideo.player!!.playWhenReady = true
                            }

                            override fun onViewDetachedFromWindow(v: View) {
                                if (holder.carouselVideo.player != null) {
                                    holder.carouselVideo.player!!.pause()
                                }
                            }
                        })
                    }
                    2 -> {
                        holder.carouselVideo.player = player2
                        val mediaItem =
                            MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4") // TODO : real url here
                        player2!!.setMediaItem(mediaItem)
                        player2!!.prepare()
                        holder.carouselVideo.addOnAttachStateChangeListener(object :
                            View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: View) {
                                holder.carouselVideo.player!!.playWhenReady = true
                            }

                            override fun onViewDetachedFromWindow(v: View) {
                                if (holder.carouselVideo.player != null) {
                                    holder.carouselVideo.player!!.pause()
                                }
                            }
                        })
                    }
                    3 -> {
                        holder.carouselVideo.player = player3
                        val mediaItem =
                            MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4") // TODO : real url here
                        player3!!.setMediaItem(mediaItem)
                        player3!!.prepare()
                        holder.carouselVideo.addOnAttachStateChangeListener(object :
                            View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: View) {
                                holder.carouselVideo.player!!.playWhenReady = true
                            }

                            override fun onViewDetachedFromWindow(v: View) {
                                if (holder.carouselVideo.player != null) {
                                    holder.carouselVideo.player!!.pause()
                                }
                            }
                        })
                    }
                    4 -> {
                        holder.carouselVideo.player = player4
                        val mediaItem =
                            MediaItem.fromUri("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4") // TODO : real url here
                        player4!!.setMediaItem(mediaItem)
                        player4!!.prepare()
                        holder.carouselVideo.addOnAttachStateChangeListener(object :
                            View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: View) {
                                holder.carouselVideo.player!!.playWhenReady = true
                            }

                            override fun onViewDetachedFromWindow(v: View) {
                                if (holder.carouselVideo.player != null) {
                                    holder.carouselVideo.player!!.pause()
                                }
                            }
                        })
                    }
                }
            } else {
                holder.carouselVideo.visibility = View.GONE
            }
        }

        if (!mCarouselItems!![position].title.isNullOrEmpty()) {
            holder.carouselTitle.text = mCarouselItems!![position].title!!.replace("\\n", "\n")
            if (!mCarouselItems!![position].titleColor.isNullOrEmpty()) {
                holder.carouselTitle.setTextColor(Color.parseColor(mCarouselItems!![position].titleColor))
            }
            holder.carouselTitle.textSize =
                mCarouselItems!![position].titleTextsize!!.toFloat() + 12
            holder.carouselTitle.typeface = mCarouselItems!![position].getTitleFontFamily(
                mContext
            )
        } else {
            holder.carouselTitle.visibility = View.GONE
        }

        if (!mCarouselItems!![position].body.isNullOrEmpty()) {
            holder.carouselBodyText.text = mCarouselItems!![position].body!!.replace("\\n", "\n")
            if (!mCarouselItems!![position].bodyColor.isNullOrEmpty()) {
                holder.carouselBodyText.setTextColor(
                    Color.parseColor(
                        mCarouselItems!![position].bodyColor
                    )
                )
            }
            holder.carouselBodyText.textSize =
                mCarouselItems!![position].bodyTextsize!!.toFloat() + 8
            holder.carouselBodyText.typeface = mCarouselItems!![position].getBodyFontFamily(
                mContext
            )
        } else {
            holder.carouselBodyText.visibility = View.GONE
        }

        if (!mCarouselItems!![position].promotionCode.isNullOrEmpty()) {
            if (!mCarouselItems!![position].promocodeBackgroundColor.isNullOrEmpty()) {
                holder.couponContainer.setBackgroundColor(
                    Color.parseColor(
                        mCarouselItems!![position].promocodeBackgroundColor
                    )
                )
            }
            holder.couponCode.text = mCarouselItems!![position].promotionCode
            if (!mCarouselItems!![position].promocodeTextColor.isNullOrEmpty()) {
                holder.couponCode.setTextColor(Color.parseColor(mCarouselItems!![position].promocodeTextColor))
            }
            holder.couponContainer.setOnClickListener {
                val clipboard =
                    mContext.getSystemService(Activity.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    mContext.getString(R.string.coupon_code),
                    mCarouselItems!![position].promotionCode
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.copied_to_clipboard),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            holder.couponContainer.visibility = View.GONE
        }

        if (!mCarouselItems!![position].buttonText.isNullOrEmpty()) {
            if (!mCarouselItems!![position].buttonColor.isNullOrEmpty()) {
                holder.carouselButton.setBackgroundColor(
                    Color.parseColor(
                        mCarouselItems!![position].buttonColor
                    )
                )
            }
            holder.carouselButton.text = mCarouselItems!![position].buttonText
            if (!mCarouselItems!![position].buttonTextColor.isNullOrEmpty()) {
                holder.carouselButton.setTextColor(Color.parseColor(mCarouselItems!![position].buttonTextColor))
            }
            holder.carouselButton.textSize =
                mCarouselItems!![position].buttonTextsize!!.toFloat() + 12
            holder.carouselButton.typeface = mCarouselItems!![position].getButtonFontFamily(
                mContext
            )
            holder.carouselButton.setOnClickListener {
                RequestHandler.createInAppNotificationClickRequest(mContext, message, "")
                val buttonCallback = RelatedDigital.getInAppButtonInterface()
                if (buttonCallback != null) {
                    RelatedDigital.setInAppButtonInterface(null)
                    buttonCallback.onPress(mCarouselItems!![position].androidLnk)
                } else {
                    carouselButtonInterface.onPress(mCarouselItems!![position].androidLnk)
                }
                finishCallback.onFinish()
            }
        } else {
            holder.carouselButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return message!!.mActionData!!.carouselItems!!.size
    }

    /**
     * To add new data and notify the change
     */
    fun setMessage(message: InAppMessage?) {
        if (message == null) {
            Log.e("InAppCarousel", "The data could not be gotten properly!")
            finishCallback.onFinish()
        } else {
            this.message = message
            mCarouselItems = message.mActionData!!.carouselItems
            player0 = ExoPlayer.Builder(mContext).build()
            player1 = ExoPlayer.Builder(mContext).build()
            player2 = ExoPlayer.Builder(mContext).build()
            player3 = ExoPlayer.Builder(mContext).build()
            player4 = ExoPlayer.Builder(mContext).build()
            notifyDataSetChanged()
        }
    }

    private fun getCloseIcon(): Int {
        when (message!!.mActionData!!.mCloseButtonColor) {
            "white" -> return R.drawable.ic_close_white_24dp
            "black" -> return R.drawable.ic_close_black_24dp
        }
        return R.drawable.ic_close_black_24dp
    }

    fun releasePlayer() {
        if (player0 != null) {
            player0!!.release()
            player0 = null
        }
        if (player1 != null) {
            player1!!.release()
            player1 = null
        }
        if (player2 != null) {
            player2!!.release()
            player2 = null
        }
        if (player3 != null) {
            player3!!.release()
            player3 = null
        }
        if (player4 != null) {
            player4!!.release()
            player4 = null
        }
    }

    /**
     * Custom RecyclerView.ViewHolder class
     */
    inner class CarouselViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val carouselContainer: FrameLayout = view.findViewById(R.id.carousel_container)
        val backgroundImage: ImageView = view.findViewById(R.id.background)
        val carouselImage: ImageView = view.findViewById(R.id.carousel_image)
        val carouselVideo: PlayerView = view.findViewById(R.id.carousel_video)
        val carouselTitle: TextView = view.findViewById(R.id.carousel_title)
        val carouselBodyText: TextView = view.findViewById(R.id.carousel_body_text)
        val couponContainer: FrameLayout = view.findViewById(R.id.coupon_container)
        val couponCode: TextView = view.findViewById(R.id.coupon_code)
        val carouselButton: Button = view.findViewById(R.id.carousel_button)
        val dotIndicator: LinearLayout = view.findViewById(R.id.dot_indicator)
        val closeButton: ImageButton = view.findViewById(R.id.carousel_close_button)
    }
}