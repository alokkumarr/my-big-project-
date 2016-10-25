angular.module('sync.components')
    .directive('panel', function(){
        return {
            restrict: 'E',
            transclude: true,
            scope: { title:'@' },
            template:
                '<md-card class="panel-card">' +
                '   <md-card-header>' +
                '       <span class="card-title">{{title}}</span>' +
                '       <span flex></span>' +
                '       <md-button class="md-icon-button collapse-tool">' +
                '          <md-icon md-font-icon="icon-expand"></md-icon>' +
                '       </md-button>' +
                '   </md-card-header>' +
                '   <md-card-content ng-hide="isCollapsed">' +
                '       <div class="header-divider"></div>' +
                '       <ng-transclude></ng-transclude>' +
                '   </md-card-content>' +
                '</md-card>',
            link: function(scope, element, attrs) {
                var btnToggleClass = 'collapsed';
                var elemBtn = element.find('md-card-header').find('button');

                scope.isCollapsed = false;

                elemBtn.on('click', function() {
                    elemBtn.toggleClass(btnToggleClass);

                    scope.isCollapsed = !scope.isCollapsed;

                });
            }
        }
    });