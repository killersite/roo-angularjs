'use strict';

angular.module('NewApp1')
    .controller('FooController', ['Foo', '$modal', '$log', 'resolvedFoos',
        function (Foo, $modal, $log, resolvedFoos) {
            var ctrl = this;

            this.foos = resolvedFoos;

            this.delete = function (foo) {
                foo.$delete()
            }

            this.openModal = function (fooItem) {
                $modal.open({
                    templateUrl: 'editFooEntity.html',
                    controller: ModalInstanceCtrl,
                    resolve: {
                        foo: function () {
                            return fooItem || Foo.new();
                        }
                    }
                });
            };

            var ModalInstanceCtrl = function ($scope, $modalInstance, foo) {
                $scope.item = foo;
                $scope.dpopened = false;

                $scope.openDatepicker = function ($event) {
                    $event.preventDefault();
                    $event.stopPropagation();

                    $scope.dpopened = true;
                };

                $scope.dateOptions = {
                    'year-format': "'yy'",
                    'starting-day': 1
                };

                $scope.save = function () {
                    $scope.item.$save().then(function(fooObj){
                        ctrl.foos.push(fooObj);
                        $modalInstance.close(fooObj);
                    }, function(fooObj){
                        // save error
                    console.log(fooObj.$errors)
                    })
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            };
        }

    ]);
