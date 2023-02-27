package xyz.kandrac

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import java.util.regex.Pattern

internal fun <T> runWithTimeout(
    timeoutMs: Long,
    onSuccess: (T) -> Unit,
    onTooLate: (T) -> Unit,
    onTimeout: () -> Unit,
    task: suspend () -> T
) {
    val timerExecutor = newSingleThreadAsyncContext()
    val taskExecutor = newSingleThreadAsyncContext()

    var timerJob: Job? = null

    val workerJob = KtxAsync.launch {
        withContext(taskExecutor) {
            val result = task()
            if (timerJob?.isActive == true) {
                onSuccess(result)
            } else {
                onTooLate(result)
            }
        }
    }

    timerJob = KtxAsync.launch {
        withContext(timerExecutor) {
            delay(timeoutMs)
            if (workerJob.isActive) {
                onTimeout()
            }
        }
    }
}

internal enum class Os {
    WINDOWS, OTHER  // (assuming *nix based environment - Linux, Mac)
}

internal fun getOs() : Os {
    val osName = System.getProperty("os.name")
    return when {
        osName.startsWith("Windows") -> Os.WINDOWS
        else -> Os.OTHER
    }
}

internal val isWindows get() = getOs() == Os.WINDOWS
internal val isNotWindows get() = getOs() != Os.WINDOWS

internal const val emailRegexPattern = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

fun isMailValid(emailAddress: String): Boolean {
    return Pattern.compile(emailRegexPattern)
        .matcher(emailAddress)
        .matches()
}
