package com.example.zhisu

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.*


class MsgService : Service() {
    private var mBegin = 0
    private var mEnd = 0
    private val result: MutableList<Int> = ArrayList()
    private var mNotification: Notification? = null

    /**
     * 更新进度的回调接口
     */
    private var onProgressListener: OnProgressListener? = null

    /**
     * 注册回调接口的方法，供外部调用
     * @param onProgressListener
     */
    fun setOnProgressListener(onProgressListener: OnProgressListener?) {
        this.onProgressListener = onProgressListener
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //获取Activity传过来的值
        mBegin = intent.getIntExtra("startNumber", 0)
        mEnd = intent.getIntExtra("endNumber", 0)
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * 模拟下载任务，每秒钟更新一次
     */
    fun startDownLoad() {
        Thread(object : Runnable {
            override fun run() {
                for (i in mBegin..mEnd) {
                    var isPrime = true
                    for (j in 2 until i) {
                        if (i % j == 0) isPrime = false
                    }
                    if (isPrime) {
                        //i是素数
                        result.add(i)
                        //进度发生变化通知调用方
                        if (onProgressListener != null) {
                            onProgressListener!!.onProgress(result.size)
                        }
                    }
                }
                if (onProgressListener != null) {
                    //返回结果
                    onProgressListener!!.onResultCallback(result)
                }
                //通知
                notifiction()
                //结束后停止
                stopSelf()
            }

            private fun notifiction() {
                val manager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //大于Android8
                    val channel = NotificationChannel(
                        "my_service",
                        "计算完成",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    manager.createNotificationChannel(channel)
                }
                val intent = Intent(this@MsgService, MainActivity::class.java)
                val pi = PendingIntent.getActivity(this@MsgService, 0, intent, 0)
                mNotification = NotificationCompat.Builder(this@MsgService, "my_service")
                    .setContentTitle("计算完成")
                    .setContentText("共有" + result.size + "个素数")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pi)
                    .build()
                startForeground(1, mNotification)
            }
        }).start()
    }

    /**
     * 返回一个Binder对象
     */
    override fun onBind(intent: Intent): IBinder? {
        return MsgBinder()
    }

    inner class MsgBinder : Binder() {
        /**
         * 获取当前Service的实例
         * @return
         */
        val service: MsgService
            get() = this@MsgService
    }

    interface OnProgressListener {
        /**
         * 进度
         * @param progress
         */
        fun onProgress(progress: Int)

        /**
         * 结果返回
         * @param result
         */
        fun onResultCallback(result: List<Int>?)
    }

    companion object {
        private const val TAG = "MyService"
    }
}


