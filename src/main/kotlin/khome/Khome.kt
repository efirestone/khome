package khome

import io.fluidsonic.time.LocalTime
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.client.request.host
import io.ktor.client.request.port
import io.ktor.util.KtorExperimentalAPI
import khome.core.Configuration
import khome.core.DefaultConfiguration
import khome.core.boot.EventResponseConsumer
import khome.core.boot.EventResponseConsumerImpl
import khome.core.boot.HassApiInitializer
import khome.core.boot.HassApiInitializerImpl
import khome.core.boot.StateChangeEventSubscriber
import khome.core.boot.StateChangeEventSubscriberImpl
import khome.core.boot.authentication.Authenticator
import khome.core.boot.authentication.AuthenticatorImpl
import khome.core.boot.servicestore.ServiceStore
import khome.core.boot.servicestore.ServiceStoreInitializer
import khome.core.boot.servicestore.ServiceStoreInitializerImpl
import khome.core.boot.servicestore.ServiceStoreInterface
import khome.core.boot.statehandling.EntityStateInitializer
import khome.core.boot.statehandling.EntityStateInitializerImpl
import khome.core.boot.subscribing.HassEventSubscriber
import khome.core.boot.subscribing.HassEventSubscriberImpl
import khome.core.clients.RestApiClient
import khome.core.clients.WebSocketClient
import khome.core.koin.KhomeComponent
import khome.core.koin.KhomeKoinContext
import khome.core.mapping.ObjectMapper
import khome.core.mapping.ObjectMapperInterface
import khome.entities.ActuatorStateUpdater
import khome.entities.EntityRegistrationValidation
import khome.entities.SensorStateUpdater
import khome.errorHandling.ErrorResponseData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

internal typealias TypeSerializers = MutableMap<KClass<*>, KSerializer<*>>

private const val NAME = "NAME"
private const val HOST = "HOST"
private const val PORT = "PORT"
private const val ACCESS_TOKEN = "ACCESS_TOKEN"
private const val SECURE = "SECURE"

/**
 * The lambda with [Khome] as receiver to configure Khome
 */
typealias KhomeBuilder = Khome.() -> Unit

/**
 * The main entry point to start your application
 *
 * @param init The type safe builder function to access the receiver
 * @return [KhomeApplication]
 */

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@KtorExperimentalAPI
fun khomeApplication(init: KhomeBuilder = {}): KhomeApplication =
    KhomeImpl().apply(init).createApplication()

/**
 * The main application interface.
 * Serves all the tools necessary for the application to run.
 *
 * @author Dennis Schröder
 */
interface Khome {
    /**
     * Configure your Khome instance. See all available properties in
     * the [Configuration] data class.
     *
     * @param builder Lambda with [Configuration] receiver to configure Khome.
     */
    fun configure(builder: Configuration.() -> Unit): Configuration

    fun <T : Any> registerSerializer(
        serializer: KSerializer<T>,
        valueObjectType: KClass<T>
    )
}

inline fun <reified T : Any> Khome.registerSerializer(adapter: KSerializer<T>) =
    registerSerializer(adapter, T::class)

@OptIn(ExperimentalStdlibApi::class, KtorExperimentalAPI::class, ObsoleteCoroutinesApi::class)
private class KhomeImpl : Khome, KhomeComponent {

    init {
        KhomeKoinContext.startKoinApplication()
        val module = module {
            single<Configuration> {
                DefaultConfiguration(
                    name = getProperty(NAME, "Khome"),
                    host = getProperty(HOST, "localhost"),
                    port = getProperty(PORT, "8123").toInt(),
                    accessToken = getProperty(ACCESS_TOKEN, "<some-fancy-access-token>"),
                    secure = getProperty(SECURE, "false").toBoolean()
                )
            }
        }
        KhomeKoinContext.addModule(module)
    }

    private val config: Configuration by inject()
    private val typeSerializers: TypeSerializers = mutableMapOf()

    override fun configure(builder: Configuration.() -> Unit) =
        config.apply(builder)

    override fun <T : Any> registerSerializer(
        serializer: KSerializer<T>,
        valueObjectType: KClass<T>
    ) {
        typeSerializers[valueObjectType] = serializer
    }

    fun createApplication(): KhomeApplicationImpl {
        val mapperModule = module {
            single {
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    serializersModule = SerializersModule {
                        typeSerializers.forEach { entry ->
                            @Suppress("UNCHECKED_CAST")
                            this.contextual(entry.key as KClass<Any>, entry.value as KSerializer<Any>)
                        }
                        this.contextual(JsonObject::class, JsonObject.serializer())
                    }
                }
            }
            single<ObjectMapperInterface> { ObjectMapper(get()) }
        }

        val internalModule: Module =
            module {
                single<ServiceStoreInterface> { ServiceStore() }

                single {
                    val client = HttpClient(CIO) {
                        install(JsonFeature) {
                            serializer = KotlinxSerializer(Json {
                                isLenient = true
                                ignoreUnknownKeys = true
                            })
                        }

                        val config = get<Configuration>()

                        defaultRequest {
                            host = config.host
                            port = config.port
                            header("Authorization", "Bearer ${config.accessToken}")
                            header("Content-Type", "application/json")
                        }
                    }
                    RestApiClient(client)
                }
                single<HassClient> {
                    HassClientImpl(
                        get(),
                        WebSocketClient(HttpClient(CIO).config { install(WebSockets) }),
                        get()
                    )
                }
                single<Authenticator> { (khomeSession: KhomeSession) -> AuthenticatorImpl(khomeSession, get()) }
                single<ServiceStoreInitializer> { (khomeSession: KhomeSession) ->
                    ServiceStoreInitializerImpl(
                        khomeSession,
                        get()
                    )
                }
                single<HassApiInitializer> { (khomeSession: KhomeSession) -> HassApiInitializerImpl(khomeSession) }
                single<HassEventSubscriber> { (khomeSession: KhomeSession, subscriptions: EventHandlerByEventType) ->
                    HassEventSubscriberImpl(
                        khomeSession,
                        subscriptions,
                        get()
                    )
                }

                single<EntityStateInitializer> { (khomeSession: KhomeSession, sensorStateUpdater: SensorStateUpdater, actuatorStateUpdater: ActuatorStateUpdater, entityRegistrationValidation: EntityRegistrationValidation) ->
                    EntityStateInitializerImpl(
                        khomeSession,
                        sensorStateUpdater,
                        actuatorStateUpdater,
                        entityRegistrationValidation
                    )
                }

                single<StateChangeEventSubscriber> { (khomeSession: KhomeSession) ->
                    StateChangeEventSubscriberImpl(
                        khomeSession
                    )
                }

                single<EventResponseConsumer> { (khomeSession: KhomeSession, sensorStateUpdater: SensorStateUpdater, actuatorStateUpdater: ActuatorStateUpdater, eventHandlerByEventType: EventHandlerByEventType, errorResponseHandler: (ErrorResponseData) -> Unit) ->
                    EventResponseConsumerImpl(
                        khomeSession = khomeSession,
                        sensorStateUpdater = sensorStateUpdater,
                        actuatorStateUpdater = actuatorStateUpdater,
                        objectMapper = get(),
                        eventHandlerByEventType = eventHandlerByEventType,
                        errorResponseHandler = errorResponseHandler
                    )
                }
            }

        KhomeKoinContext.addModule(mapperModule, internalModule)
        return KhomeApplicationImpl()
    }
}
