package dev.suvera.opensource.scim2.compliance.biz;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

public class ScimApiException extends Exception {
    private int code;
    private Map<String, List<String>> responseHeaders;
    private JsonNode responseBody;
    private List<JsonNode> report;

    public ScimApiException() {
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
    }

    public ScimApiException(Throwable throwable) {
        super(throwable);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
    }

    public ScimApiException(String message) {
        super(message);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
    }

    public ScimApiException(ProcessingReport report, JsonNode response) {
        super("");
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = response;

        report.forEach(
            (ProcessingMessage message) -> {
                if (this.report == null) {
                    this.report = new java.util.LinkedList<>();
                }
                this.report.add(message.asJson());
            }      
        );
    }

    public ScimApiException(String message, Throwable throwable) {
        super(message, throwable);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
    }

    public ScimApiException(int code, String message) {
        super(message);
        this.code = 0;
        this.responseHeaders = null;
        this.responseBody = null;
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return this.responseHeaders;
    }

    public JsonNode getResponseBody() {
        return this.responseBody;
    }

    public List<JsonNode> getReport() {
        return this.report;
    }
}
