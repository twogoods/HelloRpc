package com.tg.rpc.core.Serializer;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Created by twogoods on 17/2/14.
 */
public class ProtostuffSerializer implements Serializer{
    @Override
    public byte[] serialize(Object obj) {
        RuntimeSchema schema = RuntimeSchema.createFrom(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    }

    @Override
    public <T> T deserialize(byte[] bytes,Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            RuntimeSchema schema = RuntimeSchema.createFrom(clazz);
            ProtostuffIOUtil.mergeFrom(bytes, t, schema);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
