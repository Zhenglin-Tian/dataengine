package com.tcredit.engine.strategy.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tcredit.engine.conf.DataProcessingUnit;
import com.tcredit.engine.strategy.DPUSelectStrategy;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-05 14:25
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-05 14:25
 * @updatedRemark:
 * @version:
 */
public class PercentDPUSelectStrategy extends DPUSelectStrategy {

    @Override
    public DataProcessingUnit select(DataProcessingUnit currentUnit) {
        List<DataProcessingUnit> enableUnits = getEnableUnits(currentUnit);
        if (enableUnits == null || enableUnits.isEmpty()) {
            return null;
        }
        return select(enableUnits);
    }

    /**
     *
     * @param unitList
     * @return
     */
    private DataProcessingUnit select(List<DataProcessingUnit> unitList) {
        if (unitList == null || unitList.isEmpty()) {
            return null;
        }

        List<String> units = Lists.newArrayList();
        for (DataProcessingUnit unit : unitList) {
            for (int i = 0; i < unit.getPercent(); i++) {
                units.add(unit.getId());
            }
        }
        String unitId = null;
        if (!units.isEmpty()) {
            Random random = new Random();
            int i = random.nextInt(units.size());
            unitId = units.get(i);
        }


        return getDataProcessingUnitById(unitList, unitId);
    }

    private DataProcessingUnit getDataProcessingUnitById(List<DataProcessingUnit> unitList, String unitId) {
        if (unitList == null || unitList.isEmpty() || StringUtils.isBlank(unitId)) {
            return null;
        }
        for (DataProcessingUnit dataProcessingUnit : unitList) {
            if (unitId.equalsIgnoreCase(dataProcessingUnit.getId())) {
                return dataProcessingUnit;
            }
        }
        return null;
    }


    public PercentDPUSelectStrategy(List<DataProcessingUnit> dataProcessingUnits) {
        super(dataProcessingUnits);
    }

    public PercentDPUSelectStrategy(Set<DataProcessingUnit> dataProcessingUnits) {
        super(dataProcessingUnits);
    }

    public static void main(String[] args) {

    }
}
