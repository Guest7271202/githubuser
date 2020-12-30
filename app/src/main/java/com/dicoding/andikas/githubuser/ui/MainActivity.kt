package com.dicoding.andikas.githubuser.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.andikas.githubuser.R
import com.dicoding.andikas.githubuser.adapter.UserAdapter
import com.dicoding.andikas.githubuser.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_list.*
import kotlinx.android.synthetic.main.item_list.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var userAdapter: UserAdapter
    val users: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getUser()

        bottom_nav.selectedItemId = R.id.home_nav
        bottom_nav.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId){
                    R.id.home_nav -> return true

                    R.id.favorites_nav -> {
                        val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                        intent.putParcelableArrayListExtra(FavoriteActivity.EXTRA_STATE, users)
                        overridePendingTransition(0,0)
                        startActivity(intent)
                        return true
                    }
                }
                return true
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.search_view -> {
                val searchView = item.actionView as SearchView
                searchConfig(searchView)
                return true
            }

            R.id.settings_menu -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return true
    }

    private fun searchConfig(searchView: SearchView){
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()){
                    users.clear()
                    searchUser(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun searchUser(login: String){
            val client = AsyncHttpClient()
            client.addHeader("Authorization", "token 0b712e3b14cabe3fc38cb0f8d426b3d7f56ad8b5")
            client.addHeader("User-Agent", "request")
            client.get("https://api.github.com/search/users?q=$login", object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                    val result = String(responseBody)
                    try {
                        val responseObject = JSONObject(result)
                        val items = responseObject.getJSONArray("items")
                        for (i in 0 until items.length()) {
                            val item = items.getJSONObject(i)
                            val username = item.getString("login")
                            getUserDetail(username)
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    val errorMessage = when (statusCode) {
                        401 -> "$statusCode : Bad Request"
                        403 -> "$statusCode : Forbidden"
                        404 -> "$statusCode : Not Found"
                        else -> "$statusCode : ${error.message}"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
        }

    private fun getUser(){
            val client = AsyncHttpClient()
            client.addHeader("Authorization", "token 0b712e3b14cabe3fc38cb0f8d426b3d7f56ad8b5")
            client.addHeader("User-Agent", "request")
            client.get("https://api.github.com/users", object : AsyncHttpResponseHandler() {
                override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                    val result = String(responseBody)
                    Log.d("result", result)
                    try {
                        val responseArray = JSONArray(result)
                        for (i in 0 until responseArray.length()) {
                            val responseObject = responseArray.getJSONObject(i)
                            val username = responseObject.getString("login").toString()
                            getUserDetail(username)
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }

                override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                    val errorMessage = when (statusCode) {
                        401 -> "$statusCode : Bad Request"
                        403 -> "$statusCode : Forbidden"
                        404 -> "$statusCode : Not Found"
                        else -> "$statusCode : ${error.message}"
                    }
                    Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
        }

    private fun getUserDetail(id: String){
        progressBar(true)
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 0b712e3b14cabe3fc38cb0f8d426b3d7f56ad8b5")
        client.addHeader("User-Agent", "request")
        client.get("https://api.github.com/users/$id", object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                progressBar(false)
                val result = String(responseBody)
                Log.d("result", result)
                try {
                    val responseObject = JSONObject(result)
                    users.add(
                            User(
                                    responseObject.getString("avatar_url").toString(),
                                    responseObject.getString("login").toString(),
                                    responseObject.getString("name").toString(),
                                    responseObject.getString("location").toString(),
                                    responseObject.getString("company").toString(),
                                    responseObject.getString("public_repos")
                            )
                    )
                    configRecyclerView()
                    userAdapter.getData(users)

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                progressBar(false)
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configRecyclerView(){
        rv_main.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            userAdapter = UserAdapter()
            adapter = userAdapter
        }

        userAdapter.setRecyclerClickListener(object : UserAdapter.RecyclerClickListener {
            override fun onClick(user: User) = clickedItem(user)
        })

    }

    private fun clickedItem(users: User){
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DETAIL, users)
        overridePendingTransition(0, 0)
        startActivity(intent)
    }

    private fun progressBar(state: Boolean){
        if (state){
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.INVISIBLE
        }
    }

}