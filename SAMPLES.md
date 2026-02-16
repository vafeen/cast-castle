# Samples 

## Many nested level mappers

### Kotlin

Source code:

```kotlin
data class A(val inner1Level1: InnerLevel1A, val inner2Level1: InnerLevel1A, val int: Int)
data class B(val inner1Level1: InnerLevel1B, val inner2Level1: InnerLevel1B)

data class InnerLevel1A(val inner1Level2: InnerLevel2A, val inner2Level2: InnerLevel2A)
data class InnerLevel1B(val inner1Level2: InnerLevel2B, val inner2Level2: InnerLevel2B)

data class InnerLevel2A(val inner1Level3: InnerLevel3A, val inner2Level3: InnerLevel3A)
data class InnerLevel2B(val inner1Level3: InnerLevel3B, val inner2Level3: InnerLevel3B)

data class InnerLevel3A(val first: Int, val second: String)
data class InnerLevel3B(val first: Int, val second: String)

@CastCastleMapper
interface SimpleNestedThreeLevelsMapper {
    @CastCastleMapper
    fun map(a: A): B = mapCastCastle(a) // used generated function

    @CastCastleMapper
    fun map(b: B): A = mapCastCastle(b, 1) // used generated function
}
```

Generated extension functions:

```kotlin
public fun ru.vafeen.samples.sample1.kotlin.SimpleNestedThreeLevelsMapper.mapCastCastle(a: ru.vafeen.samples.sample1.kotlin.A): ru.vafeen.samples.sample1.kotlin.B {
    return ru.vafeen.samples.sample1.kotlin.B(
        inner1Level1 = ru.vafeen.samples.sample1.kotlin.InnerLevel1B(
            inner1Level2 = ru.vafeen.samples.sample1.kotlin.InnerLevel2B(
                inner1Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3B(
                    first = a.inner1Level1.inner1Level2.inner1Level3.first,
                    second = a.inner1Level1.inner1Level2.inner1Level3.second
                ),
                inner2Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3B(
                    first = a.inner1Level1.inner1Level2.inner2Level3.first,
                    second = a.inner1Level1.inner1Level2.inner2Level3.second
                )
            ),
            inner2Level2 = ru.vafeen.samples.sample1.kotlin.InnerLevel2B(
                inner1Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3B(
                    first = a.inner1Level1.inner2Level2.inner1Level3.first,
                    second = a.inner1Level1.inner2Level2.inner1Level3.second
                ),
                inner2Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3B(
                    first = a.inner1Level1.inner2Level2.inner2Level3.first,
                    second = a.inner1Level1.inner2Level2.inner2Level3.second
                )
            )
        ),
        inner2Level1 = ru.vafeen.samples.sample1.kotlin.InnerLevel1B(
            inner1Level2 = ru.vafeen.samples.sample1.kotlin.InnerLevel2B(
                inner1Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3B(
                    first = a.inner2Level1.inner1Level2.inner1Level3.first,
                    second = a.inner2Level1.inner1Level2.inner1Level3.second
                ),
                inner2Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3B(
                    first = a.inner2Level1.inner1Level2.inner2Level3.first,
                    second = a.inner2Level1.inner1Level2.inner2Level3.second
                )
            ),
            inner2Level2 = ru.vafeen.samples.sample1.kotlin.InnerLevel2B(
                inner1Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3B(
                    first = a.inner2Level1.inner2Level2.inner1Level3.first,
                    second = a.inner2Level1.inner2Level2.inner1Level3.second
                ),
                inner2Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3B(
                    first = a.inner2Level1.inner2Level2.inner2Level3.first,
                    second = a.inner2Level1.inner2Level2.inner2Level3.second
                )
            )
        )
    )
}

public fun ru.vafeen.samples.sample1.kotlin.SimpleNestedThreeLevelsMapper.mapCastCastle(
    b: ru.vafeen.samples.sample1.kotlin.B,
    int: kotlin.Int
): ru.vafeen.samples.sample1.kotlin.A {
    return ru.vafeen.samples.sample1.kotlin.A(
        inner1Level1 = ru.vafeen.samples.sample1.kotlin.InnerLevel1A(
            inner1Level2 = ru.vafeen.samples.sample1.kotlin.InnerLevel2A(
                inner1Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3A(
                    first = b.inner1Level1.inner1Level2.inner1Level3.first,
                    second = b.inner1Level1.inner1Level2.inner1Level3.second
                ),
                inner2Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3A(
                    first = b.inner1Level1.inner1Level2.inner2Level3.first,
                    second = b.inner1Level1.inner1Level2.inner2Level3.second
                )
            ),
            inner2Level2 = ru.vafeen.samples.sample1.kotlin.InnerLevel2A(
                inner1Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3A(
                    first = b.inner1Level1.inner2Level2.inner1Level3.first,
                    second = b.inner1Level1.inner2Level2.inner1Level3.second
                ),
                inner2Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3A(
                    first = b.inner1Level1.inner2Level2.inner2Level3.first,
                    second = b.inner1Level1.inner2Level2.inner2Level3.second
                )
            )
        ),
        inner2Level1 = ru.vafeen.samples.sample1.kotlin.InnerLevel1A(
            inner1Level2 = ru.vafeen.samples.sample1.kotlin.InnerLevel2A(
                inner1Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3A(
                    first = b.inner2Level1.inner1Level2.inner1Level3.first,
                    second = b.inner2Level1.inner1Level2.inner1Level3.second
                ),
                inner2Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3A(
                    first = b.inner2Level1.inner1Level2.inner2Level3.first,
                    second = b.inner2Level1.inner1Level2.inner2Level3.second
                )
            ),
            inner2Level2 = ru.vafeen.samples.sample1.kotlin.InnerLevel2A(
                inner1Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3A(
                    first = b.inner2Level1.inner2Level2.inner1Level3.first,
                    second = b.inner2Level1.inner2Level2.inner1Level3.second
                ),
                inner2Level3 = ru.vafeen.samples.sample1.kotlin.InnerLevel3A(
                    first = b.inner2Level1.inner2Level2.inner2Level3.first,
                    second = b.inner2Level1.inner2Level2.inner2Level3.second
                )
            )
        ),
        int = int
    )
}
```

