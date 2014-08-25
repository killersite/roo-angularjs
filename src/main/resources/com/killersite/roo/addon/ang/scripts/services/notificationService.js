'use strict';

angular.module('NotificationService', [])
    .factory('NotificationService', function ($rootScope) {

        window.toastr.options = {
            "positionClass": "toast-bottom-right"
        };

        // TODO make this into a provider with tostr options and other notification options
        // msg(Msg.LEVEL, title, body);
        // also make this accept an implementation for what to do with the messages

//            <div>
//                <alert ng-repeat="alert in alerts" type="alert.type" close="closeAlert($index)">{{ alert.msg }}</alert>
//            </div>
//
        // create an array of alerts available globally
        $rootScope.alerts = [];

        return {
            add: function (type, msg) {
                $rootScope.alerts.push({'type': type, 'msg': msg});
            },

            closeAlert: function (index) {
                $rootScope.alerts.splice(index, 1)
            },

            flash: function (message) {
                switch (message.type) {
                    case 'success':
                        toastr.success(message.body, message.title);
                        break;
                    case 'info':
                        toastr.info(message.body, message.title);
                        break;
                    case 'warning':
                        toastr.warning(message.body, message.title);
                        break;
                    case 'error':
                        toastr.error(message.body, message.title);
                        break;
                }
            }

        }

    });
