angular.module('sync.components', [])
.directive('badge', function(){
    return {
        scope: { label: '@'},
        link: function (scope, elem, attrs) {
            attrs.$observe('label', function(newVal) {
                if (newVal > 0) {
                    elem.attr('data-badge-count', newVal);
                } else {
                    elem.removeAttr('data-badge-count');
                }
            });
        }
    }
});