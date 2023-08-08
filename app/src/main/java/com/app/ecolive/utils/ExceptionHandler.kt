package com.localmerchants.utils

import android.content.Context
import android.content.Intent
import android.os.Process
import com.app.ecolive.common_screen.SplashActivity


import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

class ExceptionHandler(
    private var myContext: Context?
): Thread.UncaughtExceptionHandler {


    /** (non-Javadoc)
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
    override fun uncaughtException(thead: Thread, exception: Throwable) {
        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))

        System.err.println(stackTrace) // You can use LogCat too

        val intent = Intent(myContext, SplashActivity::class.java)
        intent.putExtra(CRASH_REPORT, stackTrace.toString())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        myContext?.startActivity(intent)

        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    /**
     * To prevent memory leak caused by {@code myContext} after
     * Activity extending {@link CustomActivity} is destroyed
     */
    fun clearActivityInstance() {
        myContext = null
    }

    companion object {
        /** The Constant CRASH_REPORT. */
        const val CRASH_REPORT = "crashReport"
    }
}