### Java

Source code:

```java
// File: ./A.java
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

// File: ./B.java
public class B {
    private final InnerLevel1B inner1Level1;
    private final InnerLevel1B inner2Level1;

    public B(InnerLevel1B inner1Level1, InnerLevel1B inner2Level1) {
        this.inner1Level1 = inner1Level1;
        this.inner2Level1 = inner2Level1;
    }

    public InnerLevel1B getInner1Level1() {
        return inner1Level1;
    }

    public InnerLevel1B getInner2Level1() {
        return inner2Level1;
    }
}

// File: ./InnerLevel1A.java
public class InnerLevel1A {
    private final InnerLevel2A inner1Level2;
    private final InnerLevel2A inner2Level2;

    public InnerLevel1A(InnerLevel2A inner1Level2, InnerLevel2A inner2Level2) {
        this.inner1Level2 = inner1Level2;
        this.inner2Level2 = inner2Level2;
    }

    public InnerLevel2A getInner1Level2() {
        return inner1Level2;
    }

    public InnerLevel2A getInner2Level2() {
        return inner2Level2;
    }
}

// File: ./InnerLevel1B.java
public class InnerLevel1B {
    private final InnerLevel2B inner1Level2;
    private final InnerLevel2B inner2Level2;

    public InnerLevel1B(InnerLevel2B inner1Level2, InnerLevel2B inner2Level2) {
        this.inner1Level2 = inner1Level2;
        this.inner2Level2 = inner2Level2;
    }

    public InnerLevel2B getInner1Level2() {
        return inner1Level2;
    }

    public InnerLevel2B getInner2Level2() {
        return inner2Level2;
    }
}

// File: ./InnerLevel2A.java
public class InnerLevel2A {
    private final InnerLevel3A inner1Level3;
    private final InnerLevel3A inner2Level3;

    public InnerLevel2A(InnerLevel3A inner1Level3, InnerLevel3A inner2Level3) {
        this.inner1Level3 = inner1Level3;
        this.inner2Level3 = inner2Level3;
    }

    public InnerLevel3A getInner1Level3() {
        return inner1Level3;
    }

    public InnerLevel3A getInner2Level3() {
        return inner2Level3;
    }
}

// File: ./InnerLevel2B.java
public class InnerLevel2B {
    private final InnerLevel3B inner1Level3;
    private final InnerLevel3B inner2Level3;

    public InnerLevel2B(InnerLevel3B inner1Level3, InnerLevel3B inner2Level3) {
        this.inner1Level3 = inner1Level3;
        this.inner2Level3 = inner2Level3;
    }

    public InnerLevel3B getInner1Level3() {
        return inner1Level3;
    }

    public InnerLevel3B getInner2Level3() {
        return inner2Level3;
    }
}

// File: ./InnerLevel3B.java
public class InnerLevel3B {
    private final int first;
    private final String second;

    public InnerLevel3B(int first, String second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}

// File: ./InnerLevel3A.java
public class InnerLevel3A {
    private final int first;
    private final String second;

    public InnerLevel3A(int first, String second) {
        this.first = first;
        this.second = second;
    }

    public int getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}

// File: ./SimpleNestedManyLevelsMapper.java
import static ru.vafeen.samples.sample1.java.SimpleNestedManyLevelsMapperCastCastleKt.mapACastCastle;
import static ru.vafeen.samples.sample1.java.SimpleNestedManyLevelsMapperCastCastleKt.mapBCastCastle;

@CastCastleMapper
public class SimpleNestedManyLevelsMapper {

    @CastCastleMapper
    public B mapA(A a) {
        return mapACastCastle(this, a); // used generated function
    }

    @CastCastleMapper
    public A mapB(B b) {
        return mapBCastCastle(this, b, 1); // used generated function
    }
}
```

