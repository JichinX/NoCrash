package com.xujichang.nocrash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.createDataStore
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xujichang.nocrash.databinding.ActivityCheckErrorBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class ErrorCheckActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckErrorBinding
    private val TAG = "ErrorCheckActivity"
    private val dataStoreDir by lazy {
        File(this.filesDir, "datastore/error")
    }
    private val filesAdapter by lazy { ErrorFilesAdapter(::fileSelect) }


    private fun fileSelect(fileName: String) {
        //根据文件名打开datastore
        lifecycleScope.launch {
            createDataStore("error/$fileName", ErrorInfoSerializer).data.collectLatest {
                Handler(Looper.getMainLooper()).post {
                    updateErrorInfo(it)
                }
            }
        }
    }

    private fun updateErrorInfo(info: ErrorInfo?) {
        Log.i(TAG, "updateErrorInfo: $info")
        binding.drawer.closeDrawers()
        info?.also {
            binding.errorInfoContainer.visibility = View.VISIBLE
            binding.tip.visibility = View.GONE
        } ?: kotlin.run {
            binding.errorInfoContainer.visibility = View.GONE
            binding.tip.visibility = View.VISIBLE
        }
        binding.info = info
        binding.executePendingBindings()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckErrorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initAdapter(binding.rvFileNames)
        checkErrorFiles()
    }

    private fun initView() {

    }

    private fun initAdapter(recyclerView: RecyclerView?) {
        recyclerView?.also {
            //layout manager
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = filesAdapter
        }
    }

    private fun checkErrorFiles() {
        if (!dataStoreDir.exists()) {
            return
        }
        if (dataStoreDir.isFile) {
            return
        }
        val files = dataStoreDir.listFiles()?.map { file ->
            file.name
        }
        files?.also {
            Log.i(TAG, "checkErrorFiles: $it")
            val data = PagingData.from(files).apply {
                insertSeparators { str1, str2 ->
                    if (str1.isNullOrEmpty()) {
                        //前空
                        if (!str2.isNullOrEmpty()) {
                            val str2Members = str2.split("_")
                            return@insertSeparators "${str2Members[0]}_${str2Members[1]}_${str2Members[2]}"
                        } else {
                            return@insertSeparators null
                        }
                    }
                    if (str2.isNullOrEmpty()) {
                        //后空
                        return@insertSeparators null
                    }
                    //对比
                    val str1Members = str1.split("_")
                    val str2Members = str2.split("_")
                    //取长度小的
                    val length = minOf(str1Members.size, str2Members.size)
                    var isSameDay = false
                    //从0遍历
                    for (index in 0 until length) {
                        if (str1Members[index] != str2Members[index]) {
                            isSameDay = index > 2
                            break
                        }
                    }
                    if (!isSameDay) {
                        return@insertSeparators "${str2Members[0]}_${str2Members[1]}_${str2Members[2]}"
                    } else {
                        null
                    }
                }
            }
            filesAdapter.submitData(lifecycle, data)
        }
    }
}
