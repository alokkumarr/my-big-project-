import * as round from 'lodash/round';
import * as some from 'lodash/some';
import * as find from 'lodash/find';
import * as get from 'lodash/get';
import moment from 'moment';
import {
  CUSTOM_DATE_PRESET_VALUE,
  LESS_THAN_A_WEEK_DATE_PRESETS,
  LESS_THAN_4_DAYS_DATE_PRESETS,
  DATE_TYPES
} from '../consts';
import { FilterModel, QueryDSL, Filter, ArtifactColumnDSL } from '../types';

export interface ValidAnswer {
  willRequestBeValid: true;
}

type ReasonForInvalid =
  | 'too-much-data__from-minute-aggregation'
  | 'too-much-data__from-second-aggregation';

export interface InvalidAnswer {
  willRequestBeValid: false;
  reason: ReasonForInvalid;
  warning: {
    shouldShow: boolean;
    title: string;
    msg: string;
  };
}

export type WillRequestBeValidAnswer = ValidAnswer | InvalidAnswer;

export function isRequestValid(
  sipQuery: QueryDSL,
  analysisType: string
): WillRequestBeValidAnswer {
  return willRequestHaveTooMuchData(sipQuery);
}

/**
 * This service can decide based on the sipQuery if the request to be launched will be valid
 * it should also be able to show dialogs, or stop the request silently

 */

function willRequestHaveTooMuchData(
  sipQuery: QueryDSL
): WillRequestBeValidAnswer {
  const fieldWithMinuteLevelAggregation = getXFieldWithAggregation(
    sipQuery,
    'minute'
  );
  const fieldWithSecondLevelAggregation = getXFieldWithAggregation(
    sipQuery,
    'second'
  );
  const willHaveTooMuchDataFromMinuteAggregation =
    fieldWithMinuteLevelAggregation && !hasDateFilterForLessThenAWeek(sipQuery);
  const willHaveTooMuchDataFromSecondAggregation =
    fieldWithSecondLevelAggregation && !hasDateFilterForLessThen4Days(sipQuery);

  if (willHaveTooMuchDataFromMinuteAggregation) {
    const { alias, displayName } = fieldWithMinuteLevelAggregation;
    return {
      willRequestBeValid: false,
      reason: 'too-much-data__from-minute-aggregation',
      warning: {
        shouldShow: true,
        title: 'Minute level aggregation will contain too much data.',
        msg: `Select a filter for field: ${alias ||
          displayName} that is less than 1 week.`
      }
    };
  }
  if (willHaveTooMuchDataFromSecondAggregation) {
    const { alias, displayName } = fieldWithSecondLevelAggregation;
    return {
      willRequestBeValid: false,
      reason: 'too-much-data__from-second-aggregation',
      warning: {
        shouldShow: true,
        title: 'Second level aggregation will contain too much data.',
        msg: `Select a filter for field: ${alias ||
          displayName} that is less than 4 days.`
      }
    };
  }
  return { willRequestBeValid: true };
}

function getXFieldWithAggregation(
  sipQuery: QueryDSL,
  aggregation: string
): ArtifactColumnDSL {
  // TODO this only works for chart
  const fields = get(sipQuery, 'artifacts.0.fields');
  return find(
    fields,
    ({ area, type, groupInterval }: ArtifactColumnDSL) =>
      area === 'x' && DATE_TYPES.includes(type) && groupInterval === aggregation
  );
}

function hasDateFilterForLessThenAWeek(sipQuery) {
  return some(
    sipQuery.filters,
    ({ type, model, isRuntimeFilter }: Filter) =>
      DATE_TYPES.includes(type) &&
      !isRuntimeFilter &&
      filterModelIsForLessThanAWeek(model)
  );
}

function hasDateFilterForLessThen4Days(sipQuery) {
  return some(
    sipQuery.filters,
    ({ type, model, isRuntimeFilter }: Filter) =>
      DATE_TYPES.includes(type) &&
      !isRuntimeFilter &&
      filterModelIsForLessThan4Days(model)
  );
}

function filterModelIsForLessThanAWeek(filterModel: FilterModel) {
  if (filterModel.preset === CUSTOM_DATE_PRESET_VALUE) {
    const { gte, lte } = filterModel;
    const days = round(moment.duration(moment(lte).diff(moment(gte))).asDays());
    return days <= 7;
  }

  if (LESS_THAN_A_WEEK_DATE_PRESETS.includes(filterModel.preset)) {
    return true;
  }
  return false;
}

function filterModelIsForLessThan4Days(filterModel: FilterModel) {
  if (filterModel.preset === CUSTOM_DATE_PRESET_VALUE) {
    const { gte, lte } = filterModel;
    const days = round(moment.duration(moment(lte).diff(moment(gte))).asDays());
    return days <= 4;
  }

  if (LESS_THAN_4_DAYS_DATE_PRESETS.includes(filterModel.preset)) {
    return true;
  }
  return false;
}
