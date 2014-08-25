'use strict';

angular.module('NewApp1')
    .config(['$routeProvider', 'appRootPath',
        function ($routeProvider, appRootPath) {
            $routeProvider
                .when('/html/foo', {
                    templateUrl: appRootPath + 'scripts/pages/foo/foo-ptl.html',
                    controller: 'FooController as ctrl',
                    access: "user",
                    resolve: {
                        resolvedFoos: ['Foo', function (Foo) {
                            // TODO remove this sample data
                            return [
                                Foo.new({id: 1, sampleTextAttribute:"test1", sampleDateAttribute: "01/30/1990"}),
                                Foo.new({id: 2, sampleTextAttribute:"test2", sampleDateAttribute: "01/30/2000"}),
                                Foo.new({id: 3, sampleTextAttribute:"test3", sampleDateAttribute: "01/30/2010"})
                            ]
                            // this will get all Foos
                            //return Foo.all();
                        }]
                    }
                })
        }]);