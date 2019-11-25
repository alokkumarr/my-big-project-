import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import * as Highcharts from 'highcharts/highcharts';
import * as cloneDeep from 'lodash/cloneDeep';
import * as defaultsDeep from 'lodash/defaultsDeep';

import { globalChartOptions } from '../../../../../common/components/charts/default-chart-options';
import { AlertChartData } from '../../../alerts.interface';

@Component({
  selector: 'alert-chart',
  templateUrl: 'alert-chart.component.html',
  styleUrls: ['alert-chart.component.scss']
})
export class AlertChartComponent implements OnInit {
  @ViewChild('container', { static: true }) container: ElementRef;

  @Input() title: string;
  @Input() dateFilter: string;
  @Input() additionalOptions: object;
  @Input('chartData') set setChartData(chartData: AlertChartData) {
    const series = [
      {
        name: this.title,
        data: chartData.y
      }
    ];
    const xAxis = { categories: chartData.x };
    setTimeout(() => {
      this.chart = Highcharts.chart(
        this.container.nativeElement,
        defaultsDeep({ series, xAxis }, this.chartOptions)
      );
    }, 100);
  }

  public chart;
  public countData = [];
  public chartOptions;

  constructor() {
    Highcharts.setOptions(cloneDeep(globalChartOptions));
  }

  ngOnInit() {
    this.initializeChart();
  }

  initializeChart() {
    this.chartOptions = defaultsDeep(this.additionalOptions, {
      width: '100%',
      height: '100%',
      title: { text: '' },
      legend: { enabled: false },
      xAxis: {
        type: 'datetime',
        categories: [],
        gridLineWidth: 1
      },
      credits: false,
      exporting: { enabled: false },
      yAxis: {
        title: ''
      },
      series: [
        {
          name: this.title,
          data: []
        }
      ]
    });
  }
}
