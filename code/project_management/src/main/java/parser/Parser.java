package parser;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import problem_data.Element;
import problem_data.ProblemData;
import problem_data.Skill;
import problem_data.Task;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

public class Parser {
    public ProblemData parse(String filepath) throws Exception {

        ProblemData.Builder dataBuilder = new ProblemData.Builder();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(RawElement.class, new RawElementDeserializer());
        gsonBuilder.registerTypeAdapter(RawTask.class, new RawTaskDeserializer());

        Gson customGson = gsonBuilder.create();

        JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));
        JsonParser jsonParser = new JsonParser();

        JsonObject jsonGlobalObject = jsonParser.parse(jsonReader).getAsJsonObject();

        List<String> rawSkills = getRawSkills(customGson, jsonGlobalObject);
        Set<Skill> skills = convertRawSkills(rawSkills);
        dataBuilder.setSkills(skills);

        HashMap<Integer, Skill> skillsMap = new HashMap<>();
        for (Skill skill : skills) {
            skillsMap.put(skill.getId(), skill);
        }

        List<RawElement> rawElements = getRawElements(customGson, jsonGlobalObject);
        dataBuilder.setElements(convertRawElements(rawElements, skillsMap));

        List<RawTask> rawTasks = getRawTasks(customGson, jsonGlobalObject);
        dataBuilder.setTasks(convertRawTasks(rawTasks, skillsMap));

        return dataBuilder.build();
    }

    private List<String> getRawSkills(Gson gson, JsonObject jsonGlobalObject) {
        Type rawSkillsArrayListType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(jsonGlobalObject.get("skills"), rawSkillsArrayListType);
    }

    private Set<Skill> convertRawSkills(List<String> rawSkills) {
        HashSet<Skill> skills = new HashSet<>();
        int i = 0;
        for (String skillName : rawSkills) {
            skills.add(new Skill(i, skillName));
            ++i;
        }
        return skills;
    }

    private List<RawElement> getRawElements(Gson gson, JsonObject jsonGlobalObject) {
        Type rawElementsArrayListType = new TypeToken<ArrayList<RawElement>>() {
        }.getType();
        return gson.fromJson(jsonGlobalObject.get("elements"), rawElementsArrayListType);
    }

    private List<Element> convertRawElements(List<RawElement> rawElements, HashMap<Integer, Skill> skillsMap) {
        ArrayList<Element> elements = new ArrayList<>();
        for (RawElement rawElement : rawElements) {
            HashMap<Skill, Skill.Value> skillsValues = new HashMap<>();
            for (int[] rawSkillValue : rawElement.skills) {
                Skill.Value value = new Skill.Value(rawSkillValue[1]);
                skillsValues.putIfAbsent(skillsMap.get(rawSkillValue[0]), value);
            }

            elements.add(new Element(rawElement.id, rawElement.name, skillsValues));
        }
        return elements;
    }

    private List<RawTask> getRawTasks(Gson gson, JsonObject jsonGlobalObject) {
        Type rawTasksArrayListType = new TypeToken<ArrayList<RawTask>>() {
        }.getType();

        return gson.fromJson(jsonGlobalObject.get("tasks"), rawTasksArrayListType);
    }

    private List<Task> convertRawTasks(List<RawTask> rawTasks, HashMap<Integer, Skill> skillsMap) {

        final HashMap<Integer, Task.TaskBuilder> taskBuilderCache = new HashMap<>();

        Function<Integer, Task.TaskBuilder> createOrRetrieveFromTaskBuilderCache = (Integer taskId) -> {
            Task.TaskBuilder taskBuilder = taskBuilderCache.get(taskId);
            if (taskBuilder == null) {
                taskBuilder = new Task.TaskBuilder(taskId);
                taskBuilderCache.put(taskId, taskBuilder);
            }
            return taskBuilder;
        };

        for (RawTask rawTask : rawTasks) {

            Task.TaskBuilder taskBuilder = createOrRetrieveFromTaskBuilderCache.apply(rawTask.id);

            taskBuilder.setName(rawTask.name);
            taskBuilder.setDuration(rawTask.duration);

            for (Integer skillIndex : rawTask.skills) {
                taskBuilder.addSkill(skillsMap.get(skillIndex));
            }

            for (Integer precedenceTaskId : rawTask.precedences) {
                Task.TaskBuilder precedenceTaskBuilder = createOrRetrieveFromTaskBuilderCache.apply(precedenceTaskId);
                taskBuilder.addPrecedence(precedenceTaskBuilder);
                precedenceTaskBuilder.addSuccessor(taskBuilder);
            }
        }

        ArrayList<Task> tasks = new ArrayList<>();
        for (Map.Entry<Integer, Task.TaskBuilder> integerTaskEntry : taskBuilderCache.entrySet()) {
            Task.TaskBuilder taskBuilder = integerTaskEntry.getValue();
            tasks.add(taskBuilder.build());
        }

        return tasks;
    }
}