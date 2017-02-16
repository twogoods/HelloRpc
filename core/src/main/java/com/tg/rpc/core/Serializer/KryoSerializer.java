package com.tg.rpc.core.Serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
public class KryoSerializer implements Serializer{
    @Override
    public byte[] serialize(Object obj) {
        Kryo kryo = new Kryo();
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream(1024);
        Output output = new Output(byteOutputStream);
        kryo.writeClassAndObject(output, obj);
        output.close();
        return byteOutputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes,Class<T> clazz) {
        Kryo kryo = new Kryo();
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteInputStream);
        input.close();
        return (T)kryo.readClassAndObject(input);
    }
}
