export const globalChartOptions = {
  lang: {
    thousandsSep: ','
  }
};

export const chartOptions = {
  colors: ['#0375bf', '#4c9fd2', '#bfdcef', '#490094', '#9A72C4', '#C8B2DF', '#006ADE', '#6AB4FF',
    '#B5DAFF', '#014752', '#009293', '#73C3C4', '#4CEA7C', '#9DF4B7', '#C9F9D8',
    '#DD5400', '#EDA173', '#F5CDB4', '#940000', '#C47373', '#DFB2B2'],
  plotOptions: {
    series: {
      barBgColor: '#f3f5f8',
      marker: {
        fillColor: '#FFFFFF',
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
    backgroundColor: '#293D5A',
    borderWidth: 0,
    shadow: false,
    useHTML: true,
    headerFormat: '<span style="font-size: 12px; opacity: 0.8;">{point.key}</span><br/>',
    pointFormat: '<span style="color:{point.color}; stroke: white; stroke-width: 2; ' +
    'font-size: 25px;">\u25CF</span> {series.name}: <b>{point.y}</b><br/>',
    style: {
      color: '#FFFFFF',
      fontSize: '16px'
    }
  },
  title: {
    text: ''
  },
  credits: false
};
