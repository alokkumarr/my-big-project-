export interface MenuItem {
  id: string | number;
  data?: any;
  url?: string[];
  name: string;
  children: MenuItem[];
}

export interface Menu {
  items: MenuItem[];
}

export interface CommonStateModel {
  analyzeMenu: Menu;
  observeMenu: Menu;
  adminMenu: Menu;
}
