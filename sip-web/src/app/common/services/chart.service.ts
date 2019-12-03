import { Injectable } from '@angular/core';

import * as get from 'lodash/get';
import * as set from 'lodash/set';
import * as clone from 'lodash/clone';
import * as sum from 'lodash/sum';
import * as map from 'lodash/map';
import * as round from 'lodash/round';
import * as assign from 'lodash/assign';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as indexOf from 'lodash/indexOf';
import * as isEmpty from 'lodash/isEmpty';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpToPairs from 'lodash/fp/toPairs';
import * as fpMap from 'lodash/fp/map';
import * as fpMapValues from 'lodash/fp/mapValues';
import * as fpInvert from 'lodash/fp/invert';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpOrderBy from 'lodash/fp/orderBy';
import * as reduce from 'lodash/reduce';
import * as concat from 'lodash/concat';
import * as compact from 'lodash/compact';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpUniq from 'lodash/fp/uniq';
import * as findIndex from 'lodash/findIndex';
import * as mapValues from 'lodash/mapValues';
import * as sortBy from 'lodash/sortBy';
import * as moment from 'moment';
import * as values from 'lodash/values';
import * as toString from 'lodash/toString';
import * as replace from 'lodash/replace';
import * as isUndefined from 'lodash/isUndefined';
import * as isArray from 'lodash/isArray';

import * as Highcharts from 'highcharts/highcharts';

import { QueryDSL, ArtifactColumnDSL } from 'src/app/models';
import {
  getTooltipFormats,
  displayNameWithoutAggregateFor
} from './tooltipFormatter';
import { DATE_TYPES, AGGREGATE_TYPES_OBJ, CHART_COLORS } from '../consts';

const removeKeyword = (input: string) => {
  if (!input) {
    return input;
  }
  return input.replace('.keyword', '');
};

const LEGEND_POSITIONING = {
  left: {
    name: 'left',
    displayName: 'Left',
    align: 'left',
    verticalAlign: 'middle'
  },
  right: {
    name: 'right',
    displayName: 'Right',
    align: 'right',
    verticalAlign: 'middle'
  },
  top: {
    name: 'top',
    displayName: 'Top',
    align: 'center',
    verticalAlign: 'top'
  },
  bottom: {
    name: 'bottom',
    displayName: 'Bottom',
    align: 'center',
    verticalAlign: 'bottom'
  }
};

const LAYOUT_POSITIONS = {
  horizontal: {
    name: 'horizontal',
    displayName: 'Horizontal',
    layout: 'horizontal'
  },
  vertical: {
    name: 'vertical',
    displayName: 'Vertical',
    layout: 'vertical'
  }
};

/* Customize default config for stack column chart */
const stackConfig = config => {
  set(config, 'chart.type', 'column');
  set(config, 'plotOptions.column.stacking', 'normal');
  return config;
};

const pieConfig = config => {
  delete config.xAxis;
  delete config.yAxis;
  set(config, 'series', [
    {
      name: 'Brands',
      colorByPoint: true,
      data: []
    }
  ]);
  set(config, 'plotOptions.pie.showInLegend', false);
  return config;
};

const donutConfig = config => {
  set(config, 'chart.type', 'pie');
  return pieConfig(config);
};

const configCustomizer = {
  stack: stackConfig,
  pie: pieConfig,
  donut: donutConfig
};

const addCommas = nStr => {
  nStr = String(nStr);
  const x = nStr.split('.');
  let x1 = x[0];
  const x2 = x.length > 1 ? '.' + x[1] : '';
  const rgx = /(\d+)(\d{3})/;
  while (rgx.test(x1)) {
    x1 = x1.replace(rgx, '$1,$2');
  }
  return x1 + x2;
};

@Injectable()
export class ChartService {
  _Highcharts = Highcharts;
  LEGEND_POSITIONING = LEGEND_POSITIONING;
  LAYOUT_POSITIONS = LAYOUT_POSITIONS;
  constructor() {}

  analysisLegend2ChartLegend(legend) {
    const align = LEGEND_POSITIONING[get(legend, 'align')];
    const layout = LAYOUT_POSITIONS[get(legend, 'layout')];

    if (!align || !layout) {
      return null;
    }
    return {
      align: align.align,
      verticalAlign: align.verticalAlign,
      layout: layout.layout
    };
  }

