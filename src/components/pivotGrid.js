angular.module('sync.components')
    .directive('pivotGrid', function(){
        return {
            link: function (scope, elem, attrs) {
                var grid = scope.ctrl.gridOptions;

                $(elem).pivot(
                    grid.input,
                    grid.options
                );
            }
        }
    });
