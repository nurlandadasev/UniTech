package az.unibank.commons.domains;


import az.unibank.commons.config.Constants;
import az.unibank.commons.enums.ResponseCode;

import java.util.LinkedHashMap;
import java.util.Map;

public class Result {

    private final Map<String, Object> data = new LinkedHashMap<>();

    public static Result Builder() {
        return new Result();
    }

    public Result response(ResponseCode responseCodes) {
        this.data.put(Constants.RESPONSE_CODE, responseCodes.value());
        this.data.put(Constants.RESPONSE_MESSAGE, responseCodes.toString());
        return this;
    }

    public Result add(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return data;
    }
}