  initLegend(analysis) {
    const initialLegendPosition =
      analysis.chartType === 'combo'
        ? 'top'
        : analysis.chartType.substring(0, 2) === 'ts'
        ? 'bottom'
        : 'right';
    const initialLegendLayout =
      analysis.chartType === 'combo' ||
      analysis.chartType.substring(0, 2) === 'ts'
        ? 'horizontal'
        : 'vertical';

    return {
      align: get(analysis, 'legend.align', initialLegendPosition),
      layout: get(analysis, 'legend.layout', initialLegendLayout),
      options: {
        align: values(this.LEGEND_POSITIONING),
        layout: values(this.LAYOUT_POSITIONS)
      }
    };
  }

  /* Returns default chart config for various chart types */
  getChartConfigFor(type, options) {
    const legendPosition = LEGEND_POSITIONING[get(options, 'legend.align')];
    const legendLayout = LAYOUT_POSITIONS[get(options, 'legend.layout')];

    const SPACING = 45;
    const HEIGHT = isUndefined(options.chart) ? 400 : options.chart.height;

    const config = {
      chart: {
        type: ['combo', 'bar'].includes(type)
          ? null
          : this.splinifyChartType(type),
        spacingLeft: SPACING,
        spacingRight: SPACING,
        spacingBottom: SPACING,
        spacingTop: SPACING,
        height: HEIGHT,
        reflow: true
      },
      legend: {
        align: legendPosition.align,
        verticalAlign: legendPosition.verticalAlign,
        layout: legendLayout.layout,
        enabled: false
      },
      series: [
        {
          name: 'Series 1',
          data: []
        }
      ],
      yAxis: {
        title: { x: -15 }
      },
      xAxis: {
        categories: [],
        title: { y: 15 }
      }
    };

    if (configCustomizer[type]) {
      return configCustomizer[type](config);
    }
    return config;
  }

  /* Provides view related configuration for various chart types */
  getViewOptionsFor(type) {
    const config = {
      yAxisLabels: [],
      axisLabels: {
        x: 'Dimensions',
        y: 'Metrics',
        z: 'Z-Axis',
        g: 'Group By'
      },
      renamable: {
        x: true,
        y: true,
        z: false,
        g: false
      },
      required: {
        x: true,
        y: true,
        z: false,
        g: false
      },
      labelOptions: [],
      customTooltip: true,
      legend: true
    };

    /* prettier-ignore */
    switch (type) {
    case 'bubble':
      config.axisLabels.z = 'Size By';
      config.axisLabels.g = 'Color By';
      config.required.z = true;
      return config;

    case 'pie':
      config.axisLabels.y = 'Angle';
      config.axisLabels.x = 'Color By';
      config.renamable.x = false;
      config.labelOptions = [
        {
          value: 'percentage',
          name: 'Show Percentage'
        },
        {
          value: 'data',
          name: 'Show Data Value'
        }
      ];
      config.customTooltip = true;
      config.legend = false;
      return config;

    case 'column':
    case 'bar':
    case 'line':
    case 'area':
    case 'spline':
    case 'stack':
    case 'scatter':
    case 'tsspline':
    case 'tsPane':
    default:
      return config;
    }
  }

  splitToSeries(parsedData, fields, chartType) {
    const areMultipleYAxes = fields.y.length > 1;
    const isGrouped = fields.g;

    if (areMultipleYAxes) {
      return this.splitSeriesByYAxes(parsedData, fields, chartType);
    }

    if (isGrouped) {
      return this.splitSeriesByGroup(parsedData, fields);
    }

    const axesFieldNameMap = this.getAxesFieldNameMap(fields);
    const yField = isArray(fields.y) ? fields.y[0] : fields.y;
    return [
      {
        ...this.getSerie(
          yField,
          0,
          isArray(fields.y) ? fields.y : [fields.y],
          chartType
        ),
        data: map(parsedData, dataPoint =>
          mapValues(axesFieldNameMap, val => dataPoint[val])
        )
      }
    ];
  }

