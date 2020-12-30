package com.dicoding.andikas.githubuser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.andikas.githubuser.R
import com.dicoding.andikas.githubuser.model.User
import kotlinx.android.synthetic.main.item_list.view.*

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private var users: ArrayList<User> = ArrayList()
    private lateinit var recyclerClickListener: RecyclerClickListener

    fun getData(users: ArrayList<User>){
        this.users = users
        notifyDataSetChanged()
    }

    fun setRecyclerClickListener(recyclerClickListener: RecyclerClickListener){
        this.recyclerClickListener = recyclerClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(users: User){
            with(itemView){
                Glide.with(this).load(users.avatar).override(50, 50).into(img_avatar)
                tv_username.text = users.username
                tv_name.text = users.name

                itemView.setOnClickListener {
                    recyclerClickListener.onClick(users)
                }

            }
        }
    }

    interface RecyclerClickListener{
        fun onClick(user: User)
    }

}