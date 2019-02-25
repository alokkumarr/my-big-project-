'use strict';

const ES_REPORT = 'esReport'; // ES
const PIVOT = 'pivot';
const REPORT = 'report'; // DL
const CHART = 'chart';
const API_ROUTES = {
  AUTH: '/saw/security/doAuthenticate',
  ANALYSIS: '/saw/services/analysis',
  CATEGORIES_FETCH: '/saw/security/auth/admin/cust/manage/categories/fetch',
  SEMANTIC: '/saw/services/internal/semantic/md?projectId=workbench',
  ROLES: '/saw/security/auth/admin/cust/manage/roles/add',
  USERS: '/saw/security/auth/admin/cust/manage/users/add',
  ADD_CATEGORIES: '/saw/security/auth/admin/cust/manage/categories/add',
  PRIVILEGES: '/saw/security/auth/admin/cust/manage/privileges/upsert',
  DELETE_DASHBOARD: '/saw/services/observe/dashboards'
};

const LOG_LEVELS = {
  error: 0,
  warn: 1,
  info: 2,
  verbose: 3,
  debug: 4,
  silly: 5
};

const E2E_OUTPUT_BASE_DIR = 'target/e2e';

module.exports = {
  ES_REPORT,
  PIVOT,
  REPORT,
  CHART,
  API_ROUTES,
  LOG_LEVELS,
  E2E_OUTPUT_BASE_DIR
};
