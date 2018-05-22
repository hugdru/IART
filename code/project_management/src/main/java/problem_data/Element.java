package problem_data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Objects;

public final class Element implements Comparable<Element>, utils.Copyable<Element> {

    private final int id;
    private final String name;
    private final ImmutableMap<Skill, Skill.Value> skillsValues;

    public Element(int id, String name, Map<Skill, Skill.Value> skillsValues) {
        Preconditions.checkArgument(utils.Strings.notBlank(name), "Skill.name must not be null or empty");
        this.id = id;
        this.name = name;
        this.skillsValues = ImmutableMap.copyOf(skillsValues);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ImmutableMap<Skill, Skill.Value> getSkills() {
        return skillsValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element that = (Element) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Element that) {
        return ComparisonChain.start()
                .compare(this.id, that.id)
                .result();
    }

    @Override
    public Element copy() {
        return this;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("skillsValues", skillsValues)
                .toString();
    }
}