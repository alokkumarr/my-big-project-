'use strict';

const ES_REPORT = 'esReport';
const PIVOT = 'pivot';
const REPORT = 'report';//DL
const CHART = 'chart';
const API_ROUTES = {
  AUTH: '/saw/security/doAuthenticate',
  ANALYSIS: '/saw/services/analysis',
  CATEGORIES_FETCH: '/saw/security/auth/admin/cust/manage/categories/fetch',
  SEMANTIC: '/saw/services/internal/semantic/md?projectId=workbench',
  ROLES: '/saw/security/auth/admin/cust/manage/roles/add',
  USERS: '/saw/security/auth/admin/cust/manage/users/add',
  ADD_CATEGORIES: '/saw/security/auth/admin/cust/manage/categories/add',
  PRIVILEGES: '/saw/security/auth/admin/cust/manage/privileges/upsert'
}

module.exports = {
  ES_REPORT, PIVOT, REPORT, CHART, API_ROUTES
};
