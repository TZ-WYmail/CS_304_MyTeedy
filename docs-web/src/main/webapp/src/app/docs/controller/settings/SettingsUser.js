'use strict';




/**
 * Settings user page controller.
 */
angular.module('docs').controller('SettingsUser', function($scope, $state, Restangular) {

  class Gantt {
    constructor(container, options) {
      this.container = container;
      this.options = options || {};
      this.tasks = options.data || [];
      this.viewMode = options.view_mode || 'Day';
      this.onClick = options.on_click || function (task) {
        console.log('Task clicked:');
        console.log('Name:', task.name);
        console.log('Start:', task.start);
        console.log('End:', task.end);
      };
      this.render();
    }

    render() {
      // Clear the container
      this.container.innerHTML = '<h3 style="margin-bottom: 20px; color: #333;">Gantt Chart</h3>';

      // Create the chart container
      const chartContainer = document.createElement('div');
      chartContainer.style.width = '1000px';
      chartContainer.style.height = '700px'; // Increase the height of the container
      chartContainer.style.overflowX = 'auto';
      chartContainer.style.overflowY = 'auto';
      chartContainer.style.position = 'relative';
      this.container.appendChild(chartContainer);

      // Find the overall start and end dates
      let overallStart = new Date(this.tasks[0].start);
      let overallEnd = new Date(this.tasks[0].end);
      this.tasks.forEach(task => {
        const start = new Date(task.start);
        const end = new Date(task.end);
        if (start < overallStart) overallStart = start;
        if (end > overallEnd) overallEnd = end;
      });

      // Calculate the total number of days
      const totalDays = (overallEnd - overallStart) / (1000 * 60 * 60 * 24) + 1;

      // Create the header row
      const headerRow = document.createElement('div');
      headerRow.width=chartContainer.style.width;
      headerRow.style.display = 'flex';
      headerRow.style.alignItems = 'center';
      headerRow.style.justifyContent = 'start';
      headerRow.style.borderBottom = '1px solid #ccc';
      headerRow.style.padding = '10px 0';
      headerRow.style.backgroundColor = '#f9f9f9';

      const taskHeader = document.createElement('div');
      taskHeader.style.width = '100px';
      taskHeader.style.height = '20px';
      taskHeader.style.textAlign = 'center';
      taskHeader.style.padding = '5px';
      taskHeader.style.borderRight = '1px solid #ccc';
      taskHeader.textContent = 'Task';
      headerRow.appendChild(taskHeader);

      for (let i = 0; i < totalDays; i++) {
        const dateHeader = document.createElement('div');
        dateHeader.style.width = '200px'; // Increase the width of each date cell
        dateHeader.style.textAlign = 'center';
        dateHeader.style.padding = '5px';
        dateHeader.style.borderRight = '1px solid #ccc';
        dateHeader.textContent = new Date(overallStart.getTime() + i * 24 * 60 * 60 * 1000).getDate();
        headerRow.appendChild(dateHeader);
      }

      chartContainer.appendChild(headerRow);

      // Create the task rows
      this.tasks.forEach((task, index) => {
        const taskRow = document.createElement('div');
        taskRow.style.display = 'flex';
        taskRow.style.alignItems = 'center';
        taskRow.style.justifyContent = 'start';
        taskRow.style.borderBottom = '1px solid #ccc';
        taskRow.style.padding = '10px 0';
        taskRow.style.cursor = 'pointer';
        taskRow.style.backgroundColor = index % 2 === 0 ? '#fff' : '#f9f9f9';
        taskRow.addEventListener('click', () => this.onClick(task));

        const taskCell = document.createElement('div');
        taskCell.style.width = '150px';
        taskCell.style.height = '40px'
        taskCell.style.textAlign = 'center';
        taskCell.style.padding = '5px';
        taskCell.style.borderRight = '1px solid #ccc';
        taskCell.textContent = task.name;
        taskRow.appendChild(taskCell);

        const taskStart = new Date(task.start);
        const taskEnd = new Date(task.end);
        const taskDuration = (taskEnd - taskStart) / (1000 * 60 * 60 * 24) + 1;

        for (let i = 0; i < totalDays; i++) {
          const dateCell = document.createElement('div');
          dateCell.style.width = '200px';// Increase the width of each date cell
          dateCell.style.height = '10px'; // Increase the height of each date cell
          dateCell.style.borderRight = '1px solid #ccc';
          dateCell.style.borderTop = '1px solid #ccc';
          dateCell.style.borderBottom = '1px solid #ccc';
          dateCell.style.display = 'flex';
          dateCell.style.alignItems = 'center';
          dateCell.style.justifyContent = 'center';
          dateCell.style.backgroundColor = 'white';

          const currentDate = new Date(overallStart.getTime() + i * 24 * 60 * 60 * 1000);
          if (currentDate >= taskStart && currentDate <= taskEnd) {
            dateCell.style.backgroundColor = `hsl(${(index * 40) % 360}, 70%, 50%)`; // Different colors for different tasks
          }

          taskRow.appendChild(dateCell);
        }

        chartContainer.appendChild(taskRow);
      });

      // Add custom scrollbar styles
      chartContainer.style.scrollbarWidth = 'thin';
      chartContainer.style.scrollbarColor = '#888 #f1f1f1';

      // Add hover effect
      const taskRows = chartContainer.querySelectorAll('div');
    }
  }

  /**
   * Load users from server.
   */
  $scope.loadUsers = function() {
    Restangular.one('user/list').get({
      sort_column: 1,
      asc: true
    }).then(function(data) {
      $scope.users = data.users;
    });
  };
  
  $scope.loadUsers();
  
  /**
   * Edit a user.
   */
  $scope.editUser = function(user) {
    $state.go('settings.user.edit', { username: user.username });
  };

  $scope.registrationRequests = [];

  Restangular.one('registration').one('admin').getList('list').then(function (list) {
    $scope.registrationRequests = list;
    });

  $scope.handleRequest = function (id, action) {
    Restangular.one('registration').one('admin').post(action + '/' + id).then(function () {
      $scope.registrationRequests = $scope.registrationRequests.filter(function (r) {
        return r.id !== id;
      });
    });
  };

  $scope.Approve = function (id) {
    $scope.handleRequest(id, 'accept');
  };

  $scope.Reject = function (id) {
    $scope.handleRequest(id, 'reject');
  };

  $scope.ganttLoading = true;
  $scope.ganttError = null;
  $scope.ganttUserId = '';
  $scope.ganttStart = null;
  $scope.ganttEnd = null;

// Format date to yyyy-MM-dd
  function formatDate(dateObj) {
    const d = new Date(dateObj);
    return d.toISOString().slice(0, 10);
  }

// Render Gantt chart logic
  function renderGantt(data) {
    const taskMap = {};
    console.log("Data received:"+data);
    data.forEach(function (log) {
      const day = new Date(log.timestamp).toISOString().slice(0, 10);
      console.log(day);
      if (!taskMap[log.taskId]) {
        taskMap[log.taskId] = {
          id: log.taskId,
          name: (log.taskName || log.taskId) + ' (' + log.username + ')', // Display username
          start: day,
          end: day,
          progress: 0,
        };
      } else {
        // Update start and end dates
        console.log("start: "+taskMap[log.taskId].start);
        console.log("end: "+taskMap[log.taskId].end);
        if (day < taskMap[log.taskId].start) taskMap[log.taskId].start = day;
        if (day > taskMap[log.taskId].end) taskMap[log.taskId].end = day;
      }
    });

    const tasks = Object.values(taskMap);
    console.log("tasks: "+tasks);

    // Clear the Gantt chart container to prevent width collapse
    document.querySelector("#gantt").innerHTML = "<h3 style=\"margin-bottom: 20px; color: #333;\">Gantt Chart</h3>";

    // Render the Gantt chart

    // new Gantt("#gantt", tasks, {
    //   view_mode: "Day",
    //   on_click: function (task) {
    //     alert("Clicked task: " + task.name);
    //   },
    // });
    new Gantt(document.getElementById('gantt'), {
      data: tasks,
      on_click: function (task) {
        alert("Clicked task: " + task.name);
      },});
  }

// Load Gantt chart data
  $scope.loadGantt = function () {
    $scope.ganttLoading = true;
    $scope.ganttError = null;

    const params = {};
    if ($scope.ganttUserId) params.userId = $scope.ganttUserId;
    if ($scope.ganttStart) params.start = formatDate($scope.ganttStart);
    if ($scope.ganttEnd) params.end = formatDate($scope.ganttEnd);

    // Use Restangular to request data
    console.log("loading_gantt")
    Restangular.one('admin-dashboard').getList('user-activities-gantt', params)
        .then(function (data) {
          $scope.ganttLoading = false;
          renderGantt(data);
        }, function () {
          $scope.ganttLoading = false;
          $scope.ganttError = 'Failed to load data. Please check your permissions or network connection.';
        });
  };
});

