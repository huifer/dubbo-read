package com.huifer.dubbo.spi.impl;

import com.huifer.dubbo.spi.SpiInterface;

public class SecSpiInterfaceImpl implements SpiInterface {
    @Override
    public void sayHello() {
        System.out.println("sec say hello");
    }
}
