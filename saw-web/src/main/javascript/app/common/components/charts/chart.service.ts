import * as Highcharts from 'highcharts/highcharts';
import { Injectable } from '@angular/core';

@Injectable()
export class ChartService {

  constructor() { }

  /**
   * Takes multiple highcharts objects as input, and returns
   * a single SVG with all of them clubbed together in same
   * image.
   * Credits: https://jsfiddle.net/gh/get/jquery/1.7.2/highcharts/highcharts/tree/master/samples/highcharts/exporting/multiple-charts-offline/
   *
   * @param {any} charts
   * @param {any} options
   * @param {any} callback
   * @memberof ChartService
   */
  getSVG (charts, options, callback) {
    var svgArr = [],
      top = 0,
      width = 0,
      addSVG = function (svgres) {
        // Grab width/height from exported chart
        var svgWidth = +svgres.match(
          /^<svg[^>]*width\s*=\s*\"?(\d+)\"?[^>]*>/
        )[1],
          svgHeight = +svgres.match(
            /^<svg[^>]*height\s*=\s*\"?(\d+)\"?[^>]*>/
          )[1],
          // Offset the position of this chart in the final SVG
          svg = svgres.replace('<svg', '<g transform="translate(0,' + top + ')" ');
        svg = svg.replace('</svg>', '</g>');
        top += svgHeight;
        width = Math.max(width, svgWidth);
        svgArr.push(svg);
      },
      exportChart = function (i) {
        if (i === charts.length) {
          return callback('<svg height="' + top + '" width="' + width +
            '" version="1.1" xmlns="http://www.w3.org/2000/svg">' + svgArr.join('') + '</svg>');
        }
        charts[i].getSVGForLocalExport(options, {}, function () {
          throw new Error('Failed to get SVG');
        }, function (svg) {
          addSVG(svg);
          return exportChart(i + 1); // Export next only when this SVG is received
        });
      };
    exportChart(0);
  }

  /**
   * Takes an array of charts objects as input and downloads a single file
   * with all of them clubbed together.
   *
   * @param {any} charts
   * @param {boolean} [options={ exporting: { enabled: true, fallbackToExportServer: false}}]
   * @memberof ChartService
   */
  exportCharts(charts, options = { exporting: { enabled: true, fallbackToExportServer: false}}) {
    // Get SVG asynchronously and then download the resulting SVG
    this.getSVG(charts, options, function (svg) {
      Highcharts.downloadSVGLocal(svg, options, function () {
        throw new Error('Failed to export on client side');
      });
    });
  };
}
