package ru.vafeen.samples.sample2.java;


import java.util.List;

import ru.vafeen.castcastle.annotations.CastCastleMapper;


@CastCastleMapper
public interface CollectionsMapper {
    B map(A a);

    A map(B b);

    default int mapString(String string) {
        return Integer.parseInt(string);
    }

    default String mapInt(int i) {
        return String.valueOf(i);
    }

    InnerLevel1B mapLevel1A(InnerLevel1A inner1Level1);

    InnerLevel1A mapLevel1B(InnerLevel1B inner1Level1);

    InnerLevel2B mapLevel2A(InnerLevel2A innerLevel2A);

    InnerLevel2A mapLevel2B(InnerLevel2B innerLevel2A);

    InnerLevel3B mapLevel3A(InnerLevel3A inner1Level3);

    InnerLevel3A mapLevel3B(InnerLevel3B inner1Level3);

    InnerLevel4B mapLevel4A(InnerLevel4A inner1Level4);

    InnerLevel4A mapLevel4B(InnerLevel4B inner1Level4);

    InnerLevel5B mapLevel5A(InnerLevel5A inner1Level5);

    InnerLevel5A mapLevel5B(InnerLevel5B inner1Level5);

    List<InnerLevel1B> mapLevel1A(List<InnerLevel1A> inner1Level1);

    List<InnerLevel1A> mapLevel1B(List<InnerLevel1B> inner1Level1);

    List<InnerLevel2B> mapLevel2A(List<InnerLevel2A> innerLevel2A);

    List<InnerLevel2A> mapLevel2B(List<InnerLevel2B> innerLevel2A);

    List<InnerLevel3B> mapLevel3A(List<InnerLevel3A> inner1Level3);

    List<InnerLevel3A> mapLevel3B(List<InnerLevel3B> inner1Level3);

    List<InnerLevel4B> mapLevel4A(List<InnerLevel4A> inner1Level4);

    List<InnerLevel4A> mapLevel4B(List<InnerLevel4B> inner1Level4);

    List<InnerLevel5B> mapLevel5A(List<InnerLevel5A> inner1Level5);

    List<InnerLevel5A> mapLevel5B(List<InnerLevel5B> inner1Level5);
}