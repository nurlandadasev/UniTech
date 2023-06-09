package az.unibank.commons.util;

import az.unibank.commons.dto.auth.CommonPasswordCheckResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static <T> T convertJsonToObject(String json, Class<T> type) {
        if (isNull(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception e) {
            log.error("Error in convertJsonToObject", e);
            return null;
        }
    }

    public static String convertObjectToJson(Object object) {
        if (isNull(object)) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error in convertObjectToJson", e);
            return null;
        }
    }

    public static <T> T ifNull(T value, T defaultValue, Class<T> clazz) {
        return clazz.cast(value == null ? defaultValue : value);
    }

    public static Double castToDouble(Object number) {
        if (isNull(number)) {
            return null;
        } else if (number instanceof Integer) {
            return ((Integer) number) * 1.0;
        } else {
            return (Double) number;
        }
    }

    public static <T> List<List<T>> splitList(List<T> originalList, int partitionSize) {

        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i += partitionSize) {
            partitions.add(originalList.subList(i, Math.min(i + partitionSize, originalList.size())));
        }
        return partitions;
    }


    public static void waitUntilFileIsAvailable(File file) {
        try {
            for (int retry = 1; retry <= 10; retry++) {
                log.info("<waitUntilFileIsAvailable> retry {}", retry);
                if (file.length() > 0) {
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            log.error("<waitUntilFileIsAvailable> Error ", e);
        }
    }

    public static void decompressGzipFile(File source, File target) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(source))) {
            Files.copy(gis, target.toPath());
        }
    }


    public static RestTemplate buildRestTemplate(int timeoutInSeconds) {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(timeoutInSeconds))
                .setReadTimeout(Duration.ofSeconds(timeoutInSeconds))
                .build();
    }

    public static HttpHeaders buildBearerAuthHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    public static Double round(Double number, int decimal) {
        if (isNull(number)) {
            return null;
        }
        return round((double) number, decimal);
    }

    public static double round(double number, int decimal) {
        return Math.round(number * Math.pow(10, decimal)) / Math.pow(10, decimal);
    }

    public static boolean isNullOrEmpty(String str) {
        return isNull(str) || str.isBlank();
    }

    public static Double truncate(Double number, int places) {
        if (isNull(number)) {
            return number;
        }
        return truncate((double) number, places);
    }

    public static double truncate(double number, int places) {
        String formatted = String.valueOf(number);
        String[] parts = formatted.split("\\.");
        formatted = parts[0] + ".";
        if (parts[1].length() >= places) {
            formatted += parts[1].substring(0, places);
        } else {
            formatted += parts[1];
        }
        return Double.parseDouble(formatted);
    }


}