module.exports = {
  axisTitle: axisName => element(by.xpath(`//analyze-chart-preview[.]/descendant::*[contains(text(),'${axisName}')]`)),
  axisTitleUpdated: (chartType, axisLabel, axis) => element(by.xpath(`(//chart[@ng-reflect-e2e="chart-type:${chartType}"]/descendant::*[name()='svg'])[position()=last()]/descendant::*[name()='g' and contains(@class,'highcharts-${axis}')]/descendant::*[contains(text(),'${axisLabel}')]`)),
  closeButton: () => element(by.xpath(`//button[contains(@class,'close')]`))
};
