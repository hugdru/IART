package state;

import com.google.common.collect.*;
import org.organicdesign.fp.collections.*;
import org.organicdesign.fp.tuple.Tuple2;
import problem_data.Element;
import problem_data.ProblemData;
import problem_data.Skill;
import problem_data.Task;

import java.util.Objects;
import java.util.Set;

public final class State {

    private PersistentHashSet<Task> remainingTasks;

    private PersistentHashSet<Element> elementsNeverAllocated;

    private PersistentHashSet<Element> elementsBeenAllocated;

    private PersistentHashMap<Task, Integer> taskPrecedencesCounter;

    private PersistentHashMap<Integer, TaskResult> taskIdTaskResult;

    private PersistentTreeMap<Double, PersistentVector<TaskResult>> startTimeTaskResult;

    private double maxEndTime = 0;

    private ImmutableList<TaskResult> result = null;

    private State() {
    }

    public static State createInitialState(ProblemData problemData) {

        State state = new State();

        state.remainingTasks = PersistentHashSet.of(problemData.getTasks());

        state.elementsNeverAllocated = PersistentHashSet.of(problemData.getElements());

        state.elementsBeenAllocated = PersistentHashSet.empty();

        PersistentHashMap.MutableHashMap<Task, Integer> taskPrecedencesCounterMut = PersistentHashMap.emptyMutable();
        for (Task task : problemData.getTasks()) {
            taskPrecedencesCounterMut.assoc(task, task.getPrecedences().size());
        }
        state.taskPrecedencesCounter = taskPrecedencesCounterMut.immutable();

        state.taskIdTaskResult = PersistentHashMap.empty();

        state.startTimeTaskResult = PersistentTreeMap.empty();

        return state;
    }

    double getTotalTimeDelta(State destinationState) {
        return destinationState.maxEndTime - maxEndTime;
    }

    public ImmutableList<State> generateSuccessors() {

        ImmutableList<Task> tasksCurrentlyWithNoPrecedences = pickTasksCurrentlyWithNoPrecedences();

        ImmutableList.Builder<State> successorsStatesBuilder = new ImmutableList.Builder<>();

        for (Task selectedTask : tasksCurrentlyWithNoPrecedences) {
            ImmutableList<ElementsCombination> elementsCombinations = getCompatibleElementsCombinations(selectedTask);
            generateNewStates(selectedTask, elementsCombinations, successorsStatesBuilder);
        }

        return successorsStatesBuilder.build();
    }

    double heuristic() {
        double taskMaxEndTime = maxEndTime;
        for (Task task : pickTasksCurrentlyWithNoPrecedences()) {
            double startTime = getTaskStartTimeFromPrecedences(task);
            double elementsCompetenceSum = getCompatibleElementsSum(task);
            double duration = getTaskReducedDuration(task.getDuration(), elementsCompetenceSum);
            taskMaxEndTime = Math.max(taskMaxEndTime, startTime + duration);
        }
        return taskMaxEndTime - maxEndTime;
    }

    private ImmutableList<ElementsCombination> getCompatibleElementsCombinations(Task task) {

        Set<Skill> taskSkills = task.getSkills();

        double taskStartTimeFromPrecedences = getTaskStartTimeFromPrecedences(task);

        ImmutableSet.Builder<ElementToCombine> elementsToCombineBuilder = new ImmutableSet.Builder<>();

        appendCompatibleElementsFromElementsAllocated(elementsToCombineBuilder, taskSkills);

        appendCompatibleElementsFromElementsNeverAllocated(elementsToCombineBuilder, taskSkills);

        Set<Set<ElementToCombine>> allElementsToCombine = Sets.powerSet(elementsToCombineBuilder.build());

        ImmutableList.Builder<ElementsCombination> elementsCombinationBuilder = new ImmutableList.Builder<>();

        for (Set<ElementToCombine> elementsToCombine : allElementsToCombine) {

            if (elementsToCombine.isEmpty()) {
                continue;
            }

            double duration = getTaskReducedDuration(task, elementsToCombine);
            Tuple2<ImmutableSet<Element>, Double> tuple = getStartTime(taskStartTimeFromPrecedences, duration, elementsToCombine);
            ImmutableSet<Element> elements = tuple.getKey();
            double startTime = tuple.getValue();

            Tuple2<PersistentHashSet<Element>, PersistentHashSet<Element>> elementsAllocatedTuple = updateElementsAllocated(elementsToCombine);

            elementsCombinationBuilder.add(new ElementsCombination(startTime, duration, elements, elementsAllocatedTuple.getKey(), elementsAllocatedTuple.getValue()));
        }

        return elementsCombinationBuilder.build();
    }

