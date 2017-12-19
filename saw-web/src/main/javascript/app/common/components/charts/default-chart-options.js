import {CHART_COLORS} from '../../consts';

export const globalChartOptions = {
  lang: {
    thousandsSep: ',',
    noData: 'No data to display'
  },
  global: {
    timezoneOffset: 0
  }
};

export const chartOptions = {
  colors: CHART_COLORS,
  plotOptions: {
    series: {
      // disable turboTreshold for biggger datasets
      turboThreshold: 0,
      barBgColor: '#f3f5f8',
      marker: {
        fillColor: null,
        lineWidth: 2,
        lineColor: null // inherit from series
      }
    }
  },
  exporting: {
    enabled: false
  },
  legend: {
    verticalAlign: 'top',
    layout: 'vertical',
    itemMarginTop: 5,
    maxHeight: 200,
    itemMarginBottom: 5,
    itemStyle: {
      lineHeight: '14px'
    }
  },
  tooltip: {
    useHTML: true,
    valueDecimals: 2,
    headerFormat: '<span style="font-size: 12px; opacity: 0.8;">{point.key}</span><br/>',
    pointFormat: '<span style="color:{point.color}; stroke: white; stroke-width: 2; ' +
    'font-size: 25px;">\u25CF</span> {series.name}: <b>{point.y}</b><br/>'
  },
  title: {
    text: ''
  },
  lang: {
    noData: 'No data to display'
  },
  noData: {
    style: {
      fontWeight: 'bold',
      fontSize: '15px',
      color: '#303030'
    }
  },
  credits: false
};
