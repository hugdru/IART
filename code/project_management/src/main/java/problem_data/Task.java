package problem_data;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSet;

import java.util.Iterator;
import java.util.Objects;

public final class Task implements Comparable<Task>, utils.Copyable<Task> {
    private int id;
    private String name;
    private double duration;
    private ImmutableSet<Task> precedences;
    private ImmutableSet<Task> successors;
    private ImmutableSet<Skill> skills;

    private Task(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDuration() {
        return duration;
    }

    public ImmutableSet<Task> getPrecedences() {
        return precedences;
    }

    public ImmutableSet<Task> getSuccessors() {
        return successors;
    }

    public ImmutableSet<Skill> getSkills() {
        return skills;
    }

    @Override
    public String toString() {
        StringBuilder successorsSB = new StringBuilder();

        successorsSB.append("[");
        Iterator<Task> successorIterator = successors.iterator();
        if (successorIterator.hasNext()) {
            Task successor = successorIterator.next();
            successorsSB.append("{id: ").append(successor.id).append(", name: ").append(successor.name).append("}");
        }
        while (successorIterator.hasNext()) {
            Task successor = successorIterator.next();
            successorsSB.append(", {id: ").append(successor.id).append(", name: ").append(successor.name).append("}");
        }
        successorsSB.append("]");

        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("duration", duration)
                .add("successors", successorsSB.toString())
                .add("skills", skills)
                .toString();
    }

    @Override
    public Task copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task other = (Task) o;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Task that) {
        return ComparisonChain.start()
                .compare(this.id, that.id)
                .result();
    }

    public final static class TaskBuilder {

        private Task task;
        private ImmutableSet.Builder<Task> precedencesBuilder = new ImmutableSet.Builder<>();
        private ImmutableSet.Builder<Task> successorsBuilder = new ImmutableSet.Builder<>();
        private ImmutableSet.Builder<Skill> skillsBuilder = new ImmutableSet.Builder<>();

        public TaskBuilder(int id) {
            this.task = new Task(id);
        }

        public Task build() {
            ImmutableSet<Task> precedences = precedencesBuilder.build();
            ImmutableSet<Task> successors = successorsBuilder.build();
            ImmutableSet<Skill> skills = skillsBuilder.build();

            Preconditions.checkArgument(utils.Strings.notBlank(task.name), "Task.name must not be null or empty");
            Preconditions.checkArgument(skills != null && skills.size() != 0, "Task.skills there must be at least one skill");

            task.precedences = precedences;
            task.successors = successors;
            task.skills = skills;

            return task;
        }

        public void setName(String name) {
            task.name = name;
        }

        public void setDuration(double duration) {
            task.duration = duration;
        }

        public void addPrecedence(TaskBuilder taskBuilder) {
            precedencesBuilder.add(taskBuilder.task);
        }

        public void addSuccessor(TaskBuilder taskBuilder) {
            successorsBuilder.add(taskBuilder.task);
        }

        public void addSkill(Skill skill) {
            skillsBuilder.add(skill);
        }
    }
}
