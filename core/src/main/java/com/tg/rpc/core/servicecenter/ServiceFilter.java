package com.tg.rpc.core.servicecenter;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * 找出新旧List中新增的和被去除的元素
 * @author twogoods
 * @version 0.1
 * @since 2017-03-01
 */
public class ServiceFilter {

    public static <A, B> List<A> filterRemoved(List<A> oldList, List<B> newList, Comparable comparable) {
        List<A> shouldRemoved = new ArrayList<>();
        for (A oldItem : oldList) {
            boolean uselessContained = false;
            for (B newItem : newList) {
                if (comparable.equals(oldItem, newItem)) {
                    uselessContained = true;
                    break;
                }
            }
            if (!uselessContained) {
                shouldRemoved.add(oldItem);
            }
        }
        return shouldRemoved;
    }

    public static <A, B> List<B> filterAdded(List<A> oldList, List<B> newList, Comparable comparable) {
        List<B> shouldAdded = new ArrayList<>();
        for (B newItem : newList) {
            boolean contained = false;
            for (A oleItem : oldList) {
                if (comparable.equals(oleItem, newItem)) {
                    contained = true;
                    break;
                }
            }
            if (!contained) {
                shouldAdded.add(newItem);
            }
        }
        return shouldAdded;
    }

}
