package com.xujichang.example

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.xujichang.nocrash.DataUtil
import com.xujichang.nocrash.ErrorCheckActivity
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {
    private val performError = AtomicBoolean(false)
    private lateinit var contextA: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startNewThread()
    }

    private fun startNewThread() {
        Thread {
            while (true) {
                if (performError.get()) {
                    performError()
                }
                Log.i(TAG, "startNewThread: .......")
                Thread.sleep(1000L)
            }
        }.start()
    }

    private fun performError() {
        contextA.resources.getString(R.string.app_name)
    }

    /**
     *
     * 子线程错误
     */
    fun performSubThreadError(view: View) {
        performError.set(true)
    }

    /**
     * 主线程错误
     */
    fun performMainThreadError(view: View) {
        contextA.resources.getString(R.string.app_name)
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }

    fun performToast(view: View) {
        Toast.makeText(this, "主线程正常", Toast.LENGTH_SHORT).show()
    }

    fun toNewActivity(view: View) {
        startActivity(Intent(this, NewActivity::class.java))
    }

    fun toErrorCheckActivity(view: View) {
        startActivity(Intent(this, ErrorCheckActivity::class.java))
    }

    fun clearErrorLog(view: View) {
        DataUtil.clearData(this).also {
            Toast.makeText(this, "日志清空${if (it) "成功" else "失败"}", Toast.LENGTH_SHORT).show()
        }
    }
}