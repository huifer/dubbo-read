# JAVA spi 
- Author: [HuiFer](https://github.com/huifer)
- 源码阅读仓库: [huifer-dubbo](https://github.com/huifer/dubbo-read)


## demo
```java
public interface SpiInterface {
    void sayHello();
}

```
- 实现
```java
package com.huifer.dubbo.spi.impl;

import com.huifer.dubbo.spi.SpiInterface;

public class FirstSpiInterfaceImpl implements SpiInterface {
    @Override
    public void sayHello() {
        System.out.println("first say hello");
    }
}

```
```java
package com.huifer.dubbo.spi.impl;

import com.huifer.dubbo.spi.SpiInterface;

public class SecSpiInterfaceImpl implements SpiInterface {
    @Override
    public void sayHello() {
        System.out.println("sec say hello");
    }
}

```
- 执行
```java
package com.huifer.dubbo.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

public class SpiRun {
    public static void main(String[] args) {
        ServiceLoader<SpiInterface> spiLoader = ServiceLoader.load(SpiInterface.class);
        Iterator<SpiInterface> iteratorSpi = spiLoader.iterator();
        while (iteratorSpi.hasNext()) {
            SpiInterface demoService = iteratorSpi.next();
            demoService.sayHello();
        }

    }
}

```
- resources 目录下创建`META-INF\services\com.huifer.dubbo.spi.SpiInterface`,命名规则:`MATA-INF\service\接口全路径`

- 执行结果
```text
first say hello
sec say hello
```

## 解析
- `java.util.ServiceLoader`
```java

public final class ServiceLoader<S>
    implements Iterable<S>
{

    // 读取路径的前缀
    private static final String PREFIX = "META-INF/services/";

    // The class or interface representing the service being loaded
    // 被加载的类
    private final Class<S> service;

    // The class loader used to locate, load, and instantiate providers
    // 定位用,找文件
    private final ClassLoader loader;

    // The access control context taken when the ServiceLoader is created
    // 上下文
    private final AccessControlContext acc;

    // Cached providers, in instantiation order
    // 文件数据,
    private LinkedHashMap<String,S> providers = new LinkedHashMap<>();

    // The current lazy-lookup iterator
    // 懒惰的迭代器
    private LazyIterator lookupIterator;
}
```

### java.util.ServiceLoader.LazyIterator.hasNextService
```java
 private boolean hasNextService() {
            if (nextName != null) {
                return true;
            }
            if (configs == null) {
                try {
                    // 前缀+接口全路径
                    String fullName = PREFIX + service.getName();
                    if (loader == null)
                        configs = ClassLoader.getSystemResources(fullName);
                    else
                        configs = loader.getResources(fullName);
                } catch (IOException x) {
                    fail(service, "Error locating configuration files", x);
                }
            }
            while ((pending == null) || !pending.hasNext()) {
                if (!configs.hasMoreElements()) {
                    return false;
                }
                pending = parse(service, configs.nextElement());
            }
            nextName = pending.next();
            return true;
        }
```

- 通过 `c = Class.forName(cn, false, loader)`创建对象