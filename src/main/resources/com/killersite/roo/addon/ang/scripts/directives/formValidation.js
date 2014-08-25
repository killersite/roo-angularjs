'use strict';

angular.module('NewApp1')
    .directive('serverValidate', function () {
        return {
//            require: 'form',
            link: function (scope, element, attr/*, form*/) {
                var form = element.inheritedData('$formController');
                if (!form) return;

                // model name of validation object
                var validate = attr.serverValidate;

                scope.$watch(validate, function (data) {
                    if (!data) return; // initialization of watch

                    // note that this is form level and NOT field level validation
                    form.$serverErrors = { };

                    if (angular.isDefined(data.type) && data.type === "fieldvalidation") {
                        var errors = data.errors

                        // if errors is undefined or null just set invalid to false and return
                        if (!errors) {
                            form.$serverInvalid = false;
                            return;
                        }
                        // set $serverInvalid to true|false
                        form.$serverInvalid = (!_(errors).isEmpty());

                        // loop through errors
                        for (var key in errors) {
                            form.$serverErrors[key] = { $invalid: true, message: errors[key]};
                        }

                    }
                });
            }
        };
    });
