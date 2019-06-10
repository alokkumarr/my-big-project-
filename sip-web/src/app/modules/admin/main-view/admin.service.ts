import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';
import { ToastService } from '../../../common/services/toastMessage.service';
import AppConfig from '../../../../../appConfig';
import { JwtService } from '../../../common/services';
import { AnalyzeService } from '../../analyze/services/analyze.service';
import { from } from 'rxjs';

import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';

const ANALYZE_MODULE_NAME = 'ANALYZE';

interface RequestOptions {
  toast?: { successMsg: string; errorMsg?: string };
  forWhat?:
    | 'export'
    | 'import'
    | 'user'
    | 'role'
    | 'privilege'
    | 'category'
    | 'newScheme';
}

const loginUrl = AppConfig.login.url;
const apiUrl = AppConfig.api.url;

@Injectable()
export class AdminService {
  constructor(
    public http: HttpClient,
    public _toastMessage: ToastService,
    public _jwtService: JwtService,
    private _analyzeService: AnalyzeService
  ) {}

  showToastMessageIfNeeded(toast) {
    return resp => {
      if (!toast) {
        return;
      }
      if (resp.valid) {
        this._toastMessage.success(toast.successMsg);
      } else {
        this._toastMessage.error(toast.errorMsg || resp.validityMessage);
      }
    };
  }

  getRequestParams(params = []) {
    const reqParams = this._jwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', ANALYZE_MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });
    return reqParams;
  }

  getAnalysesByCategoryId(subCategoryId: string | number) {
    return from(this._analyzeService.getAnalysesFor(subCategoryId.toString()));
  }

  getRequest<T>(path, options: RequestOptions = {}) {
    const { toast, forWhat } = options;
    return this.http
      .get<T>(
        `${this.getBaseUrl(forWhat)}/${this.getIntermediaryPath(
          forWhat
        )}${path}`
      )
      .pipe(tap(this.showToastMessageIfNeeded(toast)));
  }

  request<T>(path, params, options: RequestOptions = {}) {
    const { toast, forWhat } = options;
    return this.http
      .post<T>(
        `${this.getBaseUrl(forWhat)}/${this.getIntermediaryPath(
          forWhat
        )}${path}`,
        params
      )
      .pipe(tap(this.showToastMessageIfNeeded(toast)));
  }

  getIntermediaryPath(forWhat) {
    switch (forWhat) {
      case 'export':
      case 'import':
        return '';
      case 'newScheme':
        return 'auth/admin/';
      default:
        return 'auth/admin/cust/manage/';
    }
  }

  getBaseUrl(forWhat) {
    switch (forWhat) {
      case 'export':
      case 'import':
        return apiUrl;
      default:
        return loginUrl;
    }
  }
}