Generated extension functions:

```kotlin
public fun ru.vafeen.samples.sample1.java.SimpleNestedManyLevelsMapper.mapACastCastle(a: ru.vafeen.samples.sample1.java.A): ru.vafeen.samples.sample1.java.B {
    return ru.vafeen.samples.sample1.java.B(
        ru.vafeen.samples.sample1.java.InnerLevel1B(
            ru.vafeen.samples.sample1.java.InnerLevel2B(
                ru.vafeen.samples.sample1.java.InnerLevel3B(
                    a.inner1Level1.inner1Level2.inner1Level3.first,
                    a.inner1Level1.inner1Level2.inner1Level3.second
                ),
                ru.vafeen.samples.sample1.java.InnerLevel3B(
                    a.inner1Level1.inner1Level2.inner2Level3.first,
                    a.inner1Level1.inner1Level2.inner2Level3.second
                )
            ),
            ru.vafeen.samples.sample1.java.InnerLevel2B(
                ru.vafeen.samples.sample1.java.InnerLevel3B(
                    a.inner1Level1.inner2Level2.inner1Level3.first,
                    a.inner1Level1.inner2Level2.inner1Level3.second
                ),
                ru.vafeen.samples.sample1.java.InnerLevel3B(
                    a.inner1Level1.inner2Level2.inner2Level3.first,
                    a.inner1Level1.inner2Level2.inner2Level3.second
                )
            )
        ),
        ru.vafeen.samples.sample1.java.InnerLevel1B(
            ru.vafeen.samples.sample1.java.InnerLevel2B(
                ru.vafeen.samples.sample1.java.InnerLevel3B(
                    a.inner2Level1.inner1Level2.inner1Level3.first,
                    a.inner2Level1.inner1Level2.inner1Level3.second
                ),
                ru.vafeen.samples.sample1.java.InnerLevel3B(
                    a.inner2Level1.inner1Level2.inner2Level3.first,
                    a.inner2Level1.inner1Level2.inner2Level3.second
                )
            ),
            ru.vafeen.samples.sample1.java.InnerLevel2B(
                ru.vafeen.samples.sample1.java.InnerLevel3B(
                    a.inner2Level1.inner2Level2.inner1Level3.first,
                    a.inner2Level1.inner2Level2.inner1Level3.second
                ),
                ru.vafeen.samples.sample1.java.InnerLevel3B(
                    a.inner2Level1.inner2Level2.inner2Level3.first,
                    a.inner2Level1.inner2Level2.inner2Level3.second
                )
            )
        )
    )
}

public fun ru.vafeen.samples.sample1.java.SimpleNestedManyLevelsMapper.mapBCastCastle(
    b: ru.vafeen.samples.sample1.java.B,
    someInt: kotlin.Int
): ru.vafeen.samples.sample1.java.A {
    return ru.vafeen.samples.sample1.java.A(
        ru.vafeen.samples.sample1.java.InnerLevel1A(
            ru.vafeen.samples.sample1.java.InnerLevel2A(
                ru.vafeen.samples.sample1.java.InnerLevel3A(
                    b.inner1Level1.inner1Level2.inner1Level3.first,
                    b.inner1Level1.inner1Level2.inner1Level3.second
                ),
                ru.vafeen.samples.sample1.java.InnerLevel3A(
                    b.inner1Level1.inner1Level2.inner2Level3.first,
                    b.inner1Level1.inner1Level2.inner2Level3.second
                )
            ),
            ru.vafeen.samples.sample1.java.InnerLevel2A(
                ru.vafeen.samples.sample1.java.InnerLevel3A(
                    b.inner1Level1.inner2Level2.inner1Level3.first,
                    b.inner1Level1.inner2Level2.inner1Level3.second
                ),
                ru.vafeen.samples.sample1.java.InnerLevel3A(
                    b.inner1Level1.inner2Level2.inner2Level3.first,
                    b.inner1Level1.inner2Level2.inner2Level3.second
                )
            )
        ),
        ru.vafeen.samples.sample1.java.InnerLevel1A(
            ru.vafeen.samples.sample1.java.InnerLevel2A(
                ru.vafeen.samples.sample1.java.InnerLevel3A(
                    b.inner2Level1.inner1Level2.inner1Level3.first,
                    b.inner2Level1.inner1Level2.inner1Level3.second
                ),
                ru.vafeen.samples.sample1.java.InnerLevel3A(
                    b.inner2Level1.inner1Level2.inner2Level3.first,
                    b.inner2Level1.inner1Level2.inner2Level3.second
                )
            ),
            ru.vafeen.samples.sample1.java.InnerLevel2A(
                ru.vafeen.samples.sample1.java.InnerLevel3A(
                    b.inner2Level1.inner2Level2.inner1Level3.first,
                    b.inner2Level1.inner2Level2.inner1Level3.second
                ),
                ru.vafeen.samples.sample1.java.InnerLevel3A(
                    b.inner2Level1.inner2Level2.inner2Level3.first,
                    b.inner2Level1.inner2Level2.inner2Level3.second
                )
            )
        ),
        someInt
    )
}
```

