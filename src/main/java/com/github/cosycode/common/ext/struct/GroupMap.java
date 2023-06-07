package com.github.cosycode.common.ext.struct;

import lombok.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <b>Description : </b>
 * <p>
 * <b>created in </b> 2023/5/18
 * </p>
 *
 * @author CPF
 * @since 1.9
 **/
public class GroupMap<T> {

    private final Map<String, List<T>> map;

    public GroupMap(@NonNull List<T> list, Function<T, String> keyFunction) {
        map = list.stream().collect(Collectors.groupingBy(keyFunction));
    }

    public Map<String, List<T>> repeatKey() {
        Map<String, List<T>> keyList = new HashMap<>();
        for (Map.Entry<String, List<T>> entry : map.entrySet()) {
            if (entry.getValue().size() > 1) {
                keyList.put(entry.getKey(), entry.getValue());
            }
        }
        return keyList;
    }
}
