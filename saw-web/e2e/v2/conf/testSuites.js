var appRoot = require('app-root-path');

const TEST_BASE_DIR = appRoot + '/e2e/v2/tests/';

const SMOKE = [
  TEST_BASE_DIR + 'login-logout/login.test.js',
  TEST_BASE_DIR + 'login-logout/logout.test.js'
];

const SANITY = [
  ...SMOKE,
  TEST_BASE_DIR + 'reports/createAndDeleteReport.test.js',
  TEST_BASE_DIR + 'charts/createAndDeleteCharts.test.js'
];
// ALL the test with minimal data set so that all the functionality is touched
const CRITICAL = [
  ...SANITY,
  TEST_BASE_DIR + 'privilege/privilege.test.js',
  TEST_BASE_DIR + 'analyze.test.js',
  TEST_BASE_DIR + 'charts/applyFiltersToCharts.js',
  TEST_BASE_DIR + 'charts/previewForCharts.test.js',
  TEST_BASE_DIR + 'charts/editAndDeleteCharts.test.js',
  TEST_BASE_DIR + 'charts/forkAndEditAndDeleteCharts.test.js',
  TEST_BASE_DIR + 'promptFilter/chartPromptFilters.test.js',
  TEST_BASE_DIR + 'promptFilter/esReportPromptFilters.test.js',
  TEST_BASE_DIR + 'promptFilter/pivotPromptFilters.test.js',
  TEST_BASE_DIR + 'promptFilter/reportPromptFilters.test.js',
  TEST_BASE_DIR + 'pivots/pivotFilters.test.js',
  TEST_BASE_DIR + 'observe/createAndDeleteDashboardWithCharts.test.js',
  TEST_BASE_DIR + 'observe/createAndDeleteDashboardWithESReport.test.js',
  TEST_BASE_DIR + 'observe/createAndDeleteDashboardWithSnapshotKPI.test.js',
  TEST_BASE_DIR +
    'observe/createAndDeleteDashboardWithActualVsTargetKpi.test.js',
  TEST_BASE_DIR + 'observe/createAndDeleteDashboardWithPivot.test.js',
  TEST_BASE_DIR + 'observe/dashboardGlobalFilter.test.js',
  TEST_BASE_DIR + 'observe/dashboardGlobalFilterWithPivot.test.js',
  TEST_BASE_DIR + 'observe/dashboardGlobalFilterWithESReport.test.js'
];
// All tests which were executed in critical with larger data set
const REGRESSION = [...CRITICAL];
// Used for local development and testing some implementations
const DEVELOPMENT = [TEST_BASE_DIR + 'analyze/analyze.test.js'];

module.exports = {
  SMOKE,
  SANITY,
  CRITICAL,
  REGRESSION,
  DEVELOPMENT
};
