var appRoot = require('app-root-path');

const TEST_BASE_DIR = appRoot + '/e2e/v2/tests/';

const SMOKE = [
  TEST_BASE_DIR + 'login-logout/login.test.js',
  TEST_BASE_DIR + 'login-logout/logout.test.js'
];

const SANITY = [
  ...SMOKE,
  TEST_BASE_DIR + 'reports/createAndDeleteReport.test.js',
  TEST_BASE_DIR + 'charts/createAndDeleteCharts.test.js',
  TEST_BASE_DIR + 'analyze/analyze.test.js'
];
// ALL the test with minimal data set so that all the functionality is touched
const CRITICAL = [
  ...SANITY,
  TEST_BASE_DIR + 'privilege/privilege.test.js',
  TEST_BASE_DIR + 'analyze.test.js',
  TEST_BASE_DIR + 'charts/applyFiltersToCharts.js',
  TEST_BASE_DIR + 'charts/previewForCharts.test.js',
  TEST_BASE_DIR + 'charts/createAndDelete.test.js',
  TEST_BASE_DIR + 'charts/editAndDelete.test.js',
  TEST_BASE_DIR + 'charts/forkEditAndDelete.test.js',
  TEST_BASE_DIR + 'promptFilter/chartPromptFilters.test.js',
  TEST_BASE_DIR + 'promptFilter/esReportPromptFilters.test.js',
  TEST_BASE_DIR + 'promptFilter/pivotPromptFilters.test.js',
  TEST_BASE_DIR + 'promptFilter/reportPromptFilters.test.js',
  TEST_BASE_DIR + 'pivots/pivotFilters.test.js',
  TEST_BASE_DIR + 'observe/createAndDeleteDashboardWithCharts.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/charts.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/snapshotKPI.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/actualVsTargetKPI.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/pivot.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/chartsGlobalFilter.test.js',
  TEST_BASE_DIR + 'observe/dashboardGlobalFilterWithPivot.test.js',
  TEST_BASE_DIR + 'observe/dashboardGlobalFilterWithESReport.test.js',
  TEST_BASE_DIR + 'create-delete-dashboards/esReport.test.js'
];
// All tests which were executed in critical with larger data set
const REGRESSION = [...CRITICAL];
// Used for local development and testing some implementations
const DEVELOPMENT = [
  //TEST_BASE_DIR + 'dummy/dummyDevelopmentTests1.js',
  //TEST_BASE_DIR + 'dummy/dummyDevelopmentTests2.js'
];

module.exports = {
  SMOKE,
  SANITY,
  CRITICAL,
  REGRESSION,
  DEVELOPMENT
};