    private Tuple2<ImmutableSet<Element>, Double> getStartTime(double taskStartTimeFromPrecedences, double duration, Set<ElementToCombine> elementsToCombine) {

        ImmutableSet.Builder<Element> elementsBuilder = new ImmutableSet.Builder<>();
        for (ElementToCombine elementToCombine : elementsToCombine) {
            elementsBuilder.add(elementToCombine.element);
        }
        ImmutableSet<Element> elements = elementsBuilder.build();

        double startTime = taskStartTimeFromPrecedences;

        boolean foundTentativeRange;
        for (UnmodMap.UnEntry<Double, PersistentVector<TaskResult>> doubleTaskResultsUnEntry : startTimeTaskResult) {
            for (TaskResult taskResult : doubleTaskResultsUnEntry.getValue()) {
                if (taskResult.getEndTime() < taskStartTimeFromPrecedences) {
                    continue;
                }

                Range<Double> tentativeTimeSpan = Range.open(startTime, startTime + duration);

                boolean rangesIntersect;
                try {
                    rangesIntersect = !tentativeTimeSpan.intersection(taskResult.getTimeSpan()).isEmpty();
                } catch (IllegalArgumentException iae) {
                    rangesIntersect = false;
                }

                boolean elementsIntersect = false;
                if (rangesIntersect) {
                    elementsIntersect = !Sets.intersection(taskResult.getElements(), elements).isEmpty();
                }

                if (!rangesIntersect) {
                    foundTentativeRange = true;
                } else {
                    foundTentativeRange = !elementsIntersect;
                }

                if (!foundTentativeRange) {
                    startTime = taskResult.getEndTime();
                }
            }
        }

        return Tuple2.of(elements, startTime);
    }

    private Tuple2<PersistentHashSet<Element>, PersistentHashSet<Element>> updateElementsAllocated(Set<ElementToCombine> elementsCombination) {
        PersistentHashSet<Element> updatedElementsNeverAllocated = elementsNeverAllocated;
        PersistentHashSet<Element> updatedElementsBeenAllocated = elementsBeenAllocated;
        for (ElementToCombine elementToCombine : elementsCombination) {
            if (elementToCombine.isElementNeverAllocated) {
                updatedElementsNeverAllocated = updatedElementsNeverAllocated.without(elementToCombine.element);
                updatedElementsBeenAllocated = updatedElementsBeenAllocated.put(elementToCombine.element);
            }
        }
        return Tuple2.of(updatedElementsNeverAllocated, updatedElementsBeenAllocated);
    }

    private void appendCompatibleElementsFromElementsAllocated(ImmutableSet.Builder<ElementToCombine> elementsToCombineBuilder, Set<Skill> taskSkills) {
        appendCompatibleElements(elementsToCombineBuilder, elementsBeenAllocated, taskSkills, false);
    }

    private void appendCompatibleElementsFromElementsNeverAllocated(ImmutableSet.Builder<ElementToCombine> elementsToCombineBuilder, Set<Skill> taskSkills) {
        appendCompatibleElements(elementsToCombineBuilder, elementsNeverAllocated, taskSkills, true);
    }

    private void appendCompatibleElements(ImmutableSet.Builder<ElementToCombine> elementsToCombineBuilder, Iterable<Element> elements, Set<Skill> taskSkills, boolean isElementNeverAllocated) {
        for (Element element : elements) {
            if (elementHasAllSkills(element, taskSkills)) {
                elementsToCombineBuilder.add(new ElementToCombine(element, isElementNeverAllocated));
            }
        }
    }

    private boolean elementHasAllSkills(Element element, Set<Skill> skills) {
        return element.getSkills().keySet().containsAll(skills);
    }

    private void generateNewStates(Task selectedTask, ImmutableList<ElementsCombination> elementsCombinations, ImmutableList.Builder<State> successorsStates) {

        for (ElementsCombination elementsCombination : elementsCombinations) {
            State generatedState = new State();

            generatedState.remainingTasks = this.remainingTasks.without(selectedTask);

            generatedState.elementsNeverAllocated = elementsCombination.elementsNeverAllocated;

            generatedState.taskPrecedencesCounter = updateTasksPrecedencesCounter(selectedTask);

            TaskResult taskResult = new TaskResult(selectedTask, elementsCombination.startTime, elementsCombination.duration, elementsCombination.elements);

            generatedState.taskIdTaskResult = taskIdTaskResult.assoc(selectedTask.getId(), taskResult);

            PersistentVector<TaskResult> taskResults = startTimeTaskResult.get(taskResult.getStartTime());
            if (taskResults == null) {
                PersistentVector.MutableVector<TaskResult> taskResultsMut = PersistentVector.emptyMutable();
                taskResultsMut.append(taskResult);
                taskResults = taskResultsMut.immutable();
            } else {
                taskResults = taskResults.append(taskResult);
            }
            generatedState.startTimeTaskResult = startTimeTaskResult.assoc(taskResult.getStartTime(), taskResults);

            generatedState.elementsBeenAllocated = elementsCombination.elementsBeenAllocated;

            generatedState.maxEndTime = Math.max(maxEndTime, taskResult.getEndTime());

            successorsStates.add(generatedState);
        }
    }

