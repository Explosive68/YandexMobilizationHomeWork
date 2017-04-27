package ru.illarionovroman.yandexmobilizationhomework.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.Toast
import ru.illarionovroman.yandexmobilizationhomework.R

class AboutActivity : AppCompatActivity() {

    var mIvLogo: ImageView? = null

    var mToast: Toast? = null
    var mClickCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        bindViews()
        initializeActivity()
    }

    private fun bindViews() {
        mIvLogo = findViewById(R.id.ivLogo) as ImageView
    }

    private fun initializeActivity() {
        initializeActionBar()
        setLogoClickListener()
    }

    private fun initializeActionBar() {
        val toolbar: Toolbar? = findViewById(R.id.toolbarAbout) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.about_title)
        }
    }

    private fun setLogoClickListener() {
        val secretCount: Int = 10
        mIvLogo?.setOnClickListener {
            mClickCount++
            if (mClickCount in 3..(secretCount - 1)) {
                val clicksLeft: Int = secretCount - mClickCount
                showToast(clicksLeft.toString() + " more to go...")
            } else if (mClickCount >= secretCount) {
                showToast(getString(R.string.easter_egg))
            }
        }
    }

    private fun showToast(textToShow: String) {
        mToast?.cancel()
        mToast = Toast.makeText(this, textToShow, Toast.LENGTH_SHORT)
        mToast!!.show()
    }
}
