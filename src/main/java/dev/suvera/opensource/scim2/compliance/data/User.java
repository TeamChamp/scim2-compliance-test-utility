package dev.suvera.opensource.scim2.compliance.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.suvera.opensource.scim2.compliance.data.json.ScimMeta;
import lombok.Data;

import java.util.List;

/**
 * author: suvera
 * date: 9/3/2020 2:19 PM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private List<String> schemas;
    private String id;
    private ScimMeta meta;
    private Name name;
}

