google.charts.load('current', {'packages': ['timeline']});
google.charts.setOnLoadCallback(chartsCallback);

function chartsCallback() {
    window.chartsLoaded = true;
}

function drawTimeline(problemResult, element) {

    if (!window.chartsLoaded) {
        return;
    }
    var chart = new google.visualization.Timeline(element);
    var dataTable = new google.visualization.DataTable();

    dataTable.addColumn({
        type: 'string',
        id: 'Term'
    });
    dataTable.addColumn({
        type: 'string',
        id: 'Phase'
    });
    dataTable.addColumn({
        type: 'date',
        id: 'Start'
    });
    dataTable.addColumn({
        type: 'date',
        id: 'End'
    });

    rows = [];
    var currentDate = new Date();

    var maxTaskEnd = currentDate;
    for (var i = 0; i < problemResult.length; i++) {
        var partialResult = problemResult[i];

        var taskStart = addDaysToDate(currentDate, partialResult.timeSpan.start);
        var taskEnd = addDaysToDate(currentDate, partialResult.timeSpan.end);

        if (maxTaskEnd.getTime() < taskEnd.getTime()) {
            maxTaskEnd = taskEnd;
        }

        var name = partialResult.task.name + "(" + partialResult.task.id.toString() + ")";
        rows.push([name, name, taskStart, taskEnd]);
    }

    dataTable.addRows(rows);

    var rowHeight = 50;
    var chartHeight = rows.length * rowHeight + rowHeight;

    var options = {
        timeline: {
            showRowLabels: false
        },
        animation: {
            startup: true,
            duration: 1000,
            easing: 'in'
        },
        height: chartHeight,
        avoidOverlappingGridLines: true,
        backgroundColor: '#fff'
    };
    chart.draw(dataTable, options);

    function resizeCharts() {
        chart.draw(dataTable, options);
    }

    var debouncedResizeCharts = _.debounce(resizeCharts, 100);

    if (window.addEventListener) {
        window.addEventListener('resize', debouncedResizeCharts);
    } else if (window.attachEvent) {
        window.attachEvent('onresize', debouncedResizeCharts);
    } else {
        window.onresize = debouncedResizeCharts;
    }

    return (maxTaskEnd.getTime() - currentDate.getTime()) / 3.6e6;
}

function addDaysToDate(date, days) {
    return new Date(date.getTime() + 24 * 60 * 60 * 1000 * days);
}