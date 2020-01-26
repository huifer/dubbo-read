# Dubbo 序列化 
- Author: [HuiFer](https://github.com/huifer)
- 源码阅读仓库: [huifer-dubbo](https://github.com/huifer/dubbo-read)
- dubbo 的序列化工程位于[dubbo-serialization](../../dubbo-serialization)
- 查看 pom 文件
```xml
    <modules>
        <module>dubbo-serialization-api</module>
        <module>dubbo-serialization-hessian2</module>
        <module>dubbo-serialization-fastjson</module>
        <module>dubbo-serialization-kryo</module>
        <module>dubbo-serialization-fst</module>
        <module>dubbo-serialization-jdk</module>
        <module>dubbo-serialization-protostuff</module>
        <module>dubbo-serialization-avro</module>
        <module>dubbo-serialization-test</module>
        <module>dubbo-serialization-gson</module>
        <module>dubbo-serialization-protobuf</module>
        <module>dubbo-serialization-native-hession</module>
    </modules>
```
- dubbo 序列化工程存在12个modules,其中`dubbo-serialization-api`定义了dubbo序列化相关的接口,`dubbo-serialization-test`为测试工程,其他为序列化实现

## 序列化接口
```java
@SPI("hessian2")
public interface Serialization {

    /**
     * 获取ContentTypeId
     */
    byte getContentTypeId();

    /**
     * 获取 ContentTypeId 类型 , 各类实现接口中直接定义
     */
    String getContentType();

    /**
     * 构造序列化对象
     */
    @Adaptive
    ObjectOutput serialize(URL url, OutputStream output) throws IOException;

    /**
     * 构造反序列化对象
     */
    @Adaptive
    ObjectInput deserialize(URL url, InputStream input) throws IOException;

}
```

- 这里对FastJSON实现方法进行源码查看,其他实现同理.

## FastJSON 实现
```java
public class FastJsonSerialization implements Serialization {

    /**
     * 获取ContentTypeId
     * @return
     */
    @Override
    public byte getContentTypeId() {
        return FASTJSON_SERIALIZATION_ID;
    }

    /**
     * 返回类型
     * @return
     */
    @Override
    public String getContentType() {
        return "text/json";
    }

    /**
     * 序列化
     */
    @Override
    public ObjectOutput serialize(URL url, OutputStream output) throws IOException {
        return new FastJsonObjectOutput(output);
    }

    /**
     * 反序列化
     */
    @Override
    public ObjectInput deserialize(URL url, InputStream input) throws IOException {
        return new FastJsonObjectInput(input);
    }

}
```
## FastJsonObjectOutput
- 使用FastJSON进行序列化
```java
    /**
     * 重写写出方法
     * @param obj object.
     * @throws IOException
     */
    @Override
    public void writeObject(Object obj) throws IOException {
        SerializeWriter out = new SerializeWriter();
        // fastJson 序列化
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.config(SerializerFeature.WriteEnumUsingToString, true);
        serializer.write(obj);
        out.writeTo(writer);
        out.close(); // for reuse SerializeWriter buf
        // 写出数据
        writer.println();
        writer.flush();
    }

    @Override
    public void flushBuffer() throws IOException {
        writer.flush();
    }
```



## FastJsonObjectInput
- 反序列化
```java
    /**
     * 读取对象
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        String json = readLine();
        return (T) JSON.parseObject(json, type);
    }

    private String readLine() throws IOException, EOFException {
        String line = reader.readLine();
        if (line == null || line.trim().length() == 0) {
            throw new EOFException();
        }
        return line;
    }

    private <T> T read(Class<T> cls) throws IOException {
        // 读取json字符串
        String json = readLine();
        // 反序列化json成对象
        return JSON.parseObject(json, cls);
    }
```


