import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MapSettings } from '../../../types';
import { MAP_STYLES } from '../../../consts';

@Component({
  selector: 'designer-map-options',
  templateUrl: 'designer-map-options.component.html'
})
export class DesignerMapOptionsComponent {
  @Output() change = new EventEmitter();

  public mapSettings: MapSettings;
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
        mapSettings: { ...this.mapSettings, mapStyle }
      }
    });
  }
}
