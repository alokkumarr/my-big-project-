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
  tiles: Array<Tile>,
  id: string,
  categoryId: string,
  name: string,
  description: string,
  options?: Object,
  filters: Array<Object>
}
