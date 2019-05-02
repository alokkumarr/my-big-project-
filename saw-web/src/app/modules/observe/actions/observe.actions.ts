export class ObserveLoadMetrics {
  static readonly type = '[Observe] Load all metrics';
  constructor() {}
}

export class ObserveLoadArtifactsForMetric {
  static readonly type = '[Observe] Load artifacts for metric';
  constructor(public semanticId: string) {}
}
