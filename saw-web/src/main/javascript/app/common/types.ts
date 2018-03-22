export type DesignerChangeEvent<TPayload> = {
  reloadType: 'none' | 'frontend' | 'backend';
  payload: TPayload;
}
