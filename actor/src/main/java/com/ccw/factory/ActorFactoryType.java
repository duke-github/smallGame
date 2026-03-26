package com.ccw.factory;

public enum ActorFactoryType {
    TEST(0, "测试"),
    DEFAULT(1, "默认"),

    ;

    public final int type;
    public final String name;


    ActorFactoryType(int type, String name) {
        this.type = type;
        this.name = name;
    }


    public static ActorFactoryType getByValue(int value) {
        ActorFactoryType[] values = ActorFactoryType.values();
        for (ActorFactoryType factoryType : values) {
            if (factoryType.type == value) {
                return factoryType;
            }
        }
        return TEST;
    }
}
