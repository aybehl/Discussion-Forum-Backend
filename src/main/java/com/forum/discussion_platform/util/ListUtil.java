package com.forum.discussion_platform.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtil {

    public static <T> List<T> mergeLists(List<T> list1, List<T> list2){
        Set<T> mergedSet = new HashSet<>(list1);
        mergedSet.addAll(list2);

        return new ArrayList<>(mergedSet);
    }
}
