export function lineChart() {
  return {
    chart: {
      type: 'line'
    }
  };
}

export function areaChart() {
  return {
    chart: {
      type: 'area'
    }
  };
}

export function barChart() {
  return {
    chart: {
      type: 'bar'
    },
    xAxis: {
      endOnTick: false,
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
}

export function snapshotBarChart() {
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
  };
}
