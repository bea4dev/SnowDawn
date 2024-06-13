package level.listener

import net.minestom.server.MinecraftServer
import net.minestom.server.event.instance.InstanceRegisterEvent
import net.minestom.server.instance.Weather

fun registerLevelListener() {
    registerLevelLoadListener()
}

private fun registerLevelLoadListener() {
    MinecraftServer.getGlobalEventHandler().addListener(InstanceRegisterEvent::class.java) { event ->
        event.instance.weather = Weather.RAIN
        event.instance.time = 6000
    }
}