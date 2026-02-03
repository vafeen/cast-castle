package ru.vafeen.samples.sample1.java;

import ru.vafeen.castcastle.annotations.CastCastleMapper;

@CastCastleMapper
public interface SimpleNestedManyLevelsMapper {
    B map(A a);

    A map(B b);

//    InnerLevel1B mapLevel1(InnerLevel1A inner1Level1);
//    InnerLevel1A mapLevel1(InnerLevel1B inner1Level1);
//    InnerLevel2B mapLevel2(InnerLevel2A innerLevel2A);
//    InnerLevel2A mapLevel2(InnerLevel2B innerLevel2A);
//    InnerLevel3B mapLevel3(InnerLevel3A inner1Level3);
//    InnerLevel3A mapLevel3(InnerLevel3B inner1Level3);
//    InnerLevel4B mapLevel4(InnerLevel4A inner1Level4);
//    InnerLevel4A mapLevel4(InnerLevel4B inner1Level4);
//    InnerLevel5B mapLevel5(InnerLevel5A inner1Level5);
//    InnerLevel5A mapLevel5(InnerLevel5B inner1Level5);
}