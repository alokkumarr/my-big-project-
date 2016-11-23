function removeProps(obj, props) {
  const result = {};
  angular.forEach(props, prop => {
    if (obj[prop] !== null) {
      result[prop] = obj[prop];
      delete obj[prop];
    }
  });
  return result;
}

function walkTree(obj, cb) {
  angular.forEach(obj, (val, key) => {
    cb(obj, key, val);
    if (angular.isArray(val)) {
      val.forEach(member => {
        walkTree(member, cb);
      });
    } else if (angular.isObject(val)) {
      walkTree(val, cb);
    }
  });
}

export function wrapChart(chartName, chartFactory) {
  /** @ngInject */
  return function ($injector, Highcharts, $compile, $rootScope, $templateRequest, $q) {
    let chartSettings = $injector.invoke(chartFactory);
    const def = removeProps(chartSettings, ['scope', 'link', 'require', 'transform']);
    const promises = [];

    walkTree(chartSettings, (obj, key, value) => {
      if (key === 'formatter' && value.templateUrl) {
        promises.push($templateRequest(value.templateUrl).then(contents => {
          delete value.templateUrl;
          value.template = contents;
        }));
      }
    });

    const definition = {
      restrict: 'E',
      scope: def.scope || {
        data: '<',
        options: '<'
      },
      compile(tElement) {
        const chartDeferred = $q.defer();
        tElement.data(`$${chartName}Controller`, chartDeferred.promise);

        return function (scope, element, attrs, ctrls) {
          if (scope.options && angular.isObject(scope.options)) {
            chartSettings = angular.merge(scope.options, chartSettings);
          }
          $q.all(promises).then(() => {
            walkTree(chartSettings, (obj, key, value) => {
              if (key === 'events') {
                angular.forEach(value, (cb, eventName) => {
                  value[eventName] = (...args) => {
                    args = [].slice.call(arguments);
                    scope.$apply(function () {
                      cb.apply(this, args.concat([scope]));
                    });
                  };
                });
              }
              if (key === 'formatter' && value.template) {
                if (!obj.useHTML) {
                  throw new Error('A formatter.template or formatter.templateUrl value must specify useHTML as true');
                }
                const template = value.template;
                const linkFn = $compile(template);
                obj.formatter = function () {
                  return {
                    linkFn,
                    scope,
                    data: this
                  };
                };
              }
            });
            const chart = Highcharts.chart(element[0], chartSettings);
            chartDeferred.resolve(chart);
            if (chartSettings.loading) {
              chart.showLoading();
            }
            function indexById(arr) {
              const result = {};
              arr.forEach(item => {
                if (item.id && !result[item.id]) {
                  result[item.id] = item;
                } else if (!item.id) {
                  throw new Error('Series without an explicit id are not supported in transform');
                } else {
                  throw new Error('Duplicate ids are not supported in transform');
                }
              });
              return result;
            }

            if ((!def.scope || def.scope.data) && def.transform) {
              let previousSeries = chartSettings.series || [];
              scope.$watch('data', data => {
                if (data && ((angular.isArray(data) && data.length > 0) || !angular.isArray(data))) {
                  if (chartSettings.loading) {
                    chart.hideLoading();
                  }
                  const currentSeries = indexById(def.transform(data, chart, scope));
                  angular.forEach(currentSeries, (series, id) => {
                    if (previousSeries[id]) {
                      const data = series.data;
                      const previousData = previousSeries[id].data;
                      const target = chart.get(id);
                      delete series.data;
                      delete previousSeries[id].data;
                      if (angular.equals(series, previousSeries[id])) {
                        if (!angular.equals(data, previousData)) {
                          target.setData(angular.copy(data), false);
                        }
                        series.data = data;
                      } else {
                        series.data = data;
                        target.update(angular.copy(series), false);
                      }
                    } else {
                      chart.addSeries(angular.copy(series), false);
                    }
                  });
                  angular.forEach(previousSeries, (series, id) => {
                    if (!currentSeries[id]) {
                      chart.get(id).remove(false);
                    }
                  });
                  chart.redraw();
                  previousSeries = currentSeries;
                }
              });
            }
            if (ctrls) {
              if (!angular.isArray(ctrls)) {
                ctrls = [ctrls];
              }
              ctrls.unshift(chart);
            } else {
              ctrls = chart;
            }
            if (def.link) {
              def.link(scope, element, attrs, ctrls);
            } else {
              defaultLink(scope, element, attrs, ctrls);
            }
          });
        };
      }
    };

    if (def.require) {
      definition.require = def.require;
    }

    return definition;
  };

  /** used if there is no link function provided in the chart component */
  function defaultLink(scope, element, attrs, chart) {

    chart.showLoading();
    scope.$watch('data', data => {

      if (data) {
        chart.hideLoading();
        if (!chart.series || (angular.isArray(chart.series) && chart.series.length === 0)) {
          // if the data is changing for the first time
          let index = 0;
          angular.forEach(data, (value, key) => {

            chart.addSeries({
              id: index++,
              name: key,
              data: value
            });

          });
        } else {
          // if the data is being updated
          const newSeries = Object.keys(data).map((key, index) => {
            return {
              id: index,
              name: key,
              data: data[key]
            };
          });
          chart.update({
            series: newSeries
          });
        }
      }

    });
  }
}
