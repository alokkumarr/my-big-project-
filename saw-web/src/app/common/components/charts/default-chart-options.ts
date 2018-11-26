import * as defaultsDeep from 'lodash/defaultsDeep';
import {CHART_COLORS} from '../../consts';

export const globalChartOptions = {
  lang: {
    thousandsSep: ',',
    noData: 'No data to display'
  },
  global: {
    timezoneOffset: 0
  },
  chart: {
    style: {
      fontFamily: 'Source Sans Pro'
    }
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
    headerFormat:
      '<span style="font-size: 12px; opacity: 0.8;">{point.key}</span><br/>',
    pointFormat:
      '<span style="color:{point.color}; stroke: white; stroke-width: 2; ' +
      'font-size: 25px;">\u25CF</span> {series.name}: <b>{point.y}</b><br/>'
  },
  title: {
    text: '',
    y: 5
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

export const geoChartOptions = defaultsDeep({

}, chartOptions);

export const stockChartOptions = defaultsDeep({
  plotOptions: {
    series: {
      turboThreshold: 5000,
      dataGrouping: {
        enabled: false
      }
    }
  },
  tooltip: {
    enabled: true
  },
  scrollbar: {
    barBackgroundColor: '#c1c1c1',
    barBorderRadius: 3,
    barBorderWidth: 0,
    buttonBackgroundColor: '#c1c1c1',
    buttonBorderWidth: 0,
    buttonArrowColor: 'rgba(0,0,0,0.54)',
    buttonBorderRadius: 3,
    rifleColor: 'rgba(0,0,0,0.54)',
    trackBackgroundColor: 'white',
    trackBorderWidth: 1,
    trackBorderColor: '#c1c1c1',
    trackBorderRadius: 3,
    height: 12
  },
  rangeSelector: {
    selected: 1,
    buttons: [
      {
        type: 'month',
        count: 1,
        text: '1m'
      },
      {
        type: 'month',
        count: 3,
        text: '3m'
      },
      {
        type: 'month',
        count: 6,
        text: '6m'
      },
      {
        type: 'year',
        count: 1,
        text: '1y'
      },
      {
        type: 'all',
        text: 'All'
      }
    ],
    buttonPosition: {
      align: 'left'
    },
    inputPosition: {
      x: 0,
      y: 0
    }
  }
}, chartOptions);

export const bulletChartOptions = {
  chart: {
    type: 'bullet',
    inverted: true,
    marginRight: 20,
    height: 170
  },
  exporting: {
    enabled: false
  },
  title: {
    text: ''
  },
  subtitle: {
    text: '',
    align: 'right',
    x: -10
  },
  legend: {
    enabled: false
  },
  plotOptions: {
    series: {
      pointPadding: 0.25,
      borderWidth: 0,
      color: '#000',
      targetOptions: {
        width: '200%'
      }
    }
  },
  tooltip: {
    enabled: true,
    useHTML: true,
    pointFormat: '<b>{point.y}</b> (with target at {point.target})'
  },
  series: [
    {
      data: []
    }
  ],
  yAxis: {
    gridLineWidth: 0,
    title: null,
    plotBands: []
  },
  credits: false
};
