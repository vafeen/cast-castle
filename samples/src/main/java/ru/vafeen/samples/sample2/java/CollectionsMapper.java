package ru.vafeen.samples.sample2.java;

import static ru.vafeen.samples.sample2.java.CollectionsMapperCastCastleKt.mapACastCastle;
import static ru.vafeen.samples.sample2.java.CollectionsMapperCastCastleKt.mapBCastCastle;
import static ru.vafeen.samples.sample2.java.CollectionsMapperCastCastleKt.mapLevel1ACastCastle;
import static ru.vafeen.samples.sample2.java.CollectionsMapperCastCastleKt.mapLevel1BCastCastle;

import ru.vafeen.castcastle.annotations.CastCastleMapper;

@CastCastleMapper
public interface CollectionsMapper {

    @CastCastleMapper
    default B mapA(A a) {
        return mapACastCastle(this, a);
    }

    @CastCastleMapper
    default A mapB(B b) {
        return mapBCastCastle(this, b);
    }

    default int string(String string) {
        return Integer.parseInt(string);
    }

    default String mapInt(int i) {
        return String.valueOf(i);
    }

    @CastCastleMapper
    default InnerLevel1B mapLevel1A(InnerLevel1A inner1Level1) {
        return mapLevel1ACastCastle(this, inner1Level1);
    }

    @CastCastleMapper
    default InnerLevel1A mapLevel1B(InnerLevel1B inner1Level1) {
        return mapLevel1BCastCastle(this, inner1Level1);
    }

    //    @CastCastleMapper
    //    default List<InnerLevel1B> mapLevel1A(List<InnerLevel1A> inner1Level1) {
    //        return mapLevel1AListCastCastle(inner1Level1);
    //    }
    //
    //    @CastCastleMapper
    //    default List<InnerLevel1A> mapLevel1B(List<InnerLevel1B> inner1Level1) {
    //        return mapLevel1BListCastCastle(inner1Level1);
    //    }
    //
    //    @CastCastleMapper
    //    default InnerLevel2B mapLevel2A(InnerLevel2A innerLevel2A) {
    //        return mapLevel2ACastCastle(innerLevel2A);
    //    }
    //
    //    @CastCastleMapper
    //    default InnerLevel2A mapLevel2B(InnerLevel2B innerLevel2A) {
    //        return mapLevel2BCastCastle(innerLevel2A);
    //    }
    //
    //    @CastCastleMapper
    //    default List<InnerLevel2B> mapLevel2A(List<InnerLevel2A> innerLevel2A) {
    //        return mapLevel2AListCastCastle(innerLevel2A);
    //    }
    //
    //    @CastCastleMapper
    //    default List<InnerLevel2A> mapLevel2B(List<InnerLevel2B> innerLevel2A) {
    //        return mapLevel2BListCastCastle(innerLevel2A);
    //    }
}