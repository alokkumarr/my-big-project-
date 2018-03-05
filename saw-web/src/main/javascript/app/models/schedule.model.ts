import { RepeatOnDaysOfWeek } from './repeat-on-days-of-week.model';

export interface Schedule {
  repeatOnDaysOfWeek: RepeatOnDaysOfWeek;
  repeatInterval:     number;
  repeatUnit:         string;
}
