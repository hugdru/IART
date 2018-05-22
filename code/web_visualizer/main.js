var problemData = null;
var problemResult = null;
var problemDataInput = null;
var problemResultInput = null;
var timelineDiv = null;
var buildSubmit = null;
var visualizerDiv = null;
var tasksDiv = null;
var elementsDiv = null;
var totalDurationP = null;

window.onload = function () {
    problemDataInput = document.getElementById("problemData");
    problemResultInput = document.getElementById("problemResult");
    timelineDiv = document.getElementById("timeline");
    buildSubmit = document.getElementById("build");
    visualizerDiv = document.getElementById("visualizer");
    tasksDiv = document.getElementById("tasks");
    elementsDiv = document.getElementById("elements");
    totalDurationP = document.getElementById("totalDuration");

    buildSubmitListener();
};

function buildSubmitListener() {
    buildSubmit.addEventListener("click", function () {

        buildObjectFromInputFile(problemDataInput, function (obj) {
            problemData = obj;
        });

        buildObjectFromInputFile(problemResultInput, function (obj) {
            problemResult = obj;
        });
    });
}

function buildObjectFromInputFile(input, fn) {
    if (input.files.length === 0) {
        return null;
    }

    var file = input.files[0];
    var reader = new FileReader();
    reader.readAsText(file);

    reader.onload = function (e) {

        var obj = JSON.parse(e.target.result);

        fn(obj);

        if (problemData && problemResult && window.chartsLoaded) {
            visualizerDiv.style.display = "block";
            totalDurationP.innerHTML = "";
            timelineDiv.innerHTML = "";
            tasksDiv.innerHTML = "";
            elementsDiv.innerHTML = "";

            totalDurationP.innerHTML = drawTimeline(problemResult, timelineDiv) + " hours";
            showInfo(problemData, problemResult, tasksDiv, elementsDiv);
        }
    }
}