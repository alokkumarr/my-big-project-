var appRoot = require('app-root-path');

const TEST_BASE_DIR = appRoot + '/e2e/v2/tests/';

const SMOKE = ['login-logout/login.test.js', 'login-logout/logout.test.js'];

const SANITY = [...SMOKE, 'ForgotPassword/preResetPwd.test.js'];
// ALL the test with minimal data set so that all the functionality is touched
const CRITICAL = [
  ...SANITY,
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
  'pivots/UpdateAndDeletePivot.test.js',
  'schedule/createDeleteSchedule.test.js',
  'alert/addAlert.test.js',
  'alert/editDeleteAlerts.test.js'
];
// All tests which were executed in critical with larger data set
const REGRESSION = [...CRITICAL];
// Used for local development and testing some implementations
const DEVELOPMENT = [
  //'dummy/dummyDevelopmentTests1.js',
  //'dummy/dummyDevelopmentTests2.js'
  ];

module.exports = {
  SMOKE: SMOKE.map(path => TEST_BASE_DIR + path),
  SANITY: SANITY.map(path => TEST_BASE_DIR + path),
  CRITICAL: CRITICAL.map(path => TEST_BASE_DIR + path),
  REGRESSION: REGRESSION.map(path => TEST_BASE_DIR + path),
  DEVELOPMENT: DEVELOPMENT.map(path => TEST_BASE_DIR + path)
};
