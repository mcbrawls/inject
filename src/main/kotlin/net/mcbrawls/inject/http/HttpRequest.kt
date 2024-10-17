package net.mcbrawls.inject.http

import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * An HTTP request.
 * @param request The request body, as a string.
 * @param headers The header map.
 */
data class HttpRequest(val request: String, val headers: Map<String, String>) {
    private val requestParts = request.split(" ")

    /**
     * The HTTP request method used by this request.
     */
    val requestMethod = requestParts[0]

    /**
     * The URI this request was sent to.
     */
    val requestURI = requestParts[1]

    /**
     * The HTTP protocol version used by the connection.
     */
    val protocolVersion = requestParts[2]

    /**
     * Gets a header.
     * @param header The headers name.
     * @return The value, if it exists.
     */
    operator fun get(header: String) = headers[header]

    companion object {
        /**
         * Parses an HTTP request from a [ByteBuf].
         */
        fun parse(buf: ByteBuf) = ByteBufInputStream(buf).use(::parse)

        private fun parse(stream: InputStream) = InputStreamReader(stream).use { reader ->
            val bufferedReader = BufferedReader(reader)
            val request = bufferedReader.readLine()
            val headers = readHeaders(bufferedReader)
            bufferedReader.close()

            HttpRequest(request, headers)
        }

        private fun readHeaders(reader: BufferedReader) = buildMap {
            var header = reader.readLine()
            while (header != null && header.isNotEmpty()) {
                val split = header.indexOf(':')
                this[header.substring(0, split)] = header.substring(split + 1).trim()

                header = reader.readLine()
            }
        }
    }
}
