package com.tg.rpc.core.Serializer;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
public interface Serializer{
    public byte[] serialize(Object obj);
    public <T> T deserialize(byte[] bytes,Class<T> clazz);
}
