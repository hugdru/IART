package problem_data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

import java.util.Objects;

public final class Skill implements Comparable<Skill>, utils.Copyable<Skill> {

    private final int id;
    private final String name;

    public Skill(int id, String name) {
        Preconditions.checkArgument(!utils.Strings.isBlank(name), "Skill.name must not be null or empty");
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skill that = (Skill) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .toString();
    }

    @Override
    public int compareTo(Skill that) {
        return ComparisonChain.start()
                .compare(this.id, that.id)
                .result();
    }

    @Override
    public Skill copy() {
        return this;
    }

    public static final class Value implements Comparable<Value>, utils.Copyable<Value> {

        private final int n;

        public Value(int n) {
            Preconditions.checkArgument(n >= 1 && n <= 5, "Skill.Value.n must be 1 <= value <= 5, got %d", n);
            this.n = n;
        }

        public int getValue() {
            return n;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Value that = (Value) o;
            return Objects.equals(n, that.n);
        }

        @Override
        public int hashCode() {
            return Objects.hash(n);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("value", n)
                    .toString();
        }

        @Override
        public int compareTo(Value that) {
            return ComparisonChain.start()
                    .compare(this.n, that.n)
                    .result();
        }

        @Override
        public Value copy() {
            return this;
        }
    }
}
