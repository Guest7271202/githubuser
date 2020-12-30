package com.dicoding.andikas.githubuser.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.andikas.githubuser.R
import com.dicoding.andikas.githubuser.model.User
import com.dicoding.andikas.githubuser.adapter.UserAdapter
import com.dicoding.andikas.githubuser.ui.DetailActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_follower.*
import org.json.JSONArray
import org.json.JSONObject

class FollowersFragment : Fragment() {
    private lateinit var userAdapter: UserAdapter
    val users: ArrayList<User> = ArrayList()
    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follower, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = activity?.intent?.getParcelableExtra(DetailActivity.EXTRA_DETAIL)
        getFollowers(user?.username.toString())
    }

    private fun getFollowers(login: String){
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 0b712e3b14cabe3fc38cb0f8d426b3d7f56ad8b5")
        client.addHeader("User-Agent", "request")
        client.get("https://api.github.com/users/$login/followers", object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                val result = String(responseBody)
                try {
                    val responseArray = JSONArray(result)
                    for (i in 0 until responseArray.length()) {
                        val responseObject = responseArray.getJSONObject(i)
                        val username = responseObject.getString("login").toString()
                        getUserDetail(username)
                    }
                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                val errorMessage = when (statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Log.d("onFailure", errorMessage)
            }
        })
    }

    private fun getUserDetail(login: String){
        progressBar(true)
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token 0b712e3b14cabe3fc38cb0f8d426b3d7f56ad8b5")
        client.addHeader("User-Agent", "request")
        client.get("https://api.github.com/users/$login", object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {
                progressBar(false)
                val result = String(responseBody)
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
                    // configRecyclerView(users)
                    configRecyclerView()
                    userAdapter.getData(users)

                } catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                    e.printStackTrace()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {
                progressBar(false)
                val errorMessage = when (statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Log.d("onFailure", errorMessage)
            }
        })
    }

    private fun configRecyclerView(/*user: ArrayList<User>*/){
        rv_follower.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
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
        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_DETAIL, users)
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