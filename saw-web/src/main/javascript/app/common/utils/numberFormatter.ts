import * as fpPipe from 'lodash/fp/pipe';
import * as split from 'lodash/split';
import * as replace from 'lodash/replace';
import * as join from 'lodash/join';

import { Format } from '../../models'
const commaRegex = /\B(?=(\d{3})+(?!\d))/g;

export function formatNumber(number, format: Format) {
  if (!format) {
    return number;
  }
  return getFormatter(format)(number);
}

export function getFormatter(format: Format) {
  if (!format) {
    return {};
  }
  return fpPipe(
    number => applyPrecisionIfNeeded(format, number),
    numberString => applyCommasIfNeeded(format, numberString),
    numberString => applyCurrencyIfNeeded(format, numberString)
  )
}

export function applyPrecisionIfNeeded(format, number) {
  const precision = format.precision;
  if (precision) {
    return Number(number).toFixed(precision);
  }
  return number.toString();
}

export function applyCommasIfNeeded(format, numberString) {
  if (format.comma) {
    return fpPipe(

      number => split(number, '.'),

      ([integerPart, decimalPart]) => {
        const integerWithCommas = replace(integerPart, commaRegex, ',');
        return [integerWithCommas, decimalPart];
      },

      ([integerPart, decimalPart]) => {
        if (decimalPart) {
          return join([integerPart, decimalPart], '.');
        }
        return integerPart;
      }

    )(numberString)
  }
  return numberString;
}

export function applyCurrencyIfNeeded(format, numberString) {
  const {
    type,
    currency,
    currencySymbol
  } = format;

  if (currency && currencySymbol) {
    return join([currencySymbol, numberString], ' ');
  }
  return numberString;
}
