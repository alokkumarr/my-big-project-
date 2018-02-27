module.exports = {
  previewBtn: element(by.css('button[e2e="open-preview-modal"]')),
  filters: {
    numberInput: element(by.xpath("(//input[@type='number'])[2]"))
  },
  pivot: {
    addFieldButton: fieldName => element(by.xpath(`(//div[contains(text(), '${fieldName}')]/following-sibling::*)[1]`)),
    expandSelectedFieldPropertiesButton: fieldName => element(by.xpath(`(//div[contains(text(), '${fieldName}')]/preceding-sibling::*)[1]`)),
    groupIntervalDropDown: element(by.xpath(`//mat-select[@placeholder='Group interval']`))
  }
};
