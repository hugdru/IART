package problem_data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import utils.Copyable;

import java.util.List;
import java.util.Set;

public final class ProblemData implements Copyable<ProblemData> {
    private final ImmutableList<Element> elements;
    private final ImmutableSet<Skill> skills;
    private final ImmutableList<Task> tasks;

    private ProblemData(Builder builder) {
        elements = builder.elements;
        skills = builder.skills;
        tasks = builder.tasks;
    }

    public ImmutableList<Element> getElements() {
        return elements;
    }

    public ImmutableSet<Skill> getSkills() {
        return skills;
    }

    public ImmutableList<Task> getTasks() {
        return tasks;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("elements", elements)
                .add("skills", skills)
                .add("tasks", tasks)
                .toString();
    }

    @Override
    public ProblemData copy() {
        return this;
    }

    public static class Builder {
        private ImmutableList<Element> elements;
        private ImmutableSet<Skill> skills;
        private ImmutableList<Task> tasks;

        public ImmutableList<Task> getTasks() {
            return tasks;
        }

        public Builder setTasks(List<Task> tasks) {
            this.tasks = ImmutableList.copyOf(tasks);
            return this;
        }

        public ImmutableList<Element> getElements() {
            return elements;
        }

        public Builder setElements(List<Element> elements) {
            this.elements = ImmutableList.copyOf(elements);
            return this;
        }

        public ImmutableSet<Skill> getSkills() {
            return skills;
        }

        public Builder setSkills(Set<Skill> skills) {
            this.skills = ImmutableSet.copyOf(skills);
            return this;
        }

        public ProblemData build() {
            Preconditions.checkArgument(elements != null, "ProblemData.Builder.elements must not be null");
            Preconditions.checkArgument(skills != null, "ProblemData.Builder.skills must not be null");
            Preconditions.checkArgument(tasks != null, "ProblemData.Builder.tasks must not be null");
            return new ProblemData(this);
        }
    }
}
