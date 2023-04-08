package com.relateddigital.relateddigital_android.inapp.inappmessages.inlineNpsWithNumbers
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.relateddigital.relateddigital_android.R
import com.relateddigital.relateddigital_android.inapp.inappmessages.NpsWithNumbersView
import com.relateddigital.relateddigital_android.inapp.inappmessages.inlineNpsWithNumbers.InlineNpsWithNumbersView
class InlineNpsWithNumberssView constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val scrollView: ScrollView
    private val image: ImageView
    private val title: TextView
    private val body: TextView
    private val npsWithNumbersView: NpsWithNumbersView
    private val button: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.inline_nps_with_numbers, this, true)
        scrollView = findViewById(R.id.ll_back)
        image = findViewById(R.id.iv_template)
        title = findViewById(R.id.tv_title)
        body = findViewById(R.id.tv_body)
        npsWithNumbersView = findViewById(R.id.npsWithNumbersView)
        button = findViewById(R.id.btn_template)
    }

    fun setImage(imageResId: Int) {
        image.setImageResource(imageResId)
    }

    fun setTitle(titleText: String) {
        title.text = titleText
    }

    fun setBody(bodyText: String) {
        body.text = bodyText
    }

   /* fun setNpsListener(listener: InlineNpsWithNumbersView.OnNpsListener) {
        npsWithNumbersView.setNpsListener(listener)
    }
*/
    fun setButtonText(buttonText: String) {
        button.text = buttonText
    }

    fun setButtonClickListener(listener: OnClickListener) {
        button.setOnClickListener(listener)
    }

    fun show() {
        scrollView.visibility = VISIBLE
    }

    fun hide() {
        scrollView.visibility = GONE
    }
}