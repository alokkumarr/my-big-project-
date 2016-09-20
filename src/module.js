(function () {
    'use strict';

    angular.module('sync.components', [
        'material.core',
        'sync.components.accordionMenu',
        'sync.components.jsPlumb',
        'sync.components.chartkit'
    ]);

    angular.module('sync.components.jsPlumb', []);
    angular.module('sync.components.accordionMenu', []);
    angular.module('sync.components.chartkit', []);

})();