## Collections mappers

### Kotlin

Source code:

```kotlin
data class A(val inner1Level1: List<InnerLevel1A>, val inner2Level1: InnerLevel1A)
data class B(val inner1Level1: List<InnerLevel1B>, val inner2Level1: InnerLevel1B)
data class InnerLevel1A(val inner1Level2: List<InnerLevel2A>, val inner2Level2: InnerLevel2A)
data class InnerLevel1B(val inner1Level2: List<InnerLevel2B>, val inner2Level2: InnerLevel2B)
data class InnerLevel2A(val x: String, val y: Int)
data class InnerLevel2B(val x: Int, val y: String)

@CastCastleMapper
internal interface CollectionsMapper {
    @CastCastleMapper
    fun mapA(a: A): B = mapACastCastle(a) // used generated function

    @CastCastleMapper
    fun mapB(b: B): A = mapBCastCastle(b) // used generated function

    fun string(string: String): Int = string.toInt()
    fun int(int: Int): String = "$int"

    @CastCastleMapper
    fun mapLevel1A(inner1Level1: InnerLevel1A): InnerLevel1B =
        mapLevel1ACastCastle(inner1Level1) // used generated function

    @CastCastleMapper
    fun mapLevel1B(inner1Level1: InnerLevel1B): InnerLevel1A =
        mapLevel1BCastCastle(inner1Level1) // used generated function
}
```

