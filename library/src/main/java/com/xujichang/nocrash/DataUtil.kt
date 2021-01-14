package com.xujichang.nocrash

import android.content.Context
import android.util.Log
import androidx.datastore.createDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

object DataUtil : CoroutineScope {
    private val numberCounter = AtomicInteger()
    fun saveErrorInfo(
        context: Context,
        errorInfo: String,
        deviceInfo: String,
        networkInfo: String,
        requestInfos: String,
        activitiesInfo: String,
        deviceRuntimeInfo: String
    ) {
        Log.e(
            TAG,
            "saveErrorInfo: ---------------------------------------------------------------"
        )
        Log.e(TAG, "error: $errorInfo")
        Log.e(TAG, "device: $deviceInfo")
        Log.e(TAG, "network: $networkInfo")
        Log.e(TAG, "request: $requestInfos")
        Log.e(TAG, "activities: $activitiesInfo")
        Log.e(TAG, "runtimeInfo: $deviceRuntimeInfo")
        Log.e(
            TAG,
            "saveErrorInfo: ---------------------------------------------------------------"
        )
        launch {
            val info = ErrorInfo.newBuilder()
                .setActivitiesInfo(activitiesInfo)
                .setDeviceInfo(deviceInfo)
                .setErrorInfo(errorInfo)
                .setNetworkInfo(networkInfo)
                .setRequestInfo(requestInfos)
                .setRuntimeInfo(deviceRuntimeInfo)
                .build()
            saveErrorInfo(context, info)
        }
    }

    /**
     * 保存错误日志
     */
    private suspend fun saveErrorInfo(context: Context, errorInfo: ErrorInfo) {
        val fileName = createUniqueFileName(context)

        val datastore = context.createDataStore(fileName, ErrorInfoSerializer)
        datastore.updateData { errorInfo }
    }

    private fun createUniqueFileName(context: Context): String {
        numberCounter.set(0)
        val fileName = generateFileName()
        var tempFileName = fileName
        val errorDir = File(context.filesDir, "datastore")
        while (File(errorDir, "$tempFileName.proto").exists()) {
            tempFileName = "${fileName}_${numberCounter.incrementAndGet()}"
        }
        return "$tempFileName.proto"
    }

    /**
     *创建文件名称
     */
    private fun generateFileName(): String {
        val dateTime = LocalDateTime.now()
        return "error/${dateTime.year}_${dateTime.monthValue}_${dateTime.dayOfMonth}_${dateTime.hour}_${dateTime.minute}"
    }

    fun clearData(context: Context): Boolean {
        val dataStoreDir = File(context.filesDir, "datastore")
        val errDir = File(dataStoreDir, "error")
        return if (errDir.exists()) {
            errDir.listFiles()?.forEach {
                it.delete()
            }
            errDir.delete()
        } else true
    }

    private val TAG = "DataUtil"
    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.IO
    }
}