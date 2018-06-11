module.exports = {
  previewBtn: element(by.css('button[e2e="open-preview-modal"]')),
  filterBtn: element(by.xpath('//button/span[text()="Filter"]')),
  applyFiltersBtn: element(by.xpath('//span[contains(text(),"Apply Filters")]')),
  // available functions: Sum, AVG, MIN, MAX, Count
  aggregateFunctionButton: aggregateFunction => element(by.xpath(`//mat-icon[@class="mat-icon material-icons icon-${aggregateFunction}"]`)),
  aggregateFunctionMenuItem: aggregateFunction => element(by.xpath(`//button[@role="menuitem"]/mat-icon[@class="mat-icon material-icons icon-${aggregateFunction}"]`)),
  filterWindow: {
    numberInputUpgraded: element(by.css('input[e2e="designer-number-filter-input"]')),
    numberInput: element(by.xpath("(//input[@type='number'])[2]")),
    columnDropDown: element(by.css('input[e2e="filter-autocomplete-input"]')),
    columnNameDropDownItem: columnName => element(by.xpath(`(//mat-option/span[contains(text(),"${columnName}")])[1]`)),
    date: {
      presetDropDown: element(by.xpath('//span[contains(text(),"Custom")]')),
      presetDropDownItem: presetName => element(by.xpath(`//mat-option[contains(text(),"${presetName}")]`))
    },
    string: {
      operator: element(by.xpath('//mat-select[@placeholder="OPERATOR"]')),
      operatorDropDownItem: operator => element(by.xpath(`//span[contains(text(),"${operator}")]`)),
      input: element(by.xpath(`//*[@e2e="filter-string-input"]`))
    },
    number: {
      operator: element(by.css('mat-select[e2e="filter-number-operator-select"]')),
      operatorDropDownItem: operator => element(by.css(`mat-option[e2e="filter-number-operator-option-${operator}"]`)),
      input: element(by.xpath('//input[@type="number"]'))
    }
  },
  pivot: {
    addFieldButton: fieldName => element(by.xpath(`(//div[contains(text(), '${fieldName}')]/following-sibling::*)[1]`)),
    expandSelectedFieldPropertiesButton: fieldName => element(by.xpath(`(//div[contains(text(), '${fieldName}')]/preceding-sibling::*)[1]`)),
    groupIntervalDropDown: element(by.xpath(`//mat-select[@placeholder='Group interval']`)),
    groupIntervalDropDownElement: groupIntervalName => element(by.xpath(`//span[@class="mat-option-text" and contains(text(), '${groupIntervalName}')]`)),
    addFilter: filterObject => addFilter(filterObject)
  },
  chart: {
    addFieldButton: fieldName => element(by.xpath(`(//div[contains(text(), '${fieldName}')]/following-sibling::*)[1]`)),
    expandSelectedFieldPropertiesButton: fieldName => element(by.xpath(`(//div[contains(text(), '${fieldName}')]/preceding-sibling::*)[1]`)),
    groupIntervalDropDown: element(by.xpath(`//mat-select[@placeholder='Group interval']`)),
    groupIntervalDropDownElement: groupIntervalName => element(by.xpath(`//span[@class="mat-option-text" and contains(text(), '${groupIntervalName}')]`)),
    addFilter: filterObject => addFilter(filterObject)
  }
};

const addFilter = filterObject => {

};
