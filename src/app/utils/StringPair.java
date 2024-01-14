package app.utils;

import java.util.Objects;

public final class StringPair {
    private String s1;
    private String s2;

    public StringPair(final String s1, final String s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public String getS1() {
        return s1;
    }

    public String getS2() {
        return s2;
    }

    public void setS1(final String s1) {
        this.s1 = s1;
    }

    public void setS2(final String s2) {
        this.s2 = s2;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StringPair that = (StringPair) o;
        return Objects.equals(s1, that.s1) && Objects.equals(s2, that.s2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s1, s2);
    }

}
