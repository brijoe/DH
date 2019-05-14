package io.github.brijoe.example.parse;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;

/**
 * @date: 2019/5/14
 * @author:bridgeliang
 */
public class HttpUrlHelper {

    private static Map<String, Field> fieldMap = new HashMap<>(9);
//    private static final String[] fields = {
//            "scheme",
//            "username",
//            "password",
//            "host",
//            "port",
//            "pathSegments",
//            "queryNamesAndValues",
//            "fragment",
//            "url"};

    private static final String[] fields = {
            "host"};


    static {
        try {
            for (String fieldName : fields) {
                Field field = HttpUrl.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                fieldMap.put(fieldName, field);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HttpUrl sHttpUrl;

    public static HttpUrl create(String baseUrl) {
        sHttpUrl = HttpUrl.parse(baseUrl);
        return sHttpUrl;
    }

    public static HttpUrl create(HttpUrl httpUrl) {
        sHttpUrl = httpUrl;
        return httpUrl;
    }


    public static void changeBaseUrl(String url) {
        if (sHttpUrl == null)
            throw new IllegalArgumentException("you must invoke HttpUrlHelper.create(url) first!");
        try {
            HttpUrlParse parse = HttpUrlParse.parse(url);
            for (Map.Entry<String, Field> entry : fieldMap.entrySet()) {
                Object value = HttpUrlParse.class.getField(entry.getKey()).get(parse);
                fieldMap.get(entry.getKey()).set(sHttpUrl, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
