package ru.vafeen.samples.sample1.java;

import static ru.vafeen.samples.sample1.java.SimpleNestedManyLevelsMapperCastCastleKt.mapACastCastle;
import static ru.vafeen.samples.sample1.java.SimpleNestedManyLevelsMapperCastCastleKt.mapBCastCastle;

import ru.vafeen.castcastle.annotations.CastCastleMapper;

@CastCastleMapper
public class SimpleNestedManyLevelsMapper {

    @CastCastleMapper
    public B mapA(A a) {
        return mapACastCastle(this, a);
    }

    @CastCastleMapper
    public A mapB(B b) {
        return mapBCastCastle(this, b, 1);
    }
}