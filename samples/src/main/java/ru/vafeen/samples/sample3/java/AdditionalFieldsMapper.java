package ru.vafeen.samples.sample3.java;

import static ru.vafeen.samples.sample3.java.AdditionalFieldsMapperCastCastleKt.mapCastCastle;

import ru.vafeen.castcastle.annotations.CastCastleMapper;

@CastCastleMapper
public class AdditionalFieldsMapper {

    @CastCastleMapper
    public A map(B b) {
        return mapCastCastle(this, b, 1);
    }

    @CastCastleMapper
    public B map(A a) {
        return mapCastCastle(this, a, 1);
    }
}
