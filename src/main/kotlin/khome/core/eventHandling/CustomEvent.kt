package khome.core.eventHandling

import khome.core.dependencyInjection.KhomeComponent
import org.koin.core.get

typealias EventData = Map<String, Any>

abstract class CustomEvent(eventName: String, delegate: Event<EventData> = Event()) : KhomeComponent(), CustomEventInterface<EventData> {
    final override val eventType: String = eventName

    init {
        registerInEventRegistry()
    }

    private fun registerInEventRegistry() = get<CustomEventRegistry>().register(eventType, this)
    private val event = delegate
    override val listenerCount get() = event.listeners.size
    override fun subscribe(handle: String?, callback: EventData.() -> Unit) {
        if (handle == null) event += callback else event[handle] = callback
    }

    override fun unsubscribe(handle: String) {
        event -= handle
    }

    override fun emit(eventData: EventData) = event(eventData)
}
