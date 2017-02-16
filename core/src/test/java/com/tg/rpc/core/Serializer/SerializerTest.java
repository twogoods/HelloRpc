package com.tg.rpc.core.Serializer;


import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 17/2/14.
 */
public class SerializerTest {



    Serializer kry = new KryoSerializer();
    Serializer protostuff = new ProtostuffSerializer();

    @Test
    public void testKry() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "twogoods");
        map.put("age", 11);

        long start=System.currentTimeMillis();

        byte[] bytes = kry.serialize(map);
        System.out.println(bytes.length);

        HashMap<String, Object> result = kry.deserialize(bytes, HashMap.class);
        System.out.println(result);
        long end =System.currentTimeMillis();
        System.out.println("time:  "+(end-start));

    }

    @Test
    public void testProtostuff() throws Exception {
        //map 的序列化只能放在一个wrap(包装)类里
        Map<String, Object> map = new HashMap<>();
        map.put("name", "twogoods");
        map.put("age", 11);

        User u=new User();
        u.setId(1);
        u.setItem(map);

        long start=System.currentTimeMillis();
        byte[] bytes = protostuff.serialize(u);
        System.out.println(bytes.length);


        //http://stackoverflow.com/questions/32994004/how-to-serialize-map-to-byte-array-by-protostuff

//        HashMap<String, Object> result = protostuff.deserialize(bytes, User.class);
        User result = protostuff.deserialize(bytes, User.class);
        System.out.println(result);
        long end =System.currentTimeMillis();
        System.out.println("time:  "+(end-start));

    }

    @Test
    public void mapCast(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "twogoods");
        map.put("age", 11);

        Object obj=map;

        Map<String,String> another= (Map<String, String>) obj;
        System.out.println(another);
    }
}