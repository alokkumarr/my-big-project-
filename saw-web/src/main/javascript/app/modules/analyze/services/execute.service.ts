import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { AnalyzeService, EXECUTION_MODES } from './analyze.service';

export enum EXECUTION_STATES {
  SUCCESS,
  ERROR,
  EXECUTING
  // TODO add a forth state
}

export interface IExecuteEvent {
  response?: { data: any[]; count: number };
  state: EXECUTION_STATES;
}

export interface IExecuteEventEmitter {
  id: string;
  subject: BehaviorSubject<IExecuteEvent>;
}

export interface IExecuteAggregateEvent {
  id: string;
  state: EXECUTION_STATES;
}

@Injectable()
export class ExecuteService {
  executingAnalyses: any;
  execs$: ReplaySubject<IExecuteEventEmitter>;

  constructor(private _analyzeService: AnalyzeService) {
    const bufferSize = 10;
    this.execs$ = new ReplaySubject<IExecuteEventEmitter>(bufferSize);
  }

  executeAnalysis(analysis, mode = EXECUTION_MODES.LIVE) {
    const id = analysis.id;
    const exec$ = new BehaviorSubject<IExecuteEvent>({
      state: EXECUTION_STATES.EXECUTING
    });
    this.execs$.next({
      id,
      subject: exec$
    });

    this._analyzeService.applyAnalysis(analysis, mode, { take: 25 }).then(
      response => {
        exec$.next({
          state: EXECUTION_STATES.SUCCESS,
          response
        });
        exec$.complete();
      },
      () => {
        exec$.next({ state: EXECUTION_STATES.ERROR });
        exec$.complete();
      }
    );
  }

  subscribe(analysisId: string, callback: (IExecuteEvent) => void) {
    return this.execs$
      .filter(eventEmitter => !eventEmitter.subject.isStopped)
      .filter(({ id }: IExecuteEventEmitter) => analysisId === id)
      .subscribe(callback);
  }

  subscribeToAllExecuting(callback: (executions: Object) => void) {
    return this.execs$
      .filter(({ subject }) => !subject.isStopped)
      .mergeMap<IExecuteEventEmitter, IExecuteAggregateEvent>(
        ({ id, subject }) => subject.map(({ state }) => ({ id, state }))
      )
      .scan<IExecuteAggregateEvent, Object>((acc, e) => {
        acc[e.id] = e.state;
        return acc;
      }, {})
      .subscribe(callback);
  }
}
