export const AdminMenuData = [{
  id: '',
  name: 'User',
  url: '#!/admin/user'
}, {
  id: '',
  name: 'Role',
  url: '#!/admin/role'
}, {
  id: '',
  name: 'Privilege',
  url: '#!/admin/privilege'
}, {
  id: '',
  name: 'Category',
  url: '#!/admin/categories'
}];

export const UsersTableHeader = [{
  caption: 'LOGIN ID',
  dataField: 'masterLoginId',
  allowSorting: true,
  alignment: 'left',
  width: '20%',
  cellTemplate: 'highlightCellTemplate'
}, {
  caption: 'ROLE',
  dataField: 'roleName',
  allowSorting: true,
  alignment: 'left',
  width: '10%',
  cellTemplate: 'highlightCellTemplate'
}, {
  caption: 'FIRST NAME',
  dataField: 'firstName',
  allowSorting: true,
  alignment: 'left',
  width: '18%',
  cellTemplate: 'highlightCellTemplate'
}, {
  caption: 'LAST NAME',
  dataField: 'lastName',
  allowSorting: true,
  alignment: 'left',
  width: '18%',
  cellTemplate: 'highlightCellTemplate'
}, {
  caption: 'EMAIL',
  dataField: 'email',
  allowSorting: true,
  alignment: 'left',
  width: '20%',
  cellTemplate: 'highlightCellTemplate'
}, {
  caption: 'STATUS',
  dataField: 'activeStatusInd',
  allowSorting: true,
  alignment: 'left',
  width: '8%',
  cellTemplate: 'highlightCellTemplate'
}, {
  caption: '',
  width: '6%',
  cellTemplate: 'actionCellTemplate'
}];

export const RolesTableHeader = [{
  name: 'All'
}, {
  name: 'ROLE NAME'
}, {
  name: 'ROLE TYPE'
}, {
  name: 'ROLE DESCRIPTION'
}, {
  name: 'STATUS'
}];

export const PrivilegesTableHeader = [{
  name: 'All'
}, {
  name: 'PRODUCT'
}, {
  name: 'MODULE'
}, {
  name: 'CATEGORY'
}, {
  name: 'ROLE'
}, {
  name: 'PRIVILEGE DESC'
}];

export const CategoriesTableHeader = [{
  name: 'All'
}, {
  name: 'PRODUCT'
}, {
  name: 'MODULE'
}, {
  name: 'CATEGORY'
}, {
  name: 'SUB CATEGORIES'
}];
