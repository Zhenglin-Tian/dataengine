package com.tcredit.engine.conf;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcredit.engine.conf.handler.Handler;
import com.tcredit.engine.conf.handler.httpHandler.HttpHandler;
import com.tcredit.engine.conf.reader.Reader;
import com.tcredit.engine.conf.writer.Writer;
import com.tcredit.engine.conf.writer.response.*;
import com.tcredit.engine.util.DeepCopy;
import com.tcredit.engine.util.FileUtil;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PathUtil;
import com.tcredit.engine.util.xml.Xml2Pojo;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 17:39
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-06 10:30:53
 * @updatedRemark:
 * @version:
 */
public class ConfigManagerV2 {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(ConfigManagerV2.class);
    public final static String MONGO = "mongo";
    public static final String CXT_LOG_WRITE_TYPE = "cxt_log";
    public static final String LOG_WRITE_TYPE = "log";
    public static final String CXT_WRITE_TYPE = "cxt";
    public static final String ORIENTDB_WRITE_TYPE = "OrientDB";
    /**
     * 维护所有的dataModule
     */
    private static Map<String, DataModule> dataModules = Maps.newHashMap();

    /**
     * 维护所有的数据处理
     */
    private static Map<String, Step> stepsCache = Maps.newHashMap();


    /**
     * 存储类型
     */
    private static Map<String, List<String>> dbType = Maps.newHashMap();

    /**
     * 根据id取出一个module
     *
     * @param moduleId
     * @return
     */
    public static DataModule get(String moduleId) {
        DataModule dataModule = getDataModules().get(moduleId);

        return DeepCopy.copy(dataModule);
    }

    /**
     * 添加一个module到全局dataModules中
     *
     * @param module
     */
    public static void put(DataModule module) {
        getDataModules().put(module.getId(), module);
    }

    /**
     * 根据stepId获取处理步骤
     *
     * @param stepId
     * @return
     */
    public static Step getStep(String stepId) {
        Step step = stepsCache.get(stepId);
        Step copy = DeepCopy.copy(step);

        return copy;
    }

    /**
     * 将step放进stepsCache
     *
     * @param step
     */
    public static void putStep(Step step) {
        getStepsCache().put(step.getId(), step);
    }

    /**
     * 初始化配置
     */
    public static void init() {
        List<String> list = new ArrayList<>();
        ConfigManagerV2.getDbType().put(ConfigManagerV2.MONGO, list);

        String projectDir = PathUtil.fetchParent(PathUtil.fetchProjectRootDir(), 2);

        /**
         * 获取所有数据处理步骤并转换
         */
        String stepDir = projectDir + "/config/step/";
        File[] files = FileUtil.listFiles(stepDir);
        if (files != null && files.length != 0) {
            for (File file : files) {
                Step step = initStep(file);
                if (step != null && StringUtils.isNotBlank(step.getId())) {
                    putStep(step);
                }
            }
        }

        /**
         * 获取所有的数据产品模块
         */
        String dpDir = projectDir + "/config/dp";
        File[] dpFiles = FileUtil.listFiles(dpDir);
        if (dpFiles != null && dpFiles.length != 0) {
            for (File dpFile : dpFiles) {
                DataModule dataModule = initDP(dpFile);
                if (dataModule != null && StringUtils.isNotBlank(dataModule.getId())) {
                    put(dataModule);
                }
            }
        }

    }

    /**
     * 处理化Step
     *
     * @param file
     * @return
     */
    public static Step initStep(File file) {
        if (file == null || !file.isFile()) return null;
        try {
            String s = Xml2Pojo.xml2Json(FileUtils.readFileToString(file, "utf-8"));

            Map<String, Object> map = JsonUtil.json2Object(s, Map.class);

            Object handlerObj = map.get("handler");
            Object writerObj = map.get("writer");
            Object readerObj = map.get("reader");
            map.remove("handler");
            map.remove("writer");
            map.remove("reader");
            Step step = JsonUtil.json2Object(JsonUtil.toJson(map), Step.class);

            Handler handler = createHandler(JsonUtil.toJson(handlerObj));
            step.setHandler(handler);
            Writer writer = createWriter(JsonUtil.toJson(writerObj));
            step.setWriter(writer);
            Reader reader = createReader(JsonUtil.toJson(readerObj));
            step.setReader(reader);

            return step;
        } catch (Exception e) {
            LOGGER.error("实例化step出错，文件信息:{},错误信息:{}", file.getName(), e);
        }
        return null;
    }

