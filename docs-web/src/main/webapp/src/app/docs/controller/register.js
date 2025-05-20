'use strict';

angular.module('docs').controller('register', function ($scope, Restangular) {
    $scope.form = {};
    $scope.confirm_password = null;

    $scope.register = function () {
        if ($scope.form.password !== $scope.confirm_password) {
            $scope.status = "Passwords do not match.";
            return;
        }

        Restangular.all('registration').post($scope.form).then(function () {
            $scope.status = "Registration request submitted, awaiting admin approval.";
            $scope.form = {};
            $scope.confirm_password = null;
        }, function () {
            $scope.status = "Submission failed, please try again later.";
        });
    };
});