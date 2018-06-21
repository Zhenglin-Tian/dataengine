package com.tcredit.engine.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcredit.engine.conf.Step;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zl.T
 * @since: 2018-03-02 17:13
 * @updatedUser: zl.T
 * @updatedDate: 2018-03-02 17:13
 * @updatedRemark:
 * @version:
 */
public class StepPriorityUtil {

    public static List<Set<String>> getStepPriority(Set<Step> steps) {
        List<Set<String>> stepPriority = Lists.newArrayList();
        Map<String, Node> nodes = createNode(steps);

        /*
            把所有的无做节点的表拿出来
         */
        List<Node> noLeftNode = Lists.newArrayList();
        for (Node node : nodes.values()) {
            if (node.left.size() == 0) {
                noLeftNode.add(node);
            }
        }
        /*
            如果所有的节点都有做节点,说明形成了闭环
         */
        if (noLeftNode.size() == 0) {
            throw new RuntimeException("step defined in dp xml have circle relyon");
        }
        /*
            从无左节点开始分层查找,把每一层的节点值放到一个list里
         */
        int nodeNumer = nodes.keySet().size();
        List<List<String>> level = Lists.newLinkedList();
        List<Node> allRightNodes = Lists.newArrayList();
        allRightNodes.addAll(noLeftNode);
        List<Node> rightNodes = Lists.newArrayList();
        while (allRightNodes.size() > 0) {
            List<String> sameLeve = Lists.newArrayList();
            rightNodes.clear();
            for (Node node : allRightNodes) {
                sameLeve.add(node.getValue());
                rightNodes.addAll(nodes.get(node.getValue()).right);
            }
            allRightNodes.clear();
            allRightNodes.addAll(rightNodes);
            level.add(sameLeve);
            // 如果层级大于节点个数,判定为有闭环
            if (level.size() > nodeNumer) {
                throw new RuntimeException(String.format("Data object defined in data_source xml have circle " +
                        "foreignKey: %s", level));
            }
        }
        /*
            去掉高层中,底层已经出现过的值
         */
        List<String> temp = Lists.newArrayList();
        for (int j = level.size() - 1; j > 0; j--) {
            temp.addAll(level.get(j));
            level.get(j - 1).removeAll(temp);
        }
        /*
            从低层到高层遍历所有节点形成LinkedList返回
         */
        for (int j = 0; j < level.size(); j++) {
            stepPriority.add(level.get(j).stream().collect(Collectors.toSet()));
        }


        return stepPriority;
    }

    /**
     * 获取所有数据处理执行步骤依赖于该步骤的步骤id
     * @param steps
     * @return
     */
    public static Map<String,Set<String>> getRelyOn(Set<Step> steps) {
        Map<String,Set<String>> stepRelyOn = Maps.newHashMap();
        Map<String, Node> nodes = createNode(steps);
        for (String nodeId:nodes.keySet()){
            Node node = nodes.get(nodeId);
            Set<Node> right = node.getRight();
            Set<String> rightIds = Sets.newHashSet();
            if (right!=null && !right.isEmpty()){
                for (Node nd:right){
                    rightIds.add(nd.getValue());
                }
            }
            stepRelyOn.put(nodeId,rightIds);
        }

        return stepRelyOn;
    }


    private static Map<String,Node> createNode(Set<Step> steps){
        Map<String, Node> nodes = Maps.newHashMap();
        /**
         * 将所有step形成节点放进链表中
         */
        for (Step step : steps) {
            nodes.put(step.getId(), new Node(step.getId()));
        }
        for (Step step : steps) {
            String relyon = step.getRelyon();
            if (StringUtils.isNotBlank(relyon)) {
                String[] split = relyon.split(",");
                for (String leftId : split) {
                    Node nodeLeft;
                    Node nodeRight;
                    leftId = leftId.trim();
                    if (StringUtils.isNotBlank(leftId)) {
                        if (null != nodes.get(leftId)) {
                            nodeLeft = nodes.get(leftId);
                        } else {
                            nodeLeft = new Node(leftId);
                        }

                        if (null != nodes.get(step.getId())) {
                            nodeRight = nodes.get(step.getId());
                        } else {
                            nodeRight = new Node(step.getId());
                        }

                        nodeLeft.putRight(nodeRight);
                        nodeRight.putLeft(nodeLeft);

                        nodes.put(nodeLeft.getValue(), nodeLeft);
                        nodes.put(nodeRight.getValue(), nodeRight);
                    }
                }
            }
        }
        return nodes;
    }



    static class Node {
        String value;
        Set<Node> left = Sets.newHashSet();
        Set<Node> right = Sets.newHashSet();

        public void putLeft(Node node) {
            if (StringUtils.isNotBlank(node.getValue())) {
                left.add(node);
            }
        }

        public void putRight(Node node) {
            if (StringUtils.isNotBlank(node.getValue())) {
                right.add(node);
            }
        }

        Node(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Set<Node> getLeft() {
            return left;
        }

        public void setLeft(Set<Node> left) {
            this.left = left;
        }

        public Set<Node> getRight() {
            return right;
        }

        public void setRight(Set<Node> right) {
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (value != null ? !value.equals(node.value) : node.value != null) return false;
            if (left != null ? !left.equals(node.left) : node.left != null) return false;
            return !(right != null ? !right.equals(node.right) : node.right != null);

        }

        /**
         *
         * @return
         */
        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (left != null ? value.hashCode() : 0);
            result = 31 * result + (right != null ? value.hashCode() : 0);
            return result;
        }
    }
}

