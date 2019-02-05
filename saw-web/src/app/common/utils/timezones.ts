import * as moment from 'moment-timezone';
import * as pad from 'lodash/padStart';
import { timezoneData } from './timezone-data';
import * as forEach from 'lodash/forEach';
interface Timezone {
  name: string;
  utcOffset: number;
  label: string;
}

const currentDate = Date.now();
export const timezones: Timezone[] = timezoneData
  .map(({ text, utc }) => {
    let zoneSelected = utc[0];
    forEach(utc, zone => {
      if (zone === moment.tz.guess()) {
        zoneSelected = zone;
      }
    });
    const name = zoneSelected;
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
