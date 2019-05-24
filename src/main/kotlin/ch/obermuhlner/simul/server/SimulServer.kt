package ch.obermuhlner.simul.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SimulServer

fun main(args: Array<String>) {
    SpringApplication.run(SimulServer::class.java, *args)
}