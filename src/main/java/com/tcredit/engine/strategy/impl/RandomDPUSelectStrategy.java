package com.tcredit.engine.strategy.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tcredit.engine.conf.DataProcessingUnit;
import com.tcredit.engine.strategy.DPUSelectStrategy;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-05 14:24
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-05 14:24
 * @updatedRemark:
 * @version:
 */
public class RandomDPUSelectStrategy extends DPUSelectStrategy {
    /**
     *
     * @param currentUnit 当前使用的数据处理单元
     * @return
     */
    @Override
    public DataProcessingUnit select(DataProcessingUnit currentUnit) {
        List<DataProcessingUnit> enableUnits = getEnableUnits(currentUnit);
        if (enableUnits.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(enableUnits.size());
        DataProcessingUnit unit = enableUnits.get(index);
        return unit;
    }

    public RandomDPUSelectStrategy(List<DataProcessingUnit> dataProcessingUnits) {
        super(dataProcessingUnits);
    }

    public RandomDPUSelectStrategy(Set<DataProcessingUnit> dataProcessingUnits) {
        super(dataProcessingUnits);
    }

    public static void main(String[] args) {
        DataProcessingUnit u1 = new DataProcessingUnit();
        u1.setId("1");
        u1.setOrder(0);
        u1.setStatus("able");
        DataProcessingUnit u2 = new DataProcessingUnit();
        u2.setId("2");
        u2.setOrder(0);
        u2.setStatus("able");
        DataProcessingUnit u3 = new DataProcessingUnit();
        u3.setId("3");
        u3.setOrder(0);
        u3.setStatus("able");
        Set<DataProcessingUnit> us = Sets.newLinkedHashSet();
        us.add(u1);
        us.add(u2);
        us.add(u3);

        DPUSelectStrategy strategy = new RandomDPUSelectStrategy(us);
        DataProcessingUnit currentUnit=null;
        for (int i = 0; i < 5; i++) {
            DataProcessingUnit select = strategy.select(currentUnit);
            currentUnit = select;
            System.out.println(select);
        }
    }
}