    /**
     * 根据类型type实例化handler
     *
     * @param handlerJson
     * @return
     */
    private static Handler createHandler(String handlerJson) {
        Handler handler = null;
        if (StringUtils.isNotBlank(handlerJson)) {
            try {
                Map map = JsonUtil.json2Object(handlerJson, Map.class);
                Object type = map.get("type");
                if (type != null && "http".equalsIgnoreCase(type.toString())) {
                    handler = JsonUtil.json2Object(handlerJson, HttpHandler.class);
                }
            } catch (Exception e) {
                LOGGER.error("实例化handler出错，handler信息:{},错误信息:{}", handlerJson, e);
            }
        }
        return handler;
    }

    /**
     * 根据类型type实例化writer
     *
     * @param writerJson
     * @return
     */
    private static Writer createWriter(String writerJson) {
        Writer writer = null;
        if (StringUtils.isNotBlank(writerJson) && writerJson != null && !writerJson.equals("null")) {
            JSONObject jsonObject = JSONObject.fromObject(writerJson);
            String writeType = String.valueOf(jsonObject.get("type"));
            if (CXT_LOG_WRITE_TYPE.equals(writeType)) {

                writer = JsonUtil.json2Object(writerJson, ContextLogWriter.class);
            } else if (CXT_WRITE_TYPE.equals(writeType)) {

                writer = JsonUtil.json2Object(writerJson, ContextWriter.class);
            } else if (LOG_WRITE_TYPE.equals(writeType)) {

                writer = JsonUtil.json2Object(writerJson, LogWriter.class);
            } else if (ORIENTDB_WRITE_TYPE.equals(writeType)) {

                writer = JsonUtil.json2Object(writerJson, OrientDBWriter.class);
            } else {
                writer = JsonUtil.json2Object(writerJson, ResponseWriter.class);
            }
        }
        return writer;
    }

    /**
     * 根据类型type实例化reader
     *
     * @param readerJson
     * @return
     */
    private static Reader createReader(String readerJson) {
        Reader reader = null;
        if (StringUtils.isNotBlank(readerJson)) {

        }
        return reader;
    }

    /**
     * 处理化dp  DataModule
     *
     * @param file
     * @return
     */
    public static DataModule initDP(File file) {
        if (file == null || !file.isFile()) return null;
        try {
            String s = Xml2Pojo.xml2Json(FileUtils.readFileToString(file, "utf-8"));
            DataModule dataModule = JsonUtil.json2Object(s, DataModule.class);

            validateDp(dataModule);

            return dataModule;
        } catch (Exception e) {
            LOGGER.error("构建DataModule出错,文件信息:{},错误信息:{}", file.getName(), e);
        }
        return null;

    }

    /**
     * 校验数据dp DataModule
     *
     * @param dataModule
     */
    private static void validateDp(DataModule dataModule) {
        if (dataModule == null) {
            throw new RuntimeException("dataModule为空");
        }
        if (StringUtils.isBlank(dataModule.getId())) {
            throw new RuntimeException("dataModule id 为空");
        }
        if (StringUtils.isBlank(dataModule.getStatus()) ||
                (!"able".equalsIgnoreCase(dataModule.getStatus()) && !"disable".equalsIgnoreCase(dataModule.getStatus()))) {
            throw new RuntimeException("dataModule status状态不合法 able|disable");
        }

        if (dataModule.getDataProcessingUnits() == null) {
            throw new RuntimeException("dataModule 需配置处理单元");
        } else {
            DataProcessingUnits dataProcessingUnits = dataModule.getDataProcessingUnits();
            String strategy = dataProcessingUnits.getStrategy();
            if (StringUtils.isBlank(strategy) ||
                    (!"order".equalsIgnoreCase(strategy) && !"random".equalsIgnoreCase(strategy) && !"percent".equalsIgnoreCase(strategy))) {
                throw new RuntimeException("dataProcessingUnits strategy 不合法，order|random|percent");
            }

            String failurePolicy = dataProcessingUnits.getFailurePolicy();
            if (StringUtils.isBlank(failurePolicy) ||
                    (!"next".equalsIgnoreCase(failurePolicy) && !"cancel".equalsIgnoreCase(failurePolicy))) {
                throw new RuntimeException("dataProcessingUnits failurePolicy 不合法，next|cancel");
            }

            Set<DataProcessingUnit> processingUnitList = dataProcessingUnits.getDataProcessingUnit();
            for (DataProcessingUnit unit : processingUnitList) {
                validateUnit(dataModule.getId(), unit);
            }
        }
    }

