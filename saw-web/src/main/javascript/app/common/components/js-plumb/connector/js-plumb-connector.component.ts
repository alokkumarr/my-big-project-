declare const require: any;
import {
  Component,
  Input,
  Output,
  Injector,
  EventEmitter,
  ComponentFactoryResolver,
  ViewContainerRef,
  ViewChild
} from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import * as find from 'lodash/find';

import {
  Join,
  JoinCriterion,
  EndpointPayload,
  EndpointSide,
  ConnectionPayload,
  JoinChangeEvent
} from '../types';
import { JS_PLUMB_DEFAULT_SETTINGS } from '../settings';
import { JsPlumbJoinLabelComponent } from '../join-label';

const ENDPOINT_ANCHORS = {
  left: 'LeftMiddle',
  right: 'RightMiddle'
}

@Component({
  selector: 'js-plumb-connector-u',
  template: `
    <div #target></div>
    `
})
export class JsPlumbConnectorComponent {
  @Output() change: EventEmitter<JoinChangeEvent> = new EventEmitter();
  @Input() join: Join;
  @Input() index: number;
  @Input() plumbInstance: any;

  @ViewChild('target', {read: ViewContainerRef}) _container: ViewContainerRef;

  private _connection: any;

  constructor (
    private _injector: Injector,
    private _resolver: ComponentFactoryResolver
  ) {}

  ngOnInit() {
    this.render();
  }

  ngOnDestroy() {
    this.detach();
  }

  detach() {
    if (this._connection) {
      if (!isEmpty(this._connection.endpoints)) {
        this.plumbInstance.detach(this._connection);
      }
      this._connection = null;
    }
  }

  render() {
    this.detach();

    setTimeout(() => {
      const [source, target] = this.join.criteria;
      this._connection = this.findConnection(source, target);

      // if the connection is not found, create one
      // this happens when opening a new analysis with joins
      if (!this._connection) {
        this._connection = this.plumbInstance.connect({
          uuids: [
            this.getIdentifier(source),
            this.getIdentifier(target)
          ]
        });
      }

      const connectionPayload: ConnectionPayload = {
        join: this.join
      }

      this._connection.setParameter('connectionPayload', connectionPayload);
      this.addJoinlabel();
    }, 300);
  }

  addJoinlabel() {
    const componentFactory = this._resolver.resolveComponentFactory(JsPlumbJoinLabelComponent);
    const component = componentFactory.create(this._injector);
    this._container.insert(component.hostView);
    component.instance.join = this.join;
    component.instance.change.subscribe(event => this.onJoinChange(event));

    const elem = <HTMLElement>component.location.nativeElement;
    elem.remove();

    this._connection.addOverlay(['Custom', {
      create: () => {
        return elem;
      },
      location: 0.5
    }]);
  }

  onJoinChange(event: JoinChangeEvent) {
    if (event) {
      event.index = this.index;
    }
    this.change.emit(event);
  }

  getIdentifier({ tableName, columnName, side }: JoinCriterion) {
    return `${tableName}:${columnName}:${side}`;
  }

  findConnection (source: JoinCriterion, target: JoinCriterion) {
    const connections = this.plumbInstance.getConnections();
    return find(connections, connection => {
      const [sourceEndpoint, targetEndpoint] = connection.endpoints;
      const sourcePayload = <EndpointPayload> sourceEndpoint.getParameter('endpointPayload');
      const targetPayload = <EndpointPayload> targetEndpoint.getParameter('endpointPayload');
      return sourcePayload.column.columnName === source.columnName &&
        sourcePayload.artifactName === source.tableName &&
        targetPayload.column.columnName === target.columnName &&
        targetPayload.artifactName === target.tableName;
    });
  }
}
