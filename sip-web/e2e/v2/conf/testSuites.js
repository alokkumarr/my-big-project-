var appRoot = require('app-root-path');

const TEST_BASE_DIR = appRoot + '/e2e/v2/tests/';

const SMOKE = ['login-logout/login.test.js', 'login-logout/logout.test.js'];

const SANITY = [...SMOKE, 'ForgotPassword/preResetPwd.test.js'];
// ALL the test with minimal data set so that all the functionality is touched
const CRITICAL = [
  ...SANITY,
  TEST_BASE_DIR + 'analyze/analyze.test.js',
  TEST_BASE_DIR + 'bis/activateAndDeActivateRoute.test.js',
  TEST_BASE_DIR + 'bis/activateDeActivateChannel.test.js',
  TEST_BASE_DIR + 'bis/createAndDeleteChannel.test.js',
  TEST_BASE_DIR + 'bis/createAndDeleteRoute.test.js',
  TEST_BASE_DIR + 'bis/scheduleRoute.test.js',
  TEST_BASE_DIR + 'bis/updateAndDeleteChannel.test.js',
  TEST_BASE_DIR + 'change-password/changepassword.test.js',
  TEST_BASE_DIR + 'charts/createAndDelete.test.js',
  TEST_BASE_DIR + 'charts/editAndDelete.test.js',
  TEST_BASE_DIR + 'charts/forkEditAndDelete.test.js',
  TEST_BASE_DIR + 'charts/preview.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/actualVsTargetKPI.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/charts.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/chartsGlobalFilter.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/esReport.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/esReportGlobalFilter.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/pivot.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/pivotWithGlobalFilter.test',
  TEST_BASE_DIR + 'create-delete-dashboards/snapshotKPI.test.js',
  TEST_BASE_DIR + 'ForgotPassword/preResetPwd.test.js',
  TEST_BASE_DIR + 'pivots/pivotFilters.test.js',
  TEST_BASE_DIR + 'privilege/privilege.test.js',
  TEST_BASE_DIR + 'prompt-filter/chartPromptFilters.test.js',
  TEST_BASE_DIR + 'prompt-filter/esReportPromptFilters.test.js',
  TEST_BASE_DIR + 'prompt-filter/pivotPrompt.test.js',
  TEST_BASE_DIR + 'prompt-filter/reportPromptFilters.test.js',
  TEST_BASE_DIR + 'reports/createAndDeleteReport.test.js',
  TEST_BASE_DIR + 'charts/topNForCharts.test.js',
  TEST_BASE_DIR + 'charts/bottomNForCharts.test.js',
  TEST_BASE_DIR + 'charts/SortingWithCharts.test.js',
  TEST_BASE_DIR + 'charts/AggregateWithCharts.test.js',
  TEST_BASE_DIR + 'pivots/PivotsWithAggregate.test.js',
  TEST_BASE_DIR + 'reports/AggregateWithESReport.test.js',
  TEST_BASE_DIR + 'reports/AggregateWithESReport.test.js',
  TEST_BASE_DIR + 'reports/AggregateWithDLReport.test.js',
  TEST_BASE_DIR + 'charts/PaginationInExecutePage.test.js',
  TEST_BASE_DIR + 'reports/EsReportPaginationInExecutePage.test.js',
  TEST_BASE_DIR + 'reports/EsReportPaginationInPreviewPage.test.js',
  TEST_BASE_DIR + 'reports/DLReportPaginationInExecutePage.test.js',
  TEST_BASE_DIR + 'reports/DLReportPaginationInPreviewPage.test.js',
  TEST_BASE_DIR + 'geolocation/createAndDelete.test.js',
  TEST_BASE_DIR + 'charts/forkFromCardViewDelete.test.js',
  TEST_BASE_DIR + 'charts/SortingWithChartsDesc.test.js',
  TEST_BASE_DIR + 'schedule/scheduleDLReports.test.js',
  'analyze/analyze.test.js',
  'bis/scheduleRoute.test.js',
  'change-password/changepassword.test.js',
  'charts/createAndDelete.test.js',
  'charts/editAndDelete.test.js',
  'charts/forkEditAndDelete.test.js',
  'charts/preview.test.js',
  'create-delete-dashboards/actualVsTargetKPI.test.js',
  'create-delete-dashboards/charts.test.js',
  'create-delete-dashboards/chartsGlobalFilter.test.js',
  'bis/activateAndDeActivateRoute.test.js',
  'bis/activateDeActivateChannel.test.js',
  'bis/createAndDeleteChannel.test.js',
  'bis/createAndDeleteRoute.test.js',
  'bis/updateAndDeleteChannel.test.js',
  'create-delete-dashboards/esReport.test.js',
  'create-delete-dashboards/esReportGlobalFilter.test.js',
  'create-delete-dashboards/pivot.test.js',
  'create-delete-dashboards/pivotWithGlobalFilter.test',
  'create-delete-dashboards/snapshotKPI.test.js',
  'ForgotPassword/preResetPwd.test.js',
  'pivots/pivotFilters.test.js',
  'privilege/privilege.test.js',
  'prompt-filter/chartPromptFilters.test.js',
  'prompt-filter/esReportPromptFilters.test.js',
  'prompt-filter/pivotPrompt.test.js',
  'prompt-filter/reportPromptFilters.test.js',
  'reports/createAndDeleteReport.test.js',
  'charts/topNForCharts.test.js',
  'charts/bottomNForCharts.test.js',
  'charts/SortingWithCharts.test.js',
  'bis/APIPullRouteSchedule.test.js',
  'charts/AggregateWithCharts.test.js',
  'pivots/PivotsWithAggregate.test.js',
  'reports/AggregateWithESReport.test.js',
  'reports/AggregateWithESReport.test.js',
  'reports/AggregateWithDLReport.test.js',
  'charts/PaginationInExecutePage.test.js',
  'reports/EsReportPaginationInExecutePage.test.js',
  'reports/EsReportPaginationInPreviewPage.test.js',
  'reports/DLReportPaginationInExecutePage.test.js',
  'reports/DLReportPaginationInPreviewPage.test.js',
  'geolocation/createAndDelete.test.js',
  'charts/forkFromCardViewDelete.test.js',
  'charts/SortingWithChartsDesc.test.js',
  'bis/APIPullChannelCreateDelete.test.js',
  'bis/APIPullChannelUpdateDelete.test.js',
  'bis/APIPullChannelActivateDeActivate.test.js',
  'bis/APIPullRouteCreateDelete.test.js',
  'bis/APIPullRouteUpdateDelete.test.js',
  'reports/DLReportQuery.test.js',
  'pivots/UpdateAndDeletePivot.test.js'
];
// All tests which were executed in critical with larger data set
const REGRESSION = [...CRITICAL];
// Used for local development and testing some implementations
const DEVELOPMENT = [
  //TEST_BASE_DIR + 'dummy/dummyDevelopmentTests1.js',
  //TEST_BASE_DIR + 'dummy/dummyDevelopmentTests2.js'
];

module.exports = {
  SANITY,
  CRITICAL,
  REGRESSION,
  DEVELOPMENT
  //'dummy/dummyDevelopmentTests1.js',
  //'dummy/dummyDevelopmentTests2.js'
};

module.exports = {
  SMOKE: SMOKE.map(path => TEST_BASE_DIR + path),
  SANITY: SANITY.map(path => TEST_BASE_DIR + path),
  CRITICAL: CRITICAL.map(path => TEST_BASE_DIR + path),
  REGRESSION: REGRESSION.map(path => TEST_BASE_DIR + path),
  DEVELOPMENT: DEVELOPMENT.map(path => TEST_BASE_DIR + path)
};
