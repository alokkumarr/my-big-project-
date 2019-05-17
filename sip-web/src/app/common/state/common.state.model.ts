export interface MenuItem {
  id: string | number;
  data?: any;
  url?: string[];
  name: string;
  children: MenuItem[];
}

export interface Menu {
  items: MenuItem[];
}

export interface AnalysisSchedule {
  activeRadio: string;
  activeTab: string;
  analysisID: string;
  analysisName: string;
  categoryID: string;
  cronExpression: string;
  description: string;
  emailList: [];
  endDate: string;
  fileType: string;
  ftp: string;
  jobGroup: string;
  jobName: string;
  jobScheduleTime: string;
  metricName: string;
  s3: [];
  timezone: string;
}

export interface Jobs {
  jobDetails: AnalysisSchedule;
  jobStatus: string;
  lastFiredTime: string;
  nextFireTime: string;
  scheduleTime: string;
}

export interface CommonStateModel {
  analyzeMenu: Menu;
  observeMenu: Menu;
  adminMenu: Menu;
  metrics: { [id: string]: any };
  jobs: Jobs;
}
