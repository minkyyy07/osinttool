import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.coroutines.runBlocking

fun main(args: Array<String>) {
    val parser = ArgParser("osint-search")
    val phone by parser.option(ArgType.String, shortName = "p", description = "Phone number to search")
    val address by parser.option(ArgType.String, shortName = "a", description = "Address to search")
    parser.parse(args)

    runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        if (phone != null) {
            println("Searching for phone number: $phone")
        }

        if (address != null) {
            println("Searching for address: $address")
            val url = "https://nominatim.openstreetmap.org/search"
            address?.let { safeAddress ->
                val response: String = client.get(url) {
                    url {
                        parameters.append("q", safeAddress)
                        parameters.append("format", "json")
                    }
                }.body()
                println("Response: $response")
            }
        }

        client.close()
    }
}