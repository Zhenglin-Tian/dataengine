package com.tcredit.engine.strategy.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tcredit.engine.conf.DataProcessingUnit;
import com.tcredit.engine.strategy.DPUSelectStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: zl.T
 * @since: 2017-12-05 14:14
 * @updatedUser: zl.T
 * @updatedDate: 2017-12-05 14:14
 * @updatedRemark:
 * @version:
 */
public class OrderDPUSelectStrategy extends DPUSelectStrategy {

    @Override
    public DataProcessingUnit select(DataProcessingUnit currentUnit) {
        List<DataProcessingUnit> sortedUnits = getEnableUnits(currentUnit);
        if (sortedUnits != null && !sortedUnits.isEmpty()) {
            DataProcessingUnit selectedUnit = sortedUnits.get(0);
            return selectedUnit;
        }
        return null;
    }


    @Override
    public List<DataProcessingUnit> getEnableUnits(DataProcessingUnit currentUnit) {
        List<DataProcessingUnit> enableUnits = super.getEnableUnits(currentUnit);
        Collections.sort(enableUnits);
        return enableUnits;
    }

    public OrderDPUSelectStrategy(List<DataProcessingUnit> dataProcessingUnits) {
        super(dataProcessingUnits);
    }

    public OrderDPUSelectStrategy(Set<DataProcessingUnit> dataProcessingUnits) {
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

        OrderDPUSelectStrategy strategy = new OrderDPUSelectStrategy(us);
        DataProcessingUnit unit = null;
        for (int i = 0; i < 5; i++) {
            unit = strategy.select(unit);
            System.out.println(unit);
        }
    }

}
