package com.relateddigital.relateddigital_android.inapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.Collections

object InAppActionManager {
    private const val LOG_TAG = "InAppActionManager"

    // Deduplication window: 30 seconds
    private const val DEDUPLICATION_INTERVAL_MILLIS = 30000L
    // Chained dismiss threshold: 5 seconds
    private const val CHAINED_DISMISS_THRESHOLD_MILLIS = 5000L
    // Fast track transition delay: 300 ms (0.3s)
    private const val FAST_TRACK_TRANSITION_DELAY_MILLIS = 300L

    private val mainHandler = Handler(Looper.getMainLooper())
    private val recentlyShownActionIds = Collections.synchronizedMap(HashMap<String, Long>())

    @Volatile
    private var lastDismissedTime: Long = 0L

    @Volatile
    var isActionShowing: Boolean = false
        private set

    private var scheduledAction: ScheduledActionItem? = null
    private val pendingQueue = ArrayList<PendingActionItem>()
    private var isLifecycleRegistered = false

    data class ScheduledActionItem(
        val actId: Any?,
        val runnable: Runnable,
        val scheduledAt: Long,
        val originalDelay: Long
    )

    data class PendingActionItem(
        val actId: Any?,
        val action: Runnable
    )

    fun init(application: Application) {
        if (isLifecycleRegistered) return
        synchronized(this) {
            if (isLifecycleRegistered) return
            application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
                override fun onActivityStarted(activity: Activity) {}
                override fun onActivityResumed(activity: Activity) {
                    if (isSdkActivity(activity)) {
                        isActionShowing = true
                    } else {
                        com.relateddigital.relateddigital_android.util.ActivityUtils.parentActivity = activity
                    }
                }
                override fun onActivityPaused(activity: Activity) {}
                override fun onActivityStopped(activity: Activity) {}
                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
                override fun onActivityDestroyed(activity: Activity) {
                    if (isSdkActivity(activity)) {
                        onActionDismissed()
                    } else if (com.relateddigital.relateddigital_android.util.ActivityUtils.parentActivity == activity) {
                        com.relateddigital.relateddigital_android.util.ActivityUtils.parentActivity = null
                    }
                }
            })
            isLifecycleRegistered = true
            Log.i(LOG_TAG, "InAppActionManager initialized with ActivityLifecycleCallbacks")
        }
    }

    private fun isSdkActivity(activity: Activity): Boolean {
        val className = activity.javaClass.name
        return className.startsWith("com.relateddigital.relateddigital_android.inapp.") &&
                !className.contains("PermissionActivity")
    }

    /**
     * Calculates the effective delay based on whether an action or notification was recently dismissed.
     */
    fun getEffectiveDelay(delayMillis: Long): Long {
        val timeSinceDismiss = System.currentTimeMillis() - lastDismissedTime
        return if (lastDismissedTime > 0 && timeSinceDismiss < CHAINED_DISMISS_THRESHOLD_MILLIS) {
            Log.i(LOG_TAG, "Chained action detected (dismissed ${timeSinceDismiss}ms ago). Overriding delay ${delayMillis}ms to ${FAST_TRACK_TRANSITION_DELAY_MILLIS}ms.")
            FAST_TRACK_TRANSITION_DELAY_MILLIS
        } else {
            delayMillis
        }
    }

    /**
     * Checks whether an actId was recently displayed/scheduled (deduplication).
     */
    fun isDuplicate(actId: Any?): Boolean {
        if (actId == null) return false
        val idStr = actId.toString()
        if (idStr.isEmpty() || idStr == "0" || idStr == "null") return false
        val lastShown = recentlyShownActionIds[idStr] ?: return false
        val elapsed = System.currentTimeMillis() - lastShown
        if (elapsed < DEDUPLICATION_INTERVAL_MILLIS) {
            Log.i(LOG_TAG, "Action with actId $idStr was shown ${elapsed}ms ago, deduplicating.")
            return true
        }
        return false
    }

    fun markShown(actId: Any?) {
        if (actId != null) {
            val idStr = actId.toString()
            if (idStr.isNotEmpty() && idStr != "0" && idStr != "null") {
                recentlyShownActionIds[idStr] = System.currentTimeMillis()
            }
        }
    }

    /**
     * Schedules or immediately shows an action/notification.
     */
    @Synchronized
    fun scheduleOrShow(actId: Any?, rawDelayMillis: Long, action: Runnable): Boolean {
        if (isDuplicate(actId)) {
            return false
        }

        markShown(actId)

        val effectiveDelay = getEffectiveDelay(rawDelayMillis)

        // If an action or popup is currently showing:
        if (isActionShowing) {
            Log.i(LOG_TAG, "An action is currently showing. Queueing actId=$actId for display on dismissal.")
            pendingQueue.add(PendingActionItem(actId, action))
            return true
        }

        if (effectiveDelay > 0) {
            // Cancel any previously scheduled action if replacing or schedule this one
            scheduledAction?.let {
                mainHandler.removeCallbacks(it.runnable)
            }
            lateinit var itemRunnable: Runnable
            itemRunnable = Runnable {
                synchronized(this@InAppActionManager) {
                    scheduledAction = null
                    isActionShowing = true
                }
                action.run()
            }
            val item = ScheduledActionItem(
                actId = actId,
                runnable = itemRunnable,
                scheduledAt = System.currentTimeMillis(),
                originalDelay = effectiveDelay
            )
            scheduledAction = item
            mainHandler.postDelayed(item.runnable, effectiveDelay)
            Log.i(LOG_TAG, "Scheduled action actId=$actId with effective delay ${effectiveDelay}ms")
        } else {
            isActionShowing = true
            mainHandler.post(action)
        }

        return true
    }

    /**
     * Called when any in-app notification or targeting action is dismissed.
     */
    @Synchronized
    fun onActionDismissed(actId: Any? = null) {
        lastDismissedTime = System.currentTimeMillis()
        isActionShowing = false
        Log.i(LOG_TAG, "Action dismissed at $lastDismissedTime. Checking scheduled or pending actions...")

        // Scenario 1: Fast-track scheduled action
        val currentScheduled = scheduledAction
        if (currentScheduled != null) {
            mainHandler.removeCallbacks(currentScheduled.runnable)
            scheduledAction = null
            Log.i(LOG_TAG, "Fast-tracking scheduled action (actId=${currentScheduled.actId}) on dismissal with ${FAST_TRACK_TRANSITION_DELAY_MILLIS}ms delay.")
            val fastTrackRunnable = Runnable {
                synchronized(this@InAppActionManager) {
                    isActionShowing = true
                }
                currentScheduled.runnable.run()
            }
            mainHandler.postDelayed(fastTrackRunnable, FAST_TRACK_TRANSITION_DELAY_MILLIS)
            return
        }

        // Scenario 4: Pop next pending action from queue
        if (pendingQueue.isNotEmpty()) {
            val nextItem = pendingQueue.removeAt(0)
            Log.i(LOG_TAG, "Displaying next pending action from queue (actId=${nextItem.actId}) with ${FAST_TRACK_TRANSITION_DELAY_MILLIS}ms delay.")
            mainHandler.postDelayed({
                synchronized(this@InAppActionManager) {
                    isActionShowing = true
                }
                nextItem.action.run()
            }, FAST_TRACK_TRANSITION_DELAY_MILLIS)
        }
    }
}
