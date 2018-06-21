package com.tcredit.engine.strategy;

import com.google.common.collect.Lists;
import com.tcredit.engine.conf.DataProcessingUnit;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-05 14:02
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-05 14:02
 * @updatedRemark:
 * @version:
 */
public abstract class DPUSelectStrategy implements Strategy {

    /**
     *
     */
    private List<DataProcessingUnit> dataProcessingUnits = Lists.newCopyOnWriteArrayList();
    /**
     * 选择一个可以执行的数据处理单元，一个数据处理单元包含多个数据处理过程，如std->tidy->varcalc->model等
     * @param currentUnit 当前使用的数据处理单元
     * @return
     */
    public abstract DataProcessingUnit select(DataProcessingUnit currentUnit);

    /**
     * 对所有的DataProcessingUnit排序，并且移除已经使用的DataProcessingUnit
     * @param currentUnit 当前正在使用的数据处理单元
     * @return
     */
    public List<DataProcessingUnit> getEnableUnits(DataProcessingUnit currentUnit) {
        for (DataProcessingUnit unit : dataProcessingUnits) {
            if (!"able".equalsIgnoreCase(unit.getStatus())) {
                dataProcessingUnits.remove(unit);
            }
        }
        dataProcessingUnits.remove(currentUnit);
        return dataProcessingUnits;
    }


    public DPUSelectStrategy(List<DataProcessingUnit> dataProcessingUnits) {
        this.dataProcessingUnits.addAll(dataProcessingUnits);
    }
    public DPUSelectStrategy(Set<DataProcessingUnit> dataProcessingUnits) {
        this.dataProcessingUnits.addAll(dataProcessingUnits);
    }

}
