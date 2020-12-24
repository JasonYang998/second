package com.example.zhisu

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.zhisu.MsgService.MsgBinder
import com.example.zhisu.MsgService.OnProgressListener
import java.util.*

class MainActivity : Activity() {
    private var msgService: MsgService? = null
    private var mBtnStartCount: Button? = null
    private var mBtnEndCount: Button? = null
    private var mEtBeginNumber: EditText? = null
    private var mEtEndNumber: EditText? = null
    private var mTvCountResult: TextView? = null
    private val countResult = ArrayList<Int>()
    private var mBtnHistory: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
    }

    private fun initListener() {
        mBtnStartCount!!.setOnClickListener(View.OnClickListener { //拿出区间
            val startNumber = mEtBeginNumber!!.text.toString()
            val endNumber = mEtEndNumber!!.text.toString()
            if (TextUtils.isEmpty(startNumber)) {
                Toast.makeText(this@MainActivity, "开始的输入不能为空", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(endNumber)) {
                Toast.makeText(this@MainActivity, "结束的输入不能为空", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            val intent = Intent(this@MainActivity, MsgService::class.java)
            try {
                intent.putExtra("startNumber", startNumber.toInt())
                intent.putExtra("endNumber", endNumber.toInt())
            } catch (e: NumberFormatException) {
                Toast.makeText(this@MainActivity, "输入非法", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            //绑定Service
            bindService(intent, conn, Context.BIND_AUTO_CREATE)
            startService(intent)
        })
        mBtnEndCount!!.setOnClickListener {
            val intent = Intent(this@MainActivity, MsgService::class.java)
            //同时调用才行
            stopService(intent)
            unbindService(conn)
        }
        mBtnHistory!!.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            intent.putIntegerArrayListExtra("result_list", countResult)
            startActivity(intent)
        }
    }

    private fun initView() {
        mBtnStartCount = findViewById(R.id.btn_start_count)
        mBtnEndCount = findViewById(R.id.btn_end_count)
        mEtBeginNumber = findViewById(R.id.et_begin_number)
        mEtEndNumber = findViewById(R.id.et_end_number)
        mTvCountResult = findViewById(R.id.tv_count_result)
        mBtnHistory = findViewById(R.id.btn_history)
    }

    var conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {}
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            //返回一个MsgService对象
            msgService = (service as MsgBinder).service
            //开始下载
            msgService!!.startDownLoad()
            /**
             * 监听进度，每秒钟获取调用MsgService的getProgress()方法来获取进度，更新UI
             */
            msgService!!.setOnProgressListener(object : OnProgressListener {
                override fun onProgress(progress: Int) {
                    mTvCountResult!!.post { mTvCountResult!!.text = "已经找到了" + progress + "个素数了" }
                }

                override fun onResultCallback(result: List<Int>?) {
                    //记录返回的数据
                    countResult.addAll(result!!)
                }
            })
        }
    }

    override fun onDestroy() {
        unbindService(conn)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}