Generated code:

```kotlin
internal fun ru.vafeen.samples.sample2.kotlin.CollectionsMapper.mapACastCastle(a: ru.vafeen.samples.sample2.kotlin.A): ru.vafeen.samples.sample2.kotlin.B {
    return ru.vafeen.samples.sample2.kotlin.B(
        inner1Level1 = mutableListOf<ru.vafeen.samples.sample2.kotlin.InnerLevel1B>().apply {
            a.inner1Level1.forEach { it1 -> add(mapLevel1A(it1)) }
        },
        inner2Level1 = mapLevel1A(a.inner2Level1)
    )
}

internal fun ru.vafeen.samples.sample2.kotlin.CollectionsMapper.mapBCastCastle(b: ru.vafeen.samples.sample2.kotlin.B): ru.vafeen.samples.sample2.kotlin.A {
    return ru.vafeen.samples.sample2.kotlin.A(
        inner1Level1 = mutableListOf<ru.vafeen.samples.sample2.kotlin.InnerLevel1A>().apply {
            b.inner1Level1.forEach { it3 -> add(mapLevel1B(it3)) }
        },
        inner2Level1 = mapLevel1B(b.inner2Level1)
    )
}

internal fun ru.vafeen.samples.sample2.kotlin.CollectionsMapper.mapLevel1ACastCastle(inner1Level1: ru.vafeen.samples.sample2.kotlin.InnerLevel1A): ru.vafeen.samples.sample2.kotlin.InnerLevel1B {
    return ru.vafeen.samples.sample2.kotlin.InnerLevel1B(
        inner1Level2 = mutableListOf<ru.vafeen.samples.sample2.kotlin.InnerLevel2B>().apply {
            inner1Level1.inner1Level2.forEach { it5 ->
                add(
                    ru.vafeen.samples.sample2.kotlin.InnerLevel2B(
                        x = string(it5.x),
                        y = int(it5.y)
                    )
                )
            }

        },
        inner2Level2 = ru.vafeen.samples.sample2.kotlin.InnerLevel2B(
            x = string(inner1Level1.inner2Level2.x),
            y = int(inner1Level1.inner2Level2.y)
        )
    )
}

internal fun ru.vafeen.samples.sample2.kotlin.CollectionsMapper.mapLevel1BCastCastle(inner1Level1: ru.vafeen.samples.sample2.kotlin.InnerLevel1B): ru.vafeen.samples.sample2.kotlin.InnerLevel1A {
    return ru.vafeen.samples.sample2.kotlin.InnerLevel1A(
        inner1Level2 = mutableListOf<ru.vafeen.samples.sample2.kotlin.InnerLevel2A>().apply {
            inner1Level1.inner1Level2.forEach { it7 ->
                add(
                    ru.vafeen.samples.sample2.kotlin.InnerLevel2A(
                        x = int(it7.x),
                        y = string(it7.y)
                    )
                )
            }

        },
        inner2Level2 = ru.vafeen.samples.sample2.kotlin.InnerLevel2A(
            x = int(inner1Level1.inner2Level2.x),
            y = string(inner1Level1.inner2Level2.y)
        )
    )
}
```

