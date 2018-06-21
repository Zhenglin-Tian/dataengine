package com.tcredit.engine.conf;

import com.google.common.collect.Maps;
import com.tcredit.engine.context.ProcessContext;
import com.tcredit.engine.context.ProcessContextV2;
import com.tcredit.engine.util.JsonUtil;
import com.tcredit.engine.util.variable.VariableUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-05 18:31
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-05 18:31
 * @updatedRemark:
 * @version:
 */
public class Params implements Serializable{
    private Set<Param> param;
    private Map<String, String> params = Maps.newHashMap();

    public Set<Param> getParam() {
        return param;
    }

    public void setParam(Set<Param> param) {
        this.param = param;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    /**
     * 参数解析
     * @param cxt
     */
    public void parseParam(ProcessContextV2 cxt) {
        if (param != null && !param.isEmpty()) {
            for (Param parameter : param) {
                String name = parameter.getName();
                String parse = VariableUtil.parse(parameter.getValue(), cxt);
                if (StringUtils.isNotBlank(parse)) {
                    params.put(name.toLowerCase(), parse);
                }
            }
        }

    }



    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }
}
