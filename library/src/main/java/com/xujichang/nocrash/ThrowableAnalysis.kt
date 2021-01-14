package com.xujichang.nocrash

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.util.Log

object ThrowableAnalysis {
    private val TAG = "ThrowableAnalysis"

    fun handle(context: Context?, thread: Thread, throwable: Throwable) {
        context?.also {
            //错误发生线程
            //错误信息
            val errorInfo = collectErrorInfo(throwable)
            //设备信息
            val deviceInfo = collectDeviceInfo(context)
            //设备运行信息
            val deviceRuntimeInfo = collectRuntimeInfo(context)
            //网络环境
            val networkInfo = collectNetWorkInfo(context)
            //请求信息
            val requestInfos = collectRequestInfo()
            //Activity栈信息
            val activitiesInfo = collectActivitiesInfo(context)
            ErrorDialog.show(thread, throwable)
            //写入缓存文件
            DataUtil.saveErrorInfo(
                context,
                errorInfo,
                deviceInfo,
                networkInfo,
                requestInfos,
                activitiesInfo,
                deviceRuntimeInfo
            )
        }
    }

    private fun collectRuntimeInfo(context: Context): String {
        //内存使用情况
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        //在运行的APP
        return """
            |可分配最大内存：${activityManager.memoryClass}
            |最大可分配内存：${Runtime.getRuntime().maxMemory().toM()}            
            |当前已分配内存：${Runtime.getRuntime().totalMemory().toM()}            
            |当前空闲内存：${Runtime.getRuntime().freeMemory().toM()}            
        """.trimMargin()
    }

    /**
     *收集网络请求信息
     */
    private fun collectRequestInfo(): String {
        return "";
    }

    /**
     *收集Activity的栈内情况
     */
    private fun collectActivitiesInfo(context: Context): String {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.appTasks.joinToString(separator = "\n") { appTask ->
            val taskInfo = appTask.taskInfo
            """|Task#${taskInfo.flag()}: 
               |栈底Activity->${taskInfo.baseActivity()?.className} 
               |栈顶Activity->${taskInfo.topActivity()?.className} 
               |Activity数量->${taskInfo.numActivities()}""".trimMargin()
        }
    }

    /**
     *收集网络信息
     */
    private fun collectNetWorkInfo(context: Context): String = NetworkUtil.getNetworkState(context)

    /**
     * 收集设备信息
     */
    private fun collectDeviceInfo(context: Context): String =
        //        val displayInfo = DisplayUtils.collectInfo(context)
        """|设备名称:${Build.DEVICE} 
           |厂家:${Build.BRAND} 
           |型号:${Build.MODEL} 
           |系统信息:${Build.DISPLAY},${Build.VERSION.INCREMENTAL},${Build.MANUFACTURER}  
           |API级别:${Build.VERSION.SDK_INT}
           |支持的API类型：${Build.SUPPORTED_ABIS?.joinToString()}    
           """.trimMargin()

    /**
     * 收集错误信息
     */
    private fun collectErrorInfo(throwable: Throwable): String =
        Log.getStackTraceString(throwable)
}

private fun ActivityManager.RecentTaskInfo.numActivities(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        numActivities
    } else {
        0
    }
}

private fun ActivityManager.RecentTaskInfo.topActivity(): ComponentName? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        topActivity
    } else {
        null
    } /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        topActivity
    } else {

    }*/
}

private fun ActivityManager.RecentTaskInfo.baseActivity(): ComponentName? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        baseActivity
    } else {
        null
    }
}

private fun ActivityManager.RecentTaskInfo.flag(): String {
    return this.description?.toString() ?: taskDescription?.label ?: id.toString()
}

private fun Long.toM(): String = (this * 1.0 / (1024 * 1024)).toString()