### Java

Source code:

```java
// File: ./A.java
public class A {
    private final List<InnerLevel1A> inner1Level1;
    private final InnerLevel1A inner2Level1;

    public A(List<InnerLevel1A> inner1Level1, InnerLevel1A inner2Level1) {
        this.inner1Level1 = inner1Level1;
        this.inner2Level1 = inner2Level1;
    }

    public List<InnerLevel1A> getInner1Level1() {
        return inner1Level1;
    }

    public InnerLevel1A getInner2Level1() {
        return inner2Level1;
    }
}

// File: ./B.java
public class B {
    private final List<InnerLevel1B> inner1Level1;
    private final InnerLevel1B inner2Level1;

    public B(List<InnerLevel1B> inner1Level1, InnerLevel1B inner2Level1) {
        this.inner1Level1 = inner1Level1;
        this.inner2Level1 = inner2Level1;
    }

    public List<InnerLevel1B> getInner1Level1() {
        return inner1Level1;
    }

    public InnerLevel1B getInner2Level1() {
        return inner2Level1;
    }
}

// File: ./InnerLevel1A.java
public class InnerLevel1A {
    private final List<InnerLevel2A> inner1Level2;
    private final InnerLevel2A inner2Level2;

    public InnerLevel1A(List<InnerLevel2A> inner1Level2, InnerLevel2A inner2Level2) {
        this.inner1Level2 = inner1Level2;
        this.inner2Level2 = inner2Level2;
    }

    public List<InnerLevel2A> getInner1Level2() {
        return inner1Level2;
    }

    public InnerLevel2A getInner2Level2() {
        return inner2Level2;
    }
}

// File: ./InnerLevel1B.java
public class InnerLevel1B {
    private final List<InnerLevel2B> inner1Level2;
    private final InnerLevel2B inner2Level2;

    public InnerLevel1B(List<InnerLevel2B> inner1Level2, InnerLevel2B inner2Level2) {
        this.inner1Level2 = inner1Level2;
        this.inner2Level2 = inner2Level2;
    }

    public List<InnerLevel2B> getInner1Level2() {
        return inner1Level2;
    }

    public InnerLevel2B getInner2Level2() {
        return inner2Level2;
    }
}


// File: ./InnerLevel2A.java
class InnerLevel2A {
    private final String x;
    private final int y;

    public InnerLevel2A(String x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}

// File: ./InnerLevel2B.java
class InnerLevel2B {
    private final int x;
    private final String y;

    public InnerLevel2B(int x, String y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}

// File: ./CollectionsMapper.java
import static ru.vafeen.samples.sample2.java.CollectionsMapperCastCastleKt.mapACastCastle;
import static ru.vafeen.samples.sample2.java.CollectionsMapperCastCastleKt.mapBCastCastle;
import static ru.vafeen.samples.sample2.java.CollectionsMapperCastCastleKt.mapLevel1ACastCastle;
import static ru.vafeen.samples.sample2.java.CollectionsMapperCastCastleKt.mapLevel1BCastCastle;

@CastCastleMapper
public interface CollectionsMapper {

    @CastCastleMapper
    default B mapA(A a) {
        return mapACastCastle(this, a); // used generated function
    }

    @CastCastleMapper
    default A mapB(B b) {
        return mapBCastCastle(this, b); // used generated function
    }

    default int string(String string) {
        return Integer.parseInt(string);
    }

    default String mapInt(int i) {
        return String.valueOf(i);
    }

    @CastCastleMapper
    default InnerLevel1B mapLevel1A(InnerLevel1A inner1Level1) {
        return mapLevel1ACastCastle(this, inner1Level1); // used generated function
    }

    @CastCastleMapper
    default InnerLevel1A mapLevel1B(InnerLevel1B inner1Level1) {
        return mapLevel1BCastCastle(this, inner1Level1); // used generated function
    }

}
```

