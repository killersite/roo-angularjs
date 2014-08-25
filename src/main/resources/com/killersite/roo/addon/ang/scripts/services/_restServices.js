'use strict';

angular.module('NewApp1.restServices', ['ngResource'])
    .factory("SecurityService", ['$http', 'apiRootPath', '$cacheFactory', function ($http, apiRootPath, $cacheFactory) {
        return {
            login: function (name, password) {
                var http = $http.post(apiRootPath + 'api/login', {username: name, password: password});
                http.then(function (data) {
                    var $httpDefaultCache = $cacheFactory.get('$http');
                    // invalidate so we get a new User from getUserObj()
                    $httpDefaultCache.removeAll();
                });
                return http;
            }, getUserObj: function () {
                return $http.get(apiRootPath + 'api/login', {cache: true});
            }, logout: function () {
                return $http.get(apiRootPath + 'api/logout');
            }
        }
    }])

    .factory("RemoteLogger", ['$http', 'apiRootPath', 'loggingUrl', function ($http, apiRootPath, loggingUrl) {
        var url = apiRootPath + loggingUrl;
        return {
            logUsage: function (featureName) {
                //this method is used to perform usage logging
                return $http.get(url, { params: {m: featureName, l: 'INFO', c: 'USAGE'} });
            },
            log: function (message, code, level) {
                //this method is used to perform general server-side logging
                return $http.get(url, { params: {m: featureName, l: level, c: code} });
            }
        }
    }])