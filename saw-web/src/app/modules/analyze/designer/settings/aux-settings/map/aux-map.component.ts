import { Component, Input, Output, EventEmitter } from '@angular/core';

import { IMapSettings } from '../../../types';
import { MAP_STYLES } from '../../../consts';

@Component({
  selector: 'designer-settings-aux-map',
  templateUrl: 'aux-map.component.html'
  // styleUrls: ['aux-map-chart.component.scss']
})
export class DesignerSettingsAuxMapComponent {

  @Output() change = new EventEmitter();

  public mapSettings: IMapSettings;
  public editMode: false;
  public MAP_STYLES = MAP_STYLES;

  @Input('mapSettings')
  set setMapStyle(mapSettings: any) {
    if (!mapSettings) {
      return;
    }
    this.mapSettings = mapSettings;
  }

  onMapStyleChange(mapStyle) {
    this.change.emit({
      subject: 'mapSettings',
      data: {
        mapSettings: {...this.mapSettings, mapStyle}
      }
    });
  }
}