Generated code:

```kotlin
public fun ru.vafeen.samples.sample2.java.CollectionsMapper.mapACastCastle(a: ru.vafeen.samples.sample2.java.A): ru.vafeen.samples.sample2.java.B {
    return ru.vafeen.samples.sample2.java.B(
        mutableListOf<ru.vafeen.samples.sample2.java.InnerLevel1B>().apply {
            a.inner1Level1.forEach { it1 -> add(mapLevel1A(it1)) }
        },
        mapLevel1A(a.inner2Level1)
    )
}

public fun ru.vafeen.samples.sample2.java.CollectionsMapper.mapBCastCastle(b: ru.vafeen.samples.sample2.java.B): ru.vafeen.samples.sample2.java.A {
    return ru.vafeen.samples.sample2.java.A(
        mutableListOf<ru.vafeen.samples.sample2.java.InnerLevel1A>().apply {
            b.inner1Level1.forEach { it3 -> add(mapLevel1B(it3)) }
        },
        mapLevel1B(b.inner2Level1)
    )
}

public fun ru.vafeen.samples.sample2.java.CollectionsMapper.mapLevel1ACastCastle(inner1Level1: ru.vafeen.samples.sample2.java.InnerLevel1A): ru.vafeen.samples.sample2.java.InnerLevel1B {
    return ru.vafeen.samples.sample2.java.InnerLevel1B(
        mutableListOf<ru.vafeen.samples.sample2.java.InnerLevel2B>().apply {
            inner1Level1.inner1Level2.forEach { it5 ->
                add(
                    ru.vafeen.samples.sample2.java.InnerLevel2B(
                        string(it5.x),
                        mapInt(it5.y)
                    )
                )
            }

        },
        ru.vafeen.samples.sample2.java.InnerLevel2B(
            string(inner1Level1.inner2Level2.x),
            mapInt(inner1Level1.inner2Level2.y)
        )
    )
}

public fun ru.vafeen.samples.sample2.java.CollectionsMapper.mapLevel1BCastCastle(inner1Level1: ru.vafeen.samples.sample2.java.InnerLevel1B): ru.vafeen.samples.sample2.java.InnerLevel1A {
    return ru.vafeen.samples.sample2.java.InnerLevel1A(
        mutableListOf<ru.vafeen.samples.sample2.java.InnerLevel2A>().apply {
            inner1Level1.inner1Level2.forEach { it7 ->
                add(
                    ru.vafeen.samples.sample2.java.InnerLevel2A(
                        mapInt(it7.x),
                        string(it7.y)
                    )
                )
            }

        },
        ru.vafeen.samples.sample2.java.InnerLevel2A(
            mapInt(inner1Level1.inner2Level2.x),
            string(inner1Level1.inner2Level2.y)
        )
    )
}
```

## Additional fields mappers

### Kotlin

Source code:

```kotlin
data class A(val x: Int, val y: Int)
data class B(val x: Int, val z: Int)

@CastCastleMapper
class AdditionalFieldsMapper {
    @CastCastleMapper
    fun mapA(a: A): B = mapACastCastle(a, 1) // used generated function

    @CastCastleMapper
    fun mapB(b: B): A = mapBCastCastle(b, 1) // used generated function
}
```

Generated code:

