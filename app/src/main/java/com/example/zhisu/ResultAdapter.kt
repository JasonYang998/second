package com.example.zhisu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ResultAdapter : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    private val result: MutableList<Int> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as TextView).text = "第" + (position + 1) + "个质数为：" + result[position]
    }

    override fun getItemCount(): Int {
        return result.size
    }

    fun setData(result: ArrayList<Int>?) {
        this.result.clear()
        result?.let { this.result.addAll(it) }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}