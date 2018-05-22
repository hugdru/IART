package parser;

final class RawElement {
    private static int idCounter = 0;
    final int id = RawElement.idCounter++;
    final String name;
    final int[][] skills;

    RawElement(String name, int[][] skills) {
        this.name = name;
        this.skills = skills;
    }
}