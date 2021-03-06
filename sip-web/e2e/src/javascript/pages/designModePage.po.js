module.exports = {
  previewBtn: element(by.css('button[e2e="open-preview-modal"]')),
  filterBtn: element(by.xpath('//button/span[text()="Filter"]')),
  applyFiltersBtn: element(
    by.xpath('//span[contains(text(),"Apply Filters")]')
  ),
  backButton: element(by.css(' [fonticon="icon-arrow-left"]')),

  // available functions: Sum, AVG, MIN, MAX, Count
  aggregateFunctionButton: aggregateFunction =>
    element(by.css(`[e2e="${aggregateFunction}"]`)),
  aggregateFunctionMenuItem: aggregateFunction =>
    element(by.css(`[e2e="${aggregateFunction}"]`)),
  filterWindow: {
    addFilter: tableName =>
      element(by.css(`[e2e="filter-add-btn-${tableName}"]`)),
    numberInputUpgraded: element(
      by.css('input[e2e="designer-number-filter-input"]')
    ),
    numberInput: element(by.xpath("(//input[@type='number'])[2]")),
    columnDropDown: element(by.css('input[e2e="filter-autocomplete-input"]')),
    columnNameDropDownItem: columnName =>
      element(
        by.xpath(`(//mat-option/span[contains(text(),"${columnName}")])[1]`)
      ),
    deleteFields: element.all(by.css('[fonticon="icon-close"]')),

    date: {
      presetDropDown: element(by.xpath('//span[contains(text(),"Custom")]')),
      presetDropDownItem: presetName =>
        element(by.xpath(`//mat-option[contains(text(),"${presetName}")]`))
    },
    string: {
      operator: element(by.css('[e2e="filter-string-select"]')),
      operatorDropDownItem: operator =>
        element(by.css(`mat-option[e2e="filter-string-option-${operator}"]`)),
      input: element(
        by.xpath(`(//input[contains(@id,"mat-input-")])[position()=last()]`)
      ),
      isInIsNotInInput: element(
        by.xpath(`//input[@e2e="designer-filter-string-input"]`)
      )
    },
    number: {
      operator: element(
        by.css('mat-select[e2e="filter-number-operator-select"]')
      ),
      operatorDropDownItem: operator =>
        element(
          by.css(`mat-option[e2e="filter-number-operator-option-${operator}"]`)
        ),
      input: element(by.xpath('//input[@type="number"]'))
    }
  },
  pivot: {
    addFieldButton: fieldName =>
      element(
        by.xpath(
          `(//div[contains(text(), '${fieldName}')]/following-sibling::*)[1]`
        )
      ),
    expandSelectedFieldPropertiesButton: fieldName =>
      element(
        by.xpath(
          `(//div[contains(text(), '${fieldName}')]/preceding-sibling::*)[1]`
        )
      ),
    groupIntervalDropDown: element(
      by.xpath(`//mat-label[contains(text(),"Group interval")]/parent::label`)
    ),
    groupIntervalDrop: id =>
      element(by.xpath(`//mat-select[@aria-labelledby="${id}"]`)),
    groupIntervalDropDownElement: groupIntervalName =>
      element(
        by.xpath(
          `//span[@class="mat-option-text" and contains(text(), '${groupIntervalName}')]`
        )
      ),
    addFilter: filterObject => addFilter(filterObject)
  },
  chart: {
    addFieldButton: fieldName =>
      element(by.css(`[e2e="designer-add-btn-${fieldName}"]`)),
    expandSelectedFieldPropertiesButton: fieldName =>
      element(
        by.xpath(
          `(//div[contains(text(), '${fieldName}')]/preceding-sibling::*)[1]`
        )
      ),
    groupIntervalDropDown: element(
      by.xpath(`//mat-select[@placeholder='Group interval']`)
    ),
    groupIntervalDropDownElement: groupIntervalName =>
      element(
        by.xpath(
          `//span[@class="mat-option-text" and contains(text(), '${groupIntervalName}')]`
        )
      ),
    addFilter: filterObject => addFilter(filterObject),
    getAxisLabel: (axisLabel, axis) =>
      element(
        by.xpath(
          `(//chart/descendant::*[name()="svg"])[position()=last()]/descendant::*[name()="g" and contains(@class,"highcharts-${axis}")]/descendant::*[name()="text"]/descendant::*[contains(text(),"${axisLabel}")]`
        )
      ),
    groupBy: element(
      by.xpath(
        `(//chart/descendant::*[name()="svg"])[position()=last()]/descendant::*[@class="highcharts-legend"]`
      )
    )
  }
};

const addFilter = filterObject => {};
