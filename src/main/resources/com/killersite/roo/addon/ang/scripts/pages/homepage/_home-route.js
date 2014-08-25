'use strict';

angular.module('NewApp1')
    .config(['$routeProvider', 'appRootPath',
        function ($routeProvider, appRootPath) {
            $routeProvider
                .when('/html/home', {
                    templateUrl: appRootPath + 'scripts/pages/homepage/home-ptl.html',
                    controller: 'HomeController',
                    access: "user"
                })
        }]);
