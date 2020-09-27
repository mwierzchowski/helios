package com.github.mwierzchowski.helios.core.rules;

import com.github.mwierzchowski.helios.core.scenes.Scene;
import lombok.Data;

import java.util.Map;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

@Data
public abstract class SceneRule {
    private Long id;
    private Integer priority;
    private Scene scene;
    private Map<String, String> data;

    public abstract boolean check(Map<String, Object> facts);

    public String getDataAsString(String key) {
        return data.get(key);
    }

    public Integer getDataAsInteger(String key) {
        return parseInt(data.get(key));
    }

    public Boolean getDataAsBoolean(String key) {
        return parseBoolean(data.get(key));
    }
}
