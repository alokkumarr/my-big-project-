'use strict';

const ES_REPORT = 'esReport'; // ES
const PIVOT = 'pivot';
const REPORT = 'report'; // DL
const CHART = 'chart';
const NUMBER_TYPES = ['long', 'integer', 'double', 'float'];
const HTTP_PROTOCOL = 'https';

const API_ROUTES = {
  AUTH: '/saw/security/doAuthenticate',
  ANALYSIS: '/saw/services/analysis',
  DSL_ANALYSIS: '/saw/services/dslanalysis/',
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

const SFTP_DETAILS = {
  sftpHost: 'sip-admin',
  sftpPort: '22',
  sftpUser: 'root',
  sftpPassword: 'root'
};
module.exports = {
  ES_REPORT,
  PIVOT,
  REPORT,
  CHART,
  API_ROUTES,
  LOG_LEVELS,
  E2E_OUTPUT_BASE_DIR,
  NUMBER_TYPES,
  SFTP_DETAILS,
  HTTP_PROTOCOL
};
