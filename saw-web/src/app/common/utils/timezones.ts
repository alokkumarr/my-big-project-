import * as moment from 'moment-timezone';
import * as pad from 'lodash/padStart';
import { timezoneData } from './timezone-data';

const currentDate = Date.now();
export const timezones = timezoneData
  .map(({ text, utc }) => {
    const name = utc[0];
    const timezoneLabel = text.replace(/\(.*?\)\s*/, '');

    /* Calculate fresh offsets instead of depending on data to
       get correct data according to DST changes */
    const offset = -moment.tz.zone(name).utcOffset(currentDate);
    const offsetMinutes = pad(Math.abs(offset % 60), 2, '0');
    const offsetHours = pad(Math.abs((offset - (offset % 60)) / 60), 2, '0');
    const offsetSign = offset < 0 ? '-' : '+';

    const label = `(UTC${offsetSign}${offsetHours}:${offsetMinutes}) ${timezoneLabel}`;

    return {
      name,
      utcOffset: offset,
      label
    };
  })
  .sort((a, b) => a.utcOffset - b.utcOffset);
