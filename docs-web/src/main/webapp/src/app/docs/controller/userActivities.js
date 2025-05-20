angular.module('docs')
.controller('userActivities', function($scope, $http) {
    $scope.activities = [];
    $scope.error = null;
    $scope.loading = true;

    $scope.loadActivities = function() {
        // 调用后台接口，替换为实际路径和参数
        $http.get('/admin/user-activities-gantt', {
            params: {
                userId: 'someUserId',  // 可以根据需求动态传入
                start: '2025-01-01',
                end: '2025-12-31'
            }
        }).then(function(response) {
            $scope.activities = response.data;
            $scope.loading = false;
            renderGantt($scope.activities);
        }, function(error) {
            $scope.error = '加载用户活动数据失败';
            $scope.loading = false;
        });
    };

    // 页面初始化加载数据
    $scope.loadActivities();

    function renderGantt(activities) {
        // 把后端数据转换为Frappe Gantt格式
        // Frappe Gantt示例任务格式：
        // { id: 'Task 1', name: 'Redesign website', start: '2016-12-28', end: '2017-01-03', progress: 20 }
        
        // 这里简单按 taskId 分组取时间范围（示例）
        var tasksMap = {};
        activities.forEach(function(act) {
            var task = tasksMap[act.taskId];
            var dateStr = new Date(act.timestamp).toISOString().substr(0, 10);
            if (!task) {
                tasksMap[act.taskId] = {
                    id: act.taskId,
                    name: act.taskName || act.taskId,
                    start: dateStr,
                    end: dateStr,
                    progress: 0
                };
            } else {
                if (dateStr < task.start) task.start = dateStr;
                if (dateStr > task.end) task.end = dateStr;
            }
        });

        var tasks = Object.values(tasksMap);

        // 进度计算可以根据操作类型做细化，这里先简化为固定值
        tasks.forEach(function(task) {
            task.progress = 50; // 示例固定进度
        });

        // 创建甘特图
        var gantt = new Gantt("#gantt", tasks, {
            on_click: function(task) {
                alert("点击了任务: " + task.name);
                // 可以增加跳转详情逻辑
            }
        });
    }
});
