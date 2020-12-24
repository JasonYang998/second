package com.example.zhisu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val intent = intent
        val result =
            intent.getSerializableExtra("result_list") as ArrayList<Int>?
        val recyclerView: RecyclerView = findViewById(R.id.rv_result_list)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val adapter = ResultAdapter()
        adapter.setData(result)
        recyclerView.setAdapter(adapter)
    }
}
