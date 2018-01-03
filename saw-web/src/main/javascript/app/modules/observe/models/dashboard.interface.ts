export enum TileType {
  Analysis = 'analysis',
  KPI = 'kpi'
}

export interface Tile {
  analysis?: any,
  id: string,
  type: TileType,
  cols: number,
  rows: number,
  x: number,
  y: number,
  options?: Object
}

export interface Dashboard {
  entityId: string,
  categoryId: string,
  name: string,
  description: string,
  createdBy?: string,
  updatedBy?: string,
  createdAt?: string,
  updatedAt?: string
  tiles: Array<Tile>,
  options?: Object,
  filters: Array<Object>
}
