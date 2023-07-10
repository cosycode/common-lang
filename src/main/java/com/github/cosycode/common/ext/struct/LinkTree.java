package com.github.cosycode.common.ext.struct;

import com.github.cosycode.common.lang.BaseRuntimeException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <b>Description : </b> The formation of a node tree is used to simplify some judgments about tree nodes, such as judging the correlation results of the head and tail.
 * <p>
 * <b>created in </b> 2023/6/28
 * </p>
 *
 * @author CPF
 * @since 1.0
 **/
@Slf4j
public class LinkTree<T> {


    @FunctionalInterface
    public interface JudgeAdopt<T> {
        /**
         * judge whether it can adopt the child.
         * @param child childNode
         * @param current currentNode
         */
        boolean predicate(T child, T current);
    }

    public final List<TreeNode<T>> list = new ArrayList<>();

    public void add(T element, Function<T, String> getKey, Function<T, Collection<String>> getChildren, JudgeAdopt<T> childCheck) {
        list.add(new TreeNode<>(element, getKey, getChildren, childCheck));
    }

    public CheckReport matchesReport() {
        Map<String, TreeNode<T>> collect = list.stream().filter(Objects::nonNull).collect(Collectors.toMap(it -> it.name, it -> it));
        if (collect.size() < list.size()) {
            throw new BaseRuntimeException("There is a conflict in the children element, or there is a null value in children ==> " + list);
        }
        CheckReport report = new CheckReport();
        Set<String> topSet = new HashSet<>(collect.keySet());
        collect.forEach((key, value) -> {
            for (String toPoint : value.children.keySet()) {
                TreeNode<T> toNode = collect.get(toPoint);
                if (toNode != null) {
                    // link
                    value.children.put(toPoint, toNode);
                    // Disqualification to be a top node
                    topSet.remove(toPoint);
                } else {
                    report.addMessage(String.format("not found the target ==> from: %s[%s], to: %s", value.element.getClass().getSimpleName(), key, toPoint));
                }
            }
        });
        report.setTopSet(topSet);
        if (topSet.size() != 1) {
            log.warn("found multi root node in the behind of the link, topSet: " + topSet);
            return report;
        }
        // judge whether the linked list has a ring
        {
            for (String name : topSet) {
                TreeNode<T> topNode = collect.get(name);
                topNode.level = 1;
                LinkedList<TreeNode<T>> linkedList = new LinkedList<>();
                linkedList.add(topNode);
                while (! linkedList.isEmpty()) {
                    TreeNode<T> treeNode = linkedList.pollFirst();
                    for (TreeNode<T> value : treeNode.children.values()) {
                        if (value != null) {
                            if (value.level == 0) {
                                value.level = topNode.level + 1;
                                linkedList.add(value);
                            } else if (value.level != topNode.level + 1) {
                                report.addMessage("It may be a duplicate removal, and the chain may have a ring, suspending validation");
                                return report;
                            }
                        }
                    }
                }
            }
        }
        return report;
    }

    @Data
    @Slf4j
    public static class CheckReport {

        private Set<String> topSet;

        private boolean hasRing;

        private List<String> messageList = new ArrayList<>();

        public void addMessage(String message) {
            messageList.add(message);
        }

        public void print() {
            // validate
            if (topSet.size() != 1) {
                log.info("found multi root node in the behind of the link");
            }
            log.info(messageList.toString());
            log.info("success: " + isSuccess());
        }

        public boolean isSuccess() {
            return topSet.size() == 1 && ! hasRing && messageList.isEmpty();
        }
    }


    public static class TreeNode<T> {

        public TreeNode(T element, Function<T, String> getKey, Function<T, Collection<String>> getChildren, JudgeAdopt<T> childCheck) {
            this.element = element;
            this.childCheck = childCheck;
            this.name = getKey.apply(element);
            Collection<String> childrenKey = getChildren.apply(element);
            if (childrenKey == null) {
                this.children = new HashMap<>(1);
            } else {
                if (childrenKey.contains(this.name)) {
                    throw new BaseRuntimeException("children name is not equals with node name");
                }
                this.children = new HashMap<>();
                for (String s : childrenKey) {
                    if (s != null) {
                        this.children.put(s, null);
                    }
                }
                if (this.children.size() < childrenKey.size()) {
                    throw new BaseRuntimeException("There is a conflict in the children element, or there is a null value in children ==> " + childrenKey);
                }
            }
        }

        private final String name;

        private final T element;

        private int level;

        private final Map<String, TreeNode<T>> children;

        private final JudgeAdopt<T> childCheck;

    }

}