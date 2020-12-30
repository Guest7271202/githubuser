package com.dicoding.andikas.githubuser.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.andikas.githubuser.db.MappingHelper
import com.dicoding.andikas.githubuser.R
import com.dicoding.andikas.githubuser.db.UserHelper
import com.dicoding.andikas.githubuser.adapter.UserAdapter
import com.dicoding.andikas.githubuser.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.activity_favorite.bottom_nav
import kotlinx.android.synthetic.main.activity_favorite.progress_bar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {
    private lateinit var userAdapter: UserAdapter
    private lateinit var userHelper: UserHelper

    companion object {
        const val EXTRA_STATE = "extra_state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        supportActionBar?.title = "Favorites"

        configRecyclerView()

        userHelper = UserHelper.getInstance(applicationContext)
        userHelper.open()

        if (savedInstanceState == null){
            loadAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<User>(EXTRA_STATE)
            if (list != null){
                userAdapter.getData(list)
            }
        }

        bottom_nav.selectedItemId = R.id.favorites_nav
        bottom_nav.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId){
                    R.id.home_nav -> {
                        val intent = Intent(this@FavoriteActivity, MainActivity::class.java)
                        startActivity(intent)
                        return true
                    }

                    R.id.favorites_nav -> return true
                }
                return true
            }

        })
    }

    private fun loadAsync(){
        GlobalScope.launch(Dispatchers.Main) {
            progressBar(true)
            val deferredNotes = async(Dispatchers.IO){
                val cursor = userHelper.queryAll()
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressBar(false)
            val users = deferredNotes.await()
            if (users.size > 0){
                userAdapter.getData(users)
            } else {
                Snackbar.make(rv_favorite, "Tidak ada data saat ini", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun configRecyclerView(){
        rv_favorite.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            userAdapter = UserAdapter()
            adapter = userAdapter
        }

        userAdapter.setRecyclerClickListener(object : UserAdapter.RecyclerClickListener{
            override fun onClick(user: User) {
                clickedItem(user)
            }

        })
    }

    private fun clickedItem(users: User){
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DETAIL, users)
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    private fun progressBar(state:  Boolean){
        if (state){
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        val searchView = menu.findItem(R.id.search_view)
        searchView.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.settings_menu -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }

        }
        return true
    }

    override fun onResume() {
        super.onResume()
        loadAsync()
    }
}