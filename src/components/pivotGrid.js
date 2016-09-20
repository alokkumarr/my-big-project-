angular.module('sync.components')
    .directive('pivotGrid', function(){
        return {
            scope: { options: '@'},
            link: function (scope, elem, attrs) {
                debugger;
                $(elem).pivot(
                    [
                        {color: "blue", shape: "circle"},
                        {color: "red", shape: "triangle"}
                    ],
                    {
                        rows: ["color"],
                        cols: ["shape"]
                    }
                );
            }
        }
    });