  splitToSeriesAndCategories(parsedData, fields, { sorts }, chartType) {
    const categories = {};
    const isHighStock = chartType.substring(0, 2) === 'ts';
    const fieldsArray = compact([fields.x, ...fields.y, fields.z, fields.g]);
    const dateFields = filter(fieldsArray, ({ type }) =>
      DATE_TYPES.includes(type)
    );
    if (!isHighStock) {
      // check if Highstock timeseries(ts) or Highchart
      this.formatDatesIfNeeded(parsedData, dateFields);
    } else {
      this.dateStringToTimestamp(parsedData, dateFields);
    }
    const series = this.splitToSeries(parsedData, fields, chartType);
    // split out categories frem the data
    forEach(series, serie => {
      serie.data = map(serie.data, point => {
        const dataPoint = clone(point);
        forEach(dataPoint, (v, k) => {
          if (this.isCategoryAxis(fields, k)) {
            this.addToCategory(categories, k, v);
          }
        });
        return dataPoint;
      });
    });

    // sort the categories if a sort is specified for category field
    if (sorts && sorts.length) {
      forEach(categories, (v, k) => {
        const field =
          filter(
            fieldsArray,
            f => (typeof f.checked === 'string' ? f.checked : f.area) === k
          )[0] || {};
        const sortField =
          filter(
            sorts,
            sortF =>
              (sortF.field
                ? sortF.field.dataField
                : removeKeyword(sortF.columnName)) ===
              removeKeyword(field.columnName)
          )[0] || {};
        categories[k] = fpOrderBy(
          value => this.getSortValue(field, value),
          sortField.order === 'asc' ? 'asc' : 'desc',
          categories[k]
        );
      });
    }

    /* assign data points to category indexes. If this is done
    at the same time as creation of categories, it becomes impossible
    to get the index from sorted categories array since it'll be
    incomplete */
    forEach(series, serie => {
      serie.data = map(serie.data, point => {
        const dataPoint = clone(point);
        forEach(dataPoint, (v, k) => {
          if (this.isCategoryAxis(fields, k)) {
            if (!isHighStock) {
              dataPoint[k] = indexOf(categories[k], v);
            } else {
              dataPoint[k] = v;
            }
          }
        });
        return dataPoint;
      });
    });

    if (!isEmpty(categories)) {
      forEach(series, serie => {
        serie.data = sortBy(serie.data, 'x');
      });
    }
    return {
      series,
      categories
    };
  }

  splinifyChartType(type) {
    /* prettier-ignore */
    switch (type) {
    case 'area':
    case 'tsareaspline':
      return 'areaspline';
    case 'line':
    case 'tsspline':
    case 'tsline':
    case 'tsPane':
      return 'spline';
    default:
      return type;
    }
  }

  getSortValue(field, value) {
    if (!field) {
      return value;
    }

    if (DATE_TYPES.includes(field.type)) {
      const momentDateFormat = this.getMomentDateFormat(
        field.dateFormat || field.format
      );
      return moment(value, momentDateFormat);
    }

    return value;
  }

  /**
   * Converts date strings to unix epoch time. This is used primarily for
   * time series charts because they need timestamps.
   *
   * @param {Array<any>} parsedData
   * @param {Array<{ columnName: string }>} dateFields
   * @memberof ChartService
   */
  dateStringToTimestamp(
    parsedData: Array<any>,
    dateFields: Array<{ columnName: string }>
  ) {
    if (!isEmpty(dateFields)) {
      forEach(parsedData, dataPoint => {
        forEach(dateFields, ({ columnName, dateFormat }) => {
          const momentDateFormat = this.getMomentDateFormat(dateFormat);
          dataPoint[removeKeyword(columnName)] =
            moment(dataPoint[removeKeyword(columnName)], momentDateFormat)
              .utc()
              .unix() * 1000;
        });
      });
    }
  }

  formatDatesIfNeeded(parsedData, dateFields) {
    if (!isEmpty(dateFields)) {
      forEach(parsedData, dataPoint => {
        forEach(dateFields, ({ columnName, dateFormat }) => {
          const momentDateFormat = this.getMomentDateFormat(dateFormat);
          dataPoint[removeKeyword(columnName)] = moment
            .utc(dataPoint[removeKeyword(columnName)], momentDateFormat)
            .format(momentDateFormat);
        });
      });
    }
  }

