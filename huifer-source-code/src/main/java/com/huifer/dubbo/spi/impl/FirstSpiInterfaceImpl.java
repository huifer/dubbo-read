package com.huifer.dubbo.spi.impl;

import com.huifer.dubbo.spi.SpiInterface;

public class FirstSpiInterfaceImpl implements SpiInterface {
    @Override
    public void sayHello() {
        System.out.println("first say hello");
    }
}
