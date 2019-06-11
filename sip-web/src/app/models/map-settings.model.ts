export interface MapSettings {
  mapStyle: string;
  mapType: string;
  labelOptions: {
    enabled: boolean;
    value: string;
  };
  legend: {
    align: string;
    layout: string;
  };
}
