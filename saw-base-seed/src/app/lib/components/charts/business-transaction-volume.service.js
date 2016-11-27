import pipe from 'lodash/fp/pipe';
import get from 'lodash/fp/get';
import map from 'lodash/fp/map';
import curry from 'lodash/fp/curry';
import zipObject from 'lodash/fp/zipObject';
import split from 'lodash/split';
import parseInt from 'lodash/parseInt';
import zip from 'lodash/zip';
import spread from 'lodash/spread';
import truncate from 'lodash/truncate';

export function businessTransactionVolumeService($http) {
  'ngInject';

  const DAYS_TO_WEEK = 6;
  const MONTHS_TO_QUARTER = 2;

  // API property and path names
  const DATE_PROPERTY = 'key_as_string';
  const VALUE_PROPERTY = 'doc_count';
  const DATA_PATH = 'data.0.aggregations.filtered.split_by.buckets';

  const VIEWS = {
    DAILY: 'day',
    WEEKLY: 'week',
    MONTHLY: 'month',
    QUARTERLY: 'quarter'
  };

  // transform dates to special strings based on the design
  const TRANSFORMERS = {
    [VIEWS.DAILY]: dateToDailyView,
    [VIEWS.WEEKLY]: dateToWeeklyView,
    [VIEWS.MONTHLY]: dateToMonthlyView,
    [VIEWS.QUARTERLY]: dateToQuarterlyView
  };

  return {
    getChartData,
    VIEWS
  };

  function getChartData(endpoint) {
    return $http.get(`/api/transactionVolume/${endpoint}`)
      .then(response => {
        return pipe(
          mapResponseToDateValueData(),
          mapDateValueDataToChartData(endpoint)
        )(response);
      });
  }

  /**
   * Map the data from the api, to usable data
   */
  function mapResponseToDateValueData() {
    return pipe(
      response => {
        return get(DATA_PATH)(response);
      },
      map(bucket => {
        const date = split(bucket[DATE_PROPERTY], '-');
        return {
          date: {
            day: parseInt(date[1]),
            month: parseInt(date[0]),
            year: parseInt(date[2])
          },
          value: bucket[VALUE_PROPERTY]
        };
      })
    );
  }

  /**
   * Map the array of dateValueObjects to 2 arrays,
   * - one for the category data for HighCharts based on the view type
   * - the other is simply the value
   * @param view
   */
  function mapDateValueDataToChartData(view) {
    return pipe(
      map(curry(dateValueObjToZippableChartData)(view)),
      spread(zip),
      zipObject(['categories', 'series'])
    );
  }

  // transform the dateValueObj to an array of pairs so it can be zipped
  function dateValueObjToZippableChartData(view, dateValueObj) {
    // transforms date into 1 of the 4 views: daily, weekly, quarterly
    const transformedDate = TRANSFORMERS[view](dateValueObj.date);

    return [transformedDate, dateValueObj.value];
  }

  /**
   * dailyView: just show the day
   * if it is the first day of the month, show the month as well
   */
  function dateToDailyView(date) {
    const day = date.day;
    const month = getMonthName(date.month);

    return day === 1 ? `${month} - ${day}` : day;
  }

  /**
   * weeklyView:
   * with no transition: monthName firstDayOfWeek - lastDayOfWeek
   * with transition: currentMonthName firstDayOfWeek -  nextMonthName lastDayOfWeek
   */
  function dateToWeeklyView(date) {
    const transition = isTransitionWeek(date);
    const currentMonth = getMonthName(date.month);
    const nextMonth = getMonthName(date.month + 1);
    const lastDayOfWeek = date.day + DAYS_TO_WEEK;
    const lastDayOfWeekNextMonth = lastDayOfWeek % getMonthDays(date);

    return transition ?
      `${currentMonth} ${date.day} - ${nextMonth} ${lastDayOfWeekNextMonth}` :
      `${currentMonth} ${date.day} - ${lastDayOfWeek}`;
  }

  /**
   * monthlyView: just show the months
   * if it is the first month, show the year as well
   */
  function dateToMonthlyView(date) {
    const monthIndex = date.month;
    // get only the first 3 letters of months
    const month = truncate(getMonthName(monthIndex), {length: 3, omission: ''});
    const year = date.year;

    return monthIndex === 1 ? `${year} - ${month}` : month;
  }

  /**
   * quarterlyView: firstMonth - lastMonth (of quarter)
   * for example: January - March
    */
  function dateToQuarterlyView(date) {
    const monthIndex = date.month;

    return `${getMonthName(monthIndex)} - ${getMonthName(monthIndex + MONTHS_TO_QUARTER)}`;
  }

  function isTransitionWeek(date) {
    const daysOfTheMonth = getMonthDays(date);
    const lastDayOfWeek = date.day + DAYS_TO_WEEK;

    return lastDayOfWeek > daysOfTheMonth;
  }

  function getMonthName(monthIndex) {
    // we use year one, as it doesn't matter whether it is a leap year or not
    return getModulo(monthIndex - 1, getMonths(1)).name;
  }

  function getMonthDays(date) {
    return getModulo(date.month - 1, getMonths(date.year)).days;
  }

  /**
   * circular indexing
   * get an item from an array
   * @example: getModulo(4, ['a', 'b', 'c']) ------->  b
   * @param index
   * @param array
   * @returns {*}
   */
  function getModulo(index, array) {
    return array[index % (array.length)];
  }

  function getMonths(year) {
    const leapYear = isLeapYear(year);

    return [{
      name: 'January',
      days: 31
    }, {
      name: 'February',
      days: leapYear ? 29 : 28
    }, {
      name: 'March',
      days: 31
    }, {
      name: 'April',
      days: 30
    }, {
      name: 'May',
      days: 31
    }, {
      name: 'June',
      days: 30
    }, {
      name: 'July',
      days: 31
    }, {
      name: 'August',
      days: 31
    }, {
      name: 'September',
      days: 30
    }, {
      name: 'October',
      days: 31
    }, {
      name: 'November',
      days: 30
    }, {
      name: 'December',
      days: 31
    }];
  }

  function isLeapYear(year) {
    return ((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0);
  }
}
