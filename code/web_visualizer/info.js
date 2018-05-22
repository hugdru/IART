var tasksSource = "<h2>Tasks</h2><table class=\"pure-table pure-table-bordered\"><thead><tr><th>name(id)</th><th>elements(id)</th><th>skills</th><th>precedences</th></tr>{{#each this}}</thead><tbody><tr><td>{{name}}({{id}})</td><td>{{elements}}</td><td>{{skills}}</td><td>{{precedences}}</td></tr>{{/each}}</tbody></table>\n";
var tasksTemplate = Handlebars.compile(tasksSource);

var elementsSource = "<h2>Elements<h2><table class=\"pure-table pure-table-bordered\"><thead><tr><th>name(id)</th><th>skills</th></tr></thead><tbody>{{#each this}}<tr><td>{{name}}({{id}})</td><td>{{skills}}</td></tr>{{/each}}</tbody></table>";
var elementsTemplate = Handlebars.compile(elementsSource);

function showInfo(problemData, problemResult, tasksElement, elementsElement) {
    tasksElement.innerHTML = tasksTemplate(buildTasks(problemData, problemResult));
    elementsElement.innerHTML = elementsTemplate(buildElements(problemData));
}

function buildTasks(problemData, problemResult) {
    return _.map(problemResult, function (partialResultObj) {

        var task = problemData.tasks[partialResultObj.task.id];

        var elements = _.map(partialResultObj.elements, function (elementObj) {
            return elementObj.name + "(" + elementObj.id + ")";
        }).join(", ");

        var skills = _.map(task.skills, function (skillId) {
            return problemData.skills[skillId];
        }).join(", ");

        var precedences = _.map(task.precedences, function (precedenceId) {
            var precedence = problemData.tasks[precedenceId];
            return precedence.name + "(" + precedenceId + ")";
        }).join(", ");

        return {
            "id": partialResultObj.task.id,
            "name": partialResultObj.task.name,
            "elements": elements,
            "skills": skills,
            "precedences": precedences
        };
    });
}

function buildElements(problemData) {
    return _.map(problemData.elements, function (elementObj, elementId) {
        var skills = _.map(elementObj.skills, function (skillObj, index) {
            return problemData.skills[index];
        }).join(", ");

        return {"id": elementId, "name": elementObj.name, "skills": skills};
    });
}