    private double getTaskStartTimeFromPrecedences(Task task) {
        double startTime = 0;
        for (Task precedence : task.getPrecedences()) {
            TaskResult precedenceTaskResult = taskIdTaskResult.get(precedence.getId());
            startTime = Double.max(startTime, precedenceTaskResult.getEndTime());
        }
        return startTime;
    }

    private ImmutableList<Task> pickTasksCurrentlyWithNoPrecedences() {
        ImmutableList.Builder<Task> tasksWithNoPrecedences = new ImmutableList.Builder<>();
        for (Task remainingTask : remainingTasks) {
            int taskNumberOfPrecedences = taskPrecedencesCounter.get(remainingTask);
            if (taskNumberOfPrecedences == 0) {
                tasksWithNoPrecedences.add(remainingTask);
            }
        }
        return tasksWithNoPrecedences.build();
    }

    private PersistentHashMap<Task, Integer> updateTasksPrecedencesCounter(Task task) {
        PersistentHashMap<Task, Integer> updatedTasksElementsDone = taskPrecedencesCounter;
        for (Task successor : task.getSuccessors()) {
            int successorPrecedenceCount = updatedTasksElementsDone.get(successor);
            updatedTasksElementsDone = updatedTasksElementsDone.assoc(successor, --successorPrecedenceCount);
        }
        return updatedTasksElementsDone;
    }

    private double getTaskReducedDuration(Task task, Set<ElementToCombine> elementsToCombine) {

        int taskSkillsSize = task.getSkills().size();

        double elementsCompetenceSum = 0;
        for (ElementToCombine elementToCombine : elementsToCombine) {
            double elementCompetenceSum = 0;
            for (Skill taskSkill : task.getSkills()) {
                ImmutableMap<Skill, Skill.Value> elementSkills = elementToCombine.element.getSkills();
                elementCompetenceSum += elementSkills.get(taskSkill).getValue();
            }
            elementsCompetenceSum += elementCompetenceSum / taskSkillsSize;
        }

        return getTaskReducedDuration(task.getDuration(), elementsCompetenceSum);
    }

    private double getTaskReducedDuration(double rawTaskDuration, double elementsCompetenceSum) {
        return rawTaskDuration / Math.sqrt(elementsCompetenceSum);
    }

    private double getCompatibleElementsSum(Task task, Iterable<Element> elements) {

        ImmutableSet<Skill> taskSkills = task.getSkills();
        int taskSkillsSize = taskSkills.size();

        double elementsCompetenceSum = 0;
        for (Element element : elements) {
            if (!elementHasAllSkills(element, taskSkills)) {
                continue;
            }
            double elementCompetenceSum = 0;
            for (Skill taskSkill : taskSkills) {
                ImmutableMap<Skill, Skill.Value> elementSkills = element.getSkills();
                elementCompetenceSum += elementSkills.get(taskSkill).getValue();
            }
            elementsCompetenceSum += elementCompetenceSum / taskSkillsSize;
        }

        return elementsCompetenceSum;
    }

    private double getCompatibleElementsSum(Task task) {
        return getCompatibleElementsSum(task, elementsNeverAllocated) + getCompatibleElementsSum(task, elementsBeenAllocated);
    }

    public boolean isFinalState() {
        return remainingTasks.size() == 0;
    }

    public ImmutableList<TaskResult> getResult() {
        if (result != null) {
            return result;
        }

        ImmutableList.Builder<TaskResult> resultBuilder = new ImmutableList.Builder<>();
        for (UnmodMap.UnEntry<Integer, TaskResult> integerTaskResultUnEntry : taskIdTaskResult) {
            resultBuilder.add(integerTaskResultUnEntry.getValue());
        }

        return resultBuilder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State other = (State) o;
        return Objects.equals(taskIdTaskResult, other.taskIdTaskResult);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskIdTaskResult);
    }

    private static class ElementToCombine {
        private final Element element;
        private final boolean isElementNeverAllocated;

        private ElementToCombine(Element element, boolean isElementNeverAllocated) {
            this.element = element;
            this.isElementNeverAllocated = isElementNeverAllocated;
        }
    }

    private static class ElementsCombination {
        private final double startTime;
        private final double duration;
        private final ImmutableSet<Element> elements;
        private final PersistentHashSet<Element> elementsNeverAllocated;
        private final PersistentHashSet<Element> elementsBeenAllocated;

        private ElementsCombination(double startTime, double duration, ImmutableSet<Element> elements, PersistentHashSet<Element> elementsNeverAllocated, PersistentHashSet<Element> elementsBeenAllocated) {
            this.startTime = startTime;
            this.duration = duration;
            this.elements = elements;
            this.elementsNeverAllocated = elementsNeverAllocated;
            this.elementsBeenAllocated = elementsBeenAllocated;
        }
    }
}
