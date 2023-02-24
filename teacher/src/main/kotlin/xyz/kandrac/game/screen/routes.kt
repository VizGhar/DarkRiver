package xyz.kandrac.game.screen

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking

internal val routeEvents : MutableSharedFlow<String> = MutableSharedFlow()

fun route(url: String) = runBlocking { routeEvents.emit(url) }
