package com.berry.traveldiary.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.berry.traveldiary.R
import com.berry.traveldiary.model.DiaryEntries


class ListAdapter(private val mList: MutableList<DiaryEntries>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var userList = mList

//    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}


    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvId: TextView = itemView.findViewById(R.id.tvId)
        val tvName: TextView = itemView.findViewById(R.id.firstName_txt)
        val tvEmail: TextView = itemView.findViewById(R.id.lastName_txt)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_row, parent, false)

        return ViewHolder(view)

//        return MyViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)
//        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = userList[position]

        holder.tvId.text = currentItem.entryId.toString()
        holder.tvName.text = currentItem.title
        holder.tvEmail.text = currentItem.location

//        holder.itemView.findViewById<TextView>(R.id.tvId).text = currentItem.entryId.toString()
//        holder.itemView.findViewById<TextView>(R.id.firstName_txt).text =
//            currentItem.location.toString()
//        holder.itemView.id_txt.text = currentItem.id.toString()
//        holder.itemView.firstName_txt.text = currentItem.firstName
//        holder.itemView.lastName_txt.text = currentItem.lastName
//        holder.itemView.age_txt.text = currentItem.age.toString()

//        holder.itemView.rowLayout.setOnClickListener {
////            val action = ListFragmentDirections.actionListFragmentToUpdateFragment(currentItem)
////            holder.itemView.findNavController().navigate(action)
//        }
    }

//    fun setData(user: List<DiaryEntries>) {
//        this.userList = user
//        notifyDataSetChanged()
//    }
}