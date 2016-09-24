angular.module('sync.components.charts')
    .chart('line', function() {
        return {
            // we have access to some directive properties
            scope: {
                data: '<'
            },
            // we get the chart object as a controller into our link fn
            link: function (scope, element, attrs, chart) {
                chart.showLoading();
                scope.$watch('data', function (data) {
                    if (data) {
                        chart.hideLoading();
                        angular.forEach(data, function (value, key) {
                            chart.addSeries({
                                name: key,
                                data: value
                            });
                        });
                    }
                });
            },
            chart: {
                type: 'line'
            },
            xAxis: {
                categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
            }
        }
    })
    .chart('bar', function() {
        return {
            chart: {
                type: 'bar'
            },
            series: [{
                name: 'John',
                data: [5, 3, 4, 7, 2]
            }, {
                name: 'Jane',
                data: [2, 2, 3, 2, 1]
            }, {
                name: 'Joe',
                data: [3, 4, 4, 5]
            }],
            xAxis: {
                categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas'],
                endOnTick:false,
                minorGridLineWidth: 0,
                minorTickLength: 0,
                tickLength: 0,
                lineWidth: 0
            },
            yAxis: {
                gridLineWidth: 0,
                labels: {
                    overflow: 'justify'
                }
            }
        };
    })
    .chart('snapshotBar', function() {
        return {
            tooltip: {
                enabled: false
            },
            chart: {
                type: 'column',
                spacingBottom: 2,
                spacingTop: 2,
                spacingLeft: 2,
                spacingRight: 2,
                width: 150,
                height: 40
            },
            legend: {
                enabled: false
            },
            xAxis: {
                title: null,
                labels: {
                    enabled: false
                },
                minorTickLength: 0,
                tickLength: 0,
                lineWidth: 0,
                minorGridLineWidth: 0,
                lineColor: 'transparent'
            },
            yAxis: {
                title: null,
                labels: {
                    enabled: false
                },
                endOnTick:false,
                gridLineWidth: 0,
                minorGridLineWidth: 0,
                minorTickLength: 0,
                tickLength: 0,
                lineWidth: 0
            },
            plotOptions: {
                series: {
                    borderRadius: 4,
                    enableFullBackgroundColor: true
                }
            },
            series: [{
                name: 'Jane',
                data: [2, 2, 3, 7, 1]
            }]
        };
    });
