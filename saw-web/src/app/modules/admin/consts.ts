import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as fpJoin from 'lodash/fp/join';

export const AdminMenuData = [
  {
    id: '',
    name: 'User',
    url: ['/admin/user']
  },
  {
    id: '',
    name: 'Role',
    url: ['/admin/role']
  },
  {
    id: '',
    name: 'Privilege',
    url: ['/admin/privilege']
  },
  {
    id: '',
    name: 'Category',
    url: ['/admin/categories']
  },
  {
    id: '',
    name: 'Package Utility',
    children: [
      {
        id: '',
        name: 'Export',
        url: ['/admin/export']
      },
      {
        id: '',
        name: 'Import',
        url: ['/admin/import']
      }
    ]
  }
];

export const UsersTableHeader = [
  {
    caption: 'LOGIN ID',
    dataField: 'masterLoginId',
    allowSorting: true,
    alignment: 'left',
    width: '20%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'ROLE',
    dataField: 'roleName',
    allowSorting: true,
    alignment: 'left',
    width: '10%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'FIRST NAME',
    dataField: 'firstName',
    allowSorting: true,
    alignment: 'left',
    width: '18%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'LAST NAME',
    dataField: 'lastName',
    allowSorting: true,
    alignment: 'left',
    width: '18%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'EMAIL',
    dataField: 'email',
    allowSorting: true,
    alignment: 'left',
    width: '20%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'STATUS',
    dataField: 'activeStatusInd',
    allowSorting: true,
    alignment: 'left',
    width: '8%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: '',
    width: '6%',
    cellTemplate: 'actionCellTemplate'
  }
];

export const RolesTableHeader = [
  {
    caption: 'ROLE NAME',
    dataField: 'roleName',
    allowSorting: true,
    alignment: 'left',
    width: '20%',
    cellTemplate: 'linkCellTemplate',
    cssClass: 'branded-column-name'
  },
  {
    caption: 'ROLE TYPE',
    dataField: 'roleType',
    allowSorting: true,
    alignment: 'left',
    width: '20%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'ROLE DESCRIPTION',
    dataField: 'roleDesc',
    allowSorting: true,
    alignment: 'left',
    width: '35%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'STATUS',
    dataField: 'activeStatusInd',
    allowSorting: true,
    alignment: 'left',
    width: '13%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: '',
    width: '2%',
    cellTemplate: 'actionCellTemplate'
  }
];

export const PrivilegesTableHeader = [
  {
    caption: 'PRODUCT',
    dataField: 'productName',
    allowSorting: true,
    alignment: 'left',
    width: '10%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'MODULE',
    dataField: 'moduleName',
    allowSorting: true,
    alignment: 'left',
    width: '10%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'CATEGORY',
    dataField: 'categoryName',
    allowSorting: true,
    alignment: 'left',
    width: '12%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'SUB CATEGORY',
    dataField: 'subCategoryName',
    allowSorting: true,
    alignment: 'left',
    width: '15%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'ROLE',
    dataField: 'roleName',
    allowSorting: true,
    alignment: 'left',
    width: '12%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'PRIVILEGE DESC',
    dataField: 'privilegeDesc',
    allowSorting: true,
    alignment: 'left',
    width: '35%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: '',
    width: '6%',
    cellTemplate: 'actionCellTemplate'
  }
];

export const CategoriesTableHeader = [
  {
    caption: 'PRODUCT',
    dataField: 'productName',
    allowSorting: true,
    alignment: 'left',
    width: '15%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'MODULE',
    dataField: 'moduleName',
    allowSorting: true,
    alignment: 'left',
    width: '15%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'CATEGORY',
    dataField: 'categoryName',
    allowSorting: true,
    alignment: 'left',
    width: '20%',
    cellTemplate: 'highlightCellTemplate'
  },
  {
    caption: 'SUB CATEGORIES',
    dataField: 'subCategories',
    allowSorting: true,
    alignment: 'left',
    width: '52%',
    cellTemplate: 'highlightCellTemplate',
    calculateDisplayValue: data => {
      const { subCategories } = data;

      if (subCategories) {
        return fpPipe(
          fpMap(({ subCategoryName }) => subCategoryName),
          fpJoin(', ')
        )(subCategories);
      }
      return '';
    }
  },
  {
    caption: '',
    width: '8%',
    cellTemplate: 'actionCellTemplate'
  }
];
