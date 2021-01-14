package com.xujichang.nocrash

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.startup.Initializer
import java.lang.ref.WeakReference

class NoCrashStartup : Initializer<Unit> {
    private val TAG = "NoCrashStartup"
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var contextW: WeakReference<Context>
    override fun create(context: Context) {
        Log.i(TAG, "create: ")
        contextW = WeakReference(context)
        //拦截子线程崩溃
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            mainHandler.post {
                ThrowableAnalysis.handle(contextW.get(), thread, throwable)
            }
        }
        //拦截主线程崩溃
        mainHandler.post {
            while (true) {
                try {
                    Looper.loop()
                } catch (throwable: Throwable) {
                    ThrowableAnalysis.handle(
                        contextW.get(),
                        Thread.currentThread(),
                        throwable
                    )
                }
            }
        }
        (context as? Application)?.registerActivityLifecycleCallbacks(ActivitiesCollect)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}