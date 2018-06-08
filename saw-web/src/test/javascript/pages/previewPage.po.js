module.exports = {
  axisTitle: axisName => element(by.xpath(`//analyze-chart-preview[.]/descendant::*[contains(text(),'${axisName}')]`)),
  closeButton: () => element(by.xpath(`//button[contains(@class,'close')]`))
};