```kotlin
public fun ru.vafeen.samples.sample3.kotlin.AdditionalFieldsMapper.mapACastCastle(
    a: ru.vafeen.samples.sample3.kotlin.A,
    z: kotlin.Int
): ru.vafeen.samples.sample3.kotlin.B {
    return ru.vafeen.samples.sample3.kotlin.B(
        x = a.x,
        z = z
    )
}

public fun ru.vafeen.samples.sample3.kotlin.AdditionalFieldsMapper.mapBCastCastle(
    b: ru.vafeen.samples.sample3.kotlin.B,
    y: kotlin.Int
): ru.vafeen.samples.sample3.kotlin.A {
    return ru.vafeen.samples.sample3.kotlin.A(
        x = b.x,
        y = y
    )
}
```

### Java

Source code:

```java
// File: ./A.java
public class A {
    private int x;
    private int y;

    public A(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}

// File ./B.java
public class B {
    private int x;
    private int z;

    public B(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}

// File ./AdditionalFieldsMapper.java
import static ru.vafeen.samples.sample3.java.AdditionalFieldsMapperCastCastleKt.mapCastCastle;
import static ru.vafeen.samples.sample3.java.AdditionalFieldsMapperCastCastleKt.mapCastCastle;

@CastCastleMapper
public class AdditionalFieldsMapper {

    @CastCastleMapper
    public A map(B b) {
        return mapCastCastle(this, b, 1); // used generated function
    }

    @CastCastleMapper
    public B map(A a) {
        return mapCastCastle(this, a, 1); // used generated function
    }
}
```

Generated code:

```kotlin
public fun ru.vafeen.samples.sample3.java.AdditionalFieldsMapper.mapCastCastle(
    b: ru.vafeen.samples.sample3.java.B,
    y: kotlin.Int
): ru.vafeen.samples.sample3.java.A {
    return ru.vafeen.samples.sample3.java.A(
        b.x,
        y
    )
}

public fun ru.vafeen.samples.sample3.java.AdditionalFieldsMapper.mapCastCastle(
    a: ru.vafeen.samples.sample3.java.A,
    z: kotlin.Int
): ru.vafeen.samples.sample3.java.B {
    return ru.vafeen.samples.sample3.java.B(
        a.x,
        z
    )
}
```

## Kotlin Companion object mappers

Source code:

```kotlin
data class A(val x: Int, val y: Int)
data class B(val x: Int, val z: Int)


class CompanionObjectFuncs {
    @CastCastleMapper
    companion object {
        @CastCastleMapper
        fun companionObjectFuncsMapper(a: A): B =
            companionObjectFuncsMapperCastCastle(a, 1) // used generated function
    }
}
```

Generated code:

```kotlin
public fun ru.vafeen.samples.sample4.kotlin.CompanionObjectFuncs.Companion.companionObjectFuncsMapperCastCastle(
    a: ru.vafeen.samples.sample4.kotlin.A,
    z: kotlin.Int
): ru.vafeen.samples.sample4.kotlin.B {
    return ru.vafeen.samples.sample4.kotlin.B(
        x = a.x,
        z = z
    )
}
```

## Kotlin Extension mappers (demonstrated with additional fields)

Source code:

```kotlin
data class A(val x: Int, val y: Int)
data class B(val x: Int, val z: Int)

@CastCastleMapper
fun standaloneMapper1(a: A): B = standaloneMapper1CastCastle(a, 1) // used generated function

@CastCastleMapper
fun B.standaloneMapper2(): A = standaloneMapper2CastCastle(1) // used generated function
```

Generated code:

```kotlin
public fun standaloneMapper1CastCastle(
    a: ru.vafeen.samples.sample3.kotlin.A,
    z: kotlin.Int
): ru.vafeen.samples.sample3.kotlin.B {
    return ru.vafeen.samples.sample3.kotlin.B(
        x = a.x,
        z = z
    )
}

public fun ru.vafeen.samples.sample3.kotlin.B.standaloneMapper2CastCastle(y: kotlin.Int): ru.vafeen.samples.sample3.kotlin.A {
    return ru.vafeen.samples.sample3.kotlin.A(
        x = this.x,
        y = y
    )
}
```

