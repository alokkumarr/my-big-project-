import * as get from 'lodash/get';

export function transformAlertForGridView(alert) {
  const fieldsPath = 'sipQuery.artifacts[0].fields';
  const filtersPath = 'sipQuery.filters';
  const monitoringEntityPath = `${fieldsPath}[1].columnName`;
  const aggregationPath = `${fieldsPath}[1].aggregate`;
  const operatorPath = `${filtersPath}[0].model.operator`;
  const tresholdValuePath = `${filtersPath}[0].model.value`;
  alert.aggregation = get(alert, aggregationPath);
  alert.monitoringEntity = get(alert, monitoringEntityPath);
  alert.operator = get(alert, operatorPath);
  alert.thresholdValue = get(alert, tresholdValuePath);
  return alert;
}
