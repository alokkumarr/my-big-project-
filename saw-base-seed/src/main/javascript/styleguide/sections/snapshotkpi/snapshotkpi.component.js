import template from './snapshotkpi.component.html';

export const SnapshotKpiComponent = {
  template,
  controller: class SnapshotKpiController {
    constructor() {
      this.chartOptions = {
        dynamic: {
          series: [{
            name: 'Jane',
            data: [2, 2, 3, 7, 1]
          }]
        },
        static: {
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
            endOnTick: false,
            gridLineWidth: 0,
            minorGridLineWidth: 0,
            minorTickLength: 0,
            tickLength: 0,
            lineWidth: 0
          },
          plotOptions: {
            series: {
              pointPadding: 0.1,
              pointWidth: 20,
              groupPadding: 0,
              borderRadius: 4,
              enableFullBackgroundColor: true
            }
          }
        }
      };
    }
  }
};
