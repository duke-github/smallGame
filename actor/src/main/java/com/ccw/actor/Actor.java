package com.ccw.actor;


import com.ccw.Envelope;

/**
 * actor - mailBox
 * actor是一个执行单元 保存状态和数据
 * actor中的行为修改自己的状态和数据
 * actor之间通过mail 通信
 */
public interface Actor  {
    void onReceive(Envelope msg);
}
