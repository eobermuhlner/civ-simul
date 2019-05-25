package ch.obermuhlner.simul.client.cli

import ch.obermuhlner.simul.server.RealWorldService

fun main(args: Array<String>) {
    SimulClient(RealWorldService()).execute(args)
}
