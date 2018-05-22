package state;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import problem_data.Element;
import problem_data.Task;

import java.util.Objects;

public final class TaskResult {

    private final Task task;
    private final double duration;
    private final Range<Double> timeSpan;
    private final ImmutableSet<Element> elements;

    public TaskResult(Task task, double startTime, double duration, ImmutableSet<Element> elements) {
        this.task = task;
        this.duration = duration;
        this.timeSpan = Range.closed(startTime, startTime + duration);
        this.elements = elements;
    }

    public Task getTask() {
        return task;
    }

    public double getDuration() {
        return duration;
    }

    public Range<Double> getTimeSpan() {
        return this.timeSpan;
    }

    public double getStartTime() {
        return this.timeSpan.lowerEndpoint();
    }

    public double getEndTime() {
        return this.timeSpan.upperEndpoint();
    }

    public ImmutableSet<Element> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("task", task)
                .add("duration", duration)
                .add("timeSpan", timeSpan)
                .add("elements", elements)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskResult that = (TaskResult) o;
        return Double.compare(that.duration, duration) == 0 &&
                Objects.equals(task, that.task) &&
                Objects.equals(timeSpan, that.timeSpan) &&
                Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, duration, timeSpan, elements);
    }
}
