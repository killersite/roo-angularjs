(function () {
    'use strict';

    angular.module('Validation', [])
        .directive('serverFormValidation', ['formValidationConn', '$timeout', function (conn, $timeout) {
            // keep a reference to the form
            var formController;
            // make sure we don't call validation too much
            var reqTimerId;
            var checkFieldValidation = function(entityName, formName, fieldName, fieldValue, success, failure) {
                $timeout.cancel(reqTimerId);
                reqTimerId = $timeout(function() {
                    conn.validate(entityName, formName, fieldName, fieldValue)
                        .then(setFieldValidity);
                }, 100);
            };

            // success function
            // (data, status, headers, config)
            var setFieldValidity = function(result) {
                // fieldName, validity, message
                var data = result.data;
//                formController.$setValidity(data.fieldName, data.validity, data.message );
                formController[data.fieldName].$setValidity('serverError', data.validity);
                if(!data.valiity) {
                    formController[data.fieldName].$error.serverErrorMessage = data.message;
                } else {
                    formController[data.fieldName].$error.serverErrorMessage = "";
                }
                // TODO Where to put validation message? on the field?
                $('*[name=' + result.config.data.field + ']').data('serverError', data.message);
            };

            return {
                require: "^form",
                link: link,
                controller: function($scope) {
                    $scope.showServerError = function($element, $attrs) {
                        console.dir($element);
                        console.dir($attrs);
                        return "test";
                    }
                }
            }

            // TODO initialize the form to the 'required' fields
            function link(scope, formElm, attrs, formCtrl) {
                // save a reference to the formController
                formController = formCtrl;
                // config object
                var config = scope.$eval( attrs.serverFormValidation );
                // form name
                var formName = attrs.name;
                // add a submit handler to the form
                formElm.bind('submit', function () {

                });

                // find input fields and attach a change handler
                var fields = angular.element(formElm).find('input,select');
                for (var i = 0; i < fields.length; i++) {
                    var field = fields[i];
//                    console.log(field.outerHTML);

                    var fieldObj = angular.element(field);
                    if (field.type=='text') {
                        fieldObj.bind('keyup', config, function(eventObject) {
                            eventObject.data; // the config object passed in
                            var fieldName = this.name; // name of field
                            var fieldValue = this.value; // current value of the field
                            this.type; // current type of the field
                            // TODO if the value is empty clear out the serverMessage? or server should respond with empty message
                            checkFieldValidation(eventObject.data.backingEntity, formName, fieldName, fieldValue)
                        })
                    }

                    // add an onchange
                    fieldObj.bind('change', config, function (eventObject) {
                        checkFieldValidation(eventObject.data.backingEntity, this.name, this.value)
                    })
                }

            }
        }])
    ;

}).call(this)