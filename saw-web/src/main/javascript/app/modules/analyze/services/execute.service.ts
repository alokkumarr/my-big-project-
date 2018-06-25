import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Transition, StateService } from '@uirouter/angular';
import { ToastService } from '../../../common/services/toastMessage.service';
import { AnalyzeService, EXECUTION_MODES } from './analyze.service';

export enum EXECUTION_STATES {
  SUCCESS,
  ERROR,
  EXECUTING
  // TODO add a forth state
};

export interface IExecuteEventEmitter {
  id: string,
  subject: BehaviorSubject<EXECUTION_STATES>
}

export interface IExecuteEvent {
  id: string,
  executionState: EXECUTION_STATES
}

@Injectable()
export class ExecuteService {

  executingAnalyses: any;
  execs$: ReplaySubject<IExecuteEventEmitter>

  constructor(
    private _toastMessage: ToastService,
    private _analyzeService: AnalyzeService,
    private _state: StateService
  ) {
    const bufferSize = 10;
    this.execs$ = new ReplaySubject<IExecuteEventEmitter>(bufferSize);
  }

  executeAnalysis(analysis) {
    const id = analysis.id;
    const exec$ = new BehaviorSubject<EXECUTION_STATES>(EXECUTION_STATES.EXECUTING);
    this.execs$.next({
      id,
      subject: exec$
    });

    this._analyzeService.applyAnalysis(analysis, EXECUTION_MODES.LIVE, {take: 25})
    .then((response) => {
      console.log('response', response);
      exec$.next(EXECUTION_STATES.SUCCESS);
      exec$.complete();
    }, () => {
      exec$.next(EXECUTION_STATES.ERROR);
      exec$.complete();
    });
  }

  subscribe(analysisId: string, callback: (IExecuteEvent) => void) {
    return this.execs$
    .filter(eventEmitter => !eventEmitter.subject.isStopped)
    .filter(({id}: IExecuteEventEmitter) => analysisId === id)
    .subscribe(callback);
  }

  subscribeToAllExecuting(callback: (executions: Object) => void) {
    return this.execs$
      .filter(({subject}) => !subject.isStopped)
      .mergeMap<IExecuteEventEmitter, IExecuteEvent>(
        ({id, subject}) => subject.map(
          state => ({id, executionState: state})
        )
      )
      .scan<IExecuteEvent, Object>((acc, e) => {
        acc[e.id] = e.executionState;
        return acc;
      }, {})
      .subscribe(callback);
  }
}
