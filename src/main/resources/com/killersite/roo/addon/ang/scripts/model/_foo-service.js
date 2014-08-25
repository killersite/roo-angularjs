'use strict';

angular.module('NewApp1')
    .factory('Foo', ['ActiveResource', function (ActiveResource) {

        function Foo(data) {
            if (!data) data = {};
            this.number('id');
            this.string('sampleTextAttribute');
            this.string('sampleDateAttribute');

            this.computedProperty('sampleComputedAttribute', function() {
                return this.sampleTextAttribute +":"+ this.sampleDateAttribute;
            }, ['sampleTextAttribute', 'sampleDateAttribute']);

            this.validates({
                sampleTextAttribute: { presence: true, length: { min: 5, max: 20 } }
            });

        };

        Foo.inherits(ActiveResource.Base);
        Foo.api.set('/myapp/api');

        return Foo;
    }]);