export interface WidgetType {
  name: string;
  id: string;
  description: string;
  disabled?: boolean;
  icon: string;
};

export const widgetTypes: Array<WidgetType> = [{
  name: 'Existing Analysis',
  id: 'ANALYSIS_WIDGET',
  description: 'Add a chart to your dashboard',
  icon: 'analysis-widget'
}, {
  name: 'Snapshot KPI',
  id: 'KPI_WIDGET',
  description: 'Capture a metric at any point in time',
  disabled: true,
  icon: 'kpi-widget'
}, {
  name: 'Custom Widget',
  id: 'CUSTOM_WIDGET',
  description: 'Embed a URL or add custom scripts',
  disabled: true,
  icon: 'custom-widget'
}];
