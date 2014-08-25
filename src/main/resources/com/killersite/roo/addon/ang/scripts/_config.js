'use strict';

angular.module('NewApp1.config', [])
    .constant('version', '0.1')
    .constant('appRootPath', '/')
    .constant('apiRootPath', 'myapp/')
    .constant('loginUrl', "/html/login")
    .constant('loggingUrl', "/api/logging")

    .config(['$logProvider', function ($logProvider) {
        // comment to turn off dev logging in the app
        $logProvider.debugEnabled && $logProvider.debugEnabled(true);
    }])

    .config(['$routeProvider', '$httpProvider', function ($routeProvider, $http) {
        $routeProvider.otherwise({
            //TODO: change this redirect to point to the desired default view for the app
            redirectTo: '/html/home'
        });
//            $locationProvider.html5Mode(true).hashPrefix("!");
        delete $http.defaults.headers.common['X-Requested-With'];
    }])
