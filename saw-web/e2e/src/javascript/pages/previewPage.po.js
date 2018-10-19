module.exports = {
  axisTitle: axisName => element(by.xpath(`//analyze-chart-preview[.]/descendant::*[contains(text(),'${axisName}')]`)),
  axisTitleUpdated: (chartType, axisLabel, axis) => element(by.xpath(`(//chart/descendant::*[name()="svg"])[position()=last()]/descendant::*[name()="g" and contains(@class,"highcharts-${axis}")]/descendant::*[contains(text(),"${axisLabel}")]`)),
  closeButton: () => element(by.xpath(`//button[contains(@class,'close')]`))
};