    private static void validateUnit(String id, DataProcessingUnit unit) {
        if (unit == null || StringUtils.isBlank(unit.getId())) {
            throw new RuntimeException("dataModule:" + id + "配置的处理单元错误");
        }
        if (StringUtils.isBlank(unit.getStatus()) ||
                (!"able".equalsIgnoreCase(unit.getStatus()) && !"disable".equalsIgnoreCase(unit.getStatus()))) {
            throw new RuntimeException("dataModule:" + id + "status状态不合法 able|disable");
        }

        Set<Step> steps = unit.getStep();
        List<String> stepIds = Lists.newArrayList();
        for (Step step : steps) {
            stepIds.add(step.getId());
        }

        for (Step step : steps) {
            if (StringUtils.isBlank(step.getId())) throw new RuntimeException("dataModule " + id + " step id 空");
            Step cacheStep = stepsCache.get(step.getId());
            if (cacheStep == null) throw new RuntimeException("dataModule:" + id + "配置的step:" + step.getId() + "尚未配置");

            String relyon = cacheStep.getRelyon();
            if (StringUtils.isNotBlank(relyon)) {
                String[] split = relyon.split(",");
                for (String sid : split) {
                    if (!stepIds.contains(sid)) {
                        throw new RuntimeException("dataModule:" + id + "配置的step:" + step.getId() + "其依赖step:" + sid + "未配置到dataModule");
                    }
                }
            }
        }

    }


    /**
     * 根据数据模块id选择合适的数据模块，返回的DataModule状态必须是able
     *
     * @param mId
     * @return
     */
    public static DataModule chooseDataModule(String mId) {
        if (StringUtils.isNotBlank(mId)) {
            DataModule dataModule = ConfigManagerV2.get(mId);
            if (dataModule != null && "able".equalsIgnoreCase(dataModule.getStatus())) {
                return dataModule;
            }
            //return DeepCopy.copy(dataModule);
        }
        return null;
    }

    /**
     * 查找数据模块下配置的数据单元
     */
    public static DataProcessingUnit getDPUByCmid(String mid, String cmid) {
        if (StringUtils.isBlank(mid) || StringUtils.isBlank(cmid)) return null;
        DataModule dataModule = chooseDataModule(mid);
        if (dataModule == null || dataModule.getDataProcessingUnits() == null) return null;
        DataProcessingUnits dataProcessingUnits = dataModule.getDataProcessingUnits();
        Set<DataProcessingUnit> processingUnitList = dataProcessingUnits.getDataProcessingUnit();
        for (DataProcessingUnit unit : processingUnitList) {
            if (cmid.equals(unit.getId())) {
                return unit;
            }
        }
        return null;
    }


    public ConfigManagerV2() {
    }

    //getter && setter
    private static Map<String, DataModule> getDataModules() {
        return dataModules;
    }

    public static void setDataModules(Map<String, DataModule> dataModules) {
        ConfigManagerV2.dataModules = dataModules;
    }

    private static Map<String, Step> getStepsCache() {
        return stepsCache;
    }

    public static void setStepsCache(Map<String, Step> stepsCache) {
        ConfigManagerV2.stepsCache = stepsCache;
    }

    public static Map<String, List<String>> getDbType() {
        return dbType;
    }

    public static void setDbType(Map<String, List<String>> dbType) {
        ConfigManagerV2.dbType = dbType;
    }

    public static void main(String[] args) {
        init();
    }
}
