'use strict';

var app = angular.module('NewApp1');

app.directive('loginModal', ['appRootPath', function (appRootPath) {
    return {
        restrict: 'E',
        templateUrl: appRootPath + "scripts/directives/loginModal-ptl.html",
        scope: true,
        replace: true,
        controller: [ '$scope', 'SecurityService', 'security', '$log', '$modal', function ($scope, SecurityService, security, $log, $modal) {
            $scope.showLogin = false;
            $scope.isDisplayError = false;
            $scope.opts = {
                backdropFade: true,
                dialogFade: true,
                backdropClick: false,
                keyboard: false
            };
            var modalInstance;
            $scope.$on('event:auth-loginRequired', function () {
                $log.info("loginModal.event:auth-loginRequired");
                if (!$scope.showLogin) {
                    $scope.showLogin = true;
                    modalInstance = $modal.open({
                        templateUrl: 'loginModal.html',
                        backdrop: 'static',
                        dialogFade: true,
                        keyboard: false,
                        scope: $scope,

                        controller: ['$scope', 'security', function ($scope, security) {
                            $scope.sendLogin = function (u, p) {
                                SecurityService.login(u, p)
                                    .then(
                                    function (data) {
                                        security.loginConfirmed(data);
                                        $scope.isDisplayError = false;
                                    }, function () {
                                        $scope.isDisplayError = true;
                                        $scope.loginError = "USERNAME OR PASSWORD NOT FOUND. PLEASE TRY AGAIN.";
//                    show error
                                    }
                                )
                            };

                        }]
                    });
                }
            });
            $scope.$on('event:auth-loginConfirmed', function (data, args) {
                $log.info("loginModal.event:auth-loginConfirmed");
                $scope.showLogin = false;
                if (modalInstance) modalInstance.dismiss('cancel');
            });
        } ]
    }
} ]);
