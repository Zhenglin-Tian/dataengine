package com.tcredit.engine.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.Maps;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-16 上午11:22
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-16 上午11:22
 * @updatedRemark:
 * @version:
 */

public class JsonUtil {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(JsonUtil.class);
    /**
     * 缓存数组的大小
     */
    public static final int BUFFER_ARRAY_SIZE = 1024;
    /**
     *
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private JsonUtil() {
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> T jsonToGenericObject(String jsonString, TypeReference<T> tr) {
        if (jsonString != null && !"".equals(jsonString)) {
            try {
                return objectMapper.readValue(jsonString, tr);
            } catch (Exception var3) {
                LOGGER.error("json error:" + var3.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public static Map<String, Object> pojo2Map(Object object) {
        Map<String, Object> map = new HashMap();
        String jsonString = toJson(object);

        try {
            map = (Map) json2Object(jsonString, Map.class);
        } catch (Exception var4) {
            LOGGER.warn("json error:" + var4.getMessage());
        }

        return (Map) map;
    }

    public static String toJson(Object object) {
        String jsonString = "";

        try {
            jsonString = objectMapper.writeValueAsString(object);
        } catch (Exception var3) {
            LOGGER.warn("json error:" + var3.getMessage());
        }

        return jsonString;
    }

    public static <T> T json2Object(String jsonString, Class<T> c) {
        if (jsonString != null && !"".equals(jsonString)) {
            try {
                return objectMapper.readValue(jsonString, c);
            } catch (Exception var3) {
                LOGGER.warn("json error:" + var3.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }
    public static <T> T json2ObjectNoException(String jsonString, Class<T> clazz) {
        if (jsonString != null && !"".equals(jsonString)) {
            try {
                return objectMapper.readValue(jsonString, clazz);
            } catch (Exception var3) {
                //LOGGER.warn("json error:" + var3.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public static String json2Xml(String json) {
        StringReader input = new StringReader(json);
        StringWriter output = new StringWriter();
        JsonXMLConfig config = (new JsonXMLConfigBuilder()).multiplePI(false).repairingNamespaces(false).build();

        try {
            XMLEventReader reader = (new JsonXMLInputFactory(config)).createXMLEventReader(input);
            XMLEventWriter writer1 = XMLOutputFactory.newInstance().createXMLEventWriter(output);
            XMLEventWriter writer = new PrettyXMLEventWriter(writer1);
            writer.add(reader);
            reader.close();
            writer.close();
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        return output.toString();
    }

    public static String xml2Json(String xml) {
        if (!xml.startsWith("<")) {
            xml = xml.substring(xml.indexOf("<"), xml.lastIndexOf(">") + 1);
        }

        StringReader input = new StringReader(xml);
        StringWriter output = new StringWriter();
        JsonXMLConfig config = (new JsonXMLConfigBuilder()).autoArray(true).autoPrimitive(true).prettyPrint(false).build();

        try {
            XMLEventReader reader = XMLInputFactory.newInstance().createXMLEventReader(input);
            XMLEventWriter writer = (new JsonXMLOutputFactory(config)).createXMLEventWriter(output);
            writer.add(reader);
            reader.close();
            writer.close();
        } catch (Exception var14) {
            var14.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        String all = output.toString();
        int index = all.indexOf(58);
        if (index > 0) {
            all = all.substring(index + 1, all.length() - 1);
        }

        all = all.replaceAll(",{0,1}\"\\$\"\\:\\\"\\\\n\\\\t\\\"", "");
        return all.replaceAll("(\"@)(.+?\":)", "\"$2");
    }



    static {
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.getDeserializationConfig().withoutFeatures(new DeserializationFeature[]{DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES});
        objectMapper.setDateFormat(new SimpleDateFormat(DEFAULT_TIME_FORMAT));
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public static final class IosDateTimeJsonSerializer extends JsonSerializer<Date> {
        public IosDateTimeJsonSerializer() {
        }

        public void serialize(Date value, JsonGenerator paramJsonGenerator, SerializerProvider provider) throws IOException, JsonProcessingException {
            if (value != null) {
                paramJsonGenerator.writeString((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(value));
            }

        }
    }


    /**
     * 将字节数组转化为对象
     *
     * @param in
     * @param target_class
     * @param <T>
     * @return
     */
    public static <T> T parseObject(byte[] in, Class<T> target_class) {

        String contet = null;
        try {
            contet = new String(in, "utf-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        T a = json2Object(contet, target_class);

        return a;
    }


    /**
     * 将流转换为对象
     *
     * @param in
     * @param target_class
     * @param <T>
     * @return
     */
    public static <T> T parseObject(InputStream in, Class<T> target_class) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] temp = new byte[BUFFER_ARRAY_SIZE];

        try {
            while ((len = in.read(temp)) > 0) {
                out.write(temp, 0, len);
            }
            return parseObject(out.toByteArray(), target_class);
        } catch (IOException e) {
            //Console.error(JsonUtil.class,"在json转换时发生异常",e);
            throw new RuntimeException("在json转换时发生异常", e);

        }

    }

    public static void main(String[] args) throws Exception {
        Map<String,Object> map = Maps.newHashMap();
        map.put("start",new Date());
        map.put("ss",new Integer(2));
        String s = JsonUtil.toJson(map);
        System.out.println(s);

        Map<String,Object> map1 = JsonUtil.json2Object(s, Map.class);
        Object start = map1.get("start");
        System.out.println(start.getClass());
        System.out.println(map1.get("ss").getClass());
    }



}
