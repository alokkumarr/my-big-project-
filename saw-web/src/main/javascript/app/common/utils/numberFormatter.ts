import * as fpPipe from 'lodash/fp/pipe';
import * as isFinite from 'lodash/isFinite';
import * as split from 'lodash/split';
import * as startsWith from 'lodash/startsWith';
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

export function getFormatter(format: Format | string) {
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
  if (isFinite(precision)) {
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
    currency,
    currencySymbol
  } = format;

  if (currency && currencySymbol) {
    // If it's a negative number, remove the negative sign
    // and place it before currency.
    return startsWith(numberString, '-') ?
      join(['-', currencySymbol, numberString.slice(1)], '') :
      join([currencySymbol, numberString], '');
  }
  return numberString;
}

export function isFormatted(format: Format) {
  if (format.comma) {
    return true;
  }
  if (format.currency) {
    return true;
  }
  if (isFinite(format.precision)) {
    return true;
  }
  return false;
}
