import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs/ReplaySubject';
import { Transition, StateService } from '@uirouter/angular';
import { ToastService } from '../../../common/services/toastMessage.service';
import { AnalyzeService } from './analyze.service';

enum EXECUTION_MODES {
  PREVIEW,
  LIVE
};

export enum EXECUTION_STATES {
  SUCCESS,
  ERROR,
  EXECUTING
  // TODO add a forth state
};

export interface IExecuteEvent {
  id: string,
  executionState: EXECUTION_STATES
}

@Injectable()
export class ExecuteService {

  executingAnalyses: any;
  execs$: ReplaySubject<IExecuteEvent>

  constructor(
    private _toastMessage: ToastService,
    private _analyzeService: AnalyzeService,
    private _state: StateService
  ) {
    const bufferSize = 10;
    this.execs$ = new ReplaySubject<IExecuteEvent>(bufferSize);
  }

  // isExecuting(id) {
  //   return Boolean(this.executions$[id]);
  // }

  // executeAnalysis(model) {

  //   if (this.isExecuting(model.id)) {
  //     this._toastMessage.error('Analysis is executing already. Please try again in some time.');
  //   } else {
  //     this._toastMessage.info('Analysis has been submitted for execution.');
  //     // this.executions$[model.id] = new BehaviorSubject({ executionState: EXECUTION_STATES.EXECUTING });

  //     this.applyAnalysis(model).then(({data}) => {
  //       this.executions$[model.id].next({ executionState: EXECUTION_STATES.SUCCESS, data });
  //     }, err => {
  //       this.executions$[model.id].error(err);
  //     });
  //   }
  // }

  executeAnalysis(analysis) {
    const id = analysis.id;
    this._analyzeService.applyAnalysis(analysis).then(() => {
      this._toastMessage.success('Tap this message to reload data.', 'Execution finished', {
        timeOut: 0,
        extendedTimeOut: 0,
        closeButton: true,
        onclick: this.gotoLastPublished(analysis)
      });
      this.execs$.next({
        id,
        executionState: EXECUTION_STATES.SUCCESS
      });
    }, () => {
      this.execs$.next({
        id,
        executionState: EXECUTION_STATES.ERROR
      });
    });

    this.execs$.next({
      id,
      executionState: EXECUTION_STATES.EXECUTING
    });
    this._toastMessage.info('Analysis has been submitted for execution.');
  }

  gotoLastPublished (analysis) {
    return () => {
      this._toastMessage.clear();
      this._state.go('analyze.executedDetail', {
        analysisId: analysis.id,
        analysis: analysis,
        executionId: null
      }, {reload: true});
    }
  };

  subscribe(analysisId: string, callback: (IExecuteEvent) => void) {
    return this.execs$
    .filter(({id}: IExecuteEvent) => analysisId === id)
    .subscribe(callback);
  }

  subscribeToAllExecuting(callback: (executions: Object) => void) {
    return this.execs$
      .scan<IExecuteEvent, Object>((acc, e) => {
        acc[e.id] = e.executionState;
        return acc;
      }, {})
      .subscribe(callback);
  }
}
