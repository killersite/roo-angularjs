'use strict';

angular.module('NewApp1')
    .directive('stopClick', function () {
        return function (scope, element, attrs) {
            $(element).click(function (event) {
                event.preventDefault();
            });
        }
    })
