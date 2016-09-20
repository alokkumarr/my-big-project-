angular.module('sync.components')
    .directive('rangeSlider', function () {

    function RangeSliderController($scope) {
        if(!$scope.step){
            $scope.step = 1;
        }
        $scope.$watchGroup(['min','max'],minMaxWatcher);
        $scope.$watch('lowerValue',lowerValueWatcher);

        function minMaxWatcher() {
            $scope.lowerMax = $scope.max - $scope.step;
            $scope.upperMin = $scope.lowerValue + $scope.step;

            if(!$scope.lowerValue || $scope.lowerValue < $scope.min){
                $scope.lowerValue = $scope.min;
            }else{
                $scope.lowerValue*=1;
            }
            if(!$scope.upperValue || $scope.upperValue > $scope.max){
                $scope.upperValue = $scope.max;
            }else{
                $scope.upperValue*=1;
            }
            updateWidth();
        }

        function lowerValueWatcher() {
            if($scope.lowerValue >= $scope.upperValue - $scope.step){
                $scope.lowerValue = $scope.upperValue - $scope.step;
                return;
            }
            $scope.upperMin = $scope.lowerValue + $scope.step;

            updateWidth();
        }

        function updateWidth() {
            $scope.upperWidth = ((($scope.max-($scope.lowerValue + $scope.step))/($scope.max-$scope.min)) * 100) + "%";
            if($scope.lowerValue > ($scope.upperValue - $scope.minGap) && $scope.upperValue < $scope.max) {
                $scope.upperValue = $scope.lowerValue + $scope.minGap;
            }
        }
    }

    return {
        scope      : {
            max:'=',
            min:'=',
            minGap: '=',
            step:'=',
            lowerValue: "=lowerValue",
            upperValue: "=upperValue"
        },
        template: [
            '<div class="range-slider-container">',
            '       <div class="range-label-left">{{min}}</div>',
            '   <div class="range-slider-left">',
            '       <md-slider md-discrete aria-label="upperValue" step="{{step}}" ng-model="lowerValue" min="{{min}}" max="{{lowerMax}}"></md-slider>',
            '   </div>',
            '   <div class="range-slider-right" ng-style="{width: upperWidth}">',
                    '<md-slider md-discrete aria-label="upperValue" step="{{step}}" ng-model="upperValue" min="{{upperMin}}" max="{{max}}"></md-slider>',
            '   </div>',
            '       <div class="range-label-right">{{max}}</div>',
            '</div>'
        ].join(''),
        controller: ['$scope', RangeSliderController]
    };
});
