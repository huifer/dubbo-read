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
