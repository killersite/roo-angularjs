'use strict';

angular.module('NewApp1')
    .config(['$routeProvider', 'appRootPath', 'loginUrl',
        function ($routeProvider, appRootPath, loginUrl) {
            $routeProvider.when(loginUrl, {
                access: "anon",
                templateUrl: appRootPath + 'scripts/pages/login/login-ptl.html',
                controller: 'LoginController'
            });
        } ])
