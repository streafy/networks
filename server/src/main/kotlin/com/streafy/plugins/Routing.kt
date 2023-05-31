package com.streafy.plugins

import com.streafy.routes.routes
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        routes()
    }
}
