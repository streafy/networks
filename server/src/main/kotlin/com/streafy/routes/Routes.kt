package com.streafy.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import java.io.File

fun Route.routes() {
    route("/greeting") {
        get {
            call.respond("Hello")
        }
        get("{name?}") {
            val name = call.parameters["name"] ?: return@get call.respondText(
                "No user name",
                status = HttpStatusCode.BadRequest
            )
            call.respond("Hello $name")
        }
    }
    route("/html") {
        get {
            call.respondText("HTML response", ContentType.Text.Html, status = HttpStatusCode.OK)
        }
    }

    route("/directory/{path...}") {
        get {
            val path = call.parameters.getAll("path")?.joinToString("/")
            val file = File(path ?: "")
            val parentDirectory = file.parentFile

            if (!file.exists()) {
                call.respondText("File not found", status = HttpStatusCode.NotFound)
            }

            if (file.isDirectory) {
                val files = file.listFiles()

                call.respondHtml(HttpStatusCode.OK) {
                    head {
                        title { +"Directory" }
                    }
                    body {
                        h1 { +"Directory content: ${file.absolutePath}" }
                        ul {
                            if (parentDirectory != null) {
                                li {
                                    a(href = "/directory/${parentDirectory.path}") {
                                        +"Parent Directory"
                                    }
                                }
                            }
                            files?.forEach { file ->
                                li {
                                    val fileName = if (file.isDirectory) file.name + "/" else file.name
                                    a(href = "/directory/${file.path
                                        .removePrefix(file.absolutePath)
                                        .removePrefix("/")}") {
                                        +fileName
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (file.extension == "html") {
                    call.respondFile(file)
                } else {
                    call.respondText(file.readText(), status = HttpStatusCode.BadRequest)
                }
            }
        }
    }
}