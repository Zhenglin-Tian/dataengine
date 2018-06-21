package com.tcredit.engine.conf.writer.response;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.tcredit.engine.conf.Mapping;
import com.tcredit.engine.conf.Step;
import com.tcredit.engine.conf.writer.Writer;
import com.tcredit.engine.context.ProcessContextEnum;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.data_process.orientDataProcessUtil.OrientDataProcessUtil;
import com.tcredit.engine.response.ResponseData;

import java.util.List;
import java.util.Map;

public class OrientDBWriter implements Writer {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory
            .getLogger(OrientDBWriter.class);
    private WriterWhere writerWhere;

    private String type = "OrientDB";

    @Override
    public void write(ProcessContextV2 cxt, Step step, ResponseData response) {
        String name = writerWhere.getName();
        //将结果插入orientDB
        OrientSaveData orientSaveData = new OrientSaveData();
        orientSaveData.cxt = cxt;
        orientSaveData.name = name;
        orientSaveData.response = response;
        orientSaveData.step = step;
        Thread thread = new Thread(orientSaveData);
        thread.start();
    }

    @Override
    public WriterWhere getWriterWhere() {
        return writerWhere;
    }

    @Override
    public void setWriterWhere(WriterWhere writerWhere) {
        this.writerWhere = writerWhere;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private class OrientSaveData implements Runnable {
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
    }
}
