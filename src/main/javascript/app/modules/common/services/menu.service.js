import map from 'lodash/map';
import get from 'lodash/get';
import set from 'lodash/set';
import filter from 'lodash/filter';

export function MenuService($http, JwtService, AppConfig) {
  'ngInject';

  const url = AppConfig.api.url;

  return {
    getMenu
  };

  function getRequestParams(moduleName) {
    const params = JwtService.getRequestParams();

    set(params, 'contents.action', 'search');
    set(params, 'contents.keys.type', 'menu');
    set(params, 'contents.keys.module', moduleName.toUpperCase());

    return params;
  }

  function getMenu(moduleName) {
    moduleName = moduleName.toUpperCase();
    return $http.post(url, getRequestParams(moduleName))
      .then(response => {
        const menu = filter(
          get(response, `data.contents.[0].${moduleName.toUpperCase()}`),
          category => category.module === moduleName
        );
        return map(menu, item => {
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
