import map from 'lodash/fp/map';
import range from 'lodash/range';
import memoize from 'lodash/memoize';
import truncate from 'lodash/fp/truncate';
import flatMap from 'lodash/fp/flatMap';
import pipe from 'lodash/fp/pipe';
import chunk from 'lodash/fp/chunk';
import head from 'lodash/fp/head';
import last from 'lodash/fp/last';
import get from 'lodash/fp/get';
import curry from 'lodash/fp/curry';

export function chartDateService() {
  // views
  const VIEWS = {
    DAILY: 'daily-view',
    WEEKLY: 'weekly-view',
    MONTHLY: 'monthly-view',
    QUARTERLY: 'quarterly-view'
  };

  const MAPPERS = {
    [VIEWS.DAILY]: dailyViewMapper,
    [VIEWS.WEEKLY]: weeklyViewMapper,
    [VIEWS.MONTHLY]: monthlyViewMapper,
    [VIEWS.QUARTERLY]: quarterlyViewMapper
  };

  // memoize the function so we don't recalculate everytime we use a different mapper
  const cachedGetViewCategories = memoize(curry(getViewCategories));

  return {
    CATEGORY_VIEWS: VIEWS,
    getDailyViewCategories: cachedGetViewCategories(VIEWS.DAILY),
    getWeeklyViewCategories: cachedGetViewCategories(VIEWS.WEEKLY),
    getMonthlyViewCategories: cachedGetViewCategories(VIEWS.MONTHLY),
    getQuarterlyViewCategories: cachedGetViewCategories(VIEWS.QUARTERLY),
    getViewCategories: cachedGetViewCategories
  };

  /**
   *
   * @param view: string = 'daily' | 'weekly' | 'monthly' | 'quarterly'
   * @param isLeapYear boolean
   * @return [Object] array of categories used in highCharts
   */
  function getViewCategories(view, isLeapYear) {
    return MAPPERS[view](getMonths(isLeapYear));
  }

  // display the day numbers, and the name of the month with every first day
  function dailyViewMapper(months) {
    return flatMap(month => {
      return map(day =>
        day === 1 ?
          `${month.name} - 1` : day
      )(month.days);
    })(months);
  }

  // display: MonthName firstDay - lastDay (-of week)
  function weeklyViewMapper(months) {
    return flatMap(month => {
      return pipe(
        chunk(7),
        map(week => `${month.name} ${head(week)} - ${last(week)}`)
      )(month.days);
    })(months);
  }

  // display the first 3 letters of the month
  function monthlyViewMapper(months) {
    return pipe(
      map(get('name')),
      map(truncate({length: 3, omission: ''}))
    )(months);
  }

  // display: firstMonth - lastMonth (of the quarter)
  function quarterlyViewMapper(months) {
    return pipe(
      map(get('name')),
      // 4 quarter chunks of 3 months
      chunk(3),
      map(quarter => `${head(quarter)} - ${last(quarter)}`)
    )(months);
  }

  function getMonths(isLeapYear) {
    const _30MonthDays = range(1, 30);
    const _31MonthDays = range(1, 31);
    const _februaryDays = range(1, 28);
    const _februaryLeapYearDays = range(1, 29);

    return [{
      name: 'January',
      days: _31MonthDays
    }, {
      name: 'February',
      days: isLeapYear ? _februaryLeapYearDays : _februaryDays
    }, {
      name: 'March',
      days: _31MonthDays
    }, {
      name: 'April',
      days: _30MonthDays
    }, {
      name: 'May',
      days: _31MonthDays
    }, {
      name: 'June',
      days: _30MonthDays
    }, {
      name: 'July',
      days: _31MonthDays
    }, {
      name: 'August',
      days: _31MonthDays
    }, {
      name: 'September',
      days: _30MonthDays
    }, {
      name: 'October',
      days: _31MonthDays
    }, {
      name: 'November',
      days: _30MonthDays
    }, {
      name: 'December',
      days: _31MonthDays
    }];
  }
}
