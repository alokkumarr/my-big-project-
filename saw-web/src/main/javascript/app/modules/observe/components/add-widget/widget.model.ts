export enum Widget {
  ANALYSIS,
  KPI,
  CUSTOM,
  BULLET
}

export interface WidgetType {
  name: string;
  id: Widget;
  description: string;
  disabled?: boolean;
  icon: string;
}

export const widgetTypes: Array<WidgetType> = [
  {
    name: 'Existing Analysis',
    id: Widget.ANALYSIS,
    description: 'Add a chart to your dashboard',
    icon: 'analysis-widget'
  },
  {
    name: 'Snapshot KPI',
    id: Widget.KPI,
    description: 'Capture a metric at any point in time',
    disabled: false,
    icon: 'kpi-widget'
  },
  {
    name: 'Bullet KPI',
    id: Widget.BULLET,
    description: 'Metric performance at any point in time',
    disabled: false,
    icon: 'custom-widget'
  } /*, {
  name: 'Custom Widget',
  id: Widget.CUSTOM,
  description: 'Embed a URL or add custom scripts',
  disabled: true,
  icon: 'custom-widget'
} */
];

export enum WIDGET_ACTIONS {
  ADD,
  EDIT,
  REMOVE
}
