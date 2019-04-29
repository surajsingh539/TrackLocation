package com.suraj.tracklocationapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.suraj.tracklocationapp.R
import com.suraj.tracklocationapp.model.UserLocation
import com.suraj.tracklocationapp.utils.General

class LocationAdapter(private val context: Context): RecyclerView.Adapter<LocationAdapter.ViewHolder>(){

    private var list: List<UserLocation> = ArrayList()



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.single_cluster, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        val model: UserLocation = list[p1]

        p0.setDateTime(model.time)

        p0.location.text = General.getAddressName(model.lat, model.lng, context)

    }

    //"hh:mm a"


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val location: TextView = itemView.findViewById(R.id.location)
        val date_time: TextView = itemView.findViewById(R.id.date_time)

        fun setDateTime(time: Long) {
            this.date_time.text = General.getDateFormat(time, "dd-MM-yyyy hh:mm:ss a")
        }

    }

    fun setList(list: List<UserLocation>) {
        this.list = list
        notifyDataSetChanged()
    }

}