  getSerie(
    {
      alias,
      type,
      displayName,
      displayType,
      dataField,
      expression,
      comboType,
      aggregate,
      chartType
    },
    index,
    fields,
    chartTypeOverride
  ) {
    const aggrSymbol = ['percentage', 'percentagebyrow'].includes(aggregate)
      ? '%'
      : '';
    const comboGroups = fpPipe(
      fpMap(field => field.comboType || field.displayType),
      fpUniq,
      fpInvert,
      fpMapValues(parseInt)
    )(fields);
    const splinifiedChartType = this.splinifyChartType(
      comboType || displayType
    );
    const zIndex = this.getZIndex(comboType || displayType);
    const nameWithAggregate = expression
      ? displayName
      : `${
          AGGREGATE_TYPES_OBJ[aggregate].designerLabel
        }(${displayNameWithoutAggregateFor({
          displayName,
          dataField
        } as ArtifactColumnDSL)})`;
    return {
      name: alias || nameWithAggregate,
      aggrSymbol,
      aggregate,
      type: splinifiedChartType,
      dataType: type,
      yAxis:
        chartType === 'tsPane' || chartTypeOverride === 'tsPane'
          ? index
          : comboGroups[comboType || displayType],
      zIndex,
      data: []
    };
  }

  getMomentDateFormat(dateFormat) {
    // the backend and moment.js require different date formats for days of month
    // the backend represents it with "d", and momentjs with "Do"
    return replace(dateFormat, 'd', 'Do');
  }

  getZIndex(type) {
    /* prettier-ignore */
    switch (type) {
    case 'area':
      return 0;
    case 'column':
    case 'bar':
      return 1;
    case 'pline':
      return 3;
    default:
      return 4;
    }
  }

  splitSeriesByYAxes(parsedData, fields, type) {
    const axesFieldNameMap = this.getAxesFieldNameMap(fields, 'y');
    forEach(fields.y, field => {
      field.chartType = type;
    });
    const series = map(fields.y, this.getSerie.bind(this));
    forEach(parsedData, dataPoint => {
      forEach(fields.y, (field, index) => {
        series[index].data.push(
          assign(
            {
              y: dataPoint[removeKeyword(field.dataField || field.columnName)]
            },
            mapValues(axesFieldNameMap, val => dataPoint[val])
          )
        );
      });
    });

    return series;
  }

  splitSeriesByGroup(parsedData, fields) {
    const axesFieldNameMap = this.getAxesFieldNameMap(fields);
    const [firstYField] = fields.y;
    const { aggregate } = firstYField;
    const aggrSymbol = ['percentage', 'percentagebyrow'].includes(aggregate)
      ? '%'
      : '';
    let comboType = firstYField.comboType || firstYField.displayType;
    if (!isUndefined(comboType)) {
      if (comboType === 'tsspline' || comboType === 'tsPane') {
        comboType = comboType === 'tsPane' ? 'spline' : comboType.slice(2);
      }
    }

    return fpPipe(
      fpMap(dataPoint => mapValues(axesFieldNameMap, val => dataPoint[val])),
      fpGroupBy('g'),
      fpToPairs,
      fpMap(([name, data]) => ({
        name,
        data,
        type: comboType,
        aggrSymbol,
        aggregate,
        dataType: firstYField.type
      }))
    )(parsedData);
  }

  /**
   * Get the map from colmnNames to the axes, or group
   * Ex
   * y -> AVAILABLE_MB
   */
  getAxesFieldNameMap(fields, exclude = '') {
    // Y axis ommitted because it is added in splitSeriesByYAxes
    const y = exclude === 'y' ? [] : fields.y;
    const fieldsArray = compact([fields.x, ...y, fields.z, fields.g]);
    return reduce(
      fieldsArray,
      (accumulator, field) => {
        const area =
          typeof field.checked === 'string' ? field.checked : field.area;
        accumulator[area] = removeKeyword(
          (area === 'y' || area === 'z') && field.dataField
            ? field.dataField
            : field.columnName
        );
        return accumulator;
      },
      {}
    );
  }

