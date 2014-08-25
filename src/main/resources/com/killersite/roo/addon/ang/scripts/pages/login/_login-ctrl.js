'use strict';

angular.module('NewApp1')
    .controller('LoginController', [
        '$scope', '$location', 'SecurityService', 'security', '$rootScope',
        function ($scope, $location, SecurityService, security, $rootScope) {

            // reload the User from the server on initial display
            security.getCurrentUser().then(function (res) {
                $rootScope.User = res.data;
            });

            // FIXME remove hardcoded and get default location from $routeProvider??
            $scope.nextLoc = $location.search().next || "/";

            $scope.sendLogin = function (u, p) {
                SecurityService.login(u, p)
                    .success(function (data) {
                        security.loginConfirmed(data);
                        $location.$$search = "";
                        $location.path($scope.nextLoc).replace();
                    }).error(function () {
//                    TODO: show error
                        $scope.loginError = "Please try again";
                    })
            };

        }
    ]);
