angular.module('sync.components')
    .directive('panel', function(){
        return {
            restrict: 'E',
            transclude: true,
            scope: { title:'@' },
            template:
                '<md-card>' +
                '   <md-card-header>' +
                '       <span class="card-title">{{title}}</span>' +
                '       <md-button class="md-icon-button">' +
                '          <md-icon md-font-icon="icon-expand"></md-icon>' +
                '       </md-button>' +
                '   </md-card-header>' +
                '   <md-card-content>' +
                '       <ng-transclude></ng-transclude>' +
                '   </md-card-content>' +
                '</md-card>',
            link: function(scope, element, attrs) {
                // element.parent().bind('click', function() {
                //     element.toggleClass(attrs.toggleClass);
                // });
            }
        }
    });

