package net.mcbrawls.inject.api.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String request;
    private final Map<String, String> headers;
    private final String[] requestParts;

    public HttpRequest(String request, Map<String, String> headers) {
        this.request = request;
        this.headers = headers;
        this.requestParts = request.split(" ");
    }

    public String getRequestMethod() {
        return requestParts[0];
    }

    public String getRequestURI() {
        return requestParts[1];
    }

    public String getProtocolVersion() {
        return requestParts[2];
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public static HttpRequest parse(ByteBuf buf) throws Exception {
        try (ByteBufInputStream stream = new ByteBufInputStream(buf)) {
            return parse(stream);
        }
    }

    private static HttpRequest parse(InputStream stream) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(stream);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String request = bufferedReader.readLine();
            Map<String, String> headers = readHeaders(bufferedReader);
            return new HttpRequest(request, headers);
        }
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws Exception {
        Map<String, String> headers = new HashMap<>();
        String header = reader.readLine();
        while (header != null && !header.isEmpty()) {
            int split = header.indexOf(':');
            headers.put(header.substring(0, split), header.substring(split + 1).trim());
            header = reader.readLine();
        }
        return headers;
    }
}