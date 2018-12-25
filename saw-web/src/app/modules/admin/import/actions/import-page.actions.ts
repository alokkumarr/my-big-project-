export class ClearImport {
  static readonly type = '[Admin Import OnDestroy] Clear all import page data';
}

export class SelectAnalysisGlobalCategory {
  static readonly type = '[Admin Import Page] Select global analysis category';
  constructor(public category: string | number) {}
}

export class LoadAllAnalyzeCategories {
  static readonly type =
    '[Admin Import Page OnInit] Load all analyze categories';
}

export class LoadMetrics {
  static readonly type = '[Admin Import Page OnInit] Load all metrics';
}

export class LoadAnalysesForCategory {
  static readonly type =
    '[Admin Import Category Change] Load analyses for category';
  constructor(public category: string | number) {}
}