  addToCategory(categories, key, newCategoryValue) {
    if (!categories[key]) {
      categories[key] = [];
    }
    if (indexOf(categories[key], newCategoryValue) < 0) {
      categories[key].push(newCategoryValue);
    }
  }

  isCategoryAxis(fields, key) {
    const dataType = get(fields, `${key}.type`);
    const isAxis = key !== 'g';
    // Strings should be represented as categories in the chart
    /* eslint-disable angular/typecheck-string */
    const isCategoryAxis =
      isAxis &&
      (dataType === 'string' ||
        dataType === 'String' ||
        DATE_TYPES.includes(dataType));
    /* eslint-enable angular/typecheck-string */
    return isCategoryAxis;
  }

  dataToNestedDonut(series, categories, fields) {
    /* Group by option forms the inner circle. X axis option forms the outer region
       This logic is adapted from https://www.highcharts.com/demo/pie-donut */
    const colors = this._Highcharts.getOptions().colors;
    const gCategories = map(series, s => s.name);
    const data = map(series, (s, i) => {
      const drilldown = {
        color: colors[i],
        categories: map(s.data, d => get(categories, `x.${d.x}`)),
        xData: map(s.data, d => d.x),
        yData: map(s.data, d => d.y)
      };
      return {
        drilldown,
        y: sum(drilldown.yData),
        x: sum(drilldown.xData)
      };
    });

    const innerData = [];
    const outerData = [];
    const dataLen = data.length;

    for (let i = 0; i < dataLen; i += 1) {
      const name = gCategories[i];
      const { y, x, color } = data[i];
      innerData.push({
        name,
        y,
        x,
        color
      });
      const drillDataLen = data[i].drilldown.yData.length;
      for (let j = 0; j < drillDataLen; j += 1) {
        const brightness = 0.2 - j / drillDataLen;
        const { categories: drillDownCategories, yData, xData } = data[
          i
        ].drilldown;
        outerData.push({
          name: drillDownCategories[j],
          y: yData[j],
          x: xData[j],

          /* eslint-disable */
          color: new this._Highcharts.Color(data[i].color)
            .brighten(brightness)
            .get()
          /* eslint-enable */
        });
      }
    }
    const { aggregate, aggrSymbol, dataType } = get(series, '0', {});

    const chartSeries = [
      {
        name: fields.g.displayName,
        data: innerData,
        aggrSymbol,
        dataType,
        aggregate,
        dataLabels: {
          color: '#ffffff',
          distance: -30
        },
        size: '60%'
      },
      {
        name: fields.x.displayName,
        aggrSymbol,
        dataType,
        aggregate,
        data: outerData,
        size: '100%',
        innerSize: '60%',
        id: 'outerData'
      }
    ];

    return chartSeries;
  }

  customizeSeriesForChartType(series, chartType, categories, fields) {
    let mapperFn;
    let chartSeries;

    /* prettier-ignore */
    switch (chartType) {
    case 'column':
    case 'bar':
    case 'line':
    case 'spline':
    case 'stack':
    case 'scatter':
    case 'bubble':
    case 'area':
    case 'combo':
      // the bubble chart already supports the parsed data
      return { chartSeries: series };

    case 'pie':
      if (!fields.g) {
        mapperFn = ({ x, y }) => {
          const category = get(categories, `x.${x}`, toString(x || 0));
          return { name: category, y, drilldown: category, x};
        };
        forEach(series, serie => {
          serie.data = map(serie.data, mapperFn);
        });
        chartSeries = series;
      } else {
        chartSeries = this.dataToNestedDonut(series, categories, fields);
      }
      return { chartSeries };

    default:
      throw new Error(`Chart type: ${chartType} is not supported!`);
    }
  }

  getPieChangeConfig(type, fields, gridData, opts) {
    const changes = [];
    const labelOptions = get(opts, 'labelOptions', {
      enabled: true,
      value: 'percentage'
    });
    const { series, categories } = this.splitToSeriesAndCategories(
      gridData,
      fields,
      opts,
      type
    );
    const { chartSeries } = this.customizeSeriesForChartType(
      series,
      type,
      categories,
      fields
    );

    forEach(chartSeries, seriesData => {
      seriesData.dataLabels = seriesData.dataLabels || {};
      seriesData.dataLabels.enabled = labelOptions.enabled;

      /* eslint-disable */
      seriesData.dataLabels.formatter = function() {
        if (this.percentage <= 5) {
          return null;
        }
        const data =
          labelOptions.value === 'percentage' ? this.percentage : this.y;
        const isPercent = labelOptions.value === 'percentage';
        return `${this.point.name}: ${addCommas(round(data, 2))}${
          isPercent ? '%' : ''
        }`;
      };
      /* eslint-enable */
    });

    changes.push({
      path: 'series',
      data: chartSeries
    });
    return changes;
  }

