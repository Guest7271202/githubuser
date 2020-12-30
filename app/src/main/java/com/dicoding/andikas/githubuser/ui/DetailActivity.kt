package com.dicoding.andikas.githubuser.ui

import android.content.ContentValues
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.*
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.AVATAR
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.COMPANY
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.LOCATION
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.NAME
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.REPOSITORY
import com.dicoding.andikas.githubuser.db.DatabaseContract.UserColumns.Companion.USERNAME
import com.dicoding.andikas.githubuser.db.MappingHelper
import com.dicoding.andikas.githubuser.R
import com.dicoding.andikas.githubuser.db.UserHelper
import com.dicoding.andikas.githubuser.adapter.SectionsPagerAdapter
import com.dicoding.andikas.githubuser.model.User
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.img_avatar
import kotlinx.android.synthetic.main.activity_detail.tv_name
import kotlinx.android.synthetic.main.activity_detail.tv_username

class DetailActivity : AppCompatActivity(){
    private var isFav = false
    private var user: User? = null
    private lateinit var userHelper: UserHelper

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        userHelper = UserHelper.getInstance(applicationContext)
        userHelper.open()

        user = intent.getParcelableExtra(EXTRA_DETAIL)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = user?.username

        pagerAdapter()
        getDetail()
        checkFavUser()

        btn_favorite.setOnClickListener {
            val values = ContentValues()
            values.put(AVATAR, user?.avatar)
            values.put(USERNAME, user?.username)
            values.put(NAME, user?.name)
            values.put(LOCATION, user?.location)
            values.put(COMPANY, user?.company)
            values.put(REPOSITORY, user?.repository)

            when {
                isFav -> {
                    userHelper.deleteByUsername(user?.username.toString())
                    Toast.makeText(this@DetailActivity, "${user?.username.toString()} removed from favourite", Toast.LENGTH_SHORT).show()
                    isButtonFavoriteChecked(false)
                    isFav = false
                }

                !isFav -> {
                    userHelper.insert(values)
                    Toast.makeText(this@DetailActivity, "${user?.username.toString()} added to favourite", Toast.LENGTH_SHORT).show()
                    isButtonFavoriteChecked(true)
                    isFav = true
                }
            }
        }
    }

    private fun pagerAdapter(){
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        view_pager.adapter = sectionsPagerAdapter
        tab_layout.setupWithViewPager(view_pager)

        supportActionBar?.elevation = 0f
    }

    private fun getDetail(){
        val detail = intent.getParcelableExtra(EXTRA_DETAIL) as User
        Glide.with(this).load(detail.avatar).override(150,150).into(img_avatar)
        tv_username.text = detail.username.toString()
        tv_name.text = detail.name.toString()
        tv_location.text = detail.location.toString()
        tv_company.text = detail.company.toString()
        tv_repository.text = detail.repository.toString()
    }

    private fun isButtonFavoriteChecked(state: Boolean){
        if (state){
            btn_favorite.setBackgroundResource(R.drawable.ic_baseline_favorite_24)
        } else {
            btn_favorite.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }

    private fun checkFavUser(){
        val username = user?.username
        val cursor = userHelper.queryByUsername(username.toString())
        val mFav = MappingHelper.mapCursorToArrayList(cursor)

        for (mUser in mFav){
            if (username == mUser.username){
                isButtonFavoriteChecked(true)
                isFav = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        userHelper.close()
    }
    
}