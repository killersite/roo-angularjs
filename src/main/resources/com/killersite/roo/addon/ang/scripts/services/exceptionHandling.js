(function () {
    'use strict';

    angular.module('ExceptionHandling', ['config'])
        .config(['$provide', function ($provide) {
            $provide.decorator("$exceptionHandler", ['$delegate', '$log', '$window', 'loggingUrl', function ($delegate, $log, $window, loggingUrl) {
                return function (exception, cause) {
                    $delegate(exception, cause);
                    // Now send this to the server
                    try {
                        $.ajax({
                            type: "POST",
                            url: loggingUrl,
                            contentType: "application/json",
                            data: angular.toJson({
                                errorUrl: $window.location.href,
                                message: exception.message,
                                code: exception
                            })
                        }).done(function (data) {
                                if (console && console.log) {
                                    console.log("Error sent:", data.slice(0, 100));
                                }
                            });
                    } catch (e) {
                        $log.warn("Error sending error to server");
                        $log.log(e);
                    }

                };
            }]);
        }])

})();