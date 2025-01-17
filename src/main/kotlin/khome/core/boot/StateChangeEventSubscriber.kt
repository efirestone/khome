package khome.core.boot

import co.touchlab.kermit.Kermit
import khome.KhomeSession
import khome.communicating.CALLER_ID
import khome.core.ResultResponse

internal class StateChangeEventSubscriberImpl(
    val khomeSession: KhomeSession
) : StateChangeEventSubscriber {

    private val logger = Kermit()
    private val id
        get() = CALLER_ID.incrementAndGet()

    override suspend fun subscribe() {
        sendEventListenerRequest()
        consumeResultResponse().let { resultResponse ->
            when (resultResponse.success) {
                false -> logger.e { "Could not subscribe to state change events" }
                true -> logger.i { "Successfully started listening to state changes" }
            }
        }
    }

    private val eventListenerRequest =
        EventListeningRequest(id = id, eventType = "state_changed")

    private suspend fun sendEventListenerRequest() =
        khomeSession.callWebSocketApi(eventListenerRequest)

    private suspend fun consumeResultResponse() =
        khomeSession.consumeSingleMessage<ResultResponse>()
}

interface StateChangeEventSubscriber {
    suspend fun subscribe()
}
