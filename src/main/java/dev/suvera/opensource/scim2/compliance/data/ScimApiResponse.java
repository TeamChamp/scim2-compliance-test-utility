package dev.suvera.opensource.scim2.compliance.data;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class ScimApiResponse<T> {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final T data;

    public ScimApiResponse(int statusCode, Map<String, List<String>> headers, T data) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.data = data;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public T getData() {
        return this.data;
    }

    public JsonNode getResponseBody() {

        if (data instanceof String) {
            try {
                return new ObjectMapper().readTree((String)data);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

        return null;
    }
}