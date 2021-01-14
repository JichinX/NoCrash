package com.xujichang.nocrash

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.atomic.AtomicReference

object ActivitiesCollect : Application.ActivityLifecycleCallbacks {
    private val TAG = "ActivitiesCollect"
    private val activities = LinkedList<Activity>()
    private val latestCreatedActivity = AtomicReference<WeakReference<Activity>>()
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.i(TAG, "onActivityCreated: $activity")
        updateLatestCreatedActivity(activity)
        activities.add(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        Log.i(TAG, "onActivityStarted: $activity")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.i(TAG, "onActivityResumed: $activity")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.i(TAG, "onActivityPaused: $activity")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.i(TAG, "onActivityStopped: $activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.i(TAG, "onActivityDestroyed: $activity")
        updateActivities(activity)
    }

    private fun updateActivities(activity: Activity) {
        if (activities.remove(activity)) {
            updateLatestCreatedActivity(activities.lastOrNull())
        }
    }


    private fun updateLatestCreatedActivity(activity: Activity?) {
        activity?.also {
            latestCreatedActivity.set(WeakReference(activity))
        }
    }


    fun getLatestActivity(): Activity? = latestCreatedActivity.get().get()

    /**
     * 退出 出现崩溃的Activity
     */
    fun cleanErrorActivityIfNeeded() {
        getLatestActivity()?.also {
            it.finish()
            updateActivities(it)
        }
    }

    fun getActivities(): LinkedList<Activity> = activities
}