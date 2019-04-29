import { RepeatOnDaysOfWeek } from './repeat-on-days-of-week.model';

export interface Schedule {
  repeatOnDaysOfWeek?: RepeatOnDaysOfWeek;
  repeatInterval?: number;
  repeatUnit?: string;
  categoryId?: number;
  groupName?: string;
  jobName?: string;
  scheduleState: 'new' | 'exist' | 'delete';
  activeRadio?: string;
  activeTab?: string;
  analysisID?: string;
  analysisName?: string;
  cronExpression?: string;
  description?: string;
  emailList?: string[];
  ftp?: string[];
  s3?: string[];
  zip: boolean;
  fileType?: string;
  endDate?: string;
  timezone?: string;
  metricName?: string;
  type?: string;
  userFullName?: string;
  jobScheduleTime?: string;
  categoryID?: number;
  jobGroup?: string;
}
