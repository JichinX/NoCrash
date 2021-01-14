package com.xujichang.nocrash

import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter
import java.net.UnknownHostException
import java.util.*

object ThrowableUtils {

    private const val CAUSE_CAPTION = "Caused by: "

    private const val SUPPRESSED_CAPTION = "Suppressed: "

    fun getStackTraceString(throwable: Throwable?): String =
        throwable?.let { tr ->
            var t: Throwable? = tr
            while (t != null) {
                if (t is UnknownHostException) {
                    return ""
                }
                t = t.cause
            }

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            printStackTrace(tr, pw)
            pw.flush()
            sw.toString()
        } ?: ""

    private fun printStackTrace(tr: Throwable, pw: PrintWriter) {
        printStackTrace(tr, WrappedPrintWriter(pw))
    }

    private fun printStackTrace(tr: Throwable, pw: WrappedPrintWriter) {
        val dejaVu = Collections.newSetFromMap(IdentityHashMap<Throwable, Boolean>())
        dejaVu.add(tr)

        synchronized(pw.lock()) {
            // Print our stack trace
            pw.println(tr)
            val trace: Array<StackTraceElement> = tr.stackTrace
            for (traceElement in trace) pw.println("\tat $traceElement")

            // Print suppressed exceptions, if any
            for (se in tr.suppressed) {
                printEnclosedStackTrace(
                    se,
                    pw,
                    trace,
                    SUPPRESSED_CAPTION,
                    "\t",
                    dejaVu
                )
            }

            // Print cause, if any
            tr.cause?.also {
                printEnclosedStackTrace(
                    it,
                    pw,
                    trace,
                    CAUSE_CAPTION,
                    "",
                    dejaVu
                )
            }
        }
    }

    private fun printEnclosedStackTrace(
        throwable: Throwable,
        pw: WrappedPrintWriter,
        enclosingTrace: Array<StackTraceElement>,
        caption: String,
        prefix: String,
        dejaVu: MutableSet<Throwable>
    ) {

        // Android-removed: Use of assert keyword which breaks serialization of some subclasses
        // (Using assert adds a static field that determines whether assertions are enabled.)
        // assert Thread.holdsLock(s.lock());
        if (dejaVu.contains(throwable)) {
            pw.println("\t[CIRCULAR REFERENCE:$throwable]")
        } else {
            dejaVu.add(throwable)
            // Compute number of frames in common between this and enclosing trace
            val trace: Array<StackTraceElement> = throwable.stackTrace
            var m = trace.size - 1
            var n: Int = enclosingTrace.size - 1
            while (m >= 0 && n >= 0 && trace[m] == enclosingTrace[n]) {
                m--
                n--
            }
            val framesInCommon = trace.size - 1 - m

            // Print our stack trace
            pw.println(prefix + caption + throwable)
            for (i in 0..m) {
                pw.println(prefix + "\tat " + trace[i])
            }
            if (framesInCommon != 0) {
                pw.println("$prefix\t... $framesInCommon common")
            }

            // Print suppressed exceptions, if any
            for (se in throwable.suppressed) {
                printEnclosedStackTrace(
                    se,
                    pw,
                    trace,
                    SUPPRESSED_CAPTION,
                    prefix + "\t", dejaVu
                )
            }

            // Print cause, if any
            throwable.cause?.also {
                printEnclosedStackTrace(
                    it,
                    pw,
                    trace,
                    CAUSE_CAPTION,
                    prefix,
                    dejaVu
                )
            }
        }
    }
}

private abstract class PrintStreamOrWriter {

    abstract fun lock(): Any?

    abstract fun println(o: Any?)
}

private class WrappedPrintStream constructor(private val printStream: PrintStream) :
    PrintStreamOrWriter() {
    override fun lock(): Any {
        return printStream
    }

    override fun println(o: Any?) {
        printStream.println(o)
    }
}

private class WrappedPrintWriter constructor(private val printWriter: PrintWriter) :
    PrintStreamOrWriter() {
    override fun lock(): Any {
        return printWriter
    }

    override fun println(o: Any?) {
        printWriter.println(o)
    }
}