  getYAxesChanges(type, axisFields, opts) {
    const panes = axisFields.length;
    const labelHeight = 15;
    if (type !== 'tsPane') {
      const changes = fpPipe(
        fpGroupBy(field => field.comboType || field.displayType),
        fpToPairs,
        fpMap(([, fields]) => {
          const titleText = map(fields, field => {
            if (!isUndefined(field.alias)) {
              return (
                field.alias ||
                (field.expression
                  ? field.displayName
                  : `${
                      AGGREGATE_TYPES_OBJ[field.aggregate].designerLabel
                    }(${displayNameWithoutAggregateFor(field)})`)
              );
            }
            return (
              opts.labels.y ||
              (field.expression
                ? field.dislplayName
                : `${
                    AGGREGATE_TYPES_OBJ[field.aggregate].designerLabel
                  }(${displayNameWithoutAggregateFor(field)})`)
            );
          }).join('<br/>');
          const isSingleField = fields.length === 1;
          return {
            gridLineWidth: 0,
            opposite: true,
            columnName: isSingleField
              ? removeKeyword(fields[0].columnName)
              : null,
            title: {
              useHtml: true,
              margin: labelHeight * fields.length,
              text: titleText,
              style: isSingleField
            },
            labels: {
              style: isSingleField
            }
          };
        })
      )(axisFields);

      forEach(changes, (change, changeIndex) => {
        if (changeIndex === 0) {
          change.opposite = false;
        }
        if (change.columnName) {
          const fieldIndex = findIndex(
            axisFields,
            ({ columnName }) => columnName === removeKeyword(change.columnName)
          );
          const style = { color: CHART_COLORS[fieldIndex] };
          change.title.style = change.title.style ? style : null;
          change.labels.style = change.labels.style ? style : null;
        }
      });
      return changes;
    }
    const chartChanges = [];
    forEach(axisFields, (field, index) => {
      const titleText =
        field.alias ||
        (field.expression
          ? field.displayName
          : `${
              AGGREGATE_TYPES_OBJ[field.aggregate].designerLabel
            }(${displayNameWithoutAggregateFor(field)})`);
      chartChanges.push({
        labels: {
          align: 'right',
          x: -3
        },
        title: {
          text: titleText
        },
        top: this.paneTopPercent(panes, index),
        height: panes === 1 ? '100%' : `${100 / panes}%`,
        offset: 0,
        lineWidth: 2,
        resize: {
          enabled: panes !== index + 1,
          lineWidth: 3,
          lineDashStyle: 'solid',
          lineColor: '#cccccc',
          x: 0,
          y: 0
        }
      });
    });
    return chartChanges;
  }

  paneTopPercent(panes, index) {
    if (panes === 1 || (panes !== 1 && index === 0)) {
      return null;
    }
    const height = 100 / panes;
    const top = height * index;
    return `${top}%`;
  }

  getBarChangeConfig(type, fields, gridData, opts) {
    const labels = {
      x:
        get(fields, 'x.alias') ||
        get(opts, 'labels.x') ||
        get(fields, 'x.displayName', '')
    };

    const changes = [
      {
        path: 'xAxis.title.text',
        data: labels.x || (opts.labels && opts.labels.x)
      }
    ];

    const yAxesChanges = this.getYAxesChanges(type, fields.y, opts);

    changes.push({
      path: 'yAxis',
      data: yAxesChanges
    });

    const { series, categories } = this.splitToSeriesAndCategories(
      gridData,
      fields,
      opts,
      type
    );
    changes.push({
      path: 'series',
      data: series
    });
    // add the categories
    forEach(categories, (category, k) => {
      changes.push({
        path: `${k}Axis.categories`,
        data: category
      });
    });
    return changes;
  }

