declare const require: any;
import {
  Directive,
  Input,
  Output,
  ElementRef
} from '@angular/core';
import {
  Artifact,
  JoinCriterion,
  ArtifactColumnReport,
  EndpointPayload,
  EndpointSide
} from '../types';
import { JS_PLUMB_DEFAULT_SETTINGS } from '../settings';

const ENDPOINT_ANCHORS = {
  left: 'LeftMiddle',
  right: 'RightMiddle'

}
@Directive({
  selector: 'js-plumb-endpoint-u'
})
export class JsPlumbEndpointComponent {
  @Input() column: ArtifactColumnReport;
  @Input() artifactName: string;
  @Input() side: EndpointSide;
  @Input() plumbInstance: any;

  private _endpointInstance: any;

  constructor (
    private _elementRef: ElementRef
  ) {}

  ngOnInit() {
    setTimeout(() => {
      this.addEndpoint(this.column, this.side, this.artifactName);
    });
  }

  ngOnDestroy() {
    this.removeEndpoint();
  }

  addEndpoint(column: ArtifactColumnReport, side: EndpointSide, artifactName: string) {
    const endpointSettings = JS_PLUMB_DEFAULT_SETTINGS.endpoints.source;

    const endPointIdentifier = `${artifactName}:${column.columnName}:${side}`;

    const options = {
      uuid: endPointIdentifier,
      anchor: ENDPOINT_ANCHORS[side],
      connectionsDetachable: true,
      reattachConnections: true,
      deleteEndpointsOnDetach: false
    };

    const element = this._elementRef.nativeElement;
    const endpointPayload: EndpointPayload = {
      column,
      artifactName,
      side: this.side
    };

    element.classList.add(`jsp-endpoint-${options.anchor}`);

    this._endpointInstance = this.plumbInstance.addEndpoint(element, endpointSettings, options);
    this._endpointInstance.setParameter('endpointPayload', endpointPayload);
  }

  removeEndpoint() {
    if (this._endpointInstance && this.plumbInstance) {
      this.plumbInstance.deleteEndpoint(this._endpointInstance);
    }
  }
}
