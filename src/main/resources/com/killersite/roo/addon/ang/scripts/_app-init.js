'use strict';

// name of the application is "myApp" which is referenced in the
// ng-app attribute.
angular.module("NewApp1", [
        'ngRoute',
        'NewApp1.config',
        'NewApp1.restServices',
        'NotificationService',
        'SecurityServices',
        'DateFormatting',
        'ui.bootstrap',
        'ngGrid',
        'ActiveResource'
    ])

    // setup security for this application
    .config(['securityProvider', function (securityProvider) {
        var appRoles = {
            'anon': ["*"],
            'user': ["ROLE_IT"]
            //TODO: define the remainder of your user access roles here
        };
        securityProvider.setAccessRoles(appRoles);
    }])

    // performance: in controllers do $scope.$onRootScope instead of $rootScope.on
    .config(['$provide', function ($provide) {
        $provide.decorator('$rootScope', ['$delegate', function ($delegate) {
            Object.defineProperty($delegate.constructor.prototype, '$onRootScope', {
                value: function (name, listener) {
                    var unsubscribe = $delegate.$on(name, listener);
                    this.$on('$destroy', unsubscribe);
                },
                enumerable: false
            });
            return $delegate;
        }]);
    }])

    // run after module initialization
    .run([ '$rootScope', '$location', '$log', function ($rootScope, $location, $log) {
        // make this available in child $scopes
        $rootScope.$log = $log;

        // global function to set the 'active' class on navigation
        $rootScope.getClass = function (path) {
            if ($location.path().substr(0, path.length) == path) {
                return "active";
            } else {
                return "";
            }
        };

    } ]);