  dataToChangeConfig(type, { artifacts }: QueryDSL, gridData, opts) {
    const selectedFields = fpFlatMap(x => x.fields, artifacts);

    let changes;
    const fields = {
      x:
        find(
          selectedFields,
          attr => attr.checked === 'x' || attr.area === 'x'
        ) || {},
      y: sortBy(
        filter(
          selectedFields,
          attr => attr.checked === 'y' || attr.area === 'y'
        ),
        'areaIndex'
      ),
      z: find(
        selectedFields,
        attr => attr.checked === 'z' || attr.area === 'z'
      ),
      g: find(selectedFields, attr => attr.checked === 'g' || attr.area === 'g')
    };

    switch (type) {
      case 'packedbubble':
        changes = this.getPackedBubbleChartConfig(fields, gridData);
        break;
      case 'pie':
        changes = this.getPieChangeConfig(type, fields, gridData, opts);
        break;
      case 'column':
      case 'bar':
      case 'line':
      case 'spline':
      case 'stack':
      case 'scatter':
      case 'bubble':
      case 'tsspline':
      case 'tsPane':
      default:
        changes = this.getBarChangeConfig(type, fields, gridData, opts);
        break;
    }

    return concat(changes, this.addTooltipsAndLegend(fields, type));
  }

  getPackedBubbleChartConfig(fields, gridData) {
    const groupField = fields.g;
    const hasGroupBy = Boolean(groupField);

    const dataMapper = dataPoint => {
      const valueProp = fields.y[0].dataField || fields.y[0].columnName;
      const nameProp = fields.x.dataField || removeKeyword(fields.x.columnName);
      return { name: dataPoint[nameProp], value: dataPoint[valueProp] };
    };

    const packedBubble = {
      minSize: '30%',
      maxSize: '120%',
      zMin: 0,
      zMax: 1000,
      layoutAlgorithm: {
        splitSeries: false,
        gravitationalConstant: 0.02
      },
      dataLabels: {
        enabled: true,
        format: '{point.name}',
        style: {
          color: 'black',
          textOutline: 'none',
          fontWeight: 'normal'
        }
      }
    };

    if (hasGroupBy) {
      const groupProp =
        groupField.dataField || removeKeyword(groupField.columnName);
      const series = fpPipe(
        fpGroupBy(fields.g.dataField || removeKeyword(fields.g.columnName)),
        fpMap(group => {
          const name = group[0][groupProp];
          return { name, data: map(group, dataMapper) };
        })
      )(gridData);
      return [
        { path: 'series', data: series },
        { path: 'plotOptions.packedbubble', data: packedBubble }
      ];
    }

    const data = map(gridData, dataMapper);
    return [
      { path: 'series', data: [{ data: data }] },
      { path: 'plotOptions.packedbubble', data: packedBubble }
    ];
  }

  addTooltipsAndLegendAsObject(fields, chartType) {
    const updates = this.addTooltipsAndLegend(fields, chartType);
    return reduce(
      updates,
      (acc, { path, data }) => {
        acc[path] = data;
        return acc;
      },
      {}
    );
  }

  addTooltipsAndLegend(fields, chartType) {
    const changes = [];
    const yIsSingle = fields.y.length === 1;
    if (!this.getViewOptionsFor(chartType).customTooltip) {
      return changes;
    }

    const { headerFormat, pointFormatter, footerFormat } = getTooltipFormats(
      fields,
      chartType
    );

    const isStockChart = chartType.substring(0, 2) === 'ts';

    let tooltipObj = {
      enabled: true,
      useHTML: true,
      headerFormat,
      pointFormatter,
      footerFormat,
      followPointer: true
    };

    if (isStockChart) {
      tooltipObj = {
        ...tooltipObj,
        valueDecimals: 3,
        split: true,
        shared: false,
        followPointer: true
      } as any;
    }

    changes.push({
      path: 'tooltip',
      data: tooltipObj
    });
    // if there is no grouping disable the legend
    // because there is only one data series
    changes.push({
      path: 'legend.enabled',
      data: Boolean(!yIsSingle || fields.g)
    });

    return changes;
  }
}
