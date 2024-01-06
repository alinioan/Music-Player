package app.utils;

import java.util.Objects;

public final class StringPair {
    private String s1;
    private String s2;

    public StringPair(String s1, String s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public String getS1() {
        return s1;
    }

    public String getS2() {
        return s2;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringPair that = (StringPair) o;
        return Objects.equals(s1, that.s1) && Objects.equals(s2, that.s2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s1, s2);
    }

//    public boolean myEquals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        StringPair that = (StringPair) o;
//        return s1.equals(that.s1) && s2.equals(that.s2);
////        return Objects.equals(s1, that.s1) && Objects.equals(s2, that.s2);
//    }

    @Override
    public String toString() {
        return "StringPair{" +
                "s1='" + s1 + '\'' +
                ", s2='" + s2 + '\'' +
                '}';
    }
}
