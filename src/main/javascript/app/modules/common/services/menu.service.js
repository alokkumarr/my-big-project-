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
            name: item.category_name,
            data: item
          };

          obj.children = map(item.children, child => {
            return {
              id: child.sub_category_id,
              name: child.sub_category_name,
              url: `#!/${item.module.toLowerCase()}/${child.sub_category_id}`,
              data: child
            };
          });

          return obj;
        });
      });
  }
}
