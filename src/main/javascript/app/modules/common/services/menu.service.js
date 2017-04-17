import map from 'lodash/map';

export function MenuService($http) {
  'ngInject';

  return {
    getMenu
  };

  function getMenu(moduleName) {
    return $http.get(`/api/menu/${moduleName}`)
      .then(response => {
        return map(response.data, item => {
          const obj = {
            id: item.id,
            name: item.categoryName,
            data: item
          };

          obj.children = map(item.children, child => {
            return {
              id: child.subCategoryId,
              name: child.subCategoryName,
              url: `#!/${item.module.toLowerCase()}/${child.subCategoryId}`,
              data: child
            };
          });

          return obj;
        });
      });
  }
}
