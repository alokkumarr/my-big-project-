class Filter {
  constructor(options) {
    const defaults = {
      columnName: 'NULL',
      preset: 'NULL', // option for date filters
      from: 'NULL', //date range
      to: 'NULL', // date range
      numberOperator: 'NULL', // option for numbers
      moreThen: 'NULL', // value if between operator selected
      lessThen: 'NULL', // value if between operator selected
      stringOperator: 'NULL', // option for strings
      prompt: false, // prompt checkbox
      filtersAreTrue: 'All' // toggle All/Any of the filters are true
    };
    const populated = Object.assign(defaults, options);
    for (const key in populated) {
      if (populated.hasOwnProperty(key)) {
        this[key] = populated[key];
      }
    }
  }
}

module.exports = Filter;
