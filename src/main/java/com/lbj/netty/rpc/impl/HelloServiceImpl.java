package com.lbj.netty.rpc.impl;

import com.lbj.netty.rpc.HelloService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lbj
 * @create 2023-07-01 11:04
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String sayHello(String msg) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date()) + " [服务器]返回: " + msg;
    }
}
