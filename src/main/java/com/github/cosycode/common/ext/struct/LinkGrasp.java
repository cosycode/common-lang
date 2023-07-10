package com.github.cosycode.common.ext.struct;

import com.github.cosycode.common.lang.BaseRuntimeException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <b>Description : </b> build a node grasp, and provide some feature, to simplify some judgement about node tree,
 * like find out bad relationship from tree top node to all tree leaf nodes.
 * <p>
 * <b>created in </b> 2023/6/28
 * </p>
 *
 * @author CPF
 **/
public class LinkGrasp<T> {

    @FunctionalInterface
    public interface JudgeAdopt<T> {
        /**
         * judge whether it can adopt the child.
         *
         * @param child   childNode
         * @param current currentNode
         */
        boolean predicate(T child, T current);
    }

    public final List<LevelTreeNode<T>> list = new ArrayList<>();

    public void add(T element, Function<T, LevelTreeNodeType> getNodeType, Function<T, String> getKey, Function<T, Collection<String>> getChildren, JudgeAdopt<T> childCheck) {
        list.add(new LevelTreeNode<>(element, getNodeType.apply(element), getKey.apply(element), getChildren, childCheck));
    }

    public void addRootNode(T element, Function<T, String> getKey, Function<T, Collection<String>> getChildren, JudgeAdopt<T> childCheck) {
        list.add(new LevelTreeNode<>(element, LevelTreeNodeType.ROOT, getKey.apply(element), getChildren, childCheck));
    }

    public void addBranchNode(T element, Function<T, String> getKey, Function<T, Collection<String>> getChildren, JudgeAdopt<T> childCheck) {
        list.add(new LevelTreeNode<>(element, LevelTreeNodeType.BRANCH, getKey.apply(element), getChildren, childCheck));
    }

    public void addLeafNode(T element, Function<T, String> getKey) {
        list.add(new LevelTreeNode<>(element, LevelTreeNodeType.LEAF, getKey.apply(element), null, null));
    }

    public void initRelationShip() {
        Map<String, LevelTreeNode<T>> collect = list.stream().filter(Objects::nonNull).collect(Collectors.toMap(it -> it.name, it -> it));
        if (collect.size() < list.size()) {
            throw new BaseRuntimeException("children has duplicate elements, or children has the elements with value null ==> " + list);
        }
        for (LevelTreeNode<T> item : list) {
            if (item.children == null) {
                continue;
            }
            for (String toPoint : item.children.keySet()) {
                LevelTreeNode<T> toNode = collect.get(toPoint);
                if (toNode != null) {
                    item.adoptJudge(toNode);
                }
            }
        }
    }

    public List<Map<String, Object>> getAbnormalNode() {
        return list.stream().map(LevelTreeNode::abnormalMap).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public enum LevelTreeNodeType {
        ROOT,
        BRANCH,
        LEAF
    }

    public static class LevelTreeNode<T> {

        public LevelTreeNode(T element, LevelTreeNodeType type, String name, Function<T, Collection<String>> getChildren, JudgeAdopt<T> childCheck) {
            this.element = element;
            this.type = type;
            this.name = name;
            this.childCheck = childCheck;
            this.children = geneChild(getChildren);
            this.fatherList = this.type == LevelTreeNodeType.ROOT ? null : new ArrayList<>(2);
        }

        /**
         * @return null: leaf node. a map means that the node is not a leaf node.
         */
        public Map<String, LevelTreeNode<T>> geneChild(Function<T, Collection<String>> getChildren) {
            if (getChildren == null) {
                return null;
            }
            Collection<String> childrenKey = getChildren.apply(element);
            if (childrenKey == null || childrenKey.isEmpty()) {
                return null;
            }
            // validation
            if (childrenKey.contains(this.name)) {
                throw new IllegalStateException("the name of element in children list cannot be same as the name of the current node --> " + this.name);
            }
            // convert to map
            Map<String, LevelTreeNode<T>> map = new HashMap<>();
            for (String s : childrenKey) {
                if (s != null) {
                    map.put(s, null);
                }
            }
            if (map.size() < childrenKey.size()) {
                throw new BaseRuntimeException("duplicate key or null key in childrenList --> " + childrenKey);
            }
            return map;
        }

        private final String name;

        private final T element;

        private final LevelTreeNodeType type;

        private final List<LevelTreeNode<T>> fatherList;

        private final Map<String, LevelTreeNode<T>> children;

        private final JudgeAdopt<T> childCheck;

        public void adoptJudge(LevelTreeNode<T> childNode) {
            boolean predicate = childCheck.predicate(childNode.element, this.element);
            if (predicate) {
                this.children.put(childNode.name, childNode);
                childNode.fatherList.add(this);
            }
        }

        @Override
        public String toString() {
            return "LevelTreeNode{" + this.element.getClass().getSimpleName() + "[" + this.name + "]" + ", type=" + type + '}';
        }

        public String toTagString() {
            return this.element.getClass().getSimpleName() + "[" + this.name + "]";
        }

        public Map<String, Object> abnormalMap() {
            Map<String, Object> map = null;
            if (this.type != LevelTreeNodeType.ROOT && (this.fatherList == null || this.fatherList.isEmpty())) {
                map = new HashMap<>(8);
                map.put("node", "dangling");
            }
            if (this.type != LevelTreeNodeType.LEAF) {
                List<String> missRelationShip = null;
                for (Map.Entry<String, LevelTreeNode<T>> entry : children.entrySet()) {
                    if (entry.getValue() == null) {
                        if (missRelationShip == null) {
                            missRelationShip = new ArrayList<>();
                        }
                        missRelationShip.add(entry.getKey());
                    }
                }
                if (missRelationShip != null) {
                    if (map == null) {
                        map = new HashMap<>(4);
                    }
                    map.put("miss", missRelationShip);
                }
            }
            if (map != null) {
                map.put("name", this.name);
                map.put("type", this.element.getClass().getSimpleName());
            }
            return map;
        }

    }

}