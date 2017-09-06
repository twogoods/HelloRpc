package com.tg.rpc.core.servicecenter;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-03-01
 */
public class ServiceFilterTest {
    List<String> a = Arrays.asList("A", "B", "C", "D");
    List<String> b = Arrays.asList("F", "E", "C", "D");

    Comparable<String, String> comparable = new Comparable<String, String>() {
        @Override
        public boolean equals(String s, String s2) {
            return s.equals(s2);
        }
    };

    @Test
    public void testFilterRemoved() throws Exception {
        System.out.println(ServiceFilter.filterRemoved(a, b, comparable));
    }

    @Test
    public void testFilterAdded() throws Exception {
        System.out.println(ServiceFilter.filterAdded(a, b, comparable));
    }
}