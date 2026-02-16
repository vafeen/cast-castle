package ru.vafeen.samples.sample1.java;

public class A {
    private final InnerLevel1A inner1Level1;
    private final InnerLevel1A inner2Level1;
    private final int someInt;

    public A(InnerLevel1A inner1Level1, InnerLevel1A inner2Level1, int someInt) {
        this.inner1Level1 = inner1Level1;
        this.inner2Level1 = inner2Level1;
        this.someInt = someInt;
    }

    public InnerLevel1A getInner1Level1() {
        return inner1Level1;
    }

    public InnerLevel1A getInner2Level1() {
        return inner2Level1;
    }

    public int getSomeInt() {
        return someInt;
    }
}
