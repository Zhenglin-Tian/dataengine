package com.tcredit.engine.conf.writer.response;

import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.writer.Writer;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.response.ResponseData;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-11-28 16:51
 * @updatedUser: zl.T
 * @updatedDate: 2017-11-28 16:51
 * @updatedRemark:
 * @version:
 */
public class ResponseWriter implements Writer {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(ResponseWriter.class);
    private String type = "response";
    private WriterWhere writerWhere;
    private static final String WRITERTYPE_CXT = "cxt";
    private static final String WRITERTYPE_LOG = "log";
    private static final String WRITERTYPE_CXT_LOG = "cxt_log";
    private static final String WRITERTYPE_ORIENTDB = "OrientDB";


    @Override
    public void write(ProcessContextV2 cxt, Step step, ResponseData response) {

        /*String name = writerWhere.getName();
        if (writerWhere.getType().equals(WRITERTYPE_CXT)) {
            //上下文写入数据（数据产品）
            if (response != null) {
                cxt.getStepWriteMap().put(name, response);
                LOGGER.info("gid:" + cxt.get(ProcessContextEnum.GID) + ",mid:" + step.getId() + ",write写入上下文成功,key:" + name);
            }
        } else if (writerWhere.getType().equals(WRITERTYPE_LOG)) {
            //判断std是否执行失败
            if (cxt.isExceptionFlag()) {
                BeeSmellLog beeLog = new BeeSmellLog();
                beeLog.setGid(String.valueOf(cxt.get(ProcessContextEnum.GID)));
                beeLog.setTime_risk(DateUtil.formatNowDate2Str(DateUtil.DATE_FORMAT_yMdHmsSSS));
                beeLog.setServiceType("FAIL");
                ReportLogHelper.print(beeLog);
            }

        } else if (writerWhere.getType().equals(WRITERTYPE_CXT_LOG)) {
            //上下文写入数据（数据产品）并打印日志
            BeeSmellLog beeLog = new BeeSmellLog();
            beeLog.setGid(String.valueOf(cxt.get(ProcessContextEnum.GID)));
            beeLog.setTime_risk(System.currentTimeMillis()+"");
            if (response != null) {
                cxt.getStepWriteMap().put(name, response);
                LOGGER.info("gid:" + cxt.get(ProcessContextEnum.GID) + ",mid:" + step.getId() + ",write写入上下文成功,key:" + name);

                if (cxt.isExceptionFlag()) {
                    beeLog.setServiceType("FAIL");
                } else {
                    if (response.getCode().equals("0")) {
                        beeLog.setServiceType("SUCCESS");
                        beeLog.setFloat_livecanal_hit_sum((response.getData().get(0).getData().get(0).get("livecanal_hit_sum") == null ? null : (String.valueOf(response.getData().get(0).getData().get(0).get("livecanal_hit_sum")))));
                        beeLog.setFloat_loancanal_hit_sum(response.getData().get(0).getData().get(0).get("loancanal_hit_sum") == null ? null : (String.valueOf(response.getData().get(0).getData().get(0).get("loancanal_hit_sum"))));
                        beeLog.setFloat_total_hit_sum((response.getData().get(0).getData().get(0).get("total_hit_sum")) == null ? null : String.valueOf(response.getData().get(0).getData().get(0).get("total_hit_sum")));
                    } else {
                        beeLog.setServiceType("FAIL");
                    }
                }

            } else {

                beeLog.setServiceType("FAIL");
            }

            ReportLogHelper.print(beeLog);

        } else if (writerWhere.getType().equals(WRITERTYPE_ORIENTDB)) {
            //将结果插入orientDB
            OrientSaveData orientSaveData = new OrientSaveData();
            orientSaveData.cxt = cxt;
            orientSaveData.name = name;
            orientSaveData.response = response;
            orientSaveData.step = step;
            Thread thread = new Thread(orientSaveData);
            thread.start();
        }*/


    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public WriterWhere getWriterWhere() {
        return writerWhere;
    }

    public void setWriterWhere(WriterWhere writerWhere) {
        this.writerWhere = writerWhere;
    }

    /*class OrientSaveData implements Runnable {
        ResponseData response;
        String name;
        ProcessContextV2 cxt;
        Step step;

        @Override
        public void run() {
            ODatabaseDocumentTx databaseDocument = OrientDataProcessUtil.getFactory(OrientDataProcessUtil.RC_DATAENGINE).acquire();
            Map<String, Object> map = response.getData().get(0).getData().get(0);
            List<Mapping> mappings = writerWhere.getMapper().getMappings();
            ODocument entries = new ODocument(name);
            for (Mapping mapping : mappings) {
                String source = mapping.getSource();
                String target = mapping.getTarget();
                if (target.equals("rowKey")) {
                    entries.field(target, map.get(source) + "-10000");
                } else {
                    entries.field(target, map.get(source));
                }

            }
            entries.save();
            LOGGER.info("gid:" + cxt.get(ProcessContextEnum.GID) + ",mid:" + step.getId() + ",write写入OrientDB成功");
            databaseDocument.close();

        }
    }*/
}
