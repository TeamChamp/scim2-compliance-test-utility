package dev.suvera.opensource.scim2.compliance.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;

/**
 * author: suvera
 * date: 9/3/2020 2:23 PM
 */
@Data
@NoArgsConstructor
public class TestCaseResult {
    private boolean success = false;
    private boolean notSupported = false;

    private String title = "";

    private String requestBody = "";
    private String requestMethod = "";

    private JsonNode responseBody;
    private int responseCode;
    private Map<String, List<String>> responseHeaders = new HashMap<>();

    private List<JsonNode> report;

    private String message;

    public TestCaseResult(String title) {
        this.title = title;
    }


    public void setResponseBody(JsonNode node)
    {
        this.responseBody = node;
    }

    public void setResponseBody(String input) {
        try {
            // Try to parse input as JSON if it looks like JSON
            if (input != null && (input.trim().startsWith("{") || input.trim().startsWith("["))) {
                this.responseBody = new ObjectMapper().readTree(input);
            } else {
                // Otherwise treat as plain string
                this.responseBody = new ObjectMapper().valueToTree(input);
            }
        } catch (JsonProcessingException e) {
            // If parsing fails, treat as a plain string
            this.responseBody = new ObjectMapper().valueToTree(input);
        }
    }

    public <T> void setResponseBody(T object) {
        this.responseBody = new ObjectMapper().valueToTree(object);
    }


    @Override
    public String toString() {
        return "\nTestCaseResult{" +
                "\n\t  success=" + success +
                "\n\t  notSupported=" + notSupported +
                "\n\t, title='" + title + '\'' +
                "\n\t, requestBody='" + requestBody + '\'' +
                "\n\t, requestMethod='" + requestMethod + '\'' +
                "\n\t, responseBody='" + responseBody + '\'' +
                "\n\t, responseCode=" + responseCode +
                "\n\t, responseHeaders=" + responseHeaders +
                "\n\t, messages=" + ( report != null ? report : "null\n") +
                '}';
    }
}
