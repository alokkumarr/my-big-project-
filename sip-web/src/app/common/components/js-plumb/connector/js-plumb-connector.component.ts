import {
  Component,
  Input,
  Output,
  Injector,
  EventEmitter,
  ComponentFactoryResolver,
  ViewContainerRef,
  ViewChild,
  OnInit,
  OnDestroy
} from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';

import {
  Join,
  JoinCriterion,
  EndpointPayload,
  ConnectionPayload,
  JoinChangeEvent
} from '../types';
import { JsPlumbJoinLabelComponent } from '../join-label';

@Component({
  selector: 'js-plumb-connector-u',
  template: `
    <div #target></div>
  `
})
export class JsPlumbConnectorComponent implements OnInit, OnDestroy {
  @Output() change: EventEmitter<JoinChangeEvent> = new EventEmitter();
  @Input() join: Join;
  @Input() index: number;
  @Input() plumbInstance: any;

  @ViewChild('target', { read: ViewContainerRef })
  _container: ViewContainerRef;

  public _connection: any;

  constructor(
    public _injector: Injector,
    public _resolver: ComponentFactoryResolver
  ) {}

  ngOnInit() {
    this.render();
  }

  ngOnDestroy() {
    this.detach();
  }

  detach() {
    const [source, target] = this.join.criteria;
    const connections = this.findAllConnections(source, target);
    if (!isEmpty(connections)) {
      forEach(connections, connection => {
        if (!isEmpty(connection.endpoints)) {
          this.plumbInstance.detach(connection);
        }
      });
      this._connection = null;
    }
  }

  render() {
    this.detach();

    setTimeout(() => {
      const [source, target] = this.join.criteria;
      const connections = this.findAllConnections(source, target);

      // if the connection is not found, create one
      // this happens when opening a new analysis with joins
      if (isEmpty(connections)) {
        this._connection = this.plumbInstance.connect({
          uuids: [this.getIdentifier(source), this.getIdentifier(target)]
        });
      } else {
        this._connection = connections[0];
      }

      const connectionPayload: ConnectionPayload = {
        join: this.join
      };

      this._connection.setParameter('connectionPayload', connectionPayload);
      this.addJoinlabel();
    }, 300);
  }

  addJoinlabel() {
    const componentFactory = this._resolver.resolveComponentFactory(
      JsPlumbJoinLabelComponent
    );
    const component = componentFactory.create(this._injector);
    this._container.insert(component.hostView);
    component.instance.join = this.join;
    component.instance.change.subscribe(event => this.onJoinChange(event));

    const elem = <HTMLElement>component.location.nativeElement;
    elem.remove();

    this._connection.addOverlay([
      'Custom',
      {
        create: () => {
          return elem;
        },
        location: 0.5
      }
    ]);
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

  findAllConnections(source: JoinCriterion, target: JoinCriterion) {
    const connections = this.plumbInstance.getConnections();
    return filter(connections, connection => {
      const [sourceEndpoint, targetEndpoint] = connection.endpoints;
      const sourcePayload = <EndpointPayload>(
        sourceEndpoint.getParameter('endpointPayload')
      );
      const targetPayload = <EndpointPayload>(
        targetEndpoint.getParameter('endpointPayload')
      );

      /* Match sides in reverse too because we don't support having more than
         one connection between two columns as a valid use case.
      */

      return (
        (this.compare(sourcePayload, source) &&
          this.compare(targetPayload, target)) ||
        (this.compare(targetPayload, source) &&
          this.compare(sourcePayload, target))
      );
    });
  }

  /**
   * Compares jsplumb endpoint payload (represents a column) with a
   * column in join criteria.
   *
   * @param {EndpointPayload} payload
   * @param {JoinCriterion} column
   * @returns {boolean}
   * @memberof JsPlumbConnectorComponent
   */
  compare(payload: EndpointPayload, column: JoinCriterion): boolean {
    return (
      payload.column.columnName === column.columnName &&
      payload.artifactName === column.tableName
    );
  }
}
