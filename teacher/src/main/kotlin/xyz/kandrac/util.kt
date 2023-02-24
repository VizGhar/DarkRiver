package xyz.kandrac

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext

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