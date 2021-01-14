package com.xujichang.nocrash

import android.os.Looper
import com.afollestad.materialdialogs.MaterialDialog

/**
 * APP崩溃弹窗
 */
object ErrorDialog {
    fun show(thread: Thread, throwable: Throwable) {
        ActivitiesCollect.getLatestActivity()?.also { activity ->
            MaterialDialog(activity).show {
                title(text = "${currentThreadType(thread)}出现错误！")
                message(text = throwable.message)
                setOnDismissListener {
                    ActivitiesCollect.cleanErrorActivityIfNeeded()
                }
            }
        }
    }

    private fun currentThreadType(thread: Thread): String =
        if (isMainThread(thread)) "主线程" else thread.name

    private fun isMainThread(thread: Thread): Boolean =
        thread.id == Looper.getMainLooper().thread.id
}
