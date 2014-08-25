(function () {
    'use strict';

    angular.module('SecurityServices', ['http-auth-interceptor-buffer'])

        .config(['$httpProvider', function ($httpProvider) {
            /**
             * $http interceptor.
             * On 401 response (without 'ignoreAuthModule' option) stores the request
             * and broadcasts 'event:angular-auth-loginRequired'.
             */
            var interceptor = ['$rootScope', '$q', 'httpBuffer', function ($rootScope, $q, httpBuffer) {
                function success(response) {
                    return response;
                }

                function error(response) {
                    if (response.status === 401 && !response.config.ignoreAuthModule) {
                        var deferred = $q.defer();
                        httpBuffer.append(response.config, deferred);
                        $rootScope.$broadcast('event:auth-loginRequired');
                        return deferred.promise;
                    }
                    // otherwise, default behaviour
                    return $q.reject(response);
                }

                return function (promise) {
                    return promise.then(success, error);
                };

            }];
            $httpProvider.responseInterceptors.push(interceptor);
        }])

        .provider('security', ["$httpProvider", function ($httpProvider) {
            var userAccessLevels = {};
            // pass in an object listing Roles and their sub-roles
            this.setAccessRoles = function (x) {
                userAccessLevels = x;
            };
            // get a copy of the app Role description
            this.getAccessRole = function (name) {
                return _.clone(userAccessLevels[name])
            };
            this.$get = ['$rootScope', '$http', "SecurityService", 'httpBuffer', '$log', function ($rootScope, $http, SecurityService, httpBuffer, $log) {

                return {
                    getCurrentUser: function () {
                        return SecurityService.getUserObj();
//                        return $rootScope.User;
                    },
                    // get the app Role description
                    getAppRole: function (name) {
                        return _.clone(userAccessLevels[name])
                    },
                    // return whether a User's given Role can see a level
                    isAuthorized: function (level, User) {
                        var userRoles = (User || {}).roles
//                        $log.log("Level: " + level + ": " + userRoles);
                        return _(userAccessLevels[level]).any(function (r) {
                            return _(_.union(userRoles, "*")).contains(r);
                        })
                    },
                    // clear out Auth locally and on the server
                    logout: function () {
                        return SecurityService.logout();
                    },
                    // initiate the User login process
                    login: function (name, password) {
                        SecurityService.login(u, p).success(function (data) {
                            // put User Object into $rootScope
                            this.loginConfirmed(data);
                        });
                    },
                    isLoggedIn: function () {
                        // do we have a User object on the rootScope?
                        return angular.isObject($rootScope.User);
                    },
                    /**
                     * call this function to indicate that authentication was successful and trigger a
                     * retry of all deferred requests.
                     * @param data an optional argument to pass on to $broadcast which may be useful for
                     * example if you need to pass through details of the user that was logged in
                     */
                    loginConfirmed: function (data) {
                        $log.info("security.loginConfirmed");
                        $rootScope.$broadcast('event:auth-loginConfirmed', data);
                        httpBuffer.retryAll();
                    }
                }
            }]
        }])

        // run after module initialization
        .run(['$rootScope', '$location', '$log', 'security', 'appRootPath', 'loginUrl', function ($rootScope, $location, $log, security, appRootPath, loginUrl) {

            // after a logon save the User object
            $rootScope.$on('event:auth-loginConfirmed', function (data, args) {
                $rootScope.User = args;
            });

            // this is for login handled by Angular instead of Spring Security
            $rootScope.$on('$routeChangeStart', function (e, next, current) {
                $log.log('Going to: ' + $location.path());
                var access = (next.$$route || {}).access;
                if (angular.isString(access)) {
                    security.getCurrentUser().then(
                        function (res) {
                            var user = $rootScope.User = res.data
                            if (!security.isAuthorized(access, user)) {
                                redirectToLoginPage();
                                $log.log("Not authorized");
                            }
                        },
                        function(err) {
//                            err.config, err.data, err.status
                        })
                }
            });

            var redirectToLoginPage = function () {
                $location.search('next', $location.path()).path(loginUrl)
            }

            $rootScope.$on("$routeChangeError", function (evt, curr, prev, rejection) {
                // check if the reason is "unauthorized"
                if (rejection.status && rejection.status === 401) {
                    redirectToLoginPage();
                }
                $log.log("$routeChangeError");
            });

        } ]);

    /**
     * Private module, an utility, required internally by 'http-auth-interceptor'.
     */
    angular.module('http-auth-interceptor-buffer', [])

        .factory('httpBuffer', ['$injector', function ($injector) {
            /** Holds all the requests, so they can be re-requested in future. */
            var buffer = [];

            /** Service initialized later because of circular dependency problem. */
            var $http;

            function retryHttpRequest(config, deferred) {
                function successCallback(response) {
                    deferred.resolve(response);
                }

                function errorCallback(response) {
                    deferred.reject(response);
                }

                $http = $http || $injector.get('$http');
                $http(config).then(successCallback, errorCallback);
            }

            return {
                /**
                 * Appends HTTP request configuration object with deferred response attached to buffer.
                 */
                append: function (config, deferred) {
                    buffer.push({
                        config: config,
                        deferred: deferred
                    });
                },

                /**
                 * Retries all the buffered requests clears the buffer.
                 */
                retryAll: function () {
                    for (var i = 0; i < buffer.length; ++i) {
                        retryHttpRequest(buffer[i].config, buffer[i].deferred);
                    }
                    buffer = [];
                }
            };
        }]);

}())