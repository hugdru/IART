package parser;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;

final class RawTask {
    private static int idCounter = 0;
    final int id = RawTask.idCounter++;
    final String name;
    final double duration;
    final ArrayList<Integer> precedences;
    final ArrayList<Integer> skills;

    RawTask(String name, double duration, ArrayList<Integer> precedences, ArrayList<Integer> skills) {
        this.name = name;
        this.duration = duration;
        this.precedences = precedences;
        this.skills = skills;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("duration", duration)
                .add("precedences", precedences)
                .add("skills", skills)
                .toString();
    }
}
