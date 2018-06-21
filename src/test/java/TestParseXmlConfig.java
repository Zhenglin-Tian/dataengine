import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcredit.engine.conf.DataModule;
import com.tcredit.engine.conf.DataProcessingUnit;
import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.handler.httpHandler.HttpHandler;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.PathUtil;
import com.tcredit.engine.util.StepPriorityUtil;
import com.tcredit.engine.util.xml.Xml2Pojo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-02 14:13
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-02 14:13
 * @updatedRemark:
 * @version:
 */
public class TestParseXmlConfig {
    private static Set<Step> stepCache = Sets.newHashSet();
    private static Map<String, DataModule> dpCache = Maps.newHashMap();

    public static void main(String[] args) throws IOException {
        init();

        for (Step s : stepCache) {
            System.out.println(s);
        }

        System.out.println();
        System.out.println("----------------------------------------");

        for (DataModule dp : dpCache.values()) {
            System.out.println(dp);
        }


        DataModule applyform = dpCache.get("unionpaytransactionvariableb");
        DataProcessingUnit unit = applyform.getDataProcessingUnits().getDataProcessingUnit().stream().collect(Collectors.toList()).get(0);
        Set<Step> steps = unit.getStep();
        List<Set<String>> stepPriority = sortedSteps(steps);
        for (Set<String> set : stepPriority) {
            for (String id : set) {
                System.out.println(id);
            }
            System.out.println();
        }



    }

    private static List<Set<String>> sortedSteps(Set<Step> steps) {
        if (steps == null || steps.isEmpty()) return null;
        Set<Step> stepsInUse = Sets.newHashSet();
        for (Step step : steps) {
            String id = step.getId();
            for (Step cacheStep : stepCache) {
                if (id.equalsIgnoreCase(cacheStep.getId())) {
                    stepsInUse.add(cacheStep);
                }
            }
        }

        List<Set<String>> stepPriority = StepPriorityUtil.getStepPriority(stepsInUse);
        Map<String, Set<String>> relyOn = StepPriorityUtil.getRelyOn(stepsInUse);
        for (String id:relyOn.keySet()){
            System.out.println(id);
            for (String rid:relyOn.get(id)){
                System.out.println("--"+rid);
            }
            System.out.println();
        }
        System.out.println("=============================================");

        return stepPriority;
    }

    public static void init() throws IOException {
        String projectDir = PathUtil.fetchParent(PathUtil.fetchProjectRootDir(), 1);
        String stepPath = projectDir + "/test-classes/step/";
        String dpPath = projectDir + "/test-classes/dp";

        File stepFiles = new File(stepPath);
        if (stepFiles.isDirectory()) {
            for (File stepFile : stepFiles.listFiles()) {
                Step step = initStep(stepFile);
                if (step != null) {
                    stepCache.add(step);
                }
            }
        }

        File dpFiles = new File(dpPath);
        if (dpFiles.isDirectory()) {
            for (File dpFile : dpFiles.listFiles()) {
                DataModule dataModule = initDP(dpFile);
                if (dataModule != null) {
                    dpCache.put(dataModule.getId(), dataModule);
                }
            }
        }


    }

    public static Step initStep(File file) {
        if (file == null || !file.isFile()) return null;
        try {
            String s = Xml2Pojo.xml2Json(FileUtils.readFileToString(file, "utf-8"));

            Map<String, Object> map = JsonUtil.json2Object(s, Map.class);

            Object handler = map.get("handler");
            Object writer = map.get("writer");
            Object reader = map.get("reader");
            map.remove("handler");
            map.remove("writer");
            map.remove("reader");
            Step step = JsonUtil.json2Object(JsonUtil.toJson(map), Step.class);
            if (Map.class.isInstance(handler)) {
                Map<String, Object> handler1 = (Map<String, Object>) handler;
                Object type = handler1.get("type");
                if (type != null && "http".equalsIgnoreCase(type.toString())) {
                    HttpHandler httpHandler = JsonUtil.json2Object(JsonUtil.toJson(handler), HttpHandler.class);
                    step.setHandler(httpHandler);
                }
            }
            return step;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static DataModule initDP(File file) {
        if (file == null || !file.isFile()) return null;
        try {
            String s = Xml2Pojo.xml2Json(FileUtils.readFileToString(file, "utf-8"));
            DataModule dataModule = JsonUtil.json2Object(s, DataModule.class);
            return dataModule;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
