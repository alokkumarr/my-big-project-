(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["modules-admin-admin-module"],{

/***/ "./node_modules/lodash/_baseIntersection.js":
/*!**************************************************!*\
  !*** ./node_modules/lodash/_baseIntersection.js ***!
  \**************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var SetCache = __webpack_require__(/*! ./_SetCache */ "./node_modules/lodash/_SetCache.js"),
    arrayIncludes = __webpack_require__(/*! ./_arrayIncludes */ "./node_modules/lodash/_arrayIncludes.js"),
    arrayIncludesWith = __webpack_require__(/*! ./_arrayIncludesWith */ "./node_modules/lodash/_arrayIncludesWith.js"),
    arrayMap = __webpack_require__(/*! ./_arrayMap */ "./node_modules/lodash/_arrayMap.js"),
    baseUnary = __webpack_require__(/*! ./_baseUnary */ "./node_modules/lodash/_baseUnary.js"),
    cacheHas = __webpack_require__(/*! ./_cacheHas */ "./node_modules/lodash/_cacheHas.js");

/* Built-in method references for those with the same name as other `lodash` methods. */
var nativeMin = Math.min;

/**
 * The base implementation of methods like `_.intersection`, without support
 * for iteratee shorthands, that accepts an array of arrays to inspect.
 *
 * @private
 * @param {Array} arrays The arrays to inspect.
 * @param {Function} [iteratee] The iteratee invoked per element.
 * @param {Function} [comparator] The comparator invoked per element.
 * @returns {Array} Returns the new array of shared values.
 */
function baseIntersection(arrays, iteratee, comparator) {
  var includes = comparator ? arrayIncludesWith : arrayIncludes,
      length = arrays[0].length,
      othLength = arrays.length,
      othIndex = othLength,
      caches = Array(othLength),
      maxLength = Infinity,
      result = [];

  while (othIndex--) {
    var array = arrays[othIndex];
    if (othIndex && iteratee) {
      array = arrayMap(array, baseUnary(iteratee));
    }
    maxLength = nativeMin(array.length, maxLength);
    caches[othIndex] = !comparator && (iteratee || (length >= 120 && array.length >= 120))
      ? new SetCache(othIndex && array)
      : undefined;
  }
  array = arrays[0];

  var index = -1,
      seen = caches[0];

  outer:
  while (++index < length && result.length < maxLength) {
    var value = array[index],
        computed = iteratee ? iteratee(value) : value;

    value = (comparator || value !== 0) ? value : 0;
    if (!(seen
          ? cacheHas(seen, computed)
          : includes(result, computed, comparator)
        )) {
      othIndex = othLength;
      while (--othIndex) {
        var cache = caches[othIndex];
        if (!(cache
              ? cacheHas(cache, computed)
              : includes(arrays[othIndex], computed, comparator))
            ) {
          continue outer;
        }
      }
      if (seen) {
        seen.push(computed);
      }
      result.push(value);
    }
  }
  return result;
}

module.exports = baseIntersection;


/***/ }),

/***/ "./node_modules/lodash/_castArrayLikeObject.js":
/*!*****************************************************!*\
  !*** ./node_modules/lodash/_castArrayLikeObject.js ***!
  \*****************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var isArrayLikeObject = __webpack_require__(/*! ./isArrayLikeObject */ "./node_modules/lodash/isArrayLikeObject.js");

/**
 * Casts `value` to an empty array if it's not an array like object.
 *
 * @private
 * @param {*} value The value to inspect.
 * @returns {Array|Object} Returns the cast array-like object.
 */
function castArrayLikeObject(value) {
  return isArrayLikeObject(value) ? value : [];
}

module.exports = castArrayLikeObject;


/***/ }),

/***/ "./node_modules/lodash/fp/compact.js":
/*!*******************************************!*\
  !*** ./node_modules/lodash/fp/compact.js ***!
  \*******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var convert = __webpack_require__(/*! ./convert */ "./node_modules/lodash/fp/convert.js"),
    func = convert('compact', __webpack_require__(/*! ../compact */ "./node_modules/lodash/compact.js"), __webpack_require__(/*! ./_falseOptions */ "./node_modules/lodash/fp/_falseOptions.js"));

func.placeholder = __webpack_require__(/*! ./placeholder */ "./node_modules/lodash/fp/placeholder.js");
module.exports = func;


/***/ }),

/***/ "./node_modules/lodash/intersectionBy.js":
/*!***********************************************!*\
  !*** ./node_modules/lodash/intersectionBy.js ***!
  \***********************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var arrayMap = __webpack_require__(/*! ./_arrayMap */ "./node_modules/lodash/_arrayMap.js"),
    baseIntersection = __webpack_require__(/*! ./_baseIntersection */ "./node_modules/lodash/_baseIntersection.js"),
    baseIteratee = __webpack_require__(/*! ./_baseIteratee */ "./node_modules/lodash/_baseIteratee.js"),
    baseRest = __webpack_require__(/*! ./_baseRest */ "./node_modules/lodash/_baseRest.js"),
    castArrayLikeObject = __webpack_require__(/*! ./_castArrayLikeObject */ "./node_modules/lodash/_castArrayLikeObject.js"),
    last = __webpack_require__(/*! ./last */ "./node_modules/lodash/last.js");

/**
 * This method is like `_.intersection` except that it accepts `iteratee`
 * which is invoked for each element of each `arrays` to generate the criterion
 * by which they're compared. The order and references of result values are
 * determined by the first array. The iteratee is invoked with one argument:
 * (value).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Array
 * @param {...Array} [arrays] The arrays to inspect.
 * @param {Function} [iteratee=_.identity] The iteratee invoked per element.
 * @returns {Array} Returns the new array of intersecting values.
 * @example
 *
 * _.intersectionBy([2.1, 1.2], [2.3, 3.4], Math.floor);
 * // => [2.1]
 *
 * // The `_.property` iteratee shorthand.
 * _.intersectionBy([{ 'x': 1 }], [{ 'x': 2 }, { 'x': 1 }], 'x');
 * // => [{ 'x': 1 }]
 */
var intersectionBy = baseRest(function(arrays) {
  var iteratee = last(arrays),
      mapped = arrayMap(arrays, castArrayLikeObject);

  if (iteratee === last(mapped)) {
    iteratee = undefined;
  } else {
    mapped.pop();
  }
  return (mapped.length && mapped[0] === arrays[0])
    ? baseIntersection(mapped, baseIteratee(iteratee, 2))
    : [];
});

module.exports = intersectionBy;


/***/ }),

/***/ "./src/app/common/base-dialog/index.ts":
/*!*********************************************!*\
  !*** ./src/app/common/base-dialog/index.ts ***!
  \*********************************************/
/*! exports provided: BaseDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _base_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./base-dialog.component */ "./src/app/common/base-dialog/base-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "BaseDialogComponent", function() { return _base_dialog_component__WEBPACK_IMPORTED_MODULE_0__["BaseDialogComponent"]; });




/***/ }),

/***/ "./src/app/common/utils/executeAllPromises.ts":
/*!****************************************************!*\
  !*** ./src/app/common/utils/executeAllPromises.ts ***!
  \****************************************************/
/*! exports provided: executeAllPromises */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "executeAllPromises", function() { return executeAllPromises; });
function executeAllPromises(promises) {
    // Wrap all Promises in a Promise that will always "resolve"
    var resolvingPromises = promises.map(function (promise) {
        return new Promise(function (resolve) {
            var payload = new Array(2);
            promise.then(function (result) {
                payload[0] = result;
            })
                .catch(function (error) {
                payload[1] = error;
            })
                /*
                   * The wrapped Promise returns an array:
                   * The first position in the array holds the result (if any)
                   * The second position in the array holds the error (if any)
                   */
                .then(function () { return resolve(payload); });
        });
    });
    var results = [];
    // Execute all wrapped Promises
    return Promise.all(resolvingPromises)
        .then(function (items) {
        items.forEach(function (_a) {
            var result = _a[0], error = _a[1];
            if (result) {
                results.push({ result: result });
            }
            else {
                results.push({ error: error });
            }
        });
        return results;
    });
}


/***/ }),

/***/ "./src/app/common/utils/fileManager.ts":
/*!*********************************************!*\
  !*** ./src/app/common/utils/fileManager.ts ***!
  \*********************************************/
/*! exports provided: getFileContents */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getFileContents", function() { return getFileContents; });
function getFileContents(file) {
    return new Promise(function (resolve, reject) {
        if (typeof FileReader !== 'function') {
            reject(new Error('The file API isn\'t supported on this browser.'));
        }
        var fr = new FileReader();
        fr.onload = function (e) {
            var target = e.target;
            resolve(target ? target.result : '');
        };
        fr.readAsText(file);
    });
}


/***/ }),

/***/ "./src/app/modules/admin/admin.module.ts":
/*!***********************************************!*\
  !*** ./src/app/modules/admin/admin.module.ts ***!
  \***********************************************/
/*! exports provided: AdminModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminModule", function() { return AdminModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _ngxs_store__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @ngxs/store */ "./node_modules/@ngxs/store/fesm5/ngxs-store.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm5/http.js");
/* harmony import */ var angular_tree_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! angular-tree-component */ "./node_modules/angular-tree-component/dist/angular-tree-component.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../common */ "./src/app/common/index.ts");
/* harmony import */ var _list_view__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./list-view */ "./src/app/modules/admin/list-view/index.ts");
/* harmony import */ var _main_view__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./main-view */ "./src/app/modules/admin/main-view/index.ts");
/* harmony import */ var _page__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./page */ "./src/app/modules/admin/page/index.ts");
/* harmony import */ var _main_view_admin_service__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./main-view/admin.service */ "./src/app/modules/admin/main-view/admin.service.ts");
/* harmony import */ var _role_role_service__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./role/role.service */ "./src/app/modules/admin/role/role.service.ts");
/* harmony import */ var _privilege_privilege_service__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./privilege/privilege.service */ "./src/app/modules/admin/privilege/privilege.service.ts");
/* harmony import */ var _export_export_service__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./export/export.service */ "./src/app/modules/admin/export/export.service.ts");
/* harmony import */ var _import_import_service__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ./import/import.service */ "./src/app/modules/admin/import/import.service.ts");
/* harmony import */ var _datasecurity_userassignment_service__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./datasecurity/userassignment.service */ "./src/app/modules/admin/datasecurity/userassignment.service.ts");
/* harmony import */ var _routes__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ./routes */ "./src/app/modules/admin/routes.ts");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var _analyze_services_analyze_service__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! ../analyze/services/analyze.service */ "./src/app/modules/analyze/services/analyze.service.ts");
/* harmony import */ var _state_admin_state__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! ./state/admin.state */ "./src/app/modules/admin/state/admin.state.ts");
/* harmony import */ var _export__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(/*! ./export */ "./src/app/modules/admin/export/index.ts");
/* harmony import */ var _category_category_service__WEBPACK_IMPORTED_MODULE_21__ = __webpack_require__(/*! ./category/category.service */ "./src/app/modules/admin/category/category.service.ts");
/* harmony import */ var _user__WEBPACK_IMPORTED_MODULE_22__ = __webpack_require__(/*! ./user */ "./src/app/modules/admin/user/index.ts");
/* harmony import */ var _category__WEBPACK_IMPORTED_MODULE_23__ = __webpack_require__(/*! ./category */ "./src/app/modules/admin/category/index.ts");
/* harmony import */ var _role__WEBPACK_IMPORTED_MODULE_24__ = __webpack_require__(/*! ./role */ "./src/app/modules/admin/role/index.ts");
/* harmony import */ var _import__WEBPACK_IMPORTED_MODULE_25__ = __webpack_require__(/*! ./import */ "./src/app/modules/admin/import/index.ts");
/* harmony import */ var _privilege__WEBPACK_IMPORTED_MODULE_26__ = __webpack_require__(/*! ./privilege */ "./src/app/modules/admin/privilege/index.ts");
/* harmony import */ var _datasecurity__WEBPACK_IMPORTED_MODULE_27__ = __webpack_require__(/*! ./datasecurity */ "./src/app/modules/admin/datasecurity/index.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_28__ = __webpack_require__(/*! ../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _common_interceptor__WEBPACK_IMPORTED_MODULE_29__ = __webpack_require__(/*! ../../common/interceptor */ "./src/app/common/interceptor/index.ts");
/* harmony import */ var _common_components_sidenav__WEBPACK_IMPORTED_MODULE_30__ = __webpack_require__(/*! ../../common/components/sidenav */ "./src/app/common/components/sidenav/index.ts");
/* harmony import */ var _guards__WEBPACK_IMPORTED_MODULE_31__ = __webpack_require__(/*! ./guards */ "./src/app/modules/admin/guards/index.ts");

































var COMPONENTS = [
    _page__WEBPACK_IMPORTED_MODULE_9__["AdminPageComponent"],
    _main_view__WEBPACK_IMPORTED_MODULE_8__["AdminMainViewComponent"],
    _list_view__WEBPACK_IMPORTED_MODULE_7__["AdminListViewComponent"],
    _user__WEBPACK_IMPORTED_MODULE_22__["UserEditDialogComponent"],
    _role__WEBPACK_IMPORTED_MODULE_24__["RoleEditDialogComponent"],
    _privilege__WEBPACK_IMPORTED_MODULE_26__["PrivilegeEditDialogComponent"],
    _privilege__WEBPACK_IMPORTED_MODULE_26__["PrivilegeEditorComponent"],
    _privilege__WEBPACK_IMPORTED_MODULE_26__["PrivilegeRowComponent"],
    _datasecurity__WEBPACK_IMPORTED_MODULE_27__["SecurityGroupComponent"],
    _datasecurity__WEBPACK_IMPORTED_MODULE_27__["AddSecurityDialogComponent"],
    _datasecurity__WEBPACK_IMPORTED_MODULE_27__["DeleteDialogComponent"],
    _datasecurity__WEBPACK_IMPORTED_MODULE_27__["AddAttributeDialogComponent"],
    _datasecurity__WEBPACK_IMPORTED_MODULE_27__["FieldAttributeViewComponent"],
    _export__WEBPACK_IMPORTED_MODULE_20__["AdminExportViewComponent"],
    _export__WEBPACK_IMPORTED_MODULE_20__["AdminExportListComponent"],
    _export__WEBPACK_IMPORTED_MODULE_20__["AdminExportTreeComponent"],
    _export__WEBPACK_IMPORTED_MODULE_20__["AdminExportContentComponent"],
    _category__WEBPACK_IMPORTED_MODULE_23__["CategoryEditDialogComponent"],
    _category__WEBPACK_IMPORTED_MODULE_23__["CategoryDeleteDialogComponent"],
    _import__WEBPACK_IMPORTED_MODULE_25__["AdminImportViewComponent"],
    _import__WEBPACK_IMPORTED_MODULE_25__["AdminImportListComponent"],
    _import__WEBPACK_IMPORTED_MODULE_25__["AdminImportFileListComponent"],
    _import__WEBPACK_IMPORTED_MODULE_25__["AdminImportCategorySelectComponent"]
];
var INTERCEPTORS = [
    { provide: _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HTTP_INTERCEPTORS"], useClass: _common_interceptor__WEBPACK_IMPORTED_MODULE_29__["AddTokenInterceptor"], multi: true },
    {
        provide: _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HTTP_INTERCEPTORS"],
        useClass: _common_interceptor__WEBPACK_IMPORTED_MODULE_29__["HandleErrorInterceptor"],
        multi: true
    },
    {
        provide: _angular_common_http__WEBPACK_IMPORTED_MODULE_3__["HTTP_INTERCEPTORS"],
        useClass: _common_interceptor__WEBPACK_IMPORTED_MODULE_29__["RefreshTokenInterceptor"],
        multi: true
    }
];
var GUARDS = [_guards__WEBPACK_IMPORTED_MODULE_31__["IsAdminGuard"], _guards__WEBPACK_IMPORTED_MODULE_31__["GoToDefaultAdminPageGuard"]];
var SERVICES = [
    _common_components_sidenav__WEBPACK_IMPORTED_MODULE_30__["SidenavMenuService"],
    _main_view_admin_service__WEBPACK_IMPORTED_MODULE_10__["AdminService"],
    _user__WEBPACK_IMPORTED_MODULE_22__["UserService"],
    _common_services__WEBPACK_IMPORTED_MODULE_28__["JwtService"],
    _common_services__WEBPACK_IMPORTED_MODULE_28__["DxDataGridService"],
    _common_services__WEBPACK_IMPORTED_MODULE_28__["LocalSearchService"],
    _common_services__WEBPACK_IMPORTED_MODULE_28__["ToastService"],
    _role_role_service__WEBPACK_IMPORTED_MODULE_11__["RoleService"],
    _privilege_privilege_service__WEBPACK_IMPORTED_MODULE_12__["PrivilegeService"],
    _export_export_service__WEBPACK_IMPORTED_MODULE_13__["ExportService"],
    _import_import_service__WEBPACK_IMPORTED_MODULE_14__["ImportService"],
    _datasecurity_userassignment_service__WEBPACK_IMPORTED_MODULE_15__["UserAssignmentService"],
    _category_category_service__WEBPACK_IMPORTED_MODULE_21__["CategoryService"],
    _analyze_services_analyze_service__WEBPACK_IMPORTED_MODULE_18__["AnalyzeService"]
];
var AdminModule = /** @class */ (function () {
    function AdminModule() {
    }
    AdminModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
            imports: [
                _ngxs_store__WEBPACK_IMPORTED_MODULE_2__["NgxsModule"].forFeature([_state_admin_state__WEBPACK_IMPORTED_MODULE_19__["AdminState"], _export__WEBPACK_IMPORTED_MODULE_20__["ExportPageState"], _import__WEBPACK_IMPORTED_MODULE_25__["AdminImportPageState"]]),
                _common__WEBPACK_IMPORTED_MODULE_6__["CommonModuleTs"],
                _angular_forms__WEBPACK_IMPORTED_MODULE_17__["FormsModule"],
                _angular_router__WEBPACK_IMPORTED_MODULE_5__["RouterModule"].forChild(_routes__WEBPACK_IMPORTED_MODULE_16__["routes"]),
                angular_tree_component__WEBPACK_IMPORTED_MODULE_4__["TreeModule"]
            ],
            declarations: COMPONENTS,
            entryComponents: COMPONENTS,
            providers: INTERCEPTORS.concat(SERVICES, GUARDS),
            exports: [_page__WEBPACK_IMPORTED_MODULE_9__["AdminPageComponent"]]
        })
    ], AdminModule);
    return AdminModule;
}());



/***/ }),

/***/ "./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.html":
/*!*****************************************************************************************************!*\
  !*** ./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.html ***!
  \*****************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<header class=\"base-dialog__header\">\n  <strong i18n>\n    Delete Category\n  </strong>\n</header>\n<div class=\"base-dialog__content dialog__content\" fxLayout=\"column\" fxLayoutAlign=\"start start\">\n  <strong class=\"delete-message\">Category Name: {{data.category.categoryName}}</strong>\n\n  <div class=\"delete-message\" *ngIf=\"data.category.subCategories.length > 0 && data.category.categoryId !== MY_ANALYSIS_CATEGORY_ID\" i18n>\n    Please delete the below sub-categories before deleting the category.\n  </div>\n  <div class=\"delete-message\" *ngIf=\"data.category.categoryId === MY_ANALYSIS_CATEGORY_ID\" i18n>\n    This Category cannot be deleted.\n  </div>\n\n  <div *ngIf=\"data.category.subCategories.length > 0\" class=\"categories-list\">\n    <p>\n      <strong>Sub-Categories</strong>\n    </p>\n    <ul>\n      <li *ngFor=\"let subCategory of data.category.subCategories\">\n        <div fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n          <label>{{subCategory.subCategoryName}}</label>\n          <button mat-icon-button (click)=\"deleteSubCategory(subCategory)\" matTooltip=\"Delete Sub Category\">\n            <mat-icon fontIcon=\"icon-trash\"></mat-icon>\n          </button>\n        </div>\n      </li>\n    </ul>\n  </div>\n</div>\n\n<div fxLayout=\"row\" fxLayoutAlign=\"space-between center\" class=\"base-dialog__actions\">\n  <button (click)=\"deleteCategory()\"\n          [disabled]=\"data.category.subCategories.length > 0 || data.category.categoryId == MY_ANALYSIS_CATEGORY_ID\"\n          color=\"primary\"\n          mat-raised-button\n          i18n>\n    Delete\n  </button>\n  <button mat-button mat-dialog-close i18n>Cancel</button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.scss":
/*!*****************************************************************************************************!*\
  !*** ./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.scss ***!
  \*****************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".delete-message {\n  margin-top: 10px;\n  width: 395px; }\n\nstrong {\n  font-weight: 600; }\n\n.dialog__content {\n  min-height: 60px;\n  min-width: 400px; }\n\n.dialog__content .categories-list p {\n    margin-bottom: 0; }\n\n.dialog__content .categories-list ul {\n    margin: 0 0 10px;\n    width: 350px;\n    max-height: 300px;\n    overflow-y: auto; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2NhdGVnb3J5L2NhdGVnb3J5LWRlbGV0ZS1kaWFsb2cvY2F0ZWdvcnktZGVsZXRlLWRpYWxvZy5jb21wb25lbnQuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtFQUNFLGdCQUFnQjtFQUNoQixZQUFZLEVBQUE7O0FBR2Q7RUFDRSxnQkFBZ0IsRUFBQTs7QUFHbEI7RUFDRSxnQkFBZ0I7RUFDaEIsZ0JBQWdCLEVBQUE7O0FBRmxCO0lBTU0sZ0JBQWdCLEVBQUE7O0FBTnRCO0lBVU0sZ0JBQWdCO0lBQ2hCLFlBQVk7SUFDWixpQkFBaUI7SUFDakIsZ0JBQWdCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FkbWluL2NhdGVnb3J5L2NhdGVnb3J5LWRlbGV0ZS1kaWFsb2cvY2F0ZWdvcnktZGVsZXRlLWRpYWxvZy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIi5kZWxldGUtbWVzc2FnZSB7XG4gIG1hcmdpbi10b3A6IDEwcHg7XG4gIHdpZHRoOiAzOTVweDtcbn1cblxuc3Ryb25nIHtcbiAgZm9udC13ZWlnaHQ6IDYwMDtcbn1cblxuLmRpYWxvZ19fY29udGVudCB7XG4gIG1pbi1oZWlnaHQ6IDYwcHg7XG4gIG1pbi13aWR0aDogNDAwcHg7XG5cbiAgLmNhdGVnb3JpZXMtbGlzdCB7XG4gICAgcCB7XG4gICAgICBtYXJnaW4tYm90dG9tOiAwO1xuICAgIH1cblxuICAgIHVsIHtcbiAgICAgIG1hcmdpbjogMCAwIDEwcHg7XG4gICAgICB3aWR0aDogMzUwcHg7XG4gICAgICBtYXgtaGVpZ2h0OiAzMDBweDtcbiAgICAgIG92ZXJmbG93LXk6IGF1dG87XG4gICAgfVxuICB9XG59XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.ts":
/*!***************************************************************************************************!*\
  !*** ./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.ts ***!
  \***************************************************************************************************/
/*! exports provided: CategoryDeleteDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CategoryDeleteDialogComponent", function() { return CategoryDeleteDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _category_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../category.service */ "./src/app/modules/admin/category/category.service.ts");
/* harmony import */ var _common_base_dialog__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../../common/base-dialog */ "./src/app/common/base-dialog/index.ts");





var MY_ANALYSIS_CATEGORY_ID = 3;
var CategoryDeleteDialogComponent = /** @class */ (function (_super) {
    tslib__WEBPACK_IMPORTED_MODULE_0__["__extends"](CategoryDeleteDialogComponent, _super);
    function CategoryDeleteDialogComponent(_categoryService, _dialogRef, data) {
        var _this = _super.call(this) || this;
        _this._categoryService = _categoryService;
        _this._dialogRef = _dialogRef;
        _this.data = data;
        _this.MY_ANALYSIS_CATEGORY_ID = MY_ANALYSIS_CATEGORY_ID;
        return _this;
    }
    CategoryDeleteDialogComponent.prototype.deleteCategory = function () {
        var _a = this.data, customerId = _a.customerId, masterLoginId = _a.masterLoginId, category = _a.category;
        var params = {
            customerId: customerId,
            masterLoginId: masterLoginId,
            categoryId: category.categoryId,
            categoryCode: category.categoryCode
        };
        this._dialogRef.close(params);
    };
    CategoryDeleteDialogComponent.prototype.deleteSubCategory = function (subCategory) {
        var _this = this;
        var _a = this.data, customerId = _a.customerId, masterLoginId = _a.masterLoginId, category = _a.category;
        var params = {
            customerId: customerId,
            masterLoginId: masterLoginId,
            categoryId: subCategory.subCategoryId,
            categoryCode: category.categoryCode
        };
        this._categoryService.removeSubCategory(params).then(function (subCategories) {
            _this.data.category.subCategories = subCategories;
        });
    };
    CategoryDeleteDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'delete-edit-dialog',
            template: __webpack_require__(/*! ./category-delete-dialog.component.html */ "./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.html"),
            styles: [__webpack_require__(/*! ./category-delete-dialog.component.scss */ "./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](2, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_category_service__WEBPACK_IMPORTED_MODULE_3__["CategoryService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], CategoryDeleteDialogComponent);
    return CategoryDeleteDialogComponent;
}(_common_base_dialog__WEBPACK_IMPORTED_MODULE_4__["BaseDialogComponent"]));



/***/ }),

/***/ "./src/app/modules/admin/category/category-delete-dialog/index.ts":
/*!************************************************************************!*\
  !*** ./src/app/modules/admin/category/category-delete-dialog/index.ts ***!
  \************************************************************************/
/*! exports provided: CategoryDeleteDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _category_delete_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./category-delete-dialog.component */ "./src/app/modules/admin/category/category-delete-dialog/category-delete-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CategoryDeleteDialogComponent", function() { return _category_delete_dialog_component__WEBPACK_IMPORTED_MODULE_0__["CategoryDeleteDialogComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.html":
/*!********************************************************************************************!*\
  !*** ./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.html ***!
  \********************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<header class=\"base-dialog__header\">\n  <strong [ngSwitch]=\"data.mode\">\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Add New Category or Sub-Category\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Edit Category\n    </ng-container>\n  </strong>\n</header>\n<div class=\"base-dialog__content\" fxLayout=\"column\" fxLayoutAlign=\"center center\">\n  <div style=\"width: 100%;\">\n    <form [formGroup]=\"formGroup\" fxLayout=\"column\" fxLayoutAlign=\"center start\">\n\n      <mat-button-toggle-group *ngIf=\"data.mode === 'create'\"\n                               formControlName=\"subCategoryInd\"\n                               style=\"margin-bottom: 10px;\">\n        <mat-button-toggle color=\"primary\" [value]=\"false\">Category</mat-button-toggle>\n        <mat-button-toggle color=\"primary\" [value]=\"true\">Sub-Category</mat-button-toggle>\n      </mat-button-toggle-group>\n\n      <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n        <mat-label i18n>Product</mat-label>\n        <mat-select required formControlName=\"productId\">\n          <mat-option *ngFor=\"let product of products$ | async\" [value]=\"product.productId\">\n            {{product.productName}}\n          </mat-option>\n        </mat-select>\n      </mat-form-field>\n\n      <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n        <mat-label i18n>Module</mat-label>\n        <mat-select required formControlName=\"moduleId\">\n          <mat-option *ngFor=\"let module of modules$ | async\" [value]=\"module.moduleId\">\n            {{module.moduleName}}\n          </mat-option>\n        </mat-select>\n      </mat-form-field>\n\n      <mat-form-field *ngIf=\"formGroup.controls.subCategoryInd.value\"\n                      class=\"select-form-field\" appearance=\"outline\"\n      >\n        <mat-label i18n>Category</mat-label>\n        <mat-select required formControlName=\"categoryId\">\n          <mat-option *ngFor=\"let category of categories$ | async\" [value]=\"category.categoryId\">\n            {{category.categoryName}}\n          </mat-option>\n        </mat-select>\n      </mat-form-field>\n\n      <mat-form-field>\n        <input matInput required type=\"text\"\n               formControlName=\"categoryName\"\n               autocomplete=\"off\"\n               [placeholder]=\"formGroup.controls.subCategoryInd.value ? 'Sub-Category Name' : 'Category Name'\" />\n      </mat-form-field>\n\n      <mat-form-field>\n        <input matInput type=\"text\"\n               formControlName=\"categoryDesc\"\n               autocomplete=\"off\"\n               placeholder=\"Description\" />\n      </mat-form-field>\n\n      <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n        <mat-label i18n>Status</mat-label>\n        <mat-select required formControlName=\"activeStatusInd\">\n          <mat-option *ngFor=\"let status of statuses\" [value]=\"status.id\">\n            {{status.name}}\n          </mat-option>\n        </mat-select>\n      </mat-form-field>\n\n    </form>\n  </div>\n\n  <div *ngIf=\"isInWideMode\" class=\"sub-categories-form\">\n    <div style=\"margin: 0 0 10px 0;\">\n      <strong i18n>Select one of the sub-categories to edit</strong>\n    </div>\n    <form [formGroup]=\"subCategoryFormGroup\"\n          fxLayout=\"row\"\n          fxLayoutAlign=\"space-between start\"\n    >\n      <mat-radio-group formControlName=\"selectedSubCategory\"\n      >\n        <mat-radio-button *ngFor=\"let subCategory of subCategories\"\n                          [value]=\"subCategory\"\n        >\n          {{subCategory.subCategoryName}}\n        </mat-radio-button>\n      </mat-radio-group>\n\n      <div fxLayout=\"column\"\n          fxLayoutAlign=\"start start\"\n          style=\"width: 100%;\"\n          formGroupName=\"categoryFormGroup\">\n        <mat-form-field>\n          <input matInput\n                 required\n                 type=\"text\"\n                 autocomplete=\"off\"\n                 formControlName=\"subCategoryName\"\n                 placeholder=\"Sub-Category Name\" />\n        </mat-form-field>\n\n        <mat-form-field>\n          <input matInput\n                 type=\"text\"\n                 autocomplete=\"off\"\n                 formControlName=\"subCategoryDesc\"\n                 placeholder=\"Description\" />\n        </mat-form-field>\n\n        <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n          <mat-label>Status</mat-label>\n          <mat-select required formControlName=\"activestatusInd\">\n            <mat-option *ngFor=\"let status of statuses\" [value]=\"status.id\">\n              {{status.name}}\n            </mat-option>\n          </mat-select>\n        </mat-form-field>\n      </div>\n    </form>\n  </div>\n</div>\n\n<div fxLayout=\"row\" fxLayoutAlign=\"space-between center\" class=\"base-dialog__actions\">\n  <button (click)=\"create()\" [disabled]=\"!(subCaegoryFormIsValid && formIsValid)\" color=\"primary\" [ngSwitch]=\"data.mode\" mat-raised-button>\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Create Category\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Save\n    </ng-container>\n  </button>\n  <button mat-button mat-dialog-close i18n>Cancel</button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.scss":
/*!********************************************************************************************!*\
  !*** ./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.scss ***!
  \********************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  display: block;\n  max-width: 700px;\n  width: 700px; }\n\n.wide {\n  max-width: 700px;\n  width: 700px; }\n\nstrong {\n  font-weight: 600; }\n\n.base-dialog__content {\n  padding: 10px 40px !important; }\n\n.base-dialog__content .mat-button-toggle.mat-button-toggle-checked {\n    background-color: #d7eafa; }\n\n.base-dialog__content .sub-categories-form {\n    margin-top: 20px;\n    width: 620px; }\n\n.base-dialog__content .sub-categories-list {\n    overflow-y: auto;\n    max-height: 200px; }\n\n.base-dialog__content .mat-form-field {\n    width: 100%; }\n\n.base-dialog__content .form-field {\n    margin-bottom: 5px; }\n\n.base-dialog__content .mat-radio-group {\n    margin-right: 5px;\n    min-width: 210px;\n    width: 210px;\n    overflow-y: auto;\n    max-height: 160px;\n    min-height: 160px; }\n\n.base-dialog__content .mat-radio-group .mat-radio-button {\n      margin-top: 5px;\n      margin-bottom: 5px;\n      max-width: 190px; }\n\n.base-dialog__content .mat-radio-group .mat-radio-button .mat-radio-label-content {\n        word-break: break-all;\n        width: 160px;\n        white-space: normal; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2NhdGVnb3J5L2NhdGVnb3J5LWRpYWxvZy9jYXRlZ29yeS1lZGl0LWRpYWxvZy5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLGNBQWM7RUFDZCxnQkFBZ0I7RUFDaEIsWUFBWSxFQUFBOztBQUdkO0VBQ0UsZ0JBQWdCO0VBQ2hCLFlBQVksRUFBQTs7QUFHZDtFQUNFLGdCQUFnQixFQUFBOztBQUdsQjtFQUNFLDZCQUE2QixFQUFBOztBQUQvQjtJQUlJLHlCQ2IwQixFQUFBOztBRFM5QjtJQVFJLGdCQUFnQjtJQUNoQixZQUFZLEVBQUE7O0FBVGhCO0lBYUksZ0JBQWdCO0lBQ2hCLGlCQUFpQixFQUFBOztBQWRyQjtJQWtCSSxXQUFXLEVBQUE7O0FBbEJmO0lBc0JJLGtCQUFrQixFQUFBOztBQXRCdEI7SUEwQkksaUJBQWlCO0lBQ2pCLGdCQUFnQjtJQUNoQixZQUFZO0lBQ1osZ0JBQWdCO0lBQ2hCLGlCQUFpQjtJQUNqQixpQkFBaUIsRUFBQTs7QUEvQnJCO01Ba0NNLGVBQWU7TUFDZixrQkFBa0I7TUFDbEIsZ0JBQWdCLEVBQUE7O0FBcEN0QjtRQXVDUSxxQkFBcUI7UUFDckIsWUFBWTtRQUNaLG1CQUFtQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hZG1pbi9jYXRlZ29yeS9jYXRlZ29yeS1kaWFsb2cvY2F0ZWdvcnktZWRpdC1kaWFsb2cuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0IFwic3JjL3RoZW1lcy9iYXNlL2NvbG9yc1wiO1xuXG46aG9zdCB7XG4gIGRpc3BsYXk6IGJsb2NrO1xuICBtYXgtd2lkdGg6IDcwMHB4O1xuICB3aWR0aDogNzAwcHg7XG59XG5cbi53aWRlIHtcbiAgbWF4LXdpZHRoOiA3MDBweDtcbiAgd2lkdGg6IDcwMHB4O1xufVxuXG5zdHJvbmcge1xuICBmb250LXdlaWdodDogNjAwO1xufVxuXG4uYmFzZS1kaWFsb2dfX2NvbnRlbnQge1xuICBwYWRkaW5nOiAxMHB4IDQwcHggIWltcG9ydGFudDtcblxuICAubWF0LWJ1dHRvbi10b2dnbGUubWF0LWJ1dHRvbi10b2dnbGUtY2hlY2tlZCB7XG4gICAgYmFja2dyb3VuZC1jb2xvcjogJGdyaWQtaGVhZGVyLWJnLWNvbG9yO1xuICB9XG5cbiAgLnN1Yi1jYXRlZ29yaWVzLWZvcm0ge1xuICAgIG1hcmdpbi10b3A6IDIwcHg7XG4gICAgd2lkdGg6IDYyMHB4O1xuICB9XG5cbiAgLnN1Yi1jYXRlZ29yaWVzLWxpc3Qge1xuICAgIG92ZXJmbG93LXk6IGF1dG87XG4gICAgbWF4LWhlaWdodDogMjAwcHg7XG4gIH1cblxuICAubWF0LWZvcm0tZmllbGQge1xuICAgIHdpZHRoOiAxMDAlO1xuICB9XG5cbiAgLmZvcm0tZmllbGQge1xuICAgIG1hcmdpbi1ib3R0b206IDVweDtcbiAgfVxuXG4gIC5tYXQtcmFkaW8tZ3JvdXAge1xuICAgIG1hcmdpbi1yaWdodDogNXB4O1xuICAgIG1pbi13aWR0aDogMjEwcHg7XG4gICAgd2lkdGg6IDIxMHB4O1xuICAgIG92ZXJmbG93LXk6IGF1dG87XG4gICAgbWF4LWhlaWdodDogMTYwcHg7XG4gICAgbWluLWhlaWdodDogMTYwcHg7XG5cbiAgICAubWF0LXJhZGlvLWJ1dHRvbiB7XG4gICAgICBtYXJnaW4tdG9wOiA1cHg7XG4gICAgICBtYXJnaW4tYm90dG9tOiA1cHg7XG4gICAgICBtYXgtd2lkdGg6IDE5MHB4O1xuXG4gICAgICAubWF0LXJhZGlvLWxhYmVsLWNvbnRlbnQge1xuICAgICAgICB3b3JkLWJyZWFrOiBicmVhay1hbGw7XG4gICAgICAgIHdpZHRoOiAxNjBweDtcbiAgICAgICAgd2hpdGUtc3BhY2U6IG5vcm1hbDtcbiAgICAgIH1cbiAgICB9XG4gIH1cbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.ts":
/*!******************************************************************************************!*\
  !*** ./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.ts ***!
  \******************************************************************************************/
/*! exports provided: CategoryEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CategoryEditDialogComponent", function() { return CategoryEditDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/fp/pipe */ "./node_modules/lodash/fp/pipe.js");
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/fp/filter */ "./node_modules/lodash/fp/filter.js");
/* harmony import */ var lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/fp/map */ "./node_modules/lodash/fp/map.js");
/* harmony import */ var lodash_fp_map__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var lodash_fp_omit__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! lodash/fp/omit */ "./node_modules/lodash/fp/omit.js");
/* harmony import */ var lodash_fp_omit__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_omit__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var _category_service__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../category.service */ "./src/app/modules/admin/category/category.service.ts");
/* harmony import */ var _common_base_dialog__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../../common/base-dialog */ "./src/app/common/base-dialog/index.ts");











var namePattern = /^[a-zA-Z\s]*$/;
var CategoryEditDialogComponent = /** @class */ (function (_super) {
    tslib__WEBPACK_IMPORTED_MODULE_0__["__extends"](CategoryEditDialogComponent, _super);
    function CategoryEditDialogComponent(_categoryService, _fb, _dialogRef, data) {
        var _this = _super.call(this) || this;
        _this._categoryService = _categoryService;
        _this._fb = _fb;
        _this._dialogRef = _dialogRef;
        _this.data = data;
        _this.isInWideMode = false;
        _this.formIsValid = false;
        _this.subCaegoryFormIsValid = true;
        _this.subCategories = [];
        _this.isSubCategoryModified = false;
        _this.subCategoryFlag = false;
        _this.isNewSubCategorySelecting = false;
        _this.statuses = [
            {
                id: 1,
                value: 'Active',
                name: 'ACTIVE'
            },
            {
                id: 0,
                value: 'Inactive',
                name: 'INACTIVE'
            }
        ];
        if (_this.data.mode === 'edit') {
            _this.formIsValid = true;
            var _a = _this.data.model, productId = _a.productId, moduleId = _a.moduleId, subCategories = _a.subCategories;
            _this.modules$ = _this.loadModules(productId, _this.data.formDeps);
            _this.categories$ = _this.loadParentCategories(moduleId, _this.data.formDeps);
            _this.subCategories = subCategories;
            _this.isInWideMode = subCategories && subCategories.length > 0;
        }
        var customerId = data.formDeps.customerId;
        _this.products$ = _this._categoryService.getProducts(customerId);
        _this.createForm(_this.data.model);
        return _this;
    }
    CategoryEditDialogComponent.prototype.create = function () {
        var _this = this;
        var formValues = this.formGroup.getRawValue();
        var model;
        switch (this.data.mode) {
            case 'create':
                model = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, this.data.model, formValues);
                if (!model.subCategoryInd) {
                    model.categoryId = null;
                }
                break;
            case 'edit':
                var subCategories = lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6__('modifiedFlag'), lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__(lodash_fp_omit__WEBPACK_IMPORTED_MODULE_8__('modifiedFlag')))(this.subCategories);
                model = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, this.data.model, formValues, { subCategories: subCategories });
                break;
        }
        if (model.subCategoryInd) {
            this.categories$.then(function (categories) {
                var target = lodash_find__WEBPACK_IMPORTED_MODULE_4__(categories, function (_a) {
                    var categoryId = _a.categoryId;
                    return categoryId === model.categoryId;
                });
                if (target) {
                    model.categoryType = target.categoryType;
                    model.categoryCode = target.categoryCode;
                }
                _this.save(model);
            });
        }
        else {
            this.save(model);
        }
    };
    CategoryEditDialogComponent.prototype.save = function (model) {
        var _this = this;
        var actionPromise;
        switch (this.data.mode) {
            case 'edit':
                actionPromise = this._categoryService.update(model);
                break;
            case 'create':
                actionPromise = this._categoryService.save(model);
                break;
        }
        actionPromise &&
            actionPromise.then(function (rows) {
                if (rows) {
                    _this._dialogRef.close(rows);
                }
            });
    };
    CategoryEditDialogComponent.prototype.createForm = function (formModel) {
        var _this = this;
        var _a = formModel.subCategoryInd, subCategoryInd = _a === void 0 ? false : _a, _b = formModel.productId, productId = _b === void 0 ? '' : _b, _c = formModel.moduleId, moduleId = _c === void 0 ? '' : _c, _d = formModel.categoryId, categoryId = _d === void 0 ? '' : _d, _e = formModel.categoryName, categoryName = _e === void 0 ? '' : _e, _f = formModel.categoryDesc, categoryDesc = _f === void 0 ? '' : _f, _g = formModel.activeStatusInd, activeStatusInd = _g === void 0 ? 1 : _g;
        var productIdControl = this._fb.control(productId, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required);
        var subCategoryIndControl = this._fb.control(subCategoryInd);
        var moduleIdControl = this._fb.control({ value: moduleId, disabled: true }, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required);
        var categoryIdControl = this._fb.control({
            value: categoryId,
            disabled: true
        });
        this.formGroup = this._fb.group({
            subCategoryInd: subCategoryIndControl,
            productId: productIdControl,
            moduleId: moduleIdControl,
            categoryId: categoryIdControl,
            categoryName: [
                categoryName,
                [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].pattern(namePattern)]
            ],
            categoryDesc: categoryDesc,
            activeStatusInd: [activeStatusInd, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required]
        });
        this.formGroup.statusChanges.subscribe(function (change) {
            if (change === 'VALID') {
                _this.formIsValid = true;
            }
            else {
                _this.formIsValid = false;
            }
        });
        productIdControl.valueChanges.subscribe(function (pId) {
            _this.modules$ = _this.loadModules(pId, _this.data.formDeps).then(function (modules) {
                moduleIdControl.enable();
                return modules;
            });
        });
        moduleIdControl.valueChanges.subscribe(function (mId) {
            _this.categories$ = _this.loadParentCategories(mId, _this.data.formDeps).then(function (categories) {
                categoryIdControl.enable();
                return categories;
            });
        });
        if (this.data.mode === 'edit') {
            this.createSubCategoryForm(formModel);
        }
        else {
            subCategoryIndControl.valueChanges.subscribe(function (isSubCategory) {
                if (isSubCategory) {
                    categoryIdControl.setValidators(_angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required);
                }
                else {
                    categoryIdControl.setValidators(_angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].nullValidator);
                }
            });
        }
    };
    CategoryEditDialogComponent.prototype.loadModules = function (productId, formDeps) {
        var customerId = formDeps.customerId;
        var moduleParams = {
            customerId: customerId,
            productId: productId,
            moduleId: 0
        };
        return this._categoryService.getModules(moduleParams);
    };
    CategoryEditDialogComponent.prototype.loadParentCategories = function (moduleId, formDeps) {
        var customerId = formDeps.customerId;
        var categoryParams = {
            customerId: customerId,
            productId: 0,
            moduleId: moduleId
        };
        return this._categoryService.getParentCategories(categoryParams);
    };
    CategoryEditDialogComponent.prototype.createSubCategoryForm = function (model) {
        var _this = this;
        var subCategories = model.subCategories;
        if (!(subCategories && subCategories.length > 0)) {
            return;
        }
        this.subCategoryFlag = true;
        var selected = subCategories[0];
        var selectedControl = this._fb.control(selected);
        var categoryControl = this._fb.group({
            subCategoryName: [
                selected.subCategoryName,
                [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].pattern(namePattern)]
            ],
            subCategoryDesc: selected.subCategoryDesc,
            activestatusInd: [selected.activestatusInd, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required]
        });
        this.subCategoryFormGroup = this._fb.group({
            selectedSubCategory: selectedControl,
            categoryFormGroup: categoryControl
        });
        selectedControl.valueChanges.subscribe(function (value) {
            var subCategoryName = value.subCategoryName, subCategoryDesc = value.subCategoryDesc, activestatusInd = value.activestatusInd;
            _this.isNewSubCategorySelecting = true;
            categoryControl.setValue({
                subCategoryName: subCategoryName,
                subCategoryDesc: subCategoryDesc,
                activestatusInd: activestatusInd
            });
        });
        categoryControl.valueChanges.subscribe(function (values) {
            if (_this.isNewSubCategorySelecting) {
                _this.isNewSubCategorySelecting = false;
                return;
            }
            var target = lodash_find__WEBPACK_IMPORTED_MODULE_4__(subCategories, function (cat) { return cat === selectedControl.value; });
            target.subCategoryName = values.subCategoryName;
            target.subCategoryDesc = values.subCategoryDesc;
            target.activestatusInd = values.activestatusInd;
            target.modifiedFlag = true;
            if (!_this.isSubCategoryModified) {
                _this.isSubCategoryModified = true;
                selectedControl.disable();
            }
        });
        categoryControl.statusChanges.subscribe(function (validity) {
            if (selectedControl.enabled) {
                if (validity === 'INVALID') {
                    if (!_this.isSubCategoryModified) {
                        selectedControl.disable();
                    }
                    _this.subCaegoryFormIsValid = false;
                }
            }
            else {
                if (validity === 'VALID') {
                    if (!_this.isSubCategoryModified) {
                        selectedControl.enable();
                    }
                    _this.subCaegoryFormIsValid = true;
                }
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["HostBinding"])('class.wide'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], CategoryEditDialogComponent.prototype, "isInWideMode", void 0);
    CategoryEditDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'category-edit-dialog',
            template: __webpack_require__(/*! ./category-edit-dialog.component.html */ "./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.html"),
            styles: [__webpack_require__(/*! ./category-edit-dialog.component.scss */ "./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](3, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_category_service__WEBPACK_IMPORTED_MODULE_9__["CategoryService"],
            _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], CategoryEditDialogComponent);
    return CategoryEditDialogComponent;
}(_common_base_dialog__WEBPACK_IMPORTED_MODULE_10__["BaseDialogComponent"]));



/***/ }),

/***/ "./src/app/modules/admin/category/category-dialog/index.ts":
/*!*****************************************************************!*\
  !*** ./src/app/modules/admin/category/category-dialog/index.ts ***!
  \*****************************************************************/
/*! exports provided: CategoryEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _category_edit_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./category-edit-dialog.component */ "./src/app/modules/admin/category/category-dialog/category-edit-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CategoryEditDialogComponent", function() { return _category_edit_dialog_component__WEBPACK_IMPORTED_MODULE_0__["CategoryEditDialogComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/category/category.service.ts":
/*!************************************************************!*\
  !*** ./src/app/modules/admin/category/category.service.ts ***!
  \************************************************************/
/*! exports provided: CategoryService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CategoryService", function() { return CategoryService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _main_view_admin_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../main-view/admin.service */ "./src/app/modules/admin/main-view/admin.service.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");





var CategoryService = /** @class */ (function () {
    function CategoryService(_adminService, _jwtService) {
        this._adminService = _adminService;
        this._jwtService = _jwtService;
        var token = _jwtService.getTokenObj();
        var customerId = token.ticket.custID;
        this.customerId = customerId;
    }
    CategoryService.prototype.getList$ = function () {
        var customerId = parseInt(this.customerId, 10);
        return this._adminService
            .request('categories/fetch', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (resp) { return resp.categories; }));
    };
    CategoryService.prototype.getList = function () {
        return this.getList$().toPromise();
    };
    CategoryService.prototype.save = function (user) {
        var options = {
            toast: { successMsg: 'Category is successfully added' }
        };
        return this._adminService
            .request('categories/add', user, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (resp) { return (resp.valid ? resp.categories : null); }))
            .toPromise();
    };
    CategoryService.prototype.remove = function (params) {
        var options = {
            toast: { successMsg: 'Category is successfully deleted' }
        };
        return this._adminService
            .request('categories/delete', params, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (resp) { return (resp.valid ? resp.categories : null); }))
            .toPromise();
    };
    CategoryService.prototype.removeSubCategory = function (params) {
        var options = {
            toast: { successMsg: 'Subcategory is successfully deleted' }
        };
        return this._adminService
            .request('subcategories/delete', params, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (resp) { return (resp.valid ? resp.subCategories : null); }))
            .toPromise();
    };
    CategoryService.prototype.update = function (user) {
        var options = {
            toast: { successMsg: 'Category is successfully Updated' }
        };
        return this._adminService
            .request('categories/edit', user, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (resp) { return (resp.valid ? resp.categories : null); }))
            .toPromise();
    };
    CategoryService.prototype.getProducts = function (customerId) {
        return this._adminService
            .request('products/list', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (resp) { return resp.products; }))
            .toPromise();
    };
    CategoryService.prototype.getModules = function (params) {
        return this._adminService
            .request('modules/list', params)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (resp) { return resp.modules; }))
            .toPromise();
    };
    CategoryService.prototype.getParentCategories = function (params) {
        return this._adminService
            .request('categories/parent/list', params)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (resp) { return resp.category; }))
            .toPromise();
    };
    CategoryService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_main_view_admin_service__WEBPACK_IMPORTED_MODULE_2__["AdminService"],
            _common_services__WEBPACK_IMPORTED_MODULE_3__["JwtService"]])
    ], CategoryService);
    return CategoryService;
}());



/***/ }),

/***/ "./src/app/modules/admin/category/index.ts":
/*!*************************************************!*\
  !*** ./src/app/modules/admin/category/index.ts ***!
  \*************************************************/
/*! exports provided: CategoryEditDialogComponent, CategoryDeleteDialogComponent, CategoryService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _category_dialog__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./category-dialog */ "./src/app/modules/admin/category/category-dialog/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CategoryEditDialogComponent", function() { return _category_dialog__WEBPACK_IMPORTED_MODULE_0__["CategoryEditDialogComponent"]; });

/* harmony import */ var _category_delete_dialog__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./category-delete-dialog */ "./src/app/modules/admin/category/category-delete-dialog/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CategoryDeleteDialogComponent", function() { return _category_delete_dialog__WEBPACK_IMPORTED_MODULE_1__["CategoryDeleteDialogComponent"]; });

/* harmony import */ var _category_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./category.service */ "./src/app/modules/admin/category/category.service.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CategoryService", function() { return _category_service__WEBPACK_IMPORTED_MODULE_2__["CategoryService"]; });






/***/ }),

/***/ "./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.html":
/*!*****************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.html ***!
  \*****************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<header class=\"base-dialog__header\">\n  <strong [ngSwitch]=\"data.mode\">\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Add Attribute\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Edit Attribute\n    </ng-container>\n  </strong>\n</header>\n<div class=\"base-dialog__content\">\n  <div fxLayout=\"column\" fxLayoutAlign=\"center start\">\n    <div class=\"errors\" *ngIf=\"errorState\">\n      <span [innerHtml]=\"errorMessage\"></span>\n    </div>\n    <mat-form-field>\n      <input matInput placeholder=\"Security Group Name\" [(ngModel)]=\"data.groupSelected.securityGroupName\" disabled required/>\n    </mat-form-field>\n    <div fxLayout=\"row\" fxLayoutAlign=\"center start\">\n      <mat-form-field>\n        <input matInput [(ngModel)]=\"data.attributeName\" placeholder=\"Field Name\" maxlength=\"20\" [disabled]=\"data.mode === 'edit'\" required/>\n      </mat-form-field>\n\n      <span class=\"seperator\">=</span>\n\n      <mat-form-field>\n        <input matInput [(ngModel)]=\"data.value\" maxlength=\"45\" placeholder=\"Value\" required/>\n      </mat-form-field>\n    </div>\n  </div>\n</div>\n\n<div fxLayout=\"row\" fxLayoutAlign=\"space-between center\" class=\"base-dialog__actions\">\n  <button (click)=\"submit()\"\n          e2e=\"create-analysis-btn\"\n          color=\"primary\"\n          [ngSwitch]=\"data.mode\"\n          [disabled]=\"!data.attributeName || !data.value\"\n          mat-raised-button\n  >\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Create Attribute\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Save\n    </ng-container>\n  </button>\n  <button mat-button\n          mat-dialog-close i18n\n  >Cancel</button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.scss":
/*!*****************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.scss ***!
  \*****************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "::ng-deep add-attribute-dialog {\n  max-width: 860px;\n  width: 860px; }\n  ::ng-deep add-attribute-dialog .base-dialog__header {\n    padding: 0 0 10px;\n    border-bottom: 1px solid #999; }\n  ::ng-deep add-attribute-dialog .base-dialog__header strong {\n      font-size: 20px;\n      color: #5C6670; }\n  ::ng-deep add-attribute-dialog .base-dialog__content {\n    padding: 10px !important;\n    max-height: 450px; }\n  ::ng-deep add-attribute-dialog .base-dialog__content .mat-form-field {\n      width: 100%; }\n  ::ng-deep add-attribute-dialog .base-dialog__content .form-field {\n      margin-bottom: 5px; }\n  ::ng-deep add-attribute-dialog .base-dialog__content div .errors {\n      width: 100%;\n      text-align: center;\n      color: red; }\n  ::ng-deep add-attribute-dialog .base-dialog__content .seperator {\n      width: 100px;\n      margin: 19px 0 0 27px;\n      font-weight: bold; }\n  ::ng-deep add-attribute-dialog .base-dialog__actions {\n    padding-top: 10px;\n    border-top: 1px solid #999; }\n  ::ng-deep add-attribute-dialog .red {\n    color: #E53935; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2RhdGFzZWN1cml0eS9hZGQtYXR0cmlidXRlLWRpYWxvZy9hZGQtYXR0cmlidXRlLWRpYWxvZy5jb21wb25lbnQuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLGdCQUFnQjtFQUNoQixZQUFZLEVBQUE7RUFGZDtJQUtJLGlCQUFpQjtJQUNqQiw2QkFBNkIsRUFBQTtFQU5qQztNQVNNLGVBQWU7TUFDZixjQUFjLEVBQUE7RUFWcEI7SUFlSSx3QkFBd0I7SUFDeEIsaUJBQWlCLEVBQUE7RUFoQnJCO01BbUJNLFdBQVcsRUFBQTtFQW5CakI7TUF1Qk0sa0JBQWtCLEVBQUE7RUF2QnhCO01BMkJNLFdBQVc7TUFDWCxrQkFBa0I7TUFDbEIsVUFBVSxFQUFBO0VBN0JoQjtNQWlDTSxZQUFZO01BQ1oscUJBQXFCO01BQ3JCLGlCQUFpQixFQUFBO0VBbkN2QjtJQXdDSSxpQkFBaUI7SUFDakIsMEJBQTBCLEVBQUE7RUF6QzlCO0lBNkNJLGNBQWMsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYWRtaW4vZGF0YXNlY3VyaXR5L2FkZC1hdHRyaWJ1dGUtZGlhbG9nL2FkZC1hdHRyaWJ1dGUtZGlhbG9nLmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiQGltcG9ydCBcInNyYy90aGVtZXMvYmFzZS9jb2xvcnNcIjtcblxuOjpuZy1kZWVwIGFkZC1hdHRyaWJ1dGUtZGlhbG9nIHtcbiAgbWF4LXdpZHRoOiA4NjBweDtcbiAgd2lkdGg6IDg2MHB4O1xuXG4gIC5iYXNlLWRpYWxvZ19faGVhZGVyIHtcbiAgICBwYWRkaW5nOiAwIDAgMTBweDtcbiAgICBib3JkZXItYm90dG9tOiAxcHggc29saWQgIzk5OTtcblxuICAgIHN0cm9uZyB7XG4gICAgICBmb250LXNpemU6IDIwcHg7XG4gICAgICBjb2xvcjogIzVDNjY3MDtcbiAgICB9XG4gIH1cblxuICAuYmFzZS1kaWFsb2dfX2NvbnRlbnQge1xuICAgIHBhZGRpbmc6IDEwcHggIWltcG9ydGFudDtcbiAgICBtYXgtaGVpZ2h0OiA0NTBweDtcblxuICAgIC5tYXQtZm9ybS1maWVsZCB7XG4gICAgICB3aWR0aDogMTAwJTtcbiAgICB9XG5cbiAgICAuZm9ybS1maWVsZCB7XG4gICAgICBtYXJnaW4tYm90dG9tOiA1cHg7XG4gICAgfVxuXG4gICAgZGl2IC5lcnJvcnMge1xuICAgICAgd2lkdGg6IDEwMCU7XG4gICAgICB0ZXh0LWFsaWduOiBjZW50ZXI7XG4gICAgICBjb2xvcjogcmVkO1xuICAgIH1cblxuICAgIC5zZXBlcmF0b3Ige1xuICAgICAgd2lkdGg6IDEwMHB4O1xuICAgICAgbWFyZ2luOiAxOXB4IDAgMCAyN3B4O1xuICAgICAgZm9udC13ZWlnaHQ6IGJvbGQ7XG4gICAgfVxuICB9XG5cbiAgLmJhc2UtZGlhbG9nX19hY3Rpb25zIHtcbiAgICBwYWRkaW5nLXRvcDogMTBweDtcbiAgICBib3JkZXItdG9wOiAxcHggc29saWQgIzk5OTtcbiAgfVxuXG4gIC5yZWQge1xuICAgIGNvbG9yOiAjRTUzOTM1O1xuICB9XG59XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.ts":
/*!***************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.ts ***!
  \***************************************************************************************************/
/*! exports provided: AddAttributeDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AddAttributeDialogComponent", function() { return AddAttributeDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _userassignment_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./../userassignment.service */ "./src/app/modules/admin/datasecurity/userassignment.service.ts");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_4__);





var AddAttributeDialogComponent = /** @class */ (function () {
    function AddAttributeDialogComponent(_dialogRef, _userAssignmentService, data) {
        this._dialogRef = _dialogRef;
        this._userAssignmentService = _userAssignmentService;
        this.data = data;
        this.attribute = {};
    }
    AddAttributeDialogComponent.prototype.hasWhiteSpace = function (field) {
        return /\s/g.test(field);
    };
    AddAttributeDialogComponent.prototype.submit = function () {
        var _this = this;
        if (this.hasWhiteSpace(this.data.attributeName)) {
            this.errorState = true;
            this.errorMessage = 'Field Name cannot contain spaces';
            return false;
        }
        this._userAssignmentService.attributetoGroup(this.data).then(function (response) {
            if (lodash_get__WEBPACK_IMPORTED_MODULE_4__(response, 'valid')) {
                _this.errorState = false;
                _this._dialogRef.close(lodash_get__WEBPACK_IMPORTED_MODULE_4__(response, 'valid'));
            }
        }).catch(function (err) {
            if (!lodash_get__WEBPACK_IMPORTED_MODULE_4__(err.error, 'valid')) {
                _this.errorState = !lodash_get__WEBPACK_IMPORTED_MODULE_4__(err.error, 'valid');
                _this.errorMessage = lodash_get__WEBPACK_IMPORTED_MODULE_4__(err.error, 'validityMessage');
            }
        });
    };
    AddAttributeDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'add-attribute-dialog',
            template: __webpack_require__(/*! ./add-attribute-dialog.component.html */ "./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.html"),
            styles: [__webpack_require__(/*! ./add-attribute-dialog.component.scss */ "./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](2, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"],
            _userassignment_service__WEBPACK_IMPORTED_MODULE_3__["UserAssignmentService"], Object])
    ], AddAttributeDialogComponent);
    return AddAttributeDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.html":
/*!***************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.html ***!
  \***************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<header class=\"base-dialog__header\">\n  <strong [ngSwitch]=\"data.mode\">\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Add Security Group\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Edit Security Group\n    </ng-container>\n  </strong>\n</header>\n<div class=\"base-dialog__content\">\n  <div fxLayout=\"column\" fxLayoutAlign=\"center start\">\n    <div class=\"errors\" *ngIf=\"errorState\">\n      <span [innerHtml]=\"errorMessage\"></span>\n    </div>\n    <mat-form-field>\n      <input matInput [(ngModel)]=\"data.securityGroupName\" placeholder=\"Security Group Name\" maxlength=\"30\" required/>\n    </mat-form-field>\n\n    <mat-form-field>\n        <textarea matInput [(ngModel)]=\"data.description\" maxlength=\"150\" placeholder=\"Description\"></textarea>\n      </mat-form-field>\n  </div>\n</div>\n\n<div fxLayout=\"row\" fxLayoutAlign=\"space-between center\" class=\"base-dialog__actions\">\n  <button (click)=\"submit()\"\n          e2e=\"create-analysis-btn\"\n          color=\"primary\"\n          [ngSwitch]=\"data.mode\"\n          [disabled]=\"!data.securityGroupName\"\n          mat-raised-button\n  >\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Create Group\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Save\n    </ng-container>\n  </button>\n  <button mat-button\n          mat-dialog-close i18n\n  >Cancel</button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.scss":
/*!***************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.scss ***!
  \***************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "::ng-deep add-secuirty-dialog {\n  max-width: 860px;\n  width: 860px; }\n  ::ng-deep add-secuirty-dialog .base-dialog__header {\n    padding: 0 0 10px;\n    border-bottom: 1px solid #999; }\n  ::ng-deep add-secuirty-dialog .base-dialog__header strong {\n      font-size: 20px;\n      color: #5C6670; }\n  ::ng-deep add-secuirty-dialog .base-dialog__content {\n    padding: 10px !important;\n    max-height: 450px; }\n  ::ng-deep add-secuirty-dialog .base-dialog__content .mat-form-field {\n      width: 500px; }\n  ::ng-deep add-secuirty-dialog .base-dialog__content .form-field {\n      margin-bottom: 5px; }\n  ::ng-deep add-secuirty-dialog .base-dialog__content div .errors {\n      width: 100%;\n      text-align: center;\n      color: red; }\n  ::ng-deep add-secuirty-dialog .base-dialog__actions {\n    padding-top: 10px;\n    border-top: 1px solid #999; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2RhdGFzZWN1cml0eS9hZGQtc2VjdXJpdHktZGlhbG9nL2FkZC1zZWN1cml0eS1kaWFsb2cuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxnQkFBZ0I7RUFDaEIsWUFBWSxFQUFBO0VBRmQ7SUFLSSxpQkFBaUI7SUFDakIsNkJBQTZCLEVBQUE7RUFOakM7TUFTTSxlQUFlO01BQ2YsY0FBYyxFQUFBO0VBVnBCO0lBZUksd0JBQXdCO0lBQ3hCLGlCQUFpQixFQUFBO0VBaEJyQjtNQW1CTSxZQUFZLEVBQUE7RUFuQmxCO01BdUJNLGtCQUFrQixFQUFBO0VBdkJ4QjtNQTJCTSxXQUFXO01BQ1gsa0JBQWtCO01BQ2xCLFVBQVUsRUFBQTtFQTdCaEI7SUFrQ0ksaUJBQWlCO0lBQ2pCLDBCQUEwQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hZG1pbi9kYXRhc2VjdXJpdHkvYWRkLXNlY3VyaXR5LWRpYWxvZy9hZGQtc2VjdXJpdHktZGlhbG9nLmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiQGltcG9ydCBcInNyYy90aGVtZXMvYmFzZS9jb2xvcnNcIjtcblxuOjpuZy1kZWVwIGFkZC1zZWN1aXJ0eS1kaWFsb2cge1xuICBtYXgtd2lkdGg6IDg2MHB4O1xuICB3aWR0aDogODYwcHg7XG5cbiAgLmJhc2UtZGlhbG9nX19oZWFkZXIge1xuICAgIHBhZGRpbmc6IDAgMCAxMHB4O1xuICAgIGJvcmRlci1ib3R0b206IDFweCBzb2xpZCAjOTk5O1xuXG4gICAgc3Ryb25nIHtcbiAgICAgIGZvbnQtc2l6ZTogMjBweDtcbiAgICAgIGNvbG9yOiAjNUM2NjcwO1xuICAgIH1cbiAgfVxuXG4gIC5iYXNlLWRpYWxvZ19fY29udGVudCB7XG4gICAgcGFkZGluZzogMTBweCAhaW1wb3J0YW50O1xuICAgIG1heC1oZWlnaHQ6IDQ1MHB4O1xuXG4gICAgLm1hdC1mb3JtLWZpZWxkIHtcbiAgICAgIHdpZHRoOiA1MDBweDtcbiAgICB9XG5cbiAgICAuZm9ybS1maWVsZCB7XG4gICAgICBtYXJnaW4tYm90dG9tOiA1cHg7XG4gICAgfVxuXG4gICAgZGl2IC5lcnJvcnMge1xuICAgICAgd2lkdGg6IDEwMCU7XG4gICAgICB0ZXh0LWFsaWduOiBjZW50ZXI7XG4gICAgICBjb2xvcjogcmVkO1xuICAgIH1cbiAgfVxuXG4gIC5iYXNlLWRpYWxvZ19fYWN0aW9ucyB7XG4gICAgcGFkZGluZy10b3A6IDEwcHg7XG4gICAgYm9yZGVyLXRvcDogMXB4IHNvbGlkICM5OTk7XG4gIH1cbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.ts":
/*!*************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.ts ***!
  \*************************************************************************************************/
/*! exports provided: AddSecurityDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AddSecurityDialogComponent", function() { return AddSecurityDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _userassignment_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./../userassignment.service */ "./src/app/modules/admin/datasecurity/userassignment.service.ts");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_4__);





var AddSecurityDialogComponent = /** @class */ (function () {
    function AddSecurityDialogComponent(_dialogRef, _userAssignmentService, data) {
        this._dialogRef = _dialogRef;
        this._userAssignmentService = _userAssignmentService;
        this.data = data;
        this.securityGroup = {};
    }
    AddSecurityDialogComponent.prototype.submit = function () {
        var _this = this;
        this._userAssignmentService.addSecurityGroup(this.data).then(function (response) {
            if (lodash_get__WEBPACK_IMPORTED_MODULE_4__(response, 'valid')) {
                _this._dialogRef.close(response);
            }
        }).catch(function (err) {
            if (!lodash_get__WEBPACK_IMPORTED_MODULE_4__(err.error, 'valid')) {
                _this.errorState = !lodash_get__WEBPACK_IMPORTED_MODULE_4__(err.error, 'valid');
                _this.errorMessage = lodash_get__WEBPACK_IMPORTED_MODULE_4__(err.error, 'validityMessage');
            }
        });
    };
    AddSecurityDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'add-secuirty-dialog',
            template: __webpack_require__(/*! ./add-security-dialog.component.html */ "./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.html"),
            styles: [__webpack_require__(/*! ./add-security-dialog.component.scss */ "./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](2, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"],
            _userassignment_service__WEBPACK_IMPORTED_MODULE_3__["UserAssignmentService"], Object])
    ], AddSecurityDialogComponent);
    return AddSecurityDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/datasecurity/delete-dialog/delete-dialog.component.html":
/*!***************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/delete-dialog/delete-dialog.component.html ***!
  \***************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div mat-dialog-title>\n    {{data.title}}\n  </div>\n\n  <div mat-dialog-content>\n    {{data.content}}\n  </div>\n\n  <div mat-dialog-actions>\n    <button mat-button i18n\n            mat-dialog-close\n            e2e=\"confirm-dialog-close-btn\">\n      {{data.negativeActionLabel}}\n    </button>\n    <button mat-button i18n color=\"primary\"\n            [mat-dialog-close]=\"true\"\n            e2e=\"confirm-dialog-ok-btn\"\n            cdkFocusInitial>\n      {{data.positiveActionLabel}}\n    </button>\n  </div>\n"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/delete-dialog/delete-dialog.component.ts":
/*!*************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/delete-dialog/delete-dialog.component.ts ***!
  \*************************************************************************************/
/*! exports provided: DeleteDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DeleteDialogComponent", function() { return DeleteDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");



var DeleteDialogComponent = /** @class */ (function () {
    function DeleteDialogComponent(data) {
        this.data = data;
    }
    DeleteDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'delete-dialog',
            template: __webpack_require__(/*! ./delete-dialog.component.html */ "./src/app/modules/admin/datasecurity/delete-dialog/delete-dialog.component.html")
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](0, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object])
    ], DeleteDialogComponent);
    return DeleteDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.html":
/*!*****************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.html ***!
  \*****************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div *ngIf=\"emptyState\" style=\"margin-left: 55%;\nmargin-top: 10%;\nfont-size: 25px;\">\n  Add a new Attribute\n</div>\n<div *ngIf=\"!emptyState\">\n  <dx-data-grid class=\"attribute-Attribute-grid\" [customizeColumns]=\"config.customizeColumns\"\n                [columnAutoWidth]=\"config.columnAutoWidth\"\n                [columnMinWidth]=\"config.columnMinWidth\"\n                [columnResizingMode]=\"config.columnResizingMode\"\n                [allowColumnReordering]=\"config.allowColumnReordering\"\n                [allowColumnResizing]=\"config.allowColumnResizing\"\n                [showColumnHeaders]=\"config.showColumnHeaders\"\n                [showColumnLines]=\"config.showColumnLines\"\n                [showRowLines]=\"config.showRowLines\"\n                [showBorders]=\"config.showBorders\"\n                [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n                [hoverStateEnabled]=\"config.hoverStateEnabled\"\n                [wordWrapEnabled]=\"config.wordWrapEnabled\"\n                [scrolling]=\"config.scrolling\"\n                [sorting]=\"config.sorting\"\n                [dataSource]=\"data\"\n                [columns]=\"config.columns\"\n                [pager]=\"config.pager\"\n                [paging]=\"config.paging\"\n                [width]=\"config.width\"\n                [height]=\"config.height\"\n  >\n\n  <div *dxTemplate=\"let cell of 'actionCellTemplate'\">\n    <div fxLayout=\"row\" fxLayoutAlign=\"center center\" class=\"list-action__container\">\n      <button class=\"update-security-property\"  mat-icon-button\n      i18n-matTooltip=\"Edit Attribute\"\n      matTooltip=\"Edit Attribute\" (click)=\"editAttribute(cell)\">\n        <mat-icon fontIcon=\"icon-edit\"></mat-icon>\n      </button>\n      <button class=\"update-security-property\"  mat-icon-button\n              (click)=\"deleteAtttribute(cell.data)\"\n              i18n-matTooltip=\"Delete Attribute\"\n            matTooltip=\"Delete Attribute\">\n        <mat-icon fontIcon=\"icon-trash\"></mat-icon>\n      </button>\n    </div>\n  </div>\n\n  </dx-data-grid>\n</div>\n\n<!-- (click)=\"deleteRow.emit(cell.data)\" -->\n"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.scss":
/*!*****************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.scss ***!
  \*****************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "::ng-deep field-attribute-view .attribute-group-grid {\n  width: 99% !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2RhdGFzZWN1cml0eS9maWVsZC1hdHRyaWJ1dGUtdmlldy9maWVsZC1hdHRyaWJ1dGUtdmlldy5jb21wb25lbnQuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUVJLHFCQUFxQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hZG1pbi9kYXRhc2VjdXJpdHkvZmllbGQtYXR0cmlidXRlLXZpZXcvZmllbGQtYXR0cmlidXRlLXZpZXcuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0IFwic3JjL3RoZW1lcy9iYXNlL2NvbG9yc1wiO1xuXG46Om5nLWRlZXAgZmllbGQtYXR0cmlidXRlLXZpZXcge1xuICAuYXR0cmlidXRlLWdyb3VwLWdyaWQge1xuICAgIHdpZHRoOiA5OSUgIWltcG9ydGFudDtcbiAgfVxufVxuIl19 */"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.ts":
/*!***************************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.ts ***!
  \***************************************************************************************************/
/*! exports provided: FieldAttributeViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "FieldAttributeViewComponent", function() { return FieldAttributeViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var _userassignment_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./../userassignment.service */ "./src/app/modules/admin/datasecurity/userassignment.service.ts");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _add_attribute_dialog_add_attribute_dialog_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./../add-attribute-dialog/add-attribute-dialog.component */ "./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.ts");
/* harmony import */ var _delete_dialog_delete_dialog_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./../delete-dialog/delete-dialog.component */ "./src/app/modules/admin/datasecurity/delete-dialog/delete-dialog.component.ts");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7__);








var FieldAttributeViewComponent = /** @class */ (function () {
    function FieldAttributeViewComponent(_dxDataGridService, _userAssignmentService, _dialog) {
        this._dxDataGridService = _dxDataGridService;
        this._userAssignmentService = _userAssignmentService;
        this._dialog = _dialog;
    }
    FieldAttributeViewComponent.prototype.ngOnInit = function () {
        this.config = this.getConfig();
        this.emptyState = true;
    };
    FieldAttributeViewComponent.prototype.ngOnChanges = function () {
        if (!lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7__(this.groupSelected)) {
            this.loadAttributesGrid();
        }
    };
    FieldAttributeViewComponent.prototype.loadAttributesGrid = function () {
        var _this = this;
        this._userAssignmentService.getSecurityAttributes(this.groupSelected).then(function (response) {
            _this.data = response;
            _this.emptyState = lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7__(_this.data) ? true : false;
        });
    };
    FieldAttributeViewComponent.prototype.editAttribute = function (cell) {
        var _this = this;
        var mode = 'edit';
        var data = {
            mode: mode,
            attributeName: cell.data.attributeName,
            groupSelected: this.groupSelected,
            value: cell.data.value
        };
        var component = _add_attribute_dialog_add_attribute_dialog_component__WEBPACK_IMPORTED_MODULE_5__["AddAttributeDialogComponent"];
        return this._dialog.open(component, {
            width: 'auto',
            height: 'auto',
            autoFocus: false,
            data: data
        })
            .afterClosed().subscribe(function (result) {
            if (result) {
                _this.loadAttributesGrid();
            }
        });
    };
    FieldAttributeViewComponent.prototype.deleteAtttribute = function (cellData) {
        var _this = this;
        var data = {
            title: "Are you sure you want to delete this attribute for group " + this.groupSelected.securityGroupName + "?",
            content: "Attribute Name: " + cellData.attributeName,
            positiveActionLabel: 'Delete',
            negativeActionLabel: 'Cancel'
        };
        return this._dialog.open(_delete_dialog_delete_dialog_component__WEBPACK_IMPORTED_MODULE_6__["DeleteDialogComponent"], {
            width: 'auto',
            height: 'auto',
            autoFocus: false,
            data: data
        })
            .afterClosed().subscribe(function (result) {
            if (result) {
                var path = "auth/admin/security-groups/" + _this.groupSelected.secGroupSysId + "/dsk-attributes/" + cellData.attributeName;
                _this._userAssignmentService.deleteGroupOrAttribute(path).then(function (response) {
                    _this.loadAttributesGrid();
                });
            }
        });
    };
    FieldAttributeViewComponent.prototype.getConfig = function () {
        var columns = [{
                caption: 'Field Name',
                dataField: 'attributeName',
                allowSorting: true,
                alignment: 'left',
                width: '20%'
            }, {
                caption: 'Field Value',
                dataField: 'value',
                allowSorting: true,
                alignment: 'left',
                width: '20%'
            }, {
                caption: 'Created By',
                dataField: 'created_by',
                allowSorting: true,
                alignment: 'left',
                width: '20%'
            }, {
                caption: 'Created Date',
                dataField: 'created_date',
                allowSorting: true,
                alignment: 'left',
                width: '20%'
            }, {
                caption: '',
                allowSorting: true,
                alignment: 'left',
                width: '10%',
                cellTemplate: 'actionCellTemplate'
            }];
        return this._dxDataGridService.mergeWithDefaultConfig({
            columns: columns,
            width: '100%',
            height: '100%',
            paging: {
                pageSize: 10
            },
            pager: {
                showPageSizeSelector: true,
                showInfo: true
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], FieldAttributeViewComponent.prototype, "groupSelected", void 0);
    FieldAttributeViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'field-attribute-view',
            template: __webpack_require__(/*! ./field-attribute-view.component.html */ "./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.html"),
            styles: [__webpack_require__(/*! ./field-attribute-view.component.scss */ "./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__["DxDataGridService"],
            _userassignment_service__WEBPACK_IMPORTED_MODULE_3__["UserAssignmentService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_4__["MatDialog"]])
    ], FieldAttributeViewComponent);
    return FieldAttributeViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/datasecurity/index.ts":
/*!*****************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/index.ts ***!
  \*****************************************************/
/*! exports provided: AddAttributeDialogComponent, AddSecurityDialogComponent, SecurityGroupComponent, FieldAttributeViewComponent, DeleteDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _add_attribute_dialog_add_attribute_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./add-attribute-dialog/add-attribute-dialog.component */ "./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AddAttributeDialogComponent", function() { return _add_attribute_dialog_add_attribute_dialog_component__WEBPACK_IMPORTED_MODULE_0__["AddAttributeDialogComponent"]; });

/* harmony import */ var _add_security_dialog_add_security_dialog_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./add-security-dialog/add-security-dialog.component */ "./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AddSecurityDialogComponent", function() { return _add_security_dialog_add_security_dialog_component__WEBPACK_IMPORTED_MODULE_1__["AddSecurityDialogComponent"]; });

/* harmony import */ var _security_group_security_group_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./security-group/security-group.component */ "./src/app/modules/admin/datasecurity/security-group/security-group.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SecurityGroupComponent", function() { return _security_group_security_group_component__WEBPACK_IMPORTED_MODULE_2__["SecurityGroupComponent"]; });

/* harmony import */ var _field_attribute_view_field_attribute_view_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./field-attribute-view/field-attribute-view.component */ "./src/app/modules/admin/datasecurity/field-attribute-view/field-attribute-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "FieldAttributeViewComponent", function() { return _field_attribute_view_field_attribute_view_component__WEBPACK_IMPORTED_MODULE_3__["FieldAttributeViewComponent"]; });

/* harmony import */ var _delete_dialog_delete_dialog_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./delete-dialog/delete-dialog.component */ "./src/app/modules/admin/datasecurity/delete-dialog/delete-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DeleteDialogComponent", function() { return _delete_dialog_delete_dialog_component__WEBPACK_IMPORTED_MODULE_4__["DeleteDialogComponent"]; });








/***/ }),

/***/ "./src/app/modules/admin/datasecurity/security-group/security-group.component.html":
/*!*****************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/security-group/security-group.component.html ***!
  \*****************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-toolbar fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n  <div fxLayout=\"row\" fxFlex=\"40\">\n    <div class=\"admin-title\" e2e=\"security-group-title\">Manage Security Groups</div>\n    <!-- <search-box placeholder=\"Search\"\n                [value]=\"filterObj.searchTerm\"\n                (searchTermChange)=\"applySearchFilter($event)\"\n                [delay]=\"1000\">\n    </search-box> -->\n  </div>\n\n  <div fxLayout=\"row\" class=\"customer-name\" fxLayoutAlign=\"start center\" fxFlex=\"40\">\n    Customer: {{ticket.custCode}}\n  </div>\n  <div fxLayout=\"row\" fxLayoutAlign=\"end center\" fxFlex=\"20\">\n    <button (click)=\"addPropperty('securityGroup','create')\"\n            mat-raised-button\n            color=\"primary\" style=\"margin-right:10px;\">\n      + <span i18n>Security Group</span>\n    </button>\n\n    <button (click)=\"addPropperty('attribute','create')\"\n            mat-raised-button\n            color=\"primary\"\n            [disabled]=\"addAttribute\">\n      + <span i18n>Attribute</span>\n    </button>\n  </div>\n</mat-toolbar>\n\n<div *ngIf=\"emptyState\" style=\"margin-left: 30%;\n  margin-top: 7%;\n  font-size: 25px;\">\n    Add a new Group by clicking on the \"+Security Group\" button\n</div>\n<div *ngIf=\"!emptyState\" style=\"float: left;\">\n  <dx-data-grid class=\"security-group-grid\" [customizeColumns]=\"config.customizeColumns\"\n              [selectionFilter]=\"['securityGroupName', '=', groupSelected.securityGroupName]\"\n              [columnAutoWidth]=\"config.columnAutoWidth\"\n              [columnMinWidth]=\"config.columnMinWidth\"\n              [columnResizingMode]=\"config.columnResizingMode\"\n              [allowColumnReordering]=\"config.allowColumnReordering\"\n              [allowColumnResizing]=\"config.allowColumnResizing\"\n              [showColumnHeaders]=\"config.showColumnHeaders\"\n              [showColumnLines]=\"config.showColumnLines\"\n              [showRowLines]=\"config.showRowLines\"\n              [showBorders]=\"config.showBorders\"\n              [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n              [hoverStateEnabled]=\"config.hoverStateEnabled\"\n              [wordWrapEnabled]=\"config.wordWrapEnabled\"\n              [scrolling]=\"config.scrolling\"\n              [sorting]=\"config.sorting\"\n              [dataSource]=\"data\"\n              [columns]=\"config.columns\"\n              [pager]=\"config.pager\"\n              [paging]=\"config.paging\"\n              [width]=\"config.width\"\n              [height]=\"config.height\"\n              (onRowClick)=\"config.onRowClick($event)\"\n  >\n  <dxo-selection mode=\"single\" [deferred]=\"true\"></dxo-selection>\n  <div *dxTemplate=\"let cell of 'actionCellTemplate'\">\n    <div fxLayout=\"row\" fxLayoutAlign=\"center center\" class=\"list-action__container\">\n      <button mat-icon-button class=\"update-security-property\"\n      i18n-matTooltip=\"Edit Group\"\n            matTooltip=\"Edit Group\" (click)=\"editGroupData(cell.data)\"\n              >\n        <mat-icon fontIcon=\"icon-edit\"></mat-icon>\n      </button>\n      <button class=\"update-security-property\" mat-icon-button\n              (click)=\"deleteGroup(cell.data)\"\n              i18n-matTooltip=\"Delete Group\"\n            matTooltip=\"Delete Group\">\n        <mat-icon  fontIcon=\"icon-trash\"></mat-icon>\n      </button>\n    </div>\n  </div>\n\n  <div *dxTemplate=\"let cell of 'toolTipCellTemplate'\">\n    <div fxLayout=\"row\" fxLayoutAlign=\"center center\" class=\"list-action__container\" style=\"float: left !important;\" >\n      <span i18n-matTooltip=\"{{cell.data.description}}\"\n      matTooltip=\"{{cell.data.description}}\">{{cell.data.securityGroupName}}</span>\n    </div>\n  </div>\n\n  </dx-data-grid>\n  <field-attribute-view [groupSelected]=\"groupSelected\"></field-attribute-view>\n</div>\n\n\n"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/security-group/security-group.component.scss":
/*!*****************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/security-group/security-group.component.scss ***!
  \*****************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "::ng-deep security-group .mat-toolbar {\n  background-color: transparent; }\n  ::ng-deep security-group .mat-toolbar .admin-title {\n    color: #636363;\n    font-weight: bold;\n    margin-right: 10px; }\n  ::ng-deep security-group .mat-toolbar .customer-name {\n    color: #636363; }\n  ::ng-deep security-group div .security-group-grid {\n  width: 30% !important;\n  float: left;\n  margin-left: 20px;\n  padding-right: 10px;\n  margin-right: 10px;\n  border-right: 1px solid #D1D3D3; }\n  ::ng-deep .update-security-property {\n  width: 24px !important;\n  height: 24px !important;\n  line-height: 20px !important;\n  margin-right: 18px !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2RhdGFzZWN1cml0eS9zZWN1cml0eS1ncm91cC9zZWN1cml0eS1ncm91cC5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUVJLDZCQUE2QixFQUFBO0VBRmpDO0lBS00sY0NJbUI7SURIbkIsaUJBQWlCO0lBQ2pCLGtCQUFrQixFQUFBO0VBUHhCO0lBV00sY0NGbUIsRUFBQTtFRFR6QjtFQWdCSSxxQkFBcUI7RUFDckIsV0FBVztFQUNYLGlCQUFpQjtFQUNqQixtQkFBbUI7RUFDbkIsa0JBQWtCO0VBQ2xCLCtCQUErQixFQUFBO0VBSW5DO0VBQ0Usc0JBQXNCO0VBQ3RCLHVCQUF1QjtFQUN2Qiw0QkFBNEI7RUFDNUIsNkJBQTZCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FkbWluL2RhdGFzZWN1cml0eS9zZWN1cml0eS1ncm91cC9zZWN1cml0eS1ncm91cC5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbjo6bmctZGVlcCBzZWN1cml0eS1ncm91cCB7XG4gIC5tYXQtdG9vbGJhciB7XG4gICAgYmFja2dyb3VuZC1jb2xvcjogdHJhbnNwYXJlbnQ7XG5cbiAgICAuYWRtaW4tdGl0bGUge1xuICAgICAgY29sb3I6ICRncmV5LXRleHQtY29sb3I7XG4gICAgICBmb250LXdlaWdodDogYm9sZDtcbiAgICAgIG1hcmdpbi1yaWdodDogMTBweDtcbiAgICB9XG5cbiAgICAuY3VzdG9tZXItbmFtZSB7XG4gICAgICBjb2xvcjogJGdyZXktdGV4dC1jb2xvcjtcbiAgICB9XG4gIH1cblxuICBkaXYgLnNlY3VyaXR5LWdyb3VwLWdyaWQge1xuICAgIHdpZHRoOiAzMCUgIWltcG9ydGFudDtcbiAgICBmbG9hdDogbGVmdDtcbiAgICBtYXJnaW4tbGVmdDogMjBweDtcbiAgICBwYWRkaW5nLXJpZ2h0OiAxMHB4O1xuICAgIG1hcmdpbi1yaWdodDogMTBweDtcbiAgICBib3JkZXItcmlnaHQ6IDFweCBzb2xpZCAjRDFEM0QzO1xuICB9XG59XG5cbjo6bmctZGVlcCAudXBkYXRlLXNlY3VyaXR5LXByb3BlcnR5IHtcbiAgd2lkdGg6IDI0cHggIWltcG9ydGFudDtcbiAgaGVpZ2h0OiAyNHB4ICFpbXBvcnRhbnQ7XG4gIGxpbmUtaGVpZ2h0OiAyMHB4ICFpbXBvcnRhbnQ7XG4gIG1hcmdpbi1yaWdodDogMThweCAhaW1wb3J0YW50O1xufVxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/admin/datasecurity/security-group/security-group.component.ts":
/*!***************************************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/security-group/security-group.component.ts ***!
  \***************************************************************************************/
/*! exports provided: SecurityGroupComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SecurityGroupComponent", function() { return SecurityGroupComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common_services_jwt_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../../common/services/jwt.service */ "./src/app/common/services/jwt.service.ts");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _add_security_dialog_add_security_dialog_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./../add-security-dialog/add-security-dialog.component */ "./src/app/modules/admin/datasecurity/add-security-dialog/add-security-dialog.component.ts");
/* harmony import */ var _add_attribute_dialog_add_attribute_dialog_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./../add-attribute-dialog/add-attribute-dialog.component */ "./src/app/modules/admin/datasecurity/add-attribute-dialog/add-attribute-dialog.component.ts");
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var _userassignment_service__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./../userassignment.service */ "./src/app/modules/admin/datasecurity/userassignment.service.ts");
/* harmony import */ var _delete_dialog_delete_dialog_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./../delete-dialog/delete-dialog.component */ "./src/app/modules/admin/datasecurity/delete-dialog/delete-dialog.component.ts");
/* harmony import */ var _common_services_local_search_service__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../../common/services/local-search.service */ "./src/app/common/services/local-search.service.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_12___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_12__);













var SecurityGroupComponent = /** @class */ (function () {
    function SecurityGroupComponent(_router, _jwtService, _dialog, _dxDataGridService, _userAssignmentService, _localSearch, _toastMessage) {
        var _this = this;
        this._router = _router;
        this._jwtService = _jwtService;
        this._dialog = _dialog;
        this._dxDataGridService = _dxDataGridService;
        this._userAssignmentService = _userAssignmentService;
        this._localSearch = _localSearch;
        this._toastMessage = _toastMessage;
        this.listeners = [];
        this.filterObj = {
            searchTerm: '',
            searchTermValue: ''
        };
        var navigationListener = this._router.events.subscribe(function (e) {
            if (e instanceof _angular_router__WEBPACK_IMPORTED_MODULE_2__["NavigationEnd"]) {
                _this.initialise();
            }
        });
        this.listeners.push(navigationListener);
    }
    SecurityGroupComponent.prototype.ngOnInit = function () {
        this.config = this.getConfig();
        this.loadGroupGridWithData(this.groupSelected);
        this.emptyState = true;
    };
    SecurityGroupComponent.prototype.initialise = function () {
        var token = this._jwtService.getTokenObj();
        this.ticket = token.ticket;
    };
    SecurityGroupComponent.prototype.loadGroupGridWithData = function (groupSelected) {
        var _this = this;
        this.groupSelected = {};
        this.addAttribute = true;
        this._userAssignmentService.getSecurityGroups().then(function (response) {
            _this.data = response;
            if (_this.data.length === 0) {
                _this.emptyState = true;
            }
            else {
                _this.emptyState = false;
                _this.groupSelected = (lodash_isEmpty__WEBPACK_IMPORTED_MODULE_12__(groupSelected)) ? _this.data[0] : groupSelected;
            }
            _this.addAttribute = (_this.data.length === 0);
        });
    };
    SecurityGroupComponent.prototype.addPropperty = function (property, mode) {
        var _this = this;
        if (mode === 'create') {
            this.columnData = {};
        }
        var data = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({ property: property,
            mode: mode, groupSelected: this.groupSelected }, this.columnData);
        var component = this.getModalComponent(property);
        return this._dialog.open(component, {
            width: 'auto',
            height: 'auto',
            autoFocus: false,
            data: data
        })
            .afterClosed().subscribe(function (result) {
            if (result) {
                if (property === 'securityGroup') {
                    _this.groupSelected = {
                        secGroupSysId: result.groupId,
                        securityGroupName: result.groupName,
                        description: result.description
                    };
                }
                _this.loadGroupGridWithData(_this.groupSelected);
            }
        });
    };
    SecurityGroupComponent.prototype.editGroupData = function (data) {
        this.columnData = data;
        this.addPropperty('securityGroup', 'edit');
    };
    SecurityGroupComponent.prototype.deleteGroup = function (cellData) {
        var _this = this;
        var data = {
            title: "Are you sure you want to delete this group?",
            content: "Group Name: " + cellData.securityGroupName,
            positiveActionLabel: 'Delete',
            negativeActionLabel: 'Cancel'
        };
        return this._dialog.open(_delete_dialog_delete_dialog_component__WEBPACK_IMPORTED_MODULE_9__["DeleteDialogComponent"], {
            width: 'auto',
            height: 'auto',
            autoFocus: false,
            data: data
        })
            .afterClosed().subscribe(function (result) {
            var path = "auth/admin/security-groups/" + cellData.secGroupSysId;
            if (result) {
                _this._userAssignmentService.deleteGroupOrAttribute(path).then(function (response) {
                    _this.loadGroupGridWithData(_this.groupSelected);
                });
            }
        });
    };
    SecurityGroupComponent.prototype.getModalComponent = function (property) {
        switch (property) {
            case 'securityGroup':
                return _add_security_dialog_add_security_dialog_component__WEBPACK_IMPORTED_MODULE_5__["AddSecurityDialogComponent"];
            case 'attribute':
                return _add_attribute_dialog_add_attribute_dialog_component__WEBPACK_IMPORTED_MODULE_6__["AddAttributeDialogComponent"];
        }
    };
    SecurityGroupComponent.prototype.applySearchFilter = function (value) {
        var _this = this;
        var USERGROUP_SEARCH_CONFIG = [
            { keyword: 'Group Name', fieldName: 'securityGroupName' }
        ];
        this.filterObj.searchTerm = value;
        var searchCriteria = this._localSearch.parseSearchTerm(this.filterObj.searchTerm);
        this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
        this._localSearch
            .doSearch(searchCriteria, this.data, USERGROUP_SEARCH_CONFIG)
            .then(function (data) {
            _this.data = data;
        }, function (err) {
            _this._toastMessage.error(err.message);
        });
    };
    SecurityGroupComponent.prototype.getConfig = function () {
        var _this = this;
        var columns = [{
                caption: 'Group Name',
                dataField: 'securityGroupName',
                cellTemplate: 'toolTipCellTemplate',
                allowSorting: true,
                alignment: 'left',
                width: '60%'
            }, {
                caption: 'ID',
                dataField: 'secGroupSysId',
                width: '0%'
            }, {
                caption: '',
                allowSorting: true,
                alignment: 'left',
                width: '30%',
                cellTemplate: 'actionCellTemplate'
            }];
        return this._dxDataGridService.mergeWithDefaultConfig({
            onRowClick: function (row) {
                _this.groupSelected = row.data;
            },
            columns: columns,
            width: '100%',
            height: '100%',
            paging: {
                pageSize: 10
            },
            pager: {
                showPageSizeSelector: true,
                showInfo: true
            }
        });
    };
    SecurityGroupComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'security-group',
            template: __webpack_require__(/*! ./security-group.component.html */ "./src/app/modules/admin/datasecurity/security-group/security-group.component.html"),
            styles: [__webpack_require__(/*! ./security-group.component.scss */ "./src/app/modules/admin/datasecurity/security-group/security-group.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"],
            _common_services_jwt_service__WEBPACK_IMPORTED_MODULE_3__["JwtService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_4__["MatDialog"],
            _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_7__["DxDataGridService"],
            _userassignment_service__WEBPACK_IMPORTED_MODULE_8__["UserAssignmentService"],
            _common_services_local_search_service__WEBPACK_IMPORTED_MODULE_10__["LocalSearchService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_11__["ToastService"]])
    ], SecurityGroupComponent);
    return SecurityGroupComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/datasecurity/userassignment.service.ts":
/*!**********************************************************************!*\
  !*** ./src/app/modules/admin/datasecurity/userassignment.service.ts ***!
  \**********************************************************************/
/*! exports provided: UserAssignmentService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UserAssignmentService", function() { return UserAssignmentService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm5/http.js");
/* harmony import */ var _appConfig__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../../../appConfig */ "./appConfig.ts");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__);





var loginUrl = _appConfig__WEBPACK_IMPORTED_MODULE_3__["default"].login.url;
var UserAssignmentService = /** @class */ (function () {
    function UserAssignmentService(_http) {
        this._http = _http;
    }
    UserAssignmentService.prototype.getList = function (customerId) {
        return this.getRequest('auth/admin/user-assignments');
    };
    UserAssignmentService.prototype.addSecurityGroup = function (data) {
        var path;
        switch (data.mode) {
            case 'create':
                var requestCreateBody = {
                    description: lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__(data.description) ? '' : data.description,
                    securityGroupName: data.securityGroupName
                };
                path = 'auth/admin/security-groups';
                return this.postRequest(path, requestCreateBody);
            case 'edit':
                path = "auth/admin/security-groups/" + data.secGroupSysId + "/name";
                var requestEditBody = [data.securityGroupName, data.description];
                return this.putrequest(path, requestEditBody);
        }
    };
    UserAssignmentService.prototype.attributetoGroup = function (data) {
        var requestBody = {
            attributeName: data.attributeName.trim(),
            value: data.value
        };
        var path = "auth/admin/security-groups/" + data.groupSelected.secGroupSysId + "/dsk-attribute-values";
        switch (data.mode) {
            case 'create':
                return this.postRequest(path, requestBody);
            case 'edit':
                return this.putrequest(path, requestBody);
        }
    };
    UserAssignmentService.prototype.getSecurityAttributes = function (request) {
        return this.getRequest("auth/admin/security-groups/" + request.secGroupSysId + "/dsk-attribute-values");
    };
    UserAssignmentService.prototype.getSecurityGroups = function () {
        return this.getRequest('auth/admin/security-groups');
    };
    UserAssignmentService.prototype.deleteGroupOrAttribute = function (path) {
        return this._http.delete(loginUrl + "/" + path).toPromise();
    };
    UserAssignmentService.prototype.assignGroupToUser = function (requestBody) {
        var path = "auth/admin/users/" + requestBody.userId + "/security-group";
        return this.putrequest(path, requestBody.securityGroupName);
    };
    UserAssignmentService.prototype.getRequest = function (path) {
        return this._http.get(loginUrl + "/" + path).toPromise();
    };
    UserAssignmentService.prototype.putrequest = function (path, requestBody) {
        var httpOptions = {
            headers: new _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpHeaders"]({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Origin, X-Requested-With, Content-Type, Accept',
                'Access-Control-Allow-Method': 'PUT'
            })
        };
        return this._http.put(loginUrl + "/" + path, requestBody, httpOptions).toPromise();
    };
    UserAssignmentService.prototype.postRequest = function (path, params) {
        var httpOptions = {
            headers: new _angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpHeaders"]({
                'Content-Type': 'application/json'
            })
        };
        return this._http.post(loginUrl + "/" + path, params, httpOptions).toPromise();
    };
    UserAssignmentService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"]])
    ], UserAssignmentService);
    return UserAssignmentService;
}());



/***/ }),

/***/ "./src/app/modules/admin/export/actions/export-page.actions.ts":
/*!*********************************************************************!*\
  !*** ./src/app/modules/admin/export/actions/export-page.actions.ts ***!
  \*********************************************************************/
/*! exports provided: ResetExportPageState, ExportSelectTreeItem, ExportLoadAnalyses, ExportLoadDashboards, AddAnalysisToExport, RemoveAnalysisFromExport, AddAllAnalysesToExport, RemoveAllAnalysesFromExport, ExportLoadMetrics, AddDashboardToExport, RemoveDashboardFromExport, ClearExport */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ResetExportPageState", function() { return ResetExportPageState; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExportSelectTreeItem", function() { return ExportSelectTreeItem; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExportLoadAnalyses", function() { return ExportLoadAnalyses; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExportLoadDashboards", function() { return ExportLoadDashboards; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AddAnalysisToExport", function() { return AddAnalysisToExport; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RemoveAnalysisFromExport", function() { return RemoveAnalysisFromExport; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AddAllAnalysesToExport", function() { return AddAllAnalysesToExport; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RemoveAllAnalysesFromExport", function() { return RemoveAllAnalysesFromExport; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExportLoadMetrics", function() { return ExportLoadMetrics; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AddDashboardToExport", function() { return AddDashboardToExport; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RemoveDashboardFromExport", function() { return RemoveDashboardFromExport; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ClearExport", function() { return ClearExport; });
var ResetExportPageState = /** @class */ (function () {
    function ResetExportPageState() {
    }
    ResetExportPageState.type = '[Admin Export Page OnDestroy] Reset page state';
    return ResetExportPageState;
}());

var ExportSelectTreeItem = /** @class */ (function () {
    function ExportSelectTreeItem(moduleName, item) {
        this.moduleName = moduleName;
        this.item = item;
    }
    ExportSelectTreeItem.type = '[Admin Export Page Tree] Select menu item';
    return ExportSelectTreeItem;
}());

var ExportLoadAnalyses = /** @class */ (function () {
    function ExportLoadAnalyses(categoryId) {
        this.categoryId = categoryId;
    }
    ExportLoadAnalyses.type = '[Admin Export State] Load analyses';
    return ExportLoadAnalyses;
}());

var ExportLoadDashboards = /** @class */ (function () {
    function ExportLoadDashboards(categoryId) {
        this.categoryId = categoryId;
    }
    ExportLoadDashboards.type = '[Admin Export State] Load dashboards';
    return ExportLoadDashboards;
}());

var AddAnalysisToExport = /** @class */ (function () {
    function AddAnalysisToExport(analysis) {
        this.analysis = analysis;
    }
    AddAnalysisToExport.type = '[Admin Export Page] Add analysis to export';
    return AddAnalysisToExport;
}());

var RemoveAnalysisFromExport = /** @class */ (function () {
    function RemoveAnalysisFromExport(analysis) {
        this.analysis = analysis;
    }
    RemoveAnalysisFromExport.type = '[Admin Export Page] Remove analysis from export';
    return RemoveAnalysisFromExport;
}());

var AddAllAnalysesToExport = /** @class */ (function () {
    function AddAllAnalysesToExport() {
    }
    AddAllAnalysesToExport.type = '[Admin Export Page] Add all analyses in selected category to export';
    return AddAllAnalysesToExport;
}());

var RemoveAllAnalysesFromExport = /** @class */ (function () {
    function RemoveAllAnalysesFromExport() {
    }
    RemoveAllAnalysesFromExport.type = '[Admin Export Page] Remove all analyses in selected category from export';
    return RemoveAllAnalysesFromExport;
}());

var ExportLoadMetrics = /** @class */ (function () {
    function ExportLoadMetrics() {
    }
    ExportLoadMetrics.type = '[Admin Export Page] Load metrics for all analyses';
    return ExportLoadMetrics;
}());

var AddDashboardToExport = /** @class */ (function () {
    function AddDashboardToExport(dashboard) {
        this.dashboard = dashboard;
    }
    AddDashboardToExport.type = '[Admin Export Page] Add dashboard to export';
    return AddDashboardToExport;
}());

var RemoveDashboardFromExport = /** @class */ (function () {
    function RemoveDashboardFromExport(dashboard) {
        this.dashboard = dashboard;
    }
    RemoveDashboardFromExport.type = '[Admin Export Page] Remove Dashboard from export';
    return RemoveDashboardFromExport;
}());

var ClearExport = /** @class */ (function () {
    function ClearExport() {
    }
    ClearExport.type = '[Admin Export Page] Clear export list';
    return ClearExport;
}());



/***/ }),

/***/ "./src/app/modules/admin/export/admin-export-view.component.html":
/*!***********************************************************************!*\
  !*** ./src/app/modules/admin/export/admin-export-view.component.html ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-toolbar\n  fxLayout=\"row\"\n  fxLayoutAlign=\"space-between center\"\n  style=\"width: 100%;\"\n>\n  <div fxLayout=\"row\" fxFlex=\"10\"><div class=\"admin-title\">Export</div></div>\n\n  <div class=\"admin-export__actions\">\n    <mat-checkbox\n      [hidden]=\"true\"\n      class=\"admin-export__actions--metrics-checkbox\"\n      >Include Metrics</mat-checkbox\n    >\n    <button\n      mat-raised-button\n      color=\"primary\"\n      (click)=\"export()\"\n      [disabled]=\"isExportListEmpty$ | async\"\n      i18n\n    >\n      Export\n    </button>\n  </div>\n</mat-toolbar>\n\n<div class=\"admin-export__main-content\">\n  <admin-export-tree\n    [menu]=\"categorisedMenu$ | async\"\n    (select)=\"onSelectMenuItem($event)\"\n  ></admin-export-tree>\n  <admin-export-content\n    [exportList]=\"exportList$ | async\"\n    [analyses]=\"exportAnalyses$ | async\"\n    (change)=\"onChangeItemSelection($event)\"\n    (changeAll)=\"onChangeAllSelectionList($event)\"\n  ></admin-export-content>\n  <admin-export-list\n    [exportList]=\"exportList$ | async\"\n    (change)=\"onChangeItemSelection($event)\"\n    (changeAll)=\"onChangeAllExportList($event)\"\n  ></admin-export-list>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/export/admin-export-view.component.scss":
/*!***********************************************************************!*\
  !*** ./src/app/modules/admin/export/admin-export-view.component.scss ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  max-height: calc(100vh - (63px + 55px));\n  width: 100%; }\n\n.admin-title {\n  color: #636363;\n  font-weight: bold;\n  margin-right: 10px; }\n\n.mat-toolbar {\n  background-color: transparent;\n  height: auto;\n  padding: 0 20px; }\n\n.admin-export__actions {\n  padding: 5px 0; }\n\n.admin-export__actions--metrics-checkbox {\n    margin-right: 10px;\n    font-size: 16px; }\n\n.admin-export__main-content {\n  height: calc(100% - 46px);\n  display: grid;\n  grid-template-columns: 1fr 2fr 3fr;\n  grid-template-rows: 100%; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2V4cG9ydC9hZG1pbi1leHBvcnQtdmlldy5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFHQTtFQUNFLHVDQUF1QztFQUN2QyxXQUFXLEVBQUE7O0FBR2I7RUFDRSxjQ0V1QjtFRER2QixpQkFBaUI7RUFDakIsa0JBQWtCLEVBQUE7O0FBR3BCO0VBQ0UsNkJBQTZCO0VBQzdCLFlBQVk7RUFDWixlQUFlLEVBQUE7O0FBSWY7RUFDRSxjQUFjLEVBQUE7O0FBRWQ7SUFDRSxrQkFBa0I7SUFDbEIsZUFBZSxFQUFBOztBQUluQjtFQUNFLHlCQUF5QjtFQUN6QixhQUFhO0VBQ2Isa0NBQWtDO0VBQ2xDLHdCQUF3QixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hZG1pbi9leHBvcnQvYWRtaW4tZXhwb3J0LXZpZXcuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcbiR0b29sYmFyLWhlaWdodDogNzRweDtcblxuOmhvc3Qge1xuICBtYXgtaGVpZ2h0OiBjYWxjKDEwMHZoIC0gKDYzcHggKyA1NXB4KSk7XG4gIHdpZHRoOiAxMDAlO1xufVxuXG4uYWRtaW4tdGl0bGUge1xuICBjb2xvcjogJGdyZXktdGV4dC1jb2xvcjtcbiAgZm9udC13ZWlnaHQ6IGJvbGQ7XG4gIG1hcmdpbi1yaWdodDogMTBweDtcbn1cblxuLm1hdC10b29sYmFyIHtcbiAgYmFja2dyb3VuZC1jb2xvcjogdHJhbnNwYXJlbnQ7XG4gIGhlaWdodDogYXV0bztcbiAgcGFkZGluZzogMCAyMHB4O1xufVxuXG4uYWRtaW4tZXhwb3J0IHtcbiAgJl9fYWN0aW9ucyB7XG4gICAgcGFkZGluZzogNXB4IDA7XG5cbiAgICAmLS1tZXRyaWNzLWNoZWNrYm94IHtcbiAgICAgIG1hcmdpbi1yaWdodDogMTBweDtcbiAgICAgIGZvbnQtc2l6ZTogMTZweDtcbiAgICB9XG4gIH1cblxuICAmX19tYWluLWNvbnRlbnQge1xuICAgIGhlaWdodDogY2FsYygxMDAlIC0gNDZweCk7XG4gICAgZGlzcGxheTogZ3JpZDtcbiAgICBncmlkLXRlbXBsYXRlLWNvbHVtbnM6IDFmciAyZnIgM2ZyO1xuICAgIGdyaWQtdGVtcGxhdGUtcm93czogMTAwJTtcbiAgfVxufVxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/admin/export/admin-export-view.component.ts":
/*!*********************************************************************!*\
  !*** ./src/app/modules/admin/export/admin-export-view.component.ts ***!
  \*********************************************************************/
/*! exports provided: AdminExportViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminExportViewComponent", function() { return AdminExportViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _ngxs_store__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @ngxs/store */ "./node_modules/@ngxs/store/fesm5/ngxs-store.js");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./actions/export-page.actions */ "./src/app/modules/admin/export/actions/export-page.actions.ts");
/* harmony import */ var _common_actions_menu_actions__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../common/actions/menu.actions */ "./src/app/common/actions/menu.actions.ts");
/* harmony import */ var _state_export_page_state__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./state/export-page.state */ "./src/app/modules/admin/export/state/export-page.state.ts");
/* harmony import */ var _export_service__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./export.service */ "./src/app/modules/admin/export/export.service.ts");
/* harmony import */ var _common_components_sidenav__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../common/components/sidenav */ "./src/app/common/components/sidenav/index.ts");
/* harmony import */ var _consts__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../consts */ "./src/app/modules/admin/consts.ts");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var jszip__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! jszip */ "./node_modules/jszip/dist/jszip.min.js");
/* harmony import */ var jszip__WEBPACK_IMPORTED_MODULE_12___default = /*#__PURE__*/__webpack_require__.n(jszip__WEBPACK_IMPORTED_MODULE_12__);
/* harmony import */ var file_saver__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! file-saver */ "./node_modules/file-saver/FileSaver.js");
/* harmony import */ var file_saver__WEBPACK_IMPORTED_MODULE_13___default = /*#__PURE__*/__webpack_require__.n(file_saver__WEBPACK_IMPORTED_MODULE_13__);
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! moment */ "./node_modules/moment/moment.js");
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_14___default = /*#__PURE__*/__webpack_require__.n(moment__WEBPACK_IMPORTED_MODULE_14__);
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_15___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_15__);
















var AdminExportViewComponent = /** @class */ (function () {
    function AdminExportViewComponent(_exportService, _sidenav, _jwtService, store) {
        this._exportService = _exportService;
        this._sidenav = _sidenav;
        this._jwtService = _jwtService;
        this.store = store;
        this.isExportListEmpty$ = this.exportList$.pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_11__["map"])(function (list) { return list.length <= 0; }));
        this.store.dispatch([
            new _common_actions_menu_actions__WEBPACK_IMPORTED_MODULE_5__["AdminExportLoadMenu"]('ANALYZE'),
            new _common_actions_menu_actions__WEBPACK_IMPORTED_MODULE_5__["AdminExportLoadMenu"]('OBSERVE')
        ]);
    }
    AdminExportViewComponent.prototype.ngOnInit = function () {
        this._sidenav.updateMenu(_consts__WEBPACK_IMPORTED_MODULE_9__["AdminMenuData"], 'ADMIN');
        // Group menus under their modules
        this.categorisedMenu$ = this.analyzeMenu$.pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_11__["map"])(function (menu) { return [
            {
                id: 'analyze_1',
                name: 'Analyze',
                expanded: true,
                children: menu
            }
        ]; }));
    };
    AdminExportViewComponent.prototype.ngOnDestroy = function () {
        this.store.dispatch(new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_4__["ResetExportPageState"]());
    };
    /**
     * Handler for changes in left pane - Selection of category/sub-category
     *
     * @param {*} { moduleName, menuItem }
     * @memberof AdminExportViewComponent
     */
    AdminExportViewComponent.prototype.onSelectMenuItem = function (_a) {
        var moduleName = _a.moduleName, menuItem = _a.menuItem;
        this.store.dispatch(new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_4__["ExportSelectTreeItem"](moduleName, menuItem));
    };
    /**
     * When the item in middle pane is toggled, update store with it.
     *
     * @param {*} { checked, item }
     * @memberof AdminExportViewComponent
     */
    AdminExportViewComponent.prototype.onChangeItemSelection = function (_a) {
        var checked = _a.checked, item = _a.item;
        if (item.entityId) {
            // TODO: Handle dashboard
        }
        else {
            // Item is analysis
            this.store.dispatch(checked
                ? new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_4__["AddAnalysisToExport"](item)
                : new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_4__["RemoveAnalysisFromExport"](item));
        }
    };
    /**
     * When 'All' checkbox in a list is toggled, update store accordingly.
     *
     * @param {boolean} checked
     * @memberof AdminExportViewComponent
     */
    AdminExportViewComponent.prototype.onChangeAllSelectionList = function (checked) {
        this.store.dispatch([
            checked ? new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_4__["AddAllAnalysesToExport"]() : new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_4__["RemoveAllAnalysesFromExport"]()
        ]);
    };
    /**
     * When 'All' checkbox in export list is toggled, clear the list.
     * Export list doesn't support keeping some items unchecked.
     * Item is either selected, or not present.
     *
     * @param {boolean} checked
     * @memberof AdminExportViewComponent
     */
    AdminExportViewComponent.prototype.onChangeAllExportList = function (checked) {
        this.store.dispatch(new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_4__["ClearExport"]());
    };
    /**
     * Creates a zip file and export the items.
     *
     * @memberof AdminExportViewComponent
     */
    AdminExportViewComponent.prototype.export = function () {
        var _this = this;
        var zip = new jszip__WEBPACK_IMPORTED_MODULE_12__();
        var analyses = this.store.selectSnapshot(_state_export_page_state__WEBPACK_IMPORTED_MODULE_6__["ExportPageState"].exportData).analyses;
        var fileName = this.getFileName('ANALYZE');
        zip.file(fileName + ".json", new Blob([JSON.stringify(analyses)], {
            type: 'application/json;charset=utf-8'
        }));
        zip.generateAsync({ type: 'blob' }).then(function (content) {
            var zipFileName = _this.getFileName('');
            zipFileName = zipFileName.replace('_', '');
            file_saver__WEBPACK_IMPORTED_MODULE_13__["saveAs"](content, zipFileName + ".zip");
        });
    };
    /**
     * Returns formatted file name based on input
     *
     * @param {string} name
     * @returns {string}
     * @memberof AdminExportViewComponent
     */
    AdminExportViewComponent.prototype.getFileName = function (name) {
        var formatedDate = moment__WEBPACK_IMPORTED_MODULE_14__().format('YYYYMMDDHHmmss');
        var custCode = lodash_get__WEBPACK_IMPORTED_MODULE_15__(this._jwtService.getTokenObj(), 'ticket.custCode');
        name = name.replace(' ', '_');
        name = name.replace('\\', '-');
        name = name.replace('/', '-');
        return custCode + "_" + name + "_" + formatedDate;
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_2__["Select"])(function (state) { return state.common.analyzeMenu; }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_10__["Observable"])
    ], AdminExportViewComponent.prototype, "analyzeMenu$", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_2__["Select"])(_state_export_page_state__WEBPACK_IMPORTED_MODULE_6__["ExportPageState"].exportList),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_10__["Observable"])
    ], AdminExportViewComponent.prototype, "exportList$", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_2__["Select"])(_state_export_page_state__WEBPACK_IMPORTED_MODULE_6__["ExportPageState"].categoryAnalyses),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_10__["Observable"])
    ], AdminExportViewComponent.prototype, "exportAnalyses$", void 0);
    AdminExportViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-export-view',
            template: __webpack_require__(/*! ./admin-export-view.component.html */ "./src/app/modules/admin/export/admin-export-view.component.html"),
            styles: [__webpack_require__(/*! ./admin-export-view.component.scss */ "./src/app/modules/admin/export/admin-export-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_export_service__WEBPACK_IMPORTED_MODULE_7__["ExportService"],
            _common_components_sidenav__WEBPACK_IMPORTED_MODULE_8__["SidenavMenuService"],
            _common_services__WEBPACK_IMPORTED_MODULE_3__["JwtService"],
            _ngxs_store__WEBPACK_IMPORTED_MODULE_2__["Store"]])
    ], AdminExportViewComponent);
    return AdminExportViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/export/content/content.component.html":
/*!*********************************************************************!*\
  !*** ./src/app/modules/admin/export/content/content.component.html ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<dx-data-grid\n  [customizeColumns]=\"config.customizeColumns\"\n  [columnAutoWidth]=\"config.columnAutoWidth\"\n  [noDataText]=\"config.noDataText\"\n  [columnMinWidth]=\"config.columnMinWidth\"\n  [columnResizingMode]=\"config.columnResizingMode\"\n  [allowColumnReordering]=\"config.allowColumnReordering\"\n  [allowColumnResizing]=\"config.allowColumnResizing\"\n  [showColumnHeaders]=\"config.showColumnHeaders\"\n  [showColumnLines]=\"config.showColumnLines\"\n  [showRowLines]=\"config.showRowLines\"\n  [showBorders]=\"config.showBorders\"\n  [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n  [hoverStateEnabled]=\"config.hoverStateEnabled\"\n  [wordWrapEnabled]=\"config.wordWrapEnabled\"\n  [scrolling]=\"config.scrolling\"\n  [sorting]=\"config.sorting\"\n  [dataSource]=\"analyses\"\n  [columns]=\"config.columns\"\n  [pager]=\"config.pager\"\n  [paging]=\"config.paging\"\n  [width]=\"config.width\"\n  [height]=\"config.height\"\n>\n  <div *dxTemplate=\"let cell of 'selectionCellTemplate'\">\n    <mat-checkbox\n      (change)=\"onItemToggled($event, cell.data)\"\n      [checked]=\"isSelectedForExport(cell.data)\"\n    >\n    </mat-checkbox>\n  </div>\n  <div *dxTemplate=\"let cell of 'selectionHeaderCellTemplate'\">\n    <mat-checkbox\n      [matTooltip]=\"toggleAllHint\"\n      (change)=\"onToggleAll($event)\"\n      [checked]=\"allSelected\"\n      [indeterminate]=\"someSelected\"\n      [hidden]=\"!analyses.length\"\n      i18n\n    >\n    </mat-checkbox>\n  </div>\n</dx-data-grid>\n"

/***/ }),

/***/ "./src/app/modules/admin/export/content/content.component.scss":
/*!*********************************************************************!*\
  !*** ./src/app/modules/admin/export/content/content.component.scss ***!
  \*********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  display: flex;\n  height: 100%;\n  flex-direction: column;\n  overflow: auto; }\n\n::ng-deep .dx-header-row ::ng-deep td:first-child ::ng-deep .dx-datagrid-text-content {\n  overflow: visible; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2V4cG9ydC9jb250ZW50L2NvbnRlbnQuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxhQUFhO0VBQ2IsWUFBWTtFQUNaLHNCQUFzQjtFQUN0QixjQUFjLEVBQUE7O0FBSWhCO0VBR00saUJBQWlCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FkbWluL2V4cG9ydC9jb250ZW50L2NvbnRlbnQuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyI6aG9zdCB7XG4gIGRpc3BsYXk6IGZsZXg7XG4gIGhlaWdodDogMTAwJTtcbiAgZmxleC1kaXJlY3Rpb246IGNvbHVtbjtcbiAgb3ZlcmZsb3c6IGF1dG87XG59XG5cbi8vIEZvbGxvd2luZyBpcyBhIHdvcmthcm91bmQgZm9yIGh0dHBzOi8vZ2l0aHViLmNvbS9hbmd1bGFyL21hdGVyaWFsMi9pc3N1ZXMvODYwMFxuOjpuZy1kZWVwIC5keC1oZWFkZXItcm93IHtcbiAgOjpuZy1kZWVwIHRkOmZpcnN0LWNoaWxkIHtcbiAgICA6Om5nLWRlZXAgLmR4LWRhdGFncmlkLXRleHQtY29udGVudCB7XG4gICAgICBvdmVyZmxvdzogdmlzaWJsZTtcbiAgICB9XG4gIH1cbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/export/content/content.component.ts":
/*!*******************************************************************!*\
  !*** ./src/app/modules/admin/export/content/content.component.ts ***!
  \*******************************************************************/
/*! exports provided: AdminExportContentComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminExportContentComponent", function() { return AdminExportContentComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var lodash_intersectionBy__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/intersectionBy */ "./node_modules/lodash/intersectionBy.js");
/* harmony import */ var lodash_intersectionBy__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_intersectionBy__WEBPACK_IMPORTED_MODULE_3__);




var AdminExportContentComponent = /** @class */ (function () {
    function AdminExportContentComponent(dxDataGridService) {
        this.dxDataGridService = dxDataGridService;
        // @Input() dashboards: Observable<any[]>;
        /**
         * Happens whenever an item in the list is toggled
         *
         * @type {EventEmitter<ExportItemChangeOutput>}
         * @memberof AdminExportContentComponent
         */
        this.change = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        /**
         * Happens when user toggles the 'All' checkbox in header
         *
         * @type {EventEmitter<boolean>}
         * @memberof AdminExportContentComponent
         */
        this.changeAll = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    AdminExportContentComponent.prototype.ngOnInit = function () {
        this.config = this.getConfig();
    };
    /**
     * Calculates intersection of export bucket and current selection list.
     * Meant to cacluate which analyses in current selection list are also in export.
     *
     * @returns
     * @memberof AdminExportContentComponent
     */
    AdminExportContentComponent.prototype.intersection = function () {
        return [
            lodash_intersectionBy__WEBPACK_IMPORTED_MODULE_3__(this.exportList, this.analyses, function (x) {
                return x.entityId ? x.entityId : x.id;
            }),
            this.analyses
        ];
    };
    Object.defineProperty(AdminExportContentComponent.prototype, "allSelected", {
        /**
         * Whether all analyses in current selection list have been added to export
         *
         * @readonly
         * @returns {boolean}
         * @memberof AdminExportContentComponent
         */
        get: function () {
            var _a = this.intersection(), intersection = _a[0], analyses = _a[1];
            return intersection.length === analyses.length && analyses.length > 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(AdminExportContentComponent.prototype, "someSelected", {
        /**
         * Whether some analyses in current selection list have been added to export
         *
         * @readonly
         * @returns {boolean}
         * @memberof AdminExportContentComponent
         */
        get: function () {
            var _a = this.intersection(), intersection = _a[0], analyses = _a[1];
            return intersection.length < analyses.length && intersection.length > 0;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(AdminExportContentComponent.prototype, "toggleAllHint", {
        /**
         * Returns hint text for toggle all checkbox
         *
         * @readonly
         * @type {string}
         * @memberof AdminExportContentComponent
         */
        get: function () {
            return this.allSelected ? 'Unselect All' : 'Select All';
        },
        enumerable: true,
        configurable: true
    });
    /**
     * Whether the given item is also present in export bucket
     *
     * @param {*} item
     * @returns {Observable<boolean>}
     * @memberof AdminExportContentComponent
     */
    AdminExportContentComponent.prototype.isSelectedForExport = function (item) {
        return this.exportList.some(function (a) {
            return item.entityId ? a.entityId === item.entityId : a.id === item.id;
        });
    };
    /**
     * Handler for checkbox change of each item in list
     *
     * @param {*} { checked }
     * @param {*} item
     * @memberof AdminExportContentComponent
     */
    AdminExportContentComponent.prototype.onItemToggled = function (_a, item) {
        var checked = _a.checked;
        this.change.emit({ checked: checked, item: item });
    };
    /**
     * Handle toggling the 'all' checkbox at the top of list
     *
     * @param {*} { checked }
     * @memberof AdminExportContentComponent
     */
    AdminExportContentComponent.prototype.onToggleAll = function (_a) {
        var checked = _a.checked;
        this.changeAll.emit(checked);
    };
    /**
     * Returns data grid config merged with default
     *
     * @returns
     * @memberof AdminExportContentComponent
     */
    AdminExportContentComponent.prototype.getConfig = function () {
        var columns = [
            {
                caption: '',
                allowSorting: false,
                alignment: 'center',
                headerCellTemplate: 'selectionHeaderCellTemplate',
                cellTemplate: 'selectionCellTemplate',
                width: '10%'
            },
            {
                caption: 'Name',
                dataField: 'name',
                allowSorting: true,
                alignment: 'left',
                width: '40%'
            },
            {
                caption: 'Type',
                dataField: 'type',
                allowSorting: true,
                alignment: 'left',
                width: '25%'
            },
            {
                caption: 'Metric Name',
                dataField: 'metricName',
                allowSorting: true,
                alignment: 'left',
                width: '25%'
            }
        ];
        return this.dxDataGridService.mergeWithDefaultConfig({
            columns: columns,
            noDataText: 'No Data. Select a category to view analyses.',
            width: '100%',
            height: '100%',
            paging: {
                pageSize: 10
            },
            pager: {
                showPageSizeSelector: true,
                showInfo: true
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminExportContentComponent.prototype, "exportList", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminExportContentComponent.prototype, "analyses", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminExportContentComponent.prototype, "change", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminExportContentComponent.prototype, "changeAll", void 0);
    AdminExportContentComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-export-content',
            template: __webpack_require__(/*! ./content.component.html */ "./src/app/modules/admin/export/content/content.component.html"),
            changeDetection: _angular_core__WEBPACK_IMPORTED_MODULE_1__["ChangeDetectionStrategy"].OnPush,
            styles: [__webpack_require__(/*! ./content.component.scss */ "./src/app/modules/admin/export/content/content.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__["DxDataGridService"]])
    ], AdminExportContentComponent);
    return AdminExportContentComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/export/export.service.ts":
/*!********************************************************!*\
  !*** ./src/app/modules/admin/export/export.service.ts ***!
  \********************************************************/
/*! exports provided: ExportService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExportService", function() { return ExportService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_fp_get__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/fp/get */ "./node_modules/lodash/fp/get.js");
/* harmony import */ var lodash_fp_get__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_get__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var _main_view_admin_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../main-view/admin.service */ "./src/app/modules/admin/main-view/admin.service.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");






var ANALYZE_MODULE_NAME = 'ANALYZE';
var ExportService = /** @class */ (function () {
    function ExportService(_adminService, _jwtService) {
        this._adminService = _adminService;
        this._jwtService = _jwtService;
    }
    ExportService.prototype.getDashboardsForCategory = function (categoryId) {
        var userId = this._jwtService.getUserId();
        return this._adminService
            .getRequest("observe/dashboards/" + categoryId + "/" + userId, { forWhat: 'export' })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["first"])(), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(lodash_fp_get__WEBPACK_IMPORTED_MODULE_2__("contents.observe")));
    };
    ExportService.prototype.getMetricList$ = function () {
        var projectId = 'workbench';
        return this._adminService
            .getRequest("internal/semantic/md?projectId=" + projectId, { forWhat: 'export' })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(lodash_fp_get__WEBPACK_IMPORTED_MODULE_2__("contents.[0]." + ANALYZE_MODULE_NAME)));
    };
    ExportService.prototype.getMetricList = function () {
        return this.getMetricList$().toPromise();
    };
    ExportService.prototype.getAnalysesByCategoryId = function (subCategoryId) {
        return this._adminService.getAnalysesByCategoryId(subCategoryId);
    };
    ExportService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_main_view_admin_service__WEBPACK_IMPORTED_MODULE_4__["AdminService"],
            _common_services__WEBPACK_IMPORTED_MODULE_5__["JwtService"]])
    ], ExportService);
    return ExportService;
}());



/***/ }),

/***/ "./src/app/modules/admin/export/index.ts":
/*!***********************************************!*\
  !*** ./src/app/modules/admin/export/index.ts ***!
  \***********************************************/
/*! exports provided: AdminExportViewComponent, AdminExportListComponent, AdminExportTreeComponent, AdminExportContentComponent, ExportPageState */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _admin_export_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./admin-export-view.component */ "./src/app/modules/admin/export/admin-export-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminExportViewComponent", function() { return _admin_export_view_component__WEBPACK_IMPORTED_MODULE_0__["AdminExportViewComponent"]; });

/* harmony import */ var _list__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./list */ "./src/app/modules/admin/export/list/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminExportListComponent", function() { return _list__WEBPACK_IMPORTED_MODULE_1__["AdminExportListComponent"]; });

/* harmony import */ var _tree_tree_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./tree/tree.component */ "./src/app/modules/admin/export/tree/tree.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminExportTreeComponent", function() { return _tree_tree_component__WEBPACK_IMPORTED_MODULE_2__["AdminExportTreeComponent"]; });

/* harmony import */ var _content_content_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./content/content.component */ "./src/app/modules/admin/export/content/content.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminExportContentComponent", function() { return _content_content_component__WEBPACK_IMPORTED_MODULE_3__["AdminExportContentComponent"]; });

/* harmony import */ var _state_export_page_state__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./state/export-page.state */ "./src/app/modules/admin/export/state/export-page.state.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ExportPageState", function() { return _state_export_page_state__WEBPACK_IMPORTED_MODULE_4__["ExportPageState"]; });








/***/ }),

/***/ "./src/app/modules/admin/export/list/admin-export-list.component.html":
/*!****************************************************************************!*\
  !*** ./src/app/modules/admin/export/list/admin-export-list.component.html ***!
  \****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<dx-data-grid\n  [customizeColumns]=\"config.customizeColumns\"\n  [columnAutoWidth]=\"config.columnAutoWidth\"\n  [columnMinWidth]=\"config.columnMinWidth\"\n  [columnResizingMode]=\"config.columnResizingMode\"\n  [noDataText]=\"config.noDataText\"\n  [allowColumnReordering]=\"config.allowColumnReordering\"\n  [allowColumnResizing]=\"config.allowColumnResizing\"\n  [showColumnHeaders]=\"config.showColumnHeaders\"\n  [showColumnLines]=\"config.showColumnLines\"\n  [showRowLines]=\"config.showRowLines\"\n  [showBorders]=\"config.showBorders\"\n  [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n  [hoverStateEnabled]=\"config.hoverStateEnabled\"\n  [wordWrapEnabled]=\"config.wordWrapEnabled\"\n  [scrolling]=\"config.scrolling\"\n  [sorting]=\"config.sorting\"\n  [dataSource]=\"exportList\"\n  [columns]=\"config.columns\"\n  [pager]=\"config.pager\"\n  [paging]=\"config.paging\"\n  [width]=\"config.width\"\n  [height]=\"config.height\"\n>\n  <div *dxTemplate=\"let cell of 'selectionCellTemplate'\">\n    <mat-checkbox (change)=\"onItemToggle($event, cell.data)\" [checked]=\"true\">\n    </mat-checkbox>\n  </div>\n  <div *dxTemplate=\"let cell of 'selectionHeaderCellTemplate'\">\n    <mat-checkbox\n      matTooltip=\"Remove All\"\n      (change)=\"onToggleAll($event)\"\n      [checked]=\"exportList.length > 0\"\n      [hidden]=\"!exportList.length\"\n      i18n\n    >\n    </mat-checkbox>\n  </div>\n</dx-data-grid>\n"

/***/ }),

/***/ "./src/app/modules/admin/export/list/admin-export-list.component.scss":
/*!****************************************************************************!*\
  !*** ./src/app/modules/admin/export/list/admin-export-list.component.scss ***!
  \****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  display: flex;\n  height: 100%;\n  flex-direction: column;\n  overflow: auto;\n  padding: 0 10px;\n  margin-left: 10px;\n  border-left: 2px dashed #d1d3d3; }\n\n::ng-deep .dx-header-row ::ng-deep td:first-child ::ng-deep .dx-datagrid-text-content {\n  overflow: visible; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2V4cG9ydC9saXN0L2FkbWluLWV4cG9ydC1saXN0LmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsYUFBYTtFQUNiLFlBQVk7RUFDWixzQkFBc0I7RUFDdEIsY0FBYztFQUNkLGVBQWU7RUFDZixpQkFBaUI7RUFDakIsK0JDS3VCLEVBQUE7O0FERHpCO0VBR00saUJBQWlCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FkbWluL2V4cG9ydC9saXN0L2FkbWluLWV4cG9ydC1saXN0LmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiQGltcG9ydCAnc3JjL3RoZW1lcy9iYXNlL2NvbG9ycyc7XG5cbjpob3N0IHtcbiAgZGlzcGxheTogZmxleDtcbiAgaGVpZ2h0OiAxMDAlO1xuICBmbGV4LWRpcmVjdGlvbjogY29sdW1uO1xuICBvdmVyZmxvdzogYXV0bztcbiAgcGFkZGluZzogMCAxMHB4O1xuICBtYXJnaW4tbGVmdDogMTBweDtcbiAgYm9yZGVyLWxlZnQ6IDJweCBkYXNoZWQgJHByaW1hcnktZ3JleS1nMTtcbn1cblxuLy8gRm9sbG93aW5nIGlzIGEgd29ya2Fyb3VuZCBmb3IgaHR0cHM6Ly9naXRodWIuY29tL2FuZ3VsYXIvbWF0ZXJpYWwyL2lzc3Vlcy84NjAwXG46Om5nLWRlZXAgLmR4LWhlYWRlci1yb3cge1xuICA6Om5nLWRlZXAgdGQ6Zmlyc3QtY2hpbGQge1xuICAgIDo6bmctZGVlcCAuZHgtZGF0YWdyaWQtdGV4dC1jb250ZW50IHtcbiAgICAgIG92ZXJmbG93OiB2aXNpYmxlO1xuICAgIH1cbiAgfVxufVxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/admin/export/list/admin-export-list.component.ts":
/*!**************************************************************************!*\
  !*** ./src/app/modules/admin/export/list/admin-export-list.component.ts ***!
  \**************************************************************************/
/*! exports provided: AdminExportListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminExportListComponent", function() { return AdminExportListComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");



var AdminExportListComponent = /** @class */ (function () {
    function AdminExportListComponent(dxDataGridService) {
        this.dxDataGridService = dxDataGridService;
        /**
         * Happens when individual item in the list is toggled
         *
         * @type {EventEmitter<ExportItemChangeOutput>}
         * @memberof AdminExportListComponent
         */
        this.change = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        /**
         * Happens when the 'All' checkbox in header is toggled
         *
         * @type {EventEmitter<boolean>}
         * @memberof AdminExportListComponent
         */
        this.changeAll = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.areAllSelected = false;
    }
    AdminExportListComponent.prototype.ngOnInit = function () {
        this.config = this.getConfig();
    };
    /**
     * Handle toggling individual items in the list
     *
     * @param {*} { checked }
     * @param {*} item
     * @memberof AdminExportListComponent
     */
    AdminExportListComponent.prototype.onItemToggle = function (_a, item) {
        var checked = _a.checked;
        this.change.emit({ checked: checked, item: item });
    };
    /**
     * Handle toggling the 'all' checkbox at top of the list
     *
     * @param {*} { checked }
     * @memberof AdminExportListComponent
     */
    AdminExportListComponent.prototype.onToggleAll = function (_a) {
        var checked = _a.checked;
        this.changeAll.emit(checked);
    };
    /**
     * Returns config for the grid
     *
     * @returns {Object}
     * @memberof AdminExportListComponent
     */
    AdminExportListComponent.prototype.getConfig = function () {
        var columns = [
            {
                caption: '',
                allowSorting: false,
                alignment: 'center',
                headerCellTemplate: 'selectionHeaderCellTemplate',
                cellTemplate: 'selectionCellTemplate',
                width: '10%'
            },
            {
                caption: 'Name',
                dataField: 'name',
                allowSorting: true,
                alignment: 'left',
                width: '40%'
            },
            {
                caption: 'Type',
                dataField: 'type',
                allowSorting: true,
                alignment: 'left',
                width: '20%'
            },
            {
                caption: 'Metric Name',
                dataField: 'metricName',
                allowSorting: true,
                alignment: 'left',
                width: '30%'
            }
        ];
        return this.dxDataGridService.mergeWithDefaultConfig({
            columns: columns,
            noDataText: 'No data to export. Select items from left to add them here.',
            width: '100%',
            height: '100%',
            paging: {
                pageSize: 10
            },
            pager: {
                showPageSizeSelector: true,
                showInfo: true
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminExportListComponent.prototype, "exportList", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminExportListComponent.prototype, "change", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminExportListComponent.prototype, "changeAll", void 0);
    AdminExportListComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-export-list',
            template: __webpack_require__(/*! ./admin-export-list.component.html */ "./src/app/modules/admin/export/list/admin-export-list.component.html"),
            changeDetection: _angular_core__WEBPACK_IMPORTED_MODULE_1__["ChangeDetectionStrategy"].OnPush,
            styles: [__webpack_require__(/*! ./admin-export-list.component.scss */ "./src/app/modules/admin/export/list/admin-export-list.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__["DxDataGridService"]])
    ], AdminExportListComponent);
    return AdminExportListComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/export/list/index.ts":
/*!****************************************************!*\
  !*** ./src/app/modules/admin/export/list/index.ts ***!
  \****************************************************/
/*! exports provided: AdminExportListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _admin_export_list_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./admin-export-list.component */ "./src/app/modules/admin/export/list/admin-export-list.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminExportListComponent", function() { return _admin_export_list_component__WEBPACK_IMPORTED_MODULE_0__["AdminExportListComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/export/state/export-page.state.ts":
/*!*****************************************************************!*\
  !*** ./src/app/modules/admin/export/state/export-page.state.ts ***!
  \*****************************************************************/
/*! exports provided: ExportPageState */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExportPageState", function() { return ExportPageState; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _ngxs_store__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @ngxs/store */ "./node_modules/@ngxs/store/fesm5/ngxs-store.js");
/* harmony import */ var _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../actions/export-page.actions */ "./src/app/modules/admin/export/actions/export-page.actions.ts");
/* harmony import */ var _export_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../export.service */ "./src/app/modules/admin/export/export.service.ts");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var lodash_clone__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/clone */ "./node_modules/lodash/clone.js");
/* harmony import */ var lodash_clone__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_clone__WEBPACK_IMPORTED_MODULE_5__);






var defaultExportPageState = {
    selectedModule: null,
    selectedCategory: null,
    shouldExportMetric: false,
    metrics: {},
    categoryAnalyses: [],
    categoryDashboards: [],
    exportData: {
        analyses: [],
        dashboards: [],
        metrics: []
    }
};
var ExportPageState = /** @class */ (function () {
    function ExportPageState(exportService) {
        this.exportService = exportService;
    }
    ExportPageState.exportList = function (state) {
        var analyses = state.exportData.analyses.map(function (analysis) { return (tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, analysis, { metricName: (state.metrics[analysis.semanticId] || {}).name })); });
        return analyses.concat(state.exportData.dashboards);
    };
    ExportPageState.categoryAnalyses = function (state) {
        var analyses = state.categoryAnalyses.map(function (analysis) { return (tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, analysis, { metricName: (state.metrics[analysis.semanticId] || {}).name })); });
        return analyses;
    };
    ExportPageState.exportData = function (state) {
        return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, state.exportData, { analyses: state.exportData.analyses.map(function (analysis) { return (tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, analysis, { metricName: (state.metrics[analysis.semanticId] || {}).name })); }) });
    };
    ExportPageState.prototype.treeItemSelected = function (_a, _b) {
        var patchState = _a.patchState, dispatch = _a.dispatch;
        var moduleName = _b.moduleName, item = _b.item;
        patchState(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({ selectedModule: moduleName, selectedCategory: item }, (moduleName === 'ANALYZE' ? { categoryDashboards: [] } : {}), (moduleName === 'OBSERVE' ? { categoryAnalyses: [] } : {})));
        switch (moduleName) {
            case 'ANALYZE':
                return dispatch(new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadAnalyses"](item.id));
            case 'OBSERVE':
                return dispatch(new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadDashboards"](item.id));
        }
    };
    ExportPageState.prototype.loadAnalyses = function (_a, _b) {
        var patchState = _a.patchState, dispatch = _a.dispatch;
        var categoryId = _b.categoryId;
        return this.exportService.getAnalysesByCategoryId(categoryId).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["tap"])(function (analyses) {
            patchState({
                categoryAnalyses: analyses
            });
            return dispatch(new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadMetrics"]());
        }));
    };
    ExportPageState.prototype.loadMetrics = function (_a, _b) {
        var patchState = _a.patchState, getState = _a.getState;
        var categoryId = _b.categoryId;
        var metrics = getState().metrics;
        var newMetrics = {};
        return this.exportService.getMetricList$().pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["tap"])(function (list) {
            list.forEach(function (metric) {
                newMetrics[metric.id] = { name: metric.metricName };
            });
            patchState({
                metrics: tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, metrics, newMetrics)
            });
        }));
    };
    ExportPageState.prototype.loadDashboards = function (_a, _b) {
        var patchState = _a.patchState;
        var categoryId = _b.categoryId;
    };
    ExportPageState.prototype.addAnalysisToExport = function (_a, _b) {
        var patchState = _a.patchState, getState = _a.getState;
        var analysis = _b.analysis;
        var exportData = getState().exportData;
        var alreadyInExport = exportData.analyses.some(function (exportAnalysis) { return exportAnalysis.id === analysis.id; });
        return (!alreadyInExport &&
            patchState({
                exportData: tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, exportData, { analyses: exportData.analyses.concat([analysis]) })
            }));
    };
    ExportPageState.prototype.removeAnalysisFromExport = function (_a, _b) {
        var patchState = _a.patchState, getState = _a.getState;
        var analysis = _b.analysis;
        var exportData = getState().exportData;
        return patchState({
            exportData: tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, exportData, { analyses: exportData.analyses.filter(function (a) { return a.id !== analysis.id; }) })
        });
    };
    ExportPageState.prototype.addAllAnalysesToExport = function (_a) {
        var getState = _a.getState, dispatch = _a.dispatch;
        var categoryAnalyses = getState().categoryAnalyses;
        return dispatch(categoryAnalyses.map(function (analysis) { return new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["AddAnalysisToExport"](analysis); }));
    };
    ExportPageState.prototype.removeAllAnalysesFromExport = function (_a) {
        var getState = _a.getState, dispatch = _a.dispatch;
        var categoryAnalyses = getState().categoryAnalyses;
        return dispatch(categoryAnalyses.map(function (analysis) { return new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["RemoveAnalysisFromExport"](analysis); }));
    };
    ExportPageState.prototype.addDashboardToExport = function (_a, _b) {
        var patchState = _a.patchState, getState = _a.getState;
        var dashboard = _b.dashboard;
        var exportData = getState().exportData;
        return patchState({
            exportData: tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, exportData, { dashboards: exportData.dashboards.concat([dashboard]) })
        });
    };
    ExportPageState.prototype.removeDashboardFromExport = function (_a, _b) {
        var patchState = _a.patchState, getState = _a.getState;
        var dashboard = _b.dashboard;
        var exportData = getState().exportData;
        return patchState({
            exportData: tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, exportData, { dashboards: exportData.dashboards.filter(function (d) { return d.entityId !== dashboard.entityId; }) })
        });
    };
    ExportPageState.prototype.clearExportList = function (_a) {
        var dispatch = _a.dispatch, getState = _a.getState;
        var exportData = getState().exportData;
        var actions = exportData.analyses.map(function (analysis) { return new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["RemoveAnalysisFromExport"](analysis); }).concat(exportData.dashboards.map(function (dashboard) { return new _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["RemoveDashboardFromExport"](dashboard); }));
        return dispatch(actions);
    };
    ExportPageState.prototype.resetState = function (_a) {
        var setState = _a.setState;
        return setState(lodash_clone__WEBPACK_IMPORTED_MODULE_5__(defaultExportPageState));
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportSelectTreeItem"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportSelectTreeItem"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "treeItemSelected", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadAnalyses"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadAnalyses"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "loadAnalyses", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadMetrics"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadAnalyses"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "loadMetrics", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadDashboards"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ExportLoadDashboards"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "loadDashboards", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["AddAnalysisToExport"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["AddAnalysisToExport"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "addAnalysisToExport", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["RemoveAnalysisFromExport"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["AddAnalysisToExport"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "removeAnalysisFromExport", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["AddAllAnalysesToExport"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "addAllAnalysesToExport", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["RemoveAllAnalysesFromExport"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "removeAllAnalysesFromExport", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["AddDashboardToExport"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["AddDashboardToExport"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "addDashboardToExport", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["RemoveDashboardFromExport"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["AddDashboardToExport"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "removeDashboardFromExport", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ClearExport"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "clearExportList", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_export_page_actions__WEBPACK_IMPORTED_MODULE_2__["ResetExportPageState"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], ExportPageState.prototype, "resetState", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Selector"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", Array)
    ], ExportPageState, "exportList", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Selector"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", Array)
    ], ExportPageState, "categoryAnalyses", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Selector"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", Object)
    ], ExportPageState, "exportData", null);
    ExportPageState = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["State"])({
            name: 'exportPage',
            defaults: lodash_clone__WEBPACK_IMPORTED_MODULE_5__(defaultExportPageState)
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_export_service__WEBPACK_IMPORTED_MODULE_3__["ExportService"]])
    ], ExportPageState);
    return ExportPageState;
}());



/***/ }),

/***/ "./src/app/modules/admin/export/tree/tree.component.html":
/*!***************************************************************!*\
  !*** ./src/app/modules/admin/export/tree/tree.component.html ***!
  \***************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<h1>Select category</h1>\n<tree-root\n  [nodes]=\"menu\"\n  [options]=\"treeOptions\"\n  (activate)=\"onClickMenuItem($event)\"\n></tree-root>\n\n<!--\n  <h2>Dashboards</h2>\n  <tree-root\n    [nodes]=\"observeMenu$ | async\"\n    [options]=\"treeOptions\"\n    (activate)=\"onClickDashboardMenu($event)\"\n  ></tree-root>\n-->\n"

/***/ }),

/***/ "./src/app/modules/admin/export/tree/tree.component.scss":
/*!***************************************************************!*\
  !*** ./src/app/modules/admin/export/tree/tree.component.scss ***!
  \***************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  display: flex;\n  flex-direction: column;\n  height: 100%;\n  overflow: auto;\n  padding: 0 0 0 20px; }\n\nh1 {\n  background: #d3e9fc;\n  color: #0b4d99;\n  font-size: 12px;\n  font-weight: 600;\n  line-height: 36px;\n  margin: 0;\n  padding-left: 10px; }\n\ntree-root {\n  font-size: 16px; }\n\n::ng-deep .tree-node-level-1 {\n  font-weight: 800;\n  padding: 5px 0; }\n\n::ng-deep .tree-node-level-2 {\n  font-weight: 600;\n  padding: 5px 0; }\n\n::ng-deep .tree-node-level-3 {\n  font-weight: 400;\n  padding: 0; }\n\n::ng-deep .node-content-wrapper-active.node-content-wrapper-active {\n  background: transparent;\n  color: #0077be; }\n\n::ng-deep .node-content-wrapper-active.node-content-wrapper-active:hover {\n    background: #f7fbff; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2V4cG9ydC90cmVlL3RyZWUuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxhQUFhO0VBQ2Isc0JBQXNCO0VBQ3RCLFlBQVk7RUFDWixjQUFjO0VBQ2QsbUJBQW1CLEVBQUE7O0FBR3JCO0VBQ0UsbUJBQW1CO0VBQ25CLGNBQWM7RUFDZCxlQUFlO0VBQ2YsZ0JBQWdCO0VBQ2hCLGlCQUFpQjtFQUNqQixTQUFTO0VBQ1Qsa0JBQWtCLEVBQUE7O0FBR3BCO0VBQ0UsZUFBZSxFQUFBOztBQUdqQjtFQUNFLGdCQUFnQjtFQUNoQixjQUFjLEVBQUE7O0FBR2hCO0VBQ0UsZ0JBQWdCO0VBQ2hCLGNBQWMsRUFBQTs7QUFHaEI7RUFDRSxnQkFBZ0I7RUFDaEIsVUFBVSxFQUFBOztBQUdaO0VBQ0UsdUJBQXVCO0VBQ3ZCLGNDdkN1QixFQUFBOztBRHFDekI7SUFLSSxtQkFBbUIsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYWRtaW4vZXhwb3J0L3RyZWUvdHJlZS5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgJ3NyYy90aGVtZXMvYmFzZS9jb2xvcnMnO1xuXG46aG9zdCB7XG4gIGRpc3BsYXk6IGZsZXg7XG4gIGZsZXgtZGlyZWN0aW9uOiBjb2x1bW47XG4gIGhlaWdodDogMTAwJTtcbiAgb3ZlcmZsb3c6IGF1dG87XG4gIHBhZGRpbmc6IDAgMCAwIDIwcHg7XG59XG5cbmgxIHtcbiAgYmFja2dyb3VuZDogI2QzZTlmYztcbiAgY29sb3I6ICMwYjRkOTk7XG4gIGZvbnQtc2l6ZTogMTJweDtcbiAgZm9udC13ZWlnaHQ6IDYwMDtcbiAgbGluZS1oZWlnaHQ6IDM2cHg7XG4gIG1hcmdpbjogMDtcbiAgcGFkZGluZy1sZWZ0OiAxMHB4O1xufVxuXG50cmVlLXJvb3Qge1xuICBmb250LXNpemU6IDE2cHg7XG59XG5cbjo6bmctZGVlcCAudHJlZS1ub2RlLWxldmVsLTEge1xuICBmb250LXdlaWdodDogODAwO1xuICBwYWRkaW5nOiA1cHggMDtcbn1cblxuOjpuZy1kZWVwIC50cmVlLW5vZGUtbGV2ZWwtMiB7XG4gIGZvbnQtd2VpZ2h0OiA2MDA7XG4gIHBhZGRpbmc6IDVweCAwO1xufVxuXG46Om5nLWRlZXAgLnRyZWUtbm9kZS1sZXZlbC0zIHtcbiAgZm9udC13ZWlnaHQ6IDQwMDtcbiAgcGFkZGluZzogMDtcbn1cblxuOjpuZy1kZWVwIC5ub2RlLWNvbnRlbnQtd3JhcHBlci1hY3RpdmUubm9kZS1jb250ZW50LXdyYXBwZXItYWN0aXZlIHtcbiAgYmFja2dyb3VuZDogdHJhbnNwYXJlbnQ7XG4gIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuXG4gICY6aG92ZXIge1xuICAgIGJhY2tncm91bmQ6ICNmN2ZiZmY7XG4gIH1cbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/export/tree/tree.component.ts":
/*!*************************************************************!*\
  !*** ./src/app/modules/admin/export/tree/tree.component.ts ***!
  \*************************************************************/
/*! exports provided: AdminExportTreeComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminExportTreeComponent", function() { return AdminExportTreeComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var angular_tree_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! angular-tree-component */ "./node_modules/angular-tree-component/dist/angular-tree-component.js");



var AdminExportTreeComponent = /** @class */ (function () {
    function AdminExportTreeComponent() {
        /**
         * Happens when a leaf node (which has no children) is clicked.
         *
         * @type {EventEmitter<SelectMenuOutput>}
         * @memberof AdminExportTreeComponent
         */
        this.select = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.treeOptions = {
            isExpandedField: 'expanded',
            actionMapping: {
                mouse: {
                    click: function (tree, node, $event) {
                        if (node.hasChildren) {
                            angular_tree_component__WEBPACK_IMPORTED_MODULE_2__["TREE_ACTIONS"].TOGGLE_EXPANDED(tree, node, $event);
                        }
                        else {
                            angular_tree_component__WEBPACK_IMPORTED_MODULE_2__["TREE_ACTIONS"].ACTIVATE(tree, node, $event);
                        }
                    }
                }
            }
        };
    }
    AdminExportTreeComponent.prototype.ngOnInit = function () { };
    /**
     * Handles clicking a menu item
     *
     * @param {*} event
     * @memberof AdminExportTreeComponent
     */
    AdminExportTreeComponent.prototype.onClickMenuItem = function (event) {
        this.select.emit({ moduleName: 'ANALYZE', menuItem: event.node.data });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminExportTreeComponent.prototype, "menu", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminExportTreeComponent.prototype, "select", void 0);
    AdminExportTreeComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-export-tree',
            template: __webpack_require__(/*! ./tree.component.html */ "./src/app/modules/admin/export/tree/tree.component.html"),
            changeDetection: _angular_core__WEBPACK_IMPORTED_MODULE_1__["ChangeDetectionStrategy"].OnPush,
            styles: [__webpack_require__(/*! ./tree.component.scss */ "./src/app/modules/admin/export/tree/tree.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [])
    ], AdminExportTreeComponent);
    return AdminExportTreeComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/import/actions/import-page.actions.ts":
/*!*********************************************************************!*\
  !*** ./src/app/modules/admin/import/actions/import-page.actions.ts ***!
  \*********************************************************************/
/*! exports provided: ClearImport, SelectAnalysisGlobalCategory, LoadAllAnalyzeCategories, LoadMetrics, LoadAnalysesForCategory, RemoveFileFromImport, RefreshAllCategories */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ClearImport", function() { return ClearImport; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SelectAnalysisGlobalCategory", function() { return SelectAnalysisGlobalCategory; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoadAllAnalyzeCategories", function() { return LoadAllAnalyzeCategories; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoadMetrics", function() { return LoadMetrics; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LoadAnalysesForCategory", function() { return LoadAnalysesForCategory; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RemoveFileFromImport", function() { return RemoveFileFromImport; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RefreshAllCategories", function() { return RefreshAllCategories; });
var ClearImport = /** @class */ (function () {
    function ClearImport() {
    }
    ClearImport.type = '[Admin Import OnDestroy] Clear all import page data';
    return ClearImport;
}());

var SelectAnalysisGlobalCategory = /** @class */ (function () {
    function SelectAnalysisGlobalCategory(category) {
        this.category = category;
    }
    SelectAnalysisGlobalCategory.type = '[Admin Import Page] Select global analysis category';
    return SelectAnalysisGlobalCategory;
}());

var LoadAllAnalyzeCategories = /** @class */ (function () {
    function LoadAllAnalyzeCategories() {
    }
    LoadAllAnalyzeCategories.type = '[Admin Import Page OnInit] Load all analyze categories';
    return LoadAllAnalyzeCategories;
}());

var LoadMetrics = /** @class */ (function () {
    function LoadMetrics() {
    }
    LoadMetrics.type = '[Admin Import Page OnInit] Load all metrics';
    return LoadMetrics;
}());

var LoadAnalysesForCategory = /** @class */ (function () {
    function LoadAnalysesForCategory(category) {
        this.category = category;
    }
    LoadAnalysesForCategory.type = '[Admin Import Category Change] Load analyses for category';
    return LoadAnalysesForCategory;
}());

var RemoveFileFromImport = /** @class */ (function () {
    function RemoveFileFromImport(filename) {
        this.filename = filename;
    }
    RemoveFileFromImport.type = '[Admin Import Delete File] Remove file from import';
    return RemoveFileFromImport;
}());

var RefreshAllCategories = /** @class */ (function () {
    function RefreshAllCategories() {
    }
    RefreshAllCategories.type = '[Admin Import File Load] Refresh all cached categories';
    return RefreshAllCategories;
}());



/***/ }),

/***/ "./src/app/modules/admin/import/admin-import-view.component.html":
/*!***********************************************************************!*\
  !*** ./src/app/modules/admin/import/admin-import-view.component.html ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-toolbar\n  fxLayout=\"row\"\n  fxLayoutAlign=\"space-between center\"\n  style=\"width: 100%;\"\n>\n  <div fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n    <div class=\"admin-title\" i18n>Import Analysis</div>\n    <div\n      fxLayout=\"row\"\n      fxLayoutAlign=\"start center\"\n      style=\"font-size: 16px; margin-left: 100px;\"\n    >\n      <div i18n style=\"margin-left: 10px;\">Select package file(s)</div>\n      <input\n        type=\"file\"\n        id=\"input-file-id\"\n        style=\"display: none;\"\n        (change)=\"readFiles($event)\"\n        file-upload\n        multiple\n        accept=\"json/*\"\n      />\n      <label\n        for=\"input-file-id\"\n        class=\"mat-raised-button mat-primary\"\n        style=\"margin: 0 10px;\"\n        i18n\n      >\n        Browse\n      </label>\n      <div i18n>to import into</div>\n      <admin-import-category-select\n        [categories]=\"categories$ | async\"\n        (change)=\"onCategoryChange($event)\"\n      ></admin-import-category-select>\n    </div>\n  </div>\n</mat-toolbar>\n<div class=\"admin-import-page__list-container\">\n  <mat-card>\n    <div i18n class=\"grid-title\">Package file(s) details</div>\n    <admin-import-file-list\n      [files]=\"files\"\n      (remove)=\"onRemoveFile($event)\"\n    ></admin-import-file-list>\n  </mat-card>\n\n  <mat-card>\n    <div fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n      <div i18n class=\"grid-title\">Analyses to import</div>\n      <div>\n        <button\n          mat-raised-button\n          color=\"primary\"\n          i18n\n          (click)=\"import()\"\n          style=\"margin-right: 10px;\"\n          [disabled]=\"!canImport()\"\n        >\n          Import\n        </button>\n        <button\n          mat-raised-button\n          color=\"primary\"\n          i18n\n          (click)=\"exportErrors()\"\n          [disabled]=\"!userCanExportErrors\"\n        >\n          Export Error Logs\n        </button>\n      </div>\n    </div>\n    <admin-import-list\n      [analyses]=\"analyses\"\n      [categories]=\"categories$ | async\"\n      (validityChange)=\"onAnalysesValiditychange($event)\"\n      (categorySelected)=\"onAnalysisCategoryChange($event)\"\n    ></admin-import-list>\n  </mat-card>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/import/admin-import-view.component.scss":
/*!***********************************************************************!*\
  !*** ./src/app/modules/admin/import/admin-import-view.component.scss ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.mat-toolbar {\n  background-color: transparent;\n  padding: 0 20px; }\n\n.mat-toolbar .admin-title {\n    color: #636363;\n    font-weight: bold;\n    margin-right: 10px; }\n\n.mat-toolbar admin-import-category-select {\n    margin-left: 10px; }\n\n.grid-title {\n  color: #636363;\n  font-size: 18px;\n  height: 36px;\n  margin-right: 10px; }\n\nadmin-import-file-list {\n  padding: 10px 0 0;\n  display: block; }\n\nadmin-import-list {\n  padding: 10px 0 0;\n  height: calc(100% - 120px);\n  display: block; }\n\n.admin-import-page__list-container {\n  display: grid;\n  height: calc(100% - 64px);\n  grid-template-columns: 1fr 2fr; }\n\n.admin-import-page__list-container mat-card {\n    box-shadow: none; }\n\n.admin-import-page__list-container mat-card:last-child {\n    border-left: 2px dashed #d1d3d3; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2ltcG9ydC9hZG1pbi1pbXBvcnQtdmlldy5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLFdBQVcsRUFBQTs7QUFHYjtFQUNFLDZCQUE2QjtFQUM3QixlQUFlLEVBQUE7O0FBRmpCO0lBS0ksY0NBcUI7SURDckIsaUJBQWlCO0lBQ2pCLGtCQUFrQixFQUFBOztBQVB0QjtJQVdJLGlCQUFpQixFQUFBOztBQUlyQjtFQUNFLGNDWHVCO0VEWXZCLGVBQWU7RUFDZixZQUFZO0VBQ1osa0JBQWtCLEVBQUE7O0FBR3BCO0VBQ0UsaUJBQWlCO0VBQ2pCLGNBQWMsRUFBQTs7QUFHaEI7RUFDRSxpQkFBaUI7RUFDakIsMEJBQTBCO0VBQzFCLGNBQWMsRUFBQTs7QUFJZDtFQUNFLGFBQWE7RUFDYix5QkFBeUI7RUFDekIsOEJBQThCLEVBQUE7O0FBSC9CO0lBTUcsZ0JBQWdCLEVBQUE7O0FBTm5CO0lBVUcsK0JDcENtQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hZG1pbi9pbXBvcnQvYWRtaW4taW1wb3J0LXZpZXcuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLm1hdC10b29sYmFyIHtcbiAgYmFja2dyb3VuZC1jb2xvcjogdHJhbnNwYXJlbnQ7XG4gIHBhZGRpbmc6IDAgMjBweDtcblxuICAuYWRtaW4tdGl0bGUge1xuICAgIGNvbG9yOiAkZ3JleS10ZXh0LWNvbG9yO1xuICAgIGZvbnQtd2VpZ2h0OiBib2xkO1xuICAgIG1hcmdpbi1yaWdodDogMTBweDtcbiAgfVxuXG4gIGFkbWluLWltcG9ydC1jYXRlZ29yeS1zZWxlY3Qge1xuICAgIG1hcmdpbi1sZWZ0OiAxMHB4O1xuICB9XG59XG5cbi5ncmlkLXRpdGxlIHtcbiAgY29sb3I6ICRncmV5LXRleHQtY29sb3I7XG4gIGZvbnQtc2l6ZTogMThweDtcbiAgaGVpZ2h0OiAzNnB4O1xuICBtYXJnaW4tcmlnaHQ6IDEwcHg7XG59XG5cbmFkbWluLWltcG9ydC1maWxlLWxpc3Qge1xuICBwYWRkaW5nOiAxMHB4IDAgMDtcbiAgZGlzcGxheTogYmxvY2s7XG59XG5cbmFkbWluLWltcG9ydC1saXN0IHtcbiAgcGFkZGluZzogMTBweCAwIDA7XG4gIGhlaWdodDogY2FsYygxMDAlIC0gMTIwcHgpO1xuICBkaXNwbGF5OiBibG9jaztcbn1cblxuLmFkbWluLWltcG9ydC1wYWdlIHtcbiAgJl9fbGlzdC1jb250YWluZXIge1xuICAgIGRpc3BsYXk6IGdyaWQ7XG4gICAgaGVpZ2h0OiBjYWxjKDEwMCUgLSA2NHB4KTtcbiAgICBncmlkLXRlbXBsYXRlLWNvbHVtbnM6IDFmciAyZnI7XG5cbiAgICBtYXQtY2FyZCB7XG4gICAgICBib3gtc2hhZG93OiBub25lO1xuICAgIH1cblxuICAgIG1hdC1jYXJkOmxhc3QtY2hpbGQge1xuICAgICAgYm9yZGVyLWxlZnQ6IDJweCBkYXNoZWQgJHByaW1hcnktZ3JleS1nMTtcbiAgICB9XG4gIH1cbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/import/admin-import-view.component.ts":
/*!*********************************************************************!*\
  !*** ./src/app/modules/admin/import/admin-import-view.component.ts ***!
  \*********************************************************************/
/*! exports provided: AdminImportViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminImportViewComponent", function() { return AdminImportViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _ngxs_store__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @ngxs/store */ "./node_modules/@ngxs/store/fesm5/ngxs-store.js");
/* harmony import */ var _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./actions/import-page.actions */ "./src/app/modules/admin/import/actions/import-page.actions.ts");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/fp/pipe */ "./node_modules/lodash/fp/pipe.js");
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/fp/filter */ "./node_modules/lodash/fp/filter.js");
/* harmony import */ var lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/fp/map */ "./node_modules/lodash/fp/map.js");
/* harmony import */ var lodash_fp_map__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var lodash_reduce__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! lodash/reduce */ "./node_modules/lodash/reduce.js");
/* harmony import */ var lodash_reduce__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(lodash_reduce__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_10__);
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! lodash/filter */ "./node_modules/lodash/filter.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_11___default = /*#__PURE__*/__webpack_require__.n(lodash_filter__WEBPACK_IMPORTED_MODULE_11__);
/* harmony import */ var lodash_toString__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! lodash/toString */ "./node_modules/lodash/toString.js");
/* harmony import */ var lodash_toString__WEBPACK_IMPORTED_MODULE_12___default = /*#__PURE__*/__webpack_require__.n(lodash_toString__WEBPACK_IMPORTED_MODULE_12__);
/* harmony import */ var lodash_pick__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! lodash/pick */ "./node_modules/lodash/pick.js");
/* harmony import */ var lodash_pick__WEBPACK_IMPORTED_MODULE_13___default = /*#__PURE__*/__webpack_require__.n(lodash_pick__WEBPACK_IMPORTED_MODULE_13__);
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_14___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_14__);
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_15___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_15__);
/* harmony import */ var lodash_fp_flatMap__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! lodash/fp/flatMap */ "./node_modules/lodash/fp/flatMap.js");
/* harmony import */ var lodash_fp_flatMap__WEBPACK_IMPORTED_MODULE_16___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_flatMap__WEBPACK_IMPORTED_MODULE_16__);
/* harmony import */ var json_2_csv__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! json-2-csv */ "./node_modules/json-2-csv/lib/converter.js");
/* harmony import */ var json_2_csv__WEBPACK_IMPORTED_MODULE_17___default = /*#__PURE__*/__webpack_require__.n(json_2_csv__WEBPACK_IMPORTED_MODULE_17__);
/* harmony import */ var file_saver__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! file-saver */ "./node_modules/file-saver/FileSaver.js");
/* harmony import */ var file_saver__WEBPACK_IMPORTED_MODULE_18___default = /*#__PURE__*/__webpack_require__.n(file_saver__WEBPACK_IMPORTED_MODULE_18__);
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! moment */ "./node_modules/moment/moment.js");
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_19___default = /*#__PURE__*/__webpack_require__.n(moment__WEBPACK_IMPORTED_MODULE_19__);
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _import_service__WEBPACK_IMPORTED_MODULE_21__ = __webpack_require__(/*! ./import.service */ "./src/app/modules/admin/import/import.service.ts");
/* harmony import */ var _category_category_service__WEBPACK_IMPORTED_MODULE_22__ = __webpack_require__(/*! ../category/category.service */ "./src/app/modules/admin/category/category.service.ts");
/* harmony import */ var _common_components_sidenav__WEBPACK_IMPORTED_MODULE_23__ = __webpack_require__(/*! ../../../common/components/sidenav */ "./src/app/common/components/sidenav/index.ts");
/* harmony import */ var _common_utils_executeAllPromises__WEBPACK_IMPORTED_MODULE_24__ = __webpack_require__(/*! ../../../common/utils/executeAllPromises */ "./src/app/common/utils/executeAllPromises.ts");
/* harmony import */ var _common_utils_fileManager__WEBPACK_IMPORTED_MODULE_25__ = __webpack_require__(/*! ../../../common/utils/fileManager */ "./src/app/common/utils/fileManager.ts");
/* harmony import */ var _consts__WEBPACK_IMPORTED_MODULE_26__ = __webpack_require__(/*! ../consts */ "./src/app/modules/admin/consts.ts");
/* harmony import */ var _export_export_service__WEBPACK_IMPORTED_MODULE_27__ = __webpack_require__(/*! ../export/export.service */ "./src/app/modules/admin/export/export.service.ts");
/* harmony import */ var _analyze_types__WEBPACK_IMPORTED_MODULE_28__ = __webpack_require__(/*! ../../analyze/types */ "./src/app/modules/analyze/types.ts");
/* harmony import */ var _analyze_consts__WEBPACK_IMPORTED_MODULE_29__ = __webpack_require__(/*! ../../analyze/consts */ "./src/app/modules/analyze/consts.ts");






























var DUPLICATE_GRID_OBJECT_PROPS = {
    logColor: 'brown',
    log: 'Analysis exists. Please Overwrite to delete existing data.',
    errorMsg: 'Analysis exists. Please Overwrite to delete existing data.',
    duplicateAnalysisInd: true,
    errorInd: false,
    noMetricInd: false
};
var NORMAL_GRID_OBJECT_PROPS = {
    logColor: 'transparent',
    log: '',
    errorMsg: '',
    duplicateAnalysisInd: false,
    errorInd: false,
    noMetricInd: false
};
var LEGACY_GRID_OBJECT_PROPS = {
    logColor: 'red',
    log: 'Invalid analysis structure. Missing sipQuery property.',
    errorMsg: 'Invalid analysis structure. Missing sipQuery property.',
    duplicateAnalysisInd: false,
    errorInd: true,
    legacyInd: true,
    noMetricInd: false
};
var AdminImportViewComponent = /** @class */ (function () {
    function AdminImportViewComponent(_importService, _exportService, _categoryService, _sidenav, _jwtService, store) {
        this._importService = _importService;
        this._exportService = _exportService;
        this._categoryService = _categoryService;
        this._sidenav = _sidenav;
        this._jwtService = _jwtService;
        this.store = store;
        this.userCanExportErrors = false;
        this.atLeast1AnalysisIsSelected = false;
    }
    AdminImportViewComponent.prototype.ngOnInit = function () {
        this._sidenav.updateMenu(_consts__WEBPACK_IMPORTED_MODULE_26__["AdminMenuData"], 'ADMIN');
        this.store.dispatch([new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_3__["LoadAllAnalyzeCategories"](), new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_3__["LoadMetrics"]()]);
    };
    AdminImportViewComponent.prototype.ngOnDestroy = function () {
        this.store.dispatch(new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_3__["ClearImport"]());
    };
    /**
     * Reads a file that user just added. Tries to parse it
     * and add analyses to list for selection.
     *
     * @param {*} event
     * @memberof AdminImportViewComponent
     */
    AdminImportViewComponent.prototype.readFiles = function (event) {
        var _this = this;
        var files = event.target.files;
        /* Filter out non-json files */
        var contentPromises = lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6__(function (file) { return file.type === 'application/json'; }), lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__(function (file) {
            return Object(_common_utils_fileManager__WEBPACK_IMPORTED_MODULE_25__["getFileContents"])(file).then(function (content) {
                var analyses = JSON.parse(content);
                return {
                    name: file.name,
                    count: analyses.length,
                    analyses: analyses
                };
            });
        }))(files);
        Promise.all(contentPromises).then(function (contents) {
            _this.fileContents = contents;
            _this.splitFileContents(contents);
            // clear the file input
            event.target.value = '';
        });
    };
    AdminImportViewComponent.prototype.splitFileContents = function (contents) {
        var _this = this;
        var hasErrors = false;
        this.atLeast1AnalysisIsSelected = false;
        this.files = lodash_map__WEBPACK_IMPORTED_MODULE_8__(contents, function (_a) {
            var name = _a.name, count = _a.count;
            return ({ name: name, count: count });
        });
        this.analyses = lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__(lodash_fp_flatMap__WEBPACK_IMPORTED_MODULE_16__(function (_a) {
            var analyses = _a.analyses;
            return analyses;
        }), lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__(function (analysis) {
            if (Object(_analyze_types__WEBPACK_IMPORTED_MODULE_28__["isDSLAnalysis"])(analysis)) {
                analysis.category = _this.selectedCategory || '';
            }
            else {
                analysis.categoryId = +(_this.selectedCategory || '');
            }
            var gridObj = _this.getAnalysisObjectForGrid(analysis);
            if (gridObj.errorInd) {
                hasErrors = true;
            }
            return gridObj;
        }))(contents);
        this.userCanExportErrors = hasErrors;
    };
    AdminImportViewComponent.prototype.onRemoveFile = function (fileName) {
        this.fileContents = lodash_filter__WEBPACK_IMPORTED_MODULE_11__(this.fileContents, function (_a) {
            var name = _a.name;
            return fileName !== name;
        });
        this.splitFileContents(this.fileContents);
    };
    AdminImportViewComponent.prototype.updateGridObject = function (analysisId) {
        var id = this.analyses.findIndex(function (_a) {
            var analysis = _a.analysis;
            return analysis.id === analysisId;
        });
        var newGridObject = this.getAnalysisObjectForGrid(this.analyses[id].analysis, this.analyses[id].selection);
        this.analyses.splice(id, 1, newGridObject);
    };
    AdminImportViewComponent.prototype.onCategoryChange = function (categoryId) {
        var _this = this;
        this.selectedCategory = categoryId;
        this.store
            .dispatch(new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_3__["SelectAnalysisGlobalCategory"](categoryId))
            .subscribe(function () {
            _this.splitFileContents(_this.fileContents);
        });
    };
    AdminImportViewComponent.prototype.onAnalysisCategoryChange = function (_a) {
        var _this = this;
        var categoryId = _a.categoryId, analysisId = _a.analysisId;
        this.setAnalysisCategory(categoryId, analysisId);
        this.store
            .dispatch(new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_3__["LoadAnalysesForCategory"](categoryId))
            .subscribe(function () {
            _this.updateGridObject(analysisId);
        });
    };
    AdminImportViewComponent.prototype.setAnalysisCategory = function (categoryId, analysisId) {
        var a = this.analyses.find(function (_a) {
            var analysis = _a.analysis;
            return analysis.id === analysisId;
        });
        if (a && a.analysis) {
            if (Object(_analyze_types__WEBPACK_IMPORTED_MODULE_28__["isDSLAnalysis"])(a.analysis)) {
                a.analysis.category = lodash_toString__WEBPACK_IMPORTED_MODULE_12__(categoryId);
            }
            else {
                a.analysis.categoryId = lodash_toString__WEBPACK_IMPORTED_MODULE_12__(categoryId);
            }
        }
    };
    AdminImportViewComponent.prototype.getAnalysisObjectForGrid = function (analysis, selection) {
        if (selection === void 0) { selection = false; }
        var _a = this.store.selectSnapshot(function (state) { return state.admin.importPage; }), metrics = _a.metrics, referenceAnalyses = _a.referenceAnalyses;
        var metric = metrics[analysis.metricName];
        if (metric) {
            analysis.semanticId = metric.id;
        }
        var categoryId = Object(_analyze_types__WEBPACK_IMPORTED_MODULE_28__["isDSLAnalysis"])(analysis)
            ? analysis.category
            : analysis.categoryId;
        var analysisCategory = referenceAnalyses[categoryId.toString()] || {};
        var analysisFromBE = analysisCategory[analysis.name + ":" + analysis.metricName + ":" + analysis.type];
        var possibilitySelector = !Object(_analyze_types__WEBPACK_IMPORTED_MODULE_28__["isDSLAnalysis"])(analysis) && _analyze_consts__WEBPACK_IMPORTED_MODULE_29__["DSL_ANALYSIS_TYPES"].includes(analysis.type)
            ? 'legacy'
            : metric
                ? analysisFromBE
                    ? 'duplicate'
                    : 'normal'
                : 'noMetric';
        var possibility = this.getPossibleGridObjects(possibilitySelector, analysis, analysisFromBE);
        return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, possibility, { selection: selection });
    };
    AdminImportViewComponent.prototype.getPossibleGridObjects = function (selector, analysis, analysisFromBE) {
        switch (selector) {
            case 'noMetric':
                return {
                    logColor: 'red',
                    log: "Metric doesn't exists.",
                    errorMsg: analysis.metricName + ": Metric does not exists.",
                    duplicateAnalysisInd: false,
                    errorInd: true,
                    noMetricInd: true,
                    analysis: analysis
                };
            case 'legacy':
                return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({ analysis: analysis }, LEGACY_GRID_OBJECT_PROPS);
            case 'duplicate':
                var modifiedAnalysis = this.getModifiedAnalysis(analysis, analysisFromBE);
                return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, DUPLICATE_GRID_OBJECT_PROPS, { analysis: modifiedAnalysis });
            case 'normal':
                return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, NORMAL_GRID_OBJECT_PROPS, { analysis: analysis });
        }
    };
    AdminImportViewComponent.prototype.getModifiedAnalysis = function (analysis, analysisFromBE) {
        var fields;
        if (Object(_analyze_types__WEBPACK_IMPORTED_MODULE_28__["isDSLAnalysis"])(analysisFromBE)) {
            var id = analysisFromBE.id, createdTime = analysisFromBE.createdTime, schedule = analysisFromBE.schedule;
            fields = { id: id, createdTime: createdTime, schedule: schedule };
        }
        else {
            var isScheduled = analysisFromBE.isScheduled, scheduled = analysisFromBE.scheduled, createdTimestamp = analysisFromBE.createdTimestamp, esRepository = analysisFromBE.esRepository, id = analysisFromBE.id, repository = analysisFromBE.repository;
            fields = {
                isScheduled: isScheduled,
                scheduled: scheduled,
                createdTimestamp: createdTimestamp,
                esRepository: esRepository,
                id: id,
                repository: repository
            };
        }
        var _a = this._jwtService.getTokenObj().ticket, userFullName = _a.userFullName, userId = _a.userId;
        return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, analysis, fields, { userFullName: userFullName,
            userId: userId });
    };
    AdminImportViewComponent.prototype.import = function () {
        var _this = this;
        var importPromises = lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6__('selection'), lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__(function (gridObj) {
            var duplicateAnalysisInd = gridObj.duplicateAnalysisInd, analysis = gridObj.analysis;
            if (duplicateAnalysisInd) {
                return _this.importExistingAnalysis(analysis);
            }
            else {
                return _this.importNewAnalysis(analysis).then(function (addedAnalysis) {
                    gridObj.analysis.id = addedAnalysis.id;
                    return addedAnalysis;
                });
            }
        }))(this.analyses);
        Object(_common_utils_executeAllPromises__WEBPACK_IMPORTED_MODULE_24__["executeAllPromises"])(importPromises).then(function (results) {
            var selectedAnalyses = lodash_filter__WEBPACK_IMPORTED_MODULE_11__(_this.analyses, 'selection');
            var updatedAnalysesMap = lodash_reduce__WEBPACK_IMPORTED_MODULE_9__(results, function (acc, result, index) {
                if (result.result) {
                    var analysis = result.result;
                    acc[analysis.id] = { analysis: analysis };
                }
                else {
                    var error = result.error;
                    var gridObj = selectedAnalyses[index];
                    acc[gridObj.analysis.id] = { error: error };
                }
                return acc;
            }, {});
            var hasErrors = false;
            var someImportsWereSuccesful = false;
            // update the logs
            lodash_forEach__WEBPACK_IMPORTED_MODULE_10__(_this.analyses, function (gridObj) {
                if (gridObj.selection) {
                    var id = gridObj.analysis.id;
                    var container = updatedAnalysesMap[id];
                    // if analysis was updated
                    if (container && container.analysis) {
                        gridObj.logColor = 'green';
                        gridObj.log = 'Successfully Imported';
                        gridObj.errorInd = false;
                        gridObj.duplicateAnalysisInd = true;
                        gridObj.selection = false;
                        someImportsWereSuccesful = true;
                    }
                    else {
                        hasErrors = true;
                        var error = container.error;
                        gridObj.logColor = 'red';
                        gridObj.log = 'Error While Importing';
                        gridObj.errorMsg = lodash_get__WEBPACK_IMPORTED_MODULE_14__(error, 'error.error.message');
                        gridObj.errorInd = true;
                    }
                }
            });
            _this.userCanExportErrors = hasErrors;
            if (someImportsWereSuccesful) {
                _this.analyses = _this.analyses.slice();
            }
            _this.atLeast1AnalysisIsSelected = false;
            _this.store.dispatch(new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_3__["RefreshAllCategories"]());
        }, function () {
            _this.store.dispatch(new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_3__["RefreshAllCategories"]());
        });
    };
    AdminImportViewComponent.prototype.exportErrors = function () {
        var _this = this;
        var logMessages = lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_6__('errorInd'), lodash_fp_map__WEBPACK_IMPORTED_MODULE_7__(function (gridObj) {
            var analysis = gridObj.analysis, errorMsg = gridObj.errorMsg;
            var metricName = analysis.metricName, name = analysis.name, type = analysis.type;
            return {
                analysisName: name,
                analysisType: type,
                metricName: metricName,
                errorLog: errorMsg
            };
        }))(this.analyses);
        if (!lodash_isEmpty__WEBPACK_IMPORTED_MODULE_15__(logMessages)) {
            json_2_csv__WEBPACK_IMPORTED_MODULE_17__["json2csv"](logMessages, function (err, csv) {
                if (err) {
                    throw err;
                }
                var logFileName = _this.getLogFileName();
                var newData = new Blob([csv], { type: 'text/csv;charset=utf-8' });
                file_saver__WEBPACK_IMPORTED_MODULE_18__["saveAs"](newData, logFileName);
            });
        }
    };
    AdminImportViewComponent.prototype.getLogFileName = function () {
        var formatedDate = moment__WEBPACK_IMPORTED_MODULE_19__().format('YYYYMMDDHHmmss');
        return "log" + formatedDate + ".csv";
    };
    AdminImportViewComponent.prototype.importNewAnalysis = function (analysis) {
        var _this = this;
        var semanticId = analysis.semanticId, type = analysis.type;
        return new Promise(function (resolve, reject) {
            _this._importService
                .createAnalysis(semanticId, type)
                .then(function (initializedAnalysis) {
                var fields;
                if (Object(_analyze_types__WEBPACK_IMPORTED_MODULE_28__["isDSLAnalysis"])(initializedAnalysis)) {
                    fields = lodash_pick__WEBPACK_IMPORTED_MODULE_13__(initializedAnalysis, [
                        'id',
                        'createdBy',
                        'createdTime',
                        'schedule'
                    ]);
                }
                else {
                    fields = lodash_pick__WEBPACK_IMPORTED_MODULE_13__(initializedAnalysis, [
                        'isScheduled',
                        'scheduled',
                        'createdTimestamp',
                        'id',
                        'userFullName',
                        'userId',
                        'esRepository',
                        'repository'
                    ]);
                }
                _this.importExistingAnalysis(tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, analysis, fields)).then(function (updatedAnalysis) { return resolve(updatedAnalysis); }, function (err) { return reject(err); });
            });
        });
    };
    AdminImportViewComponent.prototype.canImport = function () {
        var selectedAnalyses = lodash_filter__WEBPACK_IMPORTED_MODULE_11__(this.analyses, 'selection') || [];
        return (selectedAnalyses.length &&
            selectedAnalyses.every(function (_a) {
                var analysis = _a.analysis;
                return Boolean(Object(_analyze_types__WEBPACK_IMPORTED_MODULE_28__["isDSLAnalysis"])(analysis) ? analysis.category : analysis.categoryId);
            }));
    };
    AdminImportViewComponent.prototype.importExistingAnalysis = function (analysis) {
        return this._importService.updateAnalysis(analysis);
    };
    AdminImportViewComponent.prototype.onAnalysesValiditychange = function (atLeast1AnalysisIsSelected) {
        this.atLeast1AnalysisIsSelected = atLeast1AnalysisIsSelected;
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_2__["Select"])(function (state) { return state.admin.importPage.categories.analyze; }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_4__["Observable"])
    ], AdminImportViewComponent.prototype, "categories$", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_2__["Select"])(function (state) { return state.admin.importPage.metrics; }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_4__["Observable"])
    ], AdminImportViewComponent.prototype, "metricMap$", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminImportViewComponent.prototype, "columns", void 0);
    AdminImportViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-import-view',
            template: __webpack_require__(/*! ./admin-import-view.component.html */ "./src/app/modules/admin/import/admin-import-view.component.html"),
            styles: [__webpack_require__(/*! ./admin-import-view.component.scss */ "./src/app/modules/admin/import/admin-import-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_import_service__WEBPACK_IMPORTED_MODULE_21__["ImportService"],
            _export_export_service__WEBPACK_IMPORTED_MODULE_27__["ExportService"],
            _category_category_service__WEBPACK_IMPORTED_MODULE_22__["CategoryService"],
            _common_components_sidenav__WEBPACK_IMPORTED_MODULE_23__["SidenavMenuService"],
            _common_services__WEBPACK_IMPORTED_MODULE_20__["JwtService"],
            _ngxs_store__WEBPACK_IMPORTED_MODULE_2__["Store"]])
    ], AdminImportViewComponent);
    return AdminImportViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/import/category-select/category-select.component.html":
/*!*************************************************************************************!*\
  !*** ./src/app/modules/admin/import/category-select/category-select.component.html ***!
  \*************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-form-field\n  class=\"select-form-field category-selector\"\n  appearance=\"outline\"\n>\n  <mat-label i18n>Category</mat-label>\n  <mat-select\n    class=\"form-field\"\n    [value]=\"value\"\n    (selectionChange)=\"onCategoryChange($event.value)\"\n  >\n    <mat-optgroup\n      *ngFor=\"let category of categories\"\n      [label]=\"category.categoryName\"\n    >\n      <mat-option\n        *ngFor=\"let subCategory of category.subCategories\"\n        [value]=\"subCategory.subCategoryId.toString()\"\n      >\n        {{ subCategory.subCategoryName }}\n      </mat-option>\n    </mat-optgroup>\n  </mat-select>\n</mat-form-field>\n"

/***/ }),

/***/ "./src/app/modules/admin/import/category-select/category-select.component.scss":
/*!*************************************************************************************!*\
  !*** ./src/app/modules/admin/import/category-select/category-select.component.scss ***!
  \*************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".mat-form-field {\n  max-width: 100%;\n  width: auto;\n  margin: 0;\n  font-size: 14px; }\n  .mat-form-field .mat-form-field-infix {\n    width: unset; }\n  .category-selector {\n  max-width: 100%; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2ltcG9ydC9jYXRlZ29yeS1zZWxlY3QvY2F0ZWdvcnktc2VsZWN0LmNvbXBvbmVudC5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0UsZUFBZTtFQUNmLFdBQVc7RUFDWCxTQUFTO0VBQ1QsZUFBZSxFQUFBO0VBSmpCO0lBT0ksWUFBWSxFQUFBO0VBSWhCO0VBQ0UsZUFBZSxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hZG1pbi9pbXBvcnQvY2F0ZWdvcnktc2VsZWN0L2NhdGVnb3J5LXNlbGVjdC5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIi5tYXQtZm9ybS1maWVsZCB7XG4gIG1heC13aWR0aDogMTAwJTtcbiAgd2lkdGg6IGF1dG87XG4gIG1hcmdpbjogMDtcbiAgZm9udC1zaXplOiAxNHB4O1xuXG4gIC5tYXQtZm9ybS1maWVsZC1pbmZpeCB7XG4gICAgd2lkdGg6IHVuc2V0O1xuICB9XG59XG5cbi5jYXRlZ29yeS1zZWxlY3RvciB7XG4gIG1heC13aWR0aDogMTAwJTtcbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/import/category-select/category-select.component.ts":
/*!***********************************************************************************!*\
  !*** ./src/app/modules/admin/import/category-select/category-select.component.ts ***!
  \***********************************************************************************/
/*! exports provided: AdminImportCategorySelectComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminImportCategorySelectComponent", function() { return AdminImportCategorySelectComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");


var AdminImportCategorySelectComponent = /** @class */ (function () {
    function AdminImportCategorySelectComponent() {
        this.change = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    AdminImportCategorySelectComponent.prototype.ngOnInit = function () { };
    AdminImportCategorySelectComponent.prototype.onCategoryChange = function (categoryId) {
        this.change.emit(categoryId);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminImportCategorySelectComponent.prototype, "categories", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], AdminImportCategorySelectComponent.prototype, "value", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminImportCategorySelectComponent.prototype, "change", void 0);
    AdminImportCategorySelectComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-import-category-select',
            template: __webpack_require__(/*! ./category-select.component.html */ "./src/app/modules/admin/import/category-select/category-select.component.html"),
            styles: [__webpack_require__(/*! ./category-select.component.scss */ "./src/app/modules/admin/import/category-select/category-select.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [])
    ], AdminImportCategorySelectComponent);
    return AdminImportCategorySelectComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/import/file-list/admin-import-file-list.component.html":
/*!**************************************************************************************!*\
  !*** ./src/app/modules/admin/import/file-list/admin-import-file-list.component.html ***!
  \**************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<dx-data-grid [customizeColumns]=\"config.customizeColumns\"\n              [columnAutoWidth]=\"config.columnAutoWidth\"\n              [columnMinWidth]=\"config.columnMinWidth\"\n              [columnResizingMode]=\"config.columnResizingMode\"\n              [allowColumnReordering]=\"config.allowColumnReordering\"\n              [allowColumnResizing]=\"config.allowColumnResizing\"\n              [showColumnHeaders]=\"config.showColumnHeaders\"\n              [showColumnLines]=\"config.showColumnLines\"\n              [showRowLines]=\"config.showRowLines\"\n              [showBorders]=\"config.showBorders\"\n              [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n              [hoverStateEnabled]=\"config.hoverStateEnabled\"\n              [wordWrapEnabled]=\"config.wordWrapEnabled\"\n              [scrolling]=\"config.scrolling\"\n              [sorting]=\"config.sorting\"\n              [dataSource]=\"files\"\n              [columns]=\"config.columns\"\n              [pager]=\"config.pager\"\n              [paging]=\"config.paging\"\n              [width]=\"config.width\"\n              [height]=\"config.height\"\n>\n<div *dxTemplate=\"let cell of 'actionCellTemplate'\">\n  <button mat-icon-button\n          (click)=\"onRemove(cell.data)\"\n          class=\"list-action__button\"\n          i18n-matTooltip=\"Remove file\"\n          matTooltip=\"Remove file\"\n    >\n      <mat-icon fontIcon=\"icon-trash\"></mat-icon>\n    </button>\n</div>\n</dx-data-grid>\n"

/***/ }),

/***/ "./src/app/modules/admin/import/file-list/admin-import-file-list.component.scss":
/*!**************************************************************************************!*\
  !*** ./src/app/modules/admin/import/file-list/admin-import-file-list.component.scss ***!
  \**************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".list-action__button {\n  display: inline-flex;\n  min-width: 24px;\n  min-height: 24px;\n  line-height: 24px;\n  height: 24px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2ltcG9ydC9maWxlLWxpc3QvYWRtaW4taW1wb3J0LWZpbGUtbGlzdC5jb21wb25lbnQuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtFQUNFLG9CQUFvQjtFQUNwQixlQUFlO0VBQ2YsZ0JBQWdCO0VBQ2hCLGlCQUFpQjtFQUNqQixZQUFZLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FkbWluL2ltcG9ydC9maWxlLWxpc3QvYWRtaW4taW1wb3J0LWZpbGUtbGlzdC5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIi5saXN0LWFjdGlvbl9fYnV0dG9uIHtcbiAgZGlzcGxheTogaW5saW5lLWZsZXg7XG4gIG1pbi13aWR0aDogMjRweDtcbiAgbWluLWhlaWdodDogMjRweDtcbiAgbGluZS1oZWlnaHQ6IDI0cHg7XG4gIGhlaWdodDogMjRweDtcbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/import/file-list/admin-import-file-list.component.ts":
/*!************************************************************************************!*\
  !*** ./src/app/modules/admin/import/file-list/admin-import-file-list.component.ts ***!
  \************************************************************************************/
/*! exports provided: AdminImportFileListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminImportFileListComponent", function() { return AdminImportFileListComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");



var AdminImportFileListComponent = /** @class */ (function () {
    function AdminImportFileListComponent(_DxDataGridService) {
        this._DxDataGridService = _DxDataGridService;
        this.remove = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    AdminImportFileListComponent.prototype.ngOnInit = function () {
        this.config = this.getConfig();
    };
    AdminImportFileListComponent.prototype.onRemove = function (row) {
        this.remove.emit(row.name);
    };
    AdminImportFileListComponent.prototype.getConfig = function () {
        var columns = [
            {
                caption: 'File Name',
                dataField: 'name',
                allowSorting: false,
                alignment: 'center',
                width: '50%'
            },
            {
                caption: 'Analysis Count',
                dataField: 'count',
                allowSorting: true,
                alignment: 'left',
                width: '25%'
            },
            {
                width: '25%',
                alignment: 'center',
                caption: '',
                cellTemplate: 'actionCellTemplate'
            }
        ];
        return this._DxDataGridService.mergeWithDefaultConfig({
            columns: columns,
            width: '100%',
            height: '100%',
            paging: {
                pageSize: 10
            },
            pager: {
                showPageSizeSelector: true,
                showInfo: true
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminImportFileListComponent.prototype, "files", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], AdminImportFileListComponent.prototype, "remove", void 0);
    AdminImportFileListComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-import-file-list',
            template: __webpack_require__(/*! ./admin-import-file-list.component.html */ "./src/app/modules/admin/import/file-list/admin-import-file-list.component.html"),
            styles: [__webpack_require__(/*! ./admin-import-file-list.component.scss */ "./src/app/modules/admin/import/file-list/admin-import-file-list.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__["DxDataGridService"]])
    ], AdminImportFileListComponent);
    return AdminImportFileListComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/import/file-list/index.ts":
/*!*********************************************************!*\
  !*** ./src/app/modules/admin/import/file-list/index.ts ***!
  \*********************************************************/
/*! exports provided: AdminImportFileListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _admin_import_file_list_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./admin-import-file-list.component */ "./src/app/modules/admin/import/file-list/admin-import-file-list.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminImportFileListComponent", function() { return _admin_import_file_list_component__WEBPACK_IMPORTED_MODULE_0__["AdminImportFileListComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/import/import.service.ts":
/*!********************************************************!*\
  !*** ./src/app/modules/admin/import/import.service.ts ***!
  \********************************************************/
/*! exports provided: ImportService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ImportService", function() { return ImportService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _main_view_admin_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../main-view/admin.service */ "./src/app/modules/admin/main-view/admin.service.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _analyze_services_analyze_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../analyze/services/analyze.service */ "./src/app/modules/analyze/services/analyze.service.ts");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_values__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/values */ "./node_modules/lodash/values.js");
/* harmony import */ var lodash_values__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_values__WEBPACK_IMPORTED_MODULE_6__);







var ImportService = /** @class */ (function () {
    function ImportService(_adminService, _jwtService, _analyzeService) {
        this._adminService = _adminService;
        this._jwtService = _jwtService;
        this._analyzeService = _analyzeService;
    }
    /**
     * Transforms a list of analyses into a map for easy lookup
     * by analysis name, metric name and analysis type.
     * Helps in quickly finding possible duplicates while importing.
     *
     * @param {Analysis[]} analyses
     * @returns {{ [reference: string]: Analysis }}
     * @memberof ImportService
     */
    ImportService.prototype.createReferenceMapFor = function (analyses, metrics) {
        var metricArray = lodash_values__WEBPACK_IMPORTED_MODULE_6__(metrics);
        return analyses.reduce(function (acc, analysis) {
            var metric = lodash_find__WEBPACK_IMPORTED_MODULE_5__(metricArray, function (m) { return m.id === analysis.semanticId; });
            acc[analysis.name + ":" + metric.metricName + ":" + analysis.type] = analysis;
            return acc;
        }, {});
    };
    ImportService.prototype.createAnalysis = function (semanticId, type) {
        return this._analyzeService.createAnalysis(semanticId, type);
    };
    ImportService.prototype.updateAnalysis = function (analysis) {
        return this._analyzeService.updateAnalysis(analysis);
    };
    ImportService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_main_view_admin_service__WEBPACK_IMPORTED_MODULE_2__["AdminService"],
            _common_services__WEBPACK_IMPORTED_MODULE_3__["JwtService"],
            _analyze_services_analyze_service__WEBPACK_IMPORTED_MODULE_4__["AnalyzeService"]])
    ], ImportService);
    return ImportService;
}());



/***/ }),

/***/ "./src/app/modules/admin/import/index.ts":
/*!***********************************************!*\
  !*** ./src/app/modules/admin/import/index.ts ***!
  \***********************************************/
/*! exports provided: AdminImportViewComponent, AdminImportFileListComponent, AdminImportListComponent, AdminImportCategorySelectComponent, AdminImportPageState */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _admin_import_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./admin-import-view.component */ "./src/app/modules/admin/import/admin-import-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminImportViewComponent", function() { return _admin_import_view_component__WEBPACK_IMPORTED_MODULE_0__["AdminImportViewComponent"]; });

/* harmony import */ var _file_list__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./file-list */ "./src/app/modules/admin/import/file-list/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminImportFileListComponent", function() { return _file_list__WEBPACK_IMPORTED_MODULE_1__["AdminImportFileListComponent"]; });

/* harmony import */ var _list__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./list */ "./src/app/modules/admin/import/list/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminImportListComponent", function() { return _list__WEBPACK_IMPORTED_MODULE_2__["AdminImportListComponent"]; });

/* harmony import */ var _category_select_category_select_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./category-select/category-select.component */ "./src/app/modules/admin/import/category-select/category-select.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminImportCategorySelectComponent", function() { return _category_select_category_select_component__WEBPACK_IMPORTED_MODULE_3__["AdminImportCategorySelectComponent"]; });

/* harmony import */ var _state_import_page_state__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./state/import-page.state */ "./src/app/modules/admin/import/state/import-page.state.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminImportPageState", function() { return _state_import_page_state__WEBPACK_IMPORTED_MODULE_4__["AdminImportPageState"]; });








/***/ }),

/***/ "./src/app/modules/admin/import/list/admin-import-list.component.html":
/*!****************************************************************************!*\
  !*** ./src/app/modules/admin/import/list/admin-import-list.component.html ***!
  \****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<dx-data-grid\n  [customizeColumns]=\"config.customizeColumns\"\n  [columnAutoWidth]=\"config.columnAutoWidth\"\n  [columnMinWidth]=\"config.columnMinWidth\"\n  [columnResizingMode]=\"config.columnResizingMode\"\n  [allowColumnReordering]=\"config.allowColumnReordering\"\n  [allowColumnResizing]=\"config.allowColumnResizing\"\n  [showColumnHeaders]=\"config.showColumnHeaders\"\n  [showColumnLines]=\"config.showColumnLines\"\n  [showRowLines]=\"config.showRowLines\"\n  [showBorders]=\"config.showBorders\"\n  [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n  [hoverStateEnabled]=\"config.hoverStateEnabled\"\n  [wordWrapEnabled]=\"config.wordWrapEnabled\"\n  [scrolling]=\"config.scrolling\"\n  [sorting]=\"config.sorting\"\n  [dataSource]=\"analyses\"\n  [columns]=\"config.columns\"\n  [pager]=\"{\n    showPageSizeSelector: !isEmpty(analyses),\n    showInfo: true,\n    showNavigationButtons: true,\n    allowedPageSizes: [5, 10, 20]\n  }\"\n  [paging]=\"config.paging\"\n  [width]=\"config.width\"\n  [height]=\"config.height\"\n>\n  <div *dxTemplate=\"let cell of 'selectionHeaderCellTemplate'\">\n    <mat-checkbox (change)=\"selectAll()\" [checked]=\"areAllSelected\" i18n>\n      All\n    </mat-checkbox>\n  </div>\n\n  <div\n    *dxTemplate=\"let cell of 'selectionCellTemplate'\"\n    fxLayout=\"row\"\n    fxLayoutAlign=\"space-between center\"\n  >\n    <mat-checkbox\n      (change)=\"onChecked(cell.data)\"\n      [checked]=\"cell.data.selection\"\n      [disabled]=\"\n        cell.data.noMetricInd ||\n        cell.data.legacyInd ||\n        (cell.data.duplicateAnalysisInd && !cell.data.selection)\n      \"\n    >\n    </mat-checkbox>\n    <a\n      *ngIf=\"cell.data.duplicateAnalysisInd\"\n      (click)=\"overWrite(cell.data)\"\n      style=\"font-size: x-small;\"\n      i18n\n    >\n      [OVERWRITE]\n    </a>\n  </div>\n\n  <div\n    *dxTemplate=\"let cell of 'logCellTemplate'\"\n    [style.color]=\"cell.data.logColor\"\n    [style.whiteSpace]=\"'pre-line'\"\n  >\n    <span\n      *ngIf=\"cell.data.errorInd\"\n      class=\"log-error\"\n      style=\"cursor: pointer\"\n      (click)=\"displayError(cell.data.errorMsg)\"\n    >\n      {{ cell.value }}\n    </span>\n    <span *ngIf=\"!cell.data.errorInd\" class=\"log-noerror\">\n      {{ cell.value }}\n    </span>\n  </div>\n\n  <div *dxTemplate=\"let cell of 'categoryCellTemplate'\">\n    <admin-import-category-select\n      [categories]=\"categories\"\n      [value]=\"getCategoryId(cell.data.analysis).toString()\"\n      (change)=\"onSelectCategory($event, cell.data)\"\n    ></admin-import-category-select>\n  </div>\n</dx-data-grid>\n"

/***/ }),

/***/ "./src/app/modules/admin/import/list/admin-import-list.component.ts":
/*!**************************************************************************!*\
  !*** ./src/app/modules/admin/import/list/admin-import-list.component.ts ***!
  \**************************************************************************/
/*! exports provided: AdminImportListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminImportListComponent", function() { return AdminImportListComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_some__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/some */ "./node_modules/lodash/some.js");
/* harmony import */ var lodash_some__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_some__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_every__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/every */ "./node_modules/lodash/every.js");
/* harmony import */ var lodash_every__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_every__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");







var AdminImportListComponent = /** @class */ (function () {
    function AdminImportListComponent(_DxDataGridService) {
        this._DxDataGridService = _DxDataGridService;
        this.validityChange = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.categorySelected = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.areAllSelected = false;
        this.isEmpty = lodash_isEmpty__WEBPACK_IMPORTED_MODULE_5__;
    }
    AdminImportListComponent.prototype.ngOnInit = function () {
        this.config = this.getConfig();
    };
    AdminImportListComponent.prototype.ngOnChanges = function () {
        this.areAllSelected = false;
    };
    AdminImportListComponent.prototype.overWrite = function (row) {
        row.selection = true;
        this.validityChange.emit(true);
    };
    AdminImportListComponent.prototype.getCategoryId = function (analysis) {
        return analysis.hasOwnProperty('categoryId')
            ? analysis.categoryId
            : analysis.category;
    };
    AdminImportListComponent.prototype.onChecked = function (row) {
        row.selection = !row.selection;
        var isValid = lodash_some__WEBPACK_IMPORTED_MODULE_3__(this.analyses, 
        // tslint:disable-next-line:no-shadowed-variable
        function (row) { return !row.noMetricInd && !row.legacyInd && row.selection; });
        if (row.selection) {
            this.areAllSelected = lodash_every__WEBPACK_IMPORTED_MODULE_4__(this.analyses, 
            // tslint:disable-next-line:no-shadowed-variable
            function (row) { return !row.noMetricInd && !row.legacyInd && row.selection; });
        }
        else {
            this.areAllSelected = false;
        }
        this.validityChange.emit(isValid);
    };
    AdminImportListComponent.prototype.selectAll = function () {
        var _this = this;
        this.areAllSelected = !this.areAllSelected;
        lodash_forEach__WEBPACK_IMPORTED_MODULE_2__(this.analyses, function (row) {
            if (!row.noMetricInd && !row.legacyInd) {
                row.selection = _this.areAllSelected;
            }
        });
        this.validityChange.emit(this.areAllSelected);
    };
    AdminImportListComponent.prototype.onSelectCategory = function (event, _a) {
        var analysis = _a.analysis;
        this.categorySelected.emit({
            categoryId: event,
            analysisId: analysis.id
        });
    };
    AdminImportListComponent.prototype.getConfig = function () {
        var columns = [
            {
                caption: 'All',
                dataField: 'selection',
                allowSorting: false,
                alignment: 'left',
                width: '10%',
                headerCellTemplate: 'selectionHeaderCellTemplate',
                cellTemplate: 'selectionCellTemplate'
            },
            {
                caption: 'Analysis Name',
                dataField: 'analysis.name',
                allowSorting: true,
                alignment: 'left',
                width: '25%'
            },
            {
                caption: 'Analysis Type',
                dataField: 'analysis.type',
                allowSorting: true,
                alignment: 'left',
                width: '10%'
            },
            {
                caption: 'Metric Name',
                dataField: 'analysis.metricName',
                allowSorting: true,
                alignment: 'left',
                width: '20%'
            },
            {
                caption: 'Category',
                allowSorting: false,
                cellTemplate: 'categoryCellTemplate',
                alignment: 'left',
                width: '15%'
            },
            {
                caption: 'Logs',
                dataField: 'log',
                allowSorting: false,
                alignment: 'left',
                width: '20%',
                cellTemplate: 'logCellTemplate'
            }
        ];
        return this._DxDataGridService.mergeWithDefaultConfig({
            columns: columns,
            scrolling: {
                mode: 'standard'
            },
            paging: {
                enabled: true,
                pageSize: 10,
                pageIndex: 0
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminImportListComponent.prototype, "analyses", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminImportListComponent.prototype, "categories", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminImportListComponent.prototype, "validityChange", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminImportListComponent.prototype, "categorySelected", void 0);
    AdminImportListComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-import-list',
            template: __webpack_require__(/*! ./admin-import-list.component.html */ "./src/app/modules/admin/import/list/admin-import-list.component.html")
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_6__["DxDataGridService"]])
    ], AdminImportListComponent);
    return AdminImportListComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/import/list/index.ts":
/*!****************************************************!*\
  !*** ./src/app/modules/admin/import/list/index.ts ***!
  \****************************************************/
/*! exports provided: AdminImportListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _admin_import_list_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./admin-import-list.component */ "./src/app/modules/admin/import/list/admin-import-list.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminImportListComponent", function() { return _admin_import_list_component__WEBPACK_IMPORTED_MODULE_0__["AdminImportListComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/import/state/import-page.state.ts":
/*!*****************************************************************!*\
  !*** ./src/app/modules/admin/import/state/import-page.state.ts ***!
  \*****************************************************************/
/*! exports provided: AdminImportPageState */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminImportPageState", function() { return AdminImportPageState; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _ngxs_store__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @ngxs/store */ "./node_modules/@ngxs/store/fesm5/ngxs-store.js");
/* harmony import */ var _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../actions/import-page.actions */ "./src/app/modules/admin/import/actions/import-page.actions.ts");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/cloneDeep */ "./node_modules/lodash/cloneDeep.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_keys__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/keys */ "./node_modules/lodash/keys.js");
/* harmony import */ var lodash_keys__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_keys__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var _category__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../category */ "./src/app/modules/admin/category/index.ts");
/* harmony import */ var _export_export_service__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../export/export.service */ "./src/app/modules/admin/export/export.service.ts");
/* harmony import */ var _import_service__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../import.service */ "./src/app/modules/admin/import/import.service.ts");









var defaultImportPageState = {
    analysisGlobalCategory: null,
    importFiles: [],
    metrics: {},
    referenceAnalyses: {},
    categories: {
        analyze: [],
        observe: []
    },
    importData: {
        analyses: []
    }
};
var AdminImportPageState = /** @class */ (function () {
    function AdminImportPageState(categoryService, exportService, importService) {
        this.categoryService = categoryService;
        this.exportService = exportService;
        this.importService = importService;
    }
    AdminImportPageState.prototype.loadAllAnalyzeCategories = function (_a) {
        var patchState = _a.patchState, getState = _a.getState;
        var categories = getState().categories;
        return this.categoryService.getList$().pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (allCategories) {
            return allCategories.filter(function (category) { return category.moduleName === 'ANALYZE'; });
        }), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (analyzeCategories) {
            return analyzeCategories.map(function (category) {
                category.analyses = [];
                return category;
            });
        }), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["tap"])(function (analyzeCategories) {
            return patchState({
                categories: tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, categories, { analyze: analyzeCategories })
            });
        }));
    };
    AdminImportPageState.prototype.loadMetrics = function (_a) {
        var patchState = _a.patchState;
        return this.exportService.getMetricList$().pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["tap"])(function (metrics) {
            var metricMap = metrics.reduce(function (acc, metric) {
                acc[metric.metricName] = metric;
                return acc;
            }, {});
            patchState({ metrics: metricMap });
        }));
    };
    AdminImportPageState.prototype.selectAnalysisGlobalCategory = function (_a, _b) {
        var patchState = _a.patchState, dispatch = _a.dispatch;
        var category = _b.category;
        patchState({
            analysisGlobalCategory: category
        });
        return dispatch(new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["LoadAnalysesForCategory"](category));
        // TODO: Update overwrite status for each analysis
    };
    AdminImportPageState.prototype.loadAnalysesForCategory = function (_a, _b) {
        var _this = this;
        var getState = _a.getState, patchState = _a.patchState;
        var category = _b.category;
        var referenceAnalyses = getState().referenceAnalyses;
        if (referenceAnalyses[category.toString()]) {
            return;
        }
        return this.exportService.getAnalysesByCategoryId(category).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["tap"])(function (analyses) {
            var _a;
            var metrics = getState().metrics;
            var referenceMap = _this.importService.createReferenceMapFor(analyses, metrics);
            patchState({
                referenceAnalyses: tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, referenceAnalyses, (_a = {}, _a[category] = referenceMap, _a))
            });
        }));
    };
    AdminImportPageState.prototype.refreshAllCategories = function (_a) {
        var getState = _a.getState, patchState = _a.patchState, dispatch = _a.dispatch;
        var categoryIds = lodash_keys__WEBPACK_IMPORTED_MODULE_5__(getState().referenceAnalyses);
        patchState({
            referenceAnalyses: {}
        });
        return dispatch(categoryIds.map(function (id) { return new _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["LoadAnalysesForCategory"](id); }));
    };
    AdminImportPageState.prototype.resetImportState = function (_a) {
        var patchState = _a.patchState;
        return patchState(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_4__(defaultImportPageState));
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["LoadAllAnalyzeCategories"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], AdminImportPageState.prototype, "loadAllAnalyzeCategories", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["LoadMetrics"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], AdminImportPageState.prototype, "loadMetrics", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["SelectAnalysisGlobalCategory"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["SelectAnalysisGlobalCategory"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], AdminImportPageState.prototype, "selectAnalysisGlobalCategory", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["LoadAnalysesForCategory"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object, _actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["LoadAnalysesForCategory"]]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], AdminImportPageState.prototype, "loadAnalysesForCategory", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["RefreshAllCategories"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], AdminImportPageState.prototype, "refreshAllCategories", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["Action"])(_actions_import_page_actions__WEBPACK_IMPORTED_MODULE_2__["ClearImport"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:returntype", void 0)
    ], AdminImportPageState.prototype, "resetImportState", null);
    AdminImportPageState = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["State"])({
            name: 'importPage',
            defaults: lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_4__(defaultImportPageState)
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_category__WEBPACK_IMPORTED_MODULE_6__["CategoryService"],
            _export_export_service__WEBPACK_IMPORTED_MODULE_7__["ExportService"],
            _import_service__WEBPACK_IMPORTED_MODULE_8__["ImportService"]])
    ], AdminImportPageState);
    return AdminImportPageState;
}());



/***/ }),

/***/ "./src/app/modules/admin/list-view/admin-list-view.component.html":
/*!************************************************************************!*\
  !*** ./src/app/modules/admin/list-view/admin-list-view.component.html ***!
  \************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<dx-data-grid [customizeColumns]=\"config.customizeColumns\"\n              [columnAutoWidth]=\"config.columnAutoWidth\"\n              [columnMinWidth]=\"config.columnMinWidth\"\n              [columnResizingMode]=\"config.columnResizingMode\"\n              [allowColumnReordering]=\"config.allowColumnReordering\"\n              [allowColumnResizing]=\"config.allowColumnResizing\"\n              [showColumnHeaders]=\"config.showColumnHeaders\"\n              [showColumnLines]=\"config.showColumnLines\"\n              [showRowLines]=\"config.showRowLines\"\n              [showBorders]=\"config.showBorders\"\n              [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n              [hoverStateEnabled]=\"config.hoverStateEnabled\"\n              [wordWrapEnabled]=\"config.wordWrapEnabled\"\n              [scrolling]=\"config.scrolling\"\n              [sorting]=\"config.sorting\"\n              [dataSource]=\"data\"\n              [columns]=\"config.columns\"\n              [pager]=\"config.pager\"\n              [paging]=\"config.paging\"\n              [width]=\"config.width\"\n              [height]=\"config.height\"\n>\n\n  <div *dxTemplate=\"let cell of 'linkCellTemplate'\">\n    <a [innerHtml]=\"cell.text | highlight: searchTerm\"\n       (click)=\"rowClick.emit(cell.data)\"\n       [matTooltip]=\"getLinkTooltip()\"\n    >\n    </a>\n  </div>\n\n  <div *dxTemplate=\"let cell of 'highlightCellTemplate'\"\n       [innerHtml]=\"cell.text | highlight: highlightTerm\">\n  </div>\n\n  <div *dxTemplate=\"let cell of 'dateCellTemplate'\">\n    {{cell.text | date: 'dd/MMM/yy'}}\n  </div>\n\n  <div *dxTemplate=\"let cell of 'actionCellTemplate'\">\n    <div fxLayout=\"row\" fxLayoutAlign=\"center center\" class=\"list-action__container\">\n      <button mat-icon-button\n              (click)=\"editRow.emit(cell.data)\"\n              [matTooltip]=\"'Edit ' + (section | changeCase:'title')\">\n        <mat-icon fontIcon=\"icon-edit\"></mat-icon>\n      </button>\n      <button mat-icon-button\n              (click)=\"deleteRow.emit(cell.data)\"\n              [matTooltip]=\"'Delete ' + (section | changeCase:'title')\">\n        <mat-icon fontIcon=\"icon-trash\"></mat-icon>\n      </button>\n    </div>\n  </div>\n\n  <div *dxTemplate=\"let cell of 'groupAssignCellTemplate'\">\n    <div fxLayout=\"row\" fxLayoutAlign=\"center center\" class=\"list-action__container group-select\">\n        <mat-form-field class=\"select-form-field assignments-grid\" appearance=\"outline\" style=\"margin-bottom: 0px;\">\n            <mat-select [(ngModel)]=\"cell.data.groupName\" (selectionChange)=\"assignGrouptoUser($event, cell)\">\n              <mat-option *ngIf=\"securityGroups.length === 0\" value=\"-2\">No group available</mat-option>\n              <mat-option *ngFor=\"let group of securityGroups\" [value]=\"group.securityGroupName\">\n                  {{group.securityGroupName}}\n              </mat-option>\n              <mat-option *ngIf=\"validateClearOption(cell)\" style=\"color:red;\" [value]=\"-1\">Clear Group</mat-option>\n            </mat-select>\n          </mat-form-field>\n      <mat-icon *ngIf=\"groupAssignSuccess === 'checkmark' && cell.data.loginId === userGroupID\" fontIcon=\"icon-checkmark\"></mat-icon>\n      <mat-icon *ngIf=\"groupAssignSuccess === 'close' && cell.data.loginId === userGroupID\" fontIcon=\"icon-close\"></mat-icon>\n    </div>\n  </div>\n</dx-data-grid>\n"

/***/ }),

/***/ "./src/app/modules/admin/list-view/admin-list-view.component.scss":
/*!************************************************************************!*\
  !*** ./src/app/modules/admin/list-view/admin-list-view.component.scss ***!
  \************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".list-action__container {\n  max-height: 24px;\n  height: 24px; }\n  .list-action__container > button {\n    display: inline-flex;\n    min-width: 24px;\n    min-height: 24px;\n    line-height: 24px;\n    height: 24px; }\n  .list-action__container.group-select {\n  place-content: start !important;\n  height: 40px; }\n  .list-action__container.group-select mat-form-field {\n    width: 80% !important; }\n  .list-action__container.group-select .icon-close {\n    color: red;\n    padding-top: 6px;\n    padding-left: 12px; }\n  .list-action__container.group-select .icon-checkmark {\n    color: green;\n    font-size: 20px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL2xpc3Qtdmlldy9hZG1pbi1saXN0LXZpZXcuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxnQkFBZ0I7RUFDaEIsWUFBWSxFQUFBO0VBRmQ7SUFLSSxvQkFBb0I7SUFDcEIsZUFBZTtJQUNmLGdCQUFnQjtJQUNoQixpQkFBaUI7SUFDakIsWUFBWSxFQUFBO0VBSWhCO0VBQ0UsK0JBQStCO0VBQy9CLFlBQVksRUFBQTtFQUZkO0lBS0kscUJBQXFCLEVBQUE7RUFMekI7SUFTSSxVQUFVO0lBQ1YsZ0JBQWdCO0lBQ2hCLGtCQUFrQixFQUFBO0VBWHRCO0lBZUksWUFBWTtJQUNaLGVBQWUsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYWRtaW4vbGlzdC12aWV3L2FkbWluLWxpc3Qtdmlldy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIi5saXN0LWFjdGlvbl9fY29udGFpbmVyIHtcbiAgbWF4LWhlaWdodDogMjRweDtcbiAgaGVpZ2h0OiAyNHB4O1xuXG4gID4gYnV0dG9uIHtcbiAgICBkaXNwbGF5OiBpbmxpbmUtZmxleDtcbiAgICBtaW4td2lkdGg6IDI0cHg7XG4gICAgbWluLWhlaWdodDogMjRweDtcbiAgICBsaW5lLWhlaWdodDogMjRweDtcbiAgICBoZWlnaHQ6IDI0cHg7XG4gIH1cbn1cblxuLmxpc3QtYWN0aW9uX19jb250YWluZXIuZ3JvdXAtc2VsZWN0IHtcbiAgcGxhY2UtY29udGVudDogc3RhcnQgIWltcG9ydGFudDtcbiAgaGVpZ2h0OiA0MHB4O1xuXG4gIG1hdC1mb3JtLWZpZWxkIHtcbiAgICB3aWR0aDogODAlICFpbXBvcnRhbnQ7XG4gIH1cblxuICAuaWNvbi1jbG9zZSB7XG4gICAgY29sb3I6IHJlZDtcbiAgICBwYWRkaW5nLXRvcDogNnB4O1xuICAgIHBhZGRpbmctbGVmdDogMTJweDtcbiAgfVxuXG4gIC5pY29uLWNoZWNrbWFyayB7XG4gICAgY29sb3I6IGdyZWVuO1xuICAgIGZvbnQtc2l6ZTogMjBweDtcbiAgfVxufVxuIl19 */"

/***/ }),

/***/ "./src/app/modules/admin/list-view/admin-list-view.component.ts":
/*!**********************************************************************!*\
  !*** ./src/app/modules/admin/list-view/admin-list-view.component.ts ***!
  \**********************************************************************/
/*! exports provided: AdminListViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminListViewComponent", function() { return AdminListViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var _datasecurity_userassignment_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./../datasecurity/userassignment.service */ "./src/app/modules/admin/datasecurity/userassignment.service.ts");
/* harmony import */ var lodash_clone__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/clone */ "./node_modules/lodash/clone.js");
/* harmony import */ var lodash_clone__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_clone__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_5__);

__webpack_require__(/*! ./admin-list-view.component.scss */ "./src/app/modules/admin/list-view/admin-list-view.component.scss");





var AdminListViewComponent = /** @class */ (function () {
    function AdminListViewComponent(_dxDataGridService, _userAssignmentService) {
        this._dxDataGridService = _dxDataGridService;
        this._userAssignmentService = _userAssignmentService;
        this.editRow = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.deleteRow = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.rowClick = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.securityGroups = [];
    }
    AdminListViewComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.config = this.getConfig();
        this._userAssignmentService.getSecurityGroups().then(function (response) {
            _this.securityGroups = lodash_clone__WEBPACK_IMPORTED_MODULE_4__(response);
        });
    };
    AdminListViewComponent.prototype.getLinkTooltip = function () {
        return 'View Privileges';
    };
    AdminListViewComponent.prototype.assignGrouptoUser = function (groupName, cell) {
        var _this = this;
        if (groupName.value === '-2') {
            return false;
        }
        var request = {
            securityGroupName: groupName.value,
            userId: cell.data.userSysId
        };
        this._userAssignmentService
            .assignGroupToUser(request)
            .then(function (response) {
            _this.groupAssignSuccess = lodash_get__WEBPACK_IMPORTED_MODULE_5__(response, 'valid')
                ? 'checkmark'
                : 'close';
            _this.userGroupID = cell.data.loginId;
            if (groupName.value === -1) {
                cell.data.groupName = '';
            }
        })
            .catch(function (err) {
            _this.groupAssignSuccess = 'close';
        });
    };
    AdminListViewComponent.prototype.validateClearOption = function (cell) {
        return (cell.data.groupName !== null &&
            cell.data.groupName !== '-2' &&
            cell.data.groupName !== '');
    };
    AdminListViewComponent.prototype.getConfig = function () {
        return this._dxDataGridService.mergeWithDefaultConfig({
            columns: this.columns,
            width: '100%',
            height: '100%'
            // customizeColumns: columns => {
            //   const last = columns.length - 1;
            // }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminListViewComponent.prototype, "data", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AdminListViewComponent.prototype, "columns", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AdminListViewComponent.prototype, "section", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AdminListViewComponent.prototype, "highlightTerm", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminListViewComponent.prototype, "editRow", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminListViewComponent.prototype, "deleteRow", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AdminListViewComponent.prototype, "rowClick", void 0);
    AdminListViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-list-view',
            template: __webpack_require__(/*! ./admin-list-view.component.html */ "./src/app/modules/admin/list-view/admin-list-view.component.html"),
            styles: [__webpack_require__(/*! ./admin-list-view.component.scss */ "./src/app/modules/admin/list-view/admin-list-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_2__["DxDataGridService"],
            _datasecurity_userassignment_service__WEBPACK_IMPORTED_MODULE_3__["UserAssignmentService"]])
    ], AdminListViewComponent);
    return AdminListViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/list-view/index.ts":
/*!**************************************************!*\
  !*** ./src/app/modules/admin/list-view/index.ts ***!
  \**************************************************/
/*! exports provided: AdminListViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _admin_list_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./admin-list-view.component */ "./src/app/modules/admin/list-view/admin-list-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminListViewComponent", function() { return _admin_list_view_component__WEBPACK_IMPORTED_MODULE_0__["AdminListViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/main-view/admin-main-view.component.html":
/*!************************************************************************!*\
  !*** ./src/app/modules/admin/main-view/admin-main-view.component.html ***!
  \************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-toolbar fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n  <div fxLayout=\"row\" fxFlex=\"40\">\n    <div class=\"admin-title\" e2e=\"category-title\">{{section | changeCase:'title'}} Management</div>\n    <search-box placeholder=\"Search\"\n                [value]=\"filterObj.searchTerm\"\n                (searchTermChange)=\"applySearchFilter($event)\"\n                [delay]=\"1000\">\n    </search-box>\n  </div>\n\n  <div fxLayout=\"row\" class=\"customer-name\" fxLayoutAlign=\"start center\" fxFlex=\"40\">\n    Customer: {{ticket.custCode}}\n  </div>\n  <div fxLayout=\"row\" fxLayoutAlign=\"end center\" fxFlex=\"20\">\n    <button *ngIf=\"showAddButton\" (click)=\"createRow()\"\n            mat-raised-button\n            color=\"primary\">\n      + <span>{{section | uppercase}}</span>\n    </button>\n  </div>\n</mat-toolbar>\n<div *ngIf=\"data\" class=\"admin-list-container\">\n  <admin-list-view (editRow)=\"editRow($event)\"\n                   (deleteRow)=\"deleteRow($event)\"\n                   (rowClick)=\"onRowClick($event)\"\n                   [data]=\"filteredData\"\n                   [columns]=\"columns\"\n                   [section]=\"section\"\n                   [highlightTerm]=\"filterObj.searchTermValue\">\n  </admin-list-view>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/main-view/admin-main-view.component.scss":
/*!************************************************************************!*\
  !*** ./src/app/modules/admin/main-view/admin-main-view.component.scss ***!
  \************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  overflow-y: hidden; }\n\n.mat-toolbar {\n  background-color: transparent; }\n\n.mat-toolbar .admin-title {\n    color: #636363;\n    font-weight: bold;\n    margin-right: 10px; }\n\n.mat-toolbar .customer-name {\n    color: #636363; }\n\n.admin-list-container {\n  height: calc(100vh - 64px - 51px - 63px);\n  padding: 0 20px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL21haW4tdmlldy9hZG1pbi1tYWluLXZpZXcuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxrQkFBa0IsRUFBQTs7QUFHcEI7RUFDRSw2QkFBNkIsRUFBQTs7QUFEL0I7SUFJSSxjQ0NxQjtJREFyQixpQkFBaUI7SUFDakIsa0JBQWtCLEVBQUE7O0FBTnRCO0lBVUksY0NMcUIsRUFBQTs7QURTekI7RUFDRSx3Q0FBd0M7RUFDeEMsZUFBZSxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hZG1pbi9tYWluLXZpZXcvYWRtaW4tbWFpbi12aWV3LmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiQGltcG9ydCBcInNyYy90aGVtZXMvYmFzZS9jb2xvcnNcIjtcblxuOmhvc3Qge1xuICBvdmVyZmxvdy15OiBoaWRkZW47XG59XG5cbi5tYXQtdG9vbGJhciB7XG4gIGJhY2tncm91bmQtY29sb3I6IHRyYW5zcGFyZW50O1xuXG4gIC5hZG1pbi10aXRsZSB7XG4gICAgY29sb3I6ICRncmV5LXRleHQtY29sb3I7XG4gICAgZm9udC13ZWlnaHQ6IGJvbGQ7XG4gICAgbWFyZ2luLXJpZ2h0OiAxMHB4O1xuICB9XG5cbiAgLmN1c3RvbWVyLW5hbWUge1xuICAgIGNvbG9yOiAkZ3JleS10ZXh0LWNvbG9yO1xuICB9XG59XG5cbi5hZG1pbi1saXN0LWNvbnRhaW5lciB7XG4gIGhlaWdodDogY2FsYygxMDB2aCAtIDY0cHggLSA1MXB4IC0gNjNweCk7XG4gIHBhZGRpbmc6IDAgMjBweDtcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/main-view/admin-main-view.component.ts":
/*!**********************************************************************!*\
  !*** ./src/app/modules/admin/main-view/admin-main-view.component.ts ***!
  \**********************************************************************/
/*! exports provided: AdminMainViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminMainViewComponent", function() { return AdminMainViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/cloneDeep */ "./node_modules/lodash/cloneDeep.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _user__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../user */ "./src/app/modules/admin/user/index.ts");
/* harmony import */ var _role__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../role */ "./src/app/modules/admin/role/index.ts");
/* harmony import */ var _privilege__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../privilege */ "./src/app/modules/admin/privilege/index.ts");
/* harmony import */ var _category__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../category */ "./src/app/modules/admin/category/index.ts");
/* harmony import */ var _role_role_service__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../role/role.service */ "./src/app/modules/admin/role/role.service.ts");
/* harmony import */ var _category_category_service__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../category/category.service */ "./src/app/modules/admin/category/category.service.ts");
/* harmony import */ var _privilege_privilege_service__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../privilege/privilege.service */ "./src/app/modules/admin/privilege/privilege.service.ts");
/* harmony import */ var _datasecurity_userassignment_service__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../datasecurity/userassignment.service */ "./src/app/modules/admin/datasecurity/userassignment.service.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _common_services_local_search_service__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ../../../common/services/local-search.service */ "./src/app/common/services/local-search.service.ts");
/* harmony import */ var _common_components_confirm_dialog__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ../../../common/components/confirm-dialog */ "./src/app/common/components/confirm-dialog/index.ts");


















var USER_SEARCH_CONFIG = [
    { keyword: 'LOGIN ID', fieldName: 'masterLoginId' },
    { keyword: 'ROLE', fieldName: 'roleName' },
    { keyword: 'FIRST NAME', fieldName: 'firstName' },
    { keyword: 'LAST NAME', fieldName: 'lastName' },
    { keyword: 'EMAIL', fieldName: 'email' },
    { keyword: 'STATUS', fieldName: 'activeStatusInd' }
];
var ROLE_SEARCH_CONFIG = [
    { keyword: 'ROLE NAME', fieldName: 'roleName' },
    { keyword: 'ROLE TYPE', fieldName: 'roleType' },
    { keyword: 'STATUS', fieldName: 'activeStatusInd' },
    { keyword: 'ROLE DESCRIPTION', fieldName: 'roleDesc' }
];
var CATEGORY_SEARCH_CONFIG = [
    { keyword: 'PRODUCT', fieldName: 'productName' },
    { keyword: 'MODULE', fieldName: 'moduleName' },
    { keyword: 'CATEGORY', fieldName: 'categoryName' },
    {
        keyword: 'SUB CATEGORIES',
        fieldName: 'subCategories',
        accessor: function (input) { return lodash_map__WEBPACK_IMPORTED_MODULE_3__(input, function (sc) { return sc.subCategoryName; }); }
    }
];
var PRIVILEGE_SEARCH_CONFIG = [
    { keyword: 'PRODUCT', fieldName: 'productName' },
    { keyword: 'MODULE', fieldName: 'moduleName' },
    { keyword: 'CATEGORY', fieldName: 'categoryName' },
    { keyword: 'ROLE', fieldName: 'roleName' },
    { keyword: 'PRIVILEGE DESC', fieldName: 'privilegeDesc' },
    { keyword: 'SUB CATEGORY', fieldName: 'subCategoryName' }
];
var deleteConfirmation = function (section, identifier, identifierValue) { return ({
    title: "Are you sure you want to delete this " + section + "?",
    content: identifier + ": " + identifierValue,
    positiveActionLabel: 'Delete',
    negativeActionLabel: 'Cancel'
}); };
var AdminMainViewComponent = /** @class */ (function () {
    function AdminMainViewComponent(_privilegeService, _categoryService, _userService, _roleService, _jwtService, _localSearch, _userassignmentsService, _toastMessage, _dialog, _router, _route) {
        var _this = this;
        this._privilegeService = _privilegeService;
        this._categoryService = _categoryService;
        this._userService = _userService;
        this._roleService = _roleService;
        this._jwtService = _jwtService;
        this._localSearch = _localSearch;
        this._userassignmentsService = _userassignmentsService;
        this._toastMessage = _toastMessage;
        this._dialog = _dialog;
        this._router = _router;
        this._route = _route;
        this.columns = [];
        this.listeners = [];
        this.filterObj = {
            searchTerm: '',
            searchTermValue: ''
        };
        var dataListener = this._route.data.subscribe(function (data) {
            return _this.onDataChange(data);
        });
        var navigationListener = this._router.events.subscribe(function (e) {
            if (e instanceof _angular_router__WEBPACK_IMPORTED_MODULE_5__["NavigationEnd"]) {
                _this.initialise();
            }
        });
        this.listeners.push(dataListener);
        this.listeners.push(navigationListener);
    }
    AdminMainViewComponent.prototype.initialise = function () {
        var _this = this;
        this.showAddButton = true;
        var token = this._jwtService.getTokenObj();
        this.ticket = token.ticket;
        var customerId = parseInt(this.ticket.custID, 10);
        this.data$ = this.getListData(customerId);
        this.data$.then(function (data) {
            if (_this.section === 'privilege') {
                var role = _this._route.snapshot.queryParams.role;
                if (role) {
                    _this.filterObj.searchTerm = "role:\"" + role + "\"";
                }
            }
            _this.setData(data);
        });
    };
    AdminMainViewComponent.prototype.ngOnDestroy = function () {
        this.listeners.forEach(function (l) { return l.unsubscribe(); });
    };
    AdminMainViewComponent.prototype.onDataChange = function (_a) {
        var columns = _a.columns, section = _a.section;
        this.columns = columns;
        this.section = section;
    };
    AdminMainViewComponent.prototype.applySearchFilter = function (value) {
        var _this = this;
        this.filterObj.searchTerm = value;
        var searchCriteria = this._localSearch.parseSearchTerm(this.filterObj.searchTerm);
        this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
        this._localSearch
            .doSearch(searchCriteria, this.data, this.getSearchConfig())
            .then(function (data) {
            _this.filteredData = data;
        }, function (err) {
            _this._toastMessage.error(err.message);
        });
    };
    AdminMainViewComponent.prototype.getSearchConfig = function () {
        /* prettier-ignore */
        switch (this.section) {
            case 'user':
                return USER_SEARCH_CONFIG;
            case 'role':
                return ROLE_SEARCH_CONFIG;
            case 'category':
                return CATEGORY_SEARCH_CONFIG;
            case 'privilege':
                return PRIVILEGE_SEARCH_CONFIG;
            case 'user assignments':
                return USER_SEARCH_CONFIG;
        }
    };
    AdminMainViewComponent.prototype.onRowClick = function (row) {
        /* prettier-ignore */
        switch (this.section) {
            case 'role':
                this._router.navigate(['/admin/privilege'], {
                    queryParams: { role: row.roleName }
                });
                break;
            default:
                break;
        }
    };
    AdminMainViewComponent.prototype.setData = function (data) {
        this.data = data;
        if (this.filterObj.searchTerm) {
            this.applySearchFilter(this.filterObj.searchTerm);
        }
        else {
            this.filteredData = data;
        }
    };
    AdminMainViewComponent.prototype.getService = function () {
        /* prettier-ignore */
        switch (this.section) {
            case 'user':
                return this._userService;
            case 'role':
                return this._roleService;
            case 'category':
                return this._categoryService;
            case 'privilege':
                return this._privilegeService;
            case 'user assignments':
                this.showAddButton = false;
                return this._userassignmentsService;
            default:
                break;
        }
    };
    AdminMainViewComponent.prototype.getFormDeps = function () {
        var customerId = parseInt(this.ticket.custID, 10);
        var masterLoginId = this.ticket.masterLoginId;
        /* prettier-ignore */
        switch (this.section) {
            case 'user':
                return { roles$: this._userService.getUserRoles(customerId) };
            case 'role':
                return { roleTypes$: this._roleService.getRoleTypes(customerId) };
            case 'category':
                return { customerId: customerId };
            case 'privilege':
                return { customerId: customerId, masterLoginId: masterLoginId };
            default:
                break;
        }
    };
    AdminMainViewComponent.prototype.getListData = function (customerId) {
        var service = this.getService();
        return service.getList(customerId);
    };
    AdminMainViewComponent.prototype.removeListItem = function (row) {
        var _this = this;
        var service = this.getService();
        return service.remove(row).then(function (rows) {
            /* prettier-ignore */
            switch (_this.section) {
                case 'privilege':
                    // for some reason, the backend doesn't return the new array of privileges
                    // so we have to delete it manually
                    var index = _this.filteredData.indexOf(row);
                    _this.filteredData.splice(index, 1);
                    break;
                default:
                    if (rows) {
                        _this.setData(rows);
                    }
                    break;
            }
        });
    };
    AdminMainViewComponent.prototype.deleteRow = function (row) {
        var _this = this;
        var modifiedRow = this.modifyRowForDeletion(row);
        var confirmation = this.getConfirmation(modifiedRow);
        var dialogAction;
        /* prettier-ignore */
        switch (this.section) {
            case 'category':
                dialogAction = this.openCategoryDeletionDialog(row);
                break;
            default:
                dialogAction = this.openConfirmationDialog(confirmation);
        }
        dialogAction.afterClosed().subscribe(function (canDelete) {
            if (canDelete) {
                _this.removeListItem(modifiedRow);
            }
        });
    };
    AdminMainViewComponent.prototype.getConfirmation = function (row) {
        /* prettier-ignore */
        switch (this.section) {
            case 'user':
                return deleteConfirmation('user', 'User ID', row.masterLoginId);
            case 'role':
                return deleteConfirmation('role', 'Role Name', row.roleName);
            case 'category':
                return deleteConfirmation('category', 'Category Name', row.categoryName);
            case 'privilege':
                var identifiervalue = row.productName + " --> " + row.moduleName + " --> " + row.categoryName + " --> " + row.subCategoryName + " --> " + row.roleName + ".";
                return deleteConfirmation('privilege', 'Privilege Details', identifiervalue);
        }
    };
    AdminMainViewComponent.prototype.modifyRowForDeletion = function (row) {
        /* prettier-ignore */
        switch (this.section) {
            case 'role':
                var customerId = parseInt(this.ticket.custID, 10);
                var masterLoginId = this.ticket.masterLoginId;
                return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, row, { roleId: row.roleSysId, customerId: customerId,
                    masterLoginId: masterLoginId });
            default:
                return row;
        }
    };
    AdminMainViewComponent.prototype.editRow = function (row) {
        var _this = this;
        this.openRowModal(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_2__(row), 'edit')
            .afterClosed()
            .subscribe(function (rows) {
            if (rows) {
                _this.setData(rows);
            }
        });
    };
    AdminMainViewComponent.prototype.createRow = function () {
        var _this = this;
        var newRow = this.getNewRow();
        this.openRowModal(newRow, 'create')
            .afterClosed()
            .subscribe(function (rows) {
            if (rows) {
                _this.setData(rows);
            }
        });
    };
    AdminMainViewComponent.prototype.getNewRow = function () {
        var custId = parseInt(this.ticket.custID, 10);
        var _a = this.ticket, custCode = _a.custCode, masterLoginId = _a.masterLoginId;
        /* prettier-ignore */
        switch (this.section) {
            case 'user':
                return {
                    customerId: custId
                };
            case 'role':
                return {
                    custSysId: custId,
                    customerCode: custCode,
                    masterLoginId: masterLoginId,
                    myAnalysis: true
                };
            case 'category':
                return {
                    customerId: custId,
                    masterLoginId: masterLoginId
                };
            case 'privilege':
                return {
                    customerId: custId,
                    masterLoginId: masterLoginId
                };
        }
    };
    AdminMainViewComponent.prototype.openRowModal = function (row, mode) {
        var formDeps = this.getFormDeps();
        var data = {
            model: row,
            formDeps: formDeps,
            mode: mode
        };
        var component = this.getModalComponent();
        return this._dialog.open(component, {
            width: 'auto',
            height: 'auto',
            autoFocus: false,
            data: data
        });
    };
    AdminMainViewComponent.prototype.openCategoryDeletionDialog = function (category) {
        var customerId = parseInt(this.ticket.custID, 10);
        var masterLoginId = this.ticket.masterLoginId;
        var data = {
            category: category,
            customerId: customerId,
            masterLoginId: masterLoginId
        };
        return this._dialog.open(_category__WEBPACK_IMPORTED_MODULE_9__["CategoryDeleteDialogComponent"], {
            width: 'auto',
            height: 'auto',
            autoFocus: false,
            data: data
        });
    };
    AdminMainViewComponent.prototype.getModalComponent = function () {
        /* prettier-ignore */
        switch (this.section) {
            case 'user':
                return _user__WEBPACK_IMPORTED_MODULE_6__["UserEditDialogComponent"];
            case 'role':
                return _role__WEBPACK_IMPORTED_MODULE_7__["RoleEditDialogComponent"];
            case 'category':
                return _category__WEBPACK_IMPORTED_MODULE_9__["CategoryEditDialogComponent"];
            case 'privilege':
                return _privilege__WEBPACK_IMPORTED_MODULE_8__["PrivilegeEditDialogComponent"];
        }
    };
    AdminMainViewComponent.prototype.openConfirmationDialog = function (data) {
        return this._dialog.open(_common_components_confirm_dialog__WEBPACK_IMPORTED_MODULE_17__["ConfirmDialogComponent"], {
            width: 'auto',
            height: 'auto',
            data: data
        });
    };
    AdminMainViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-main-view',
            template: __webpack_require__(/*! ./admin-main-view.component.html */ "./src/app/modules/admin/main-view/admin-main-view.component.html"),
            styles: [__webpack_require__(/*! ./admin-main-view.component.scss */ "./src/app/modules/admin/main-view/admin-main-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_privilege_privilege_service__WEBPACK_IMPORTED_MODULE_12__["PrivilegeService"],
            _category_category_service__WEBPACK_IMPORTED_MODULE_11__["CategoryService"],
            _user__WEBPACK_IMPORTED_MODULE_6__["UserService"],
            _role_role_service__WEBPACK_IMPORTED_MODULE_10__["RoleService"],
            _common_services__WEBPACK_IMPORTED_MODULE_14__["JwtService"],
            _common_services_local_search_service__WEBPACK_IMPORTED_MODULE_16__["LocalSearchService"],
            _datasecurity_userassignment_service__WEBPACK_IMPORTED_MODULE_13__["UserAssignmentService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_15__["ToastService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_4__["MatDialog"],
            _angular_router__WEBPACK_IMPORTED_MODULE_5__["Router"],
            _angular_router__WEBPACK_IMPORTED_MODULE_5__["ActivatedRoute"]])
    ], AdminMainViewComponent);
    return AdminMainViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/main-view/admin.service.ts":
/*!**********************************************************!*\
  !*** ./src/app/modules/admin/main-view/admin.service.ts ***!
  \**********************************************************/
/*! exports provided: AdminService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminService", function() { return AdminService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm5/http.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _appConfig__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../../appConfig */ "./appConfig.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _analyze_services_analyze_service__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../analyze/services/analyze.service */ "./src/app/modules/analyze/services/analyze.service.ts");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var lodash_set__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! lodash/set */ "./node_modules/lodash/set.js");
/* harmony import */ var lodash_set__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(lodash_set__WEBPACK_IMPORTED_MODULE_10__);











var ANALYZE_MODULE_NAME = 'ANALYZE';
var loginUrl = _appConfig__WEBPACK_IMPORTED_MODULE_5__["default"].login.url;
var apiUrl = _appConfig__WEBPACK_IMPORTED_MODULE_5__["default"].api.url;
var AdminService = /** @class */ (function () {
    function AdminService(http, _toastMessage, _jwtService, _analyzeService) {
        this.http = http;
        this._toastMessage = _toastMessage;
        this._jwtService = _jwtService;
        this._analyzeService = _analyzeService;
    }
    AdminService.prototype.showToastMessageIfNeeded = function (toast) {
        var _this = this;
        return function (resp) {
            if (!toast) {
                return;
            }
            if (resp.valid) {
                _this._toastMessage.success(toast.successMsg);
            }
            else {
                _this._toastMessage.error(toast.errorMsg || resp.validityMessage);
            }
        };
    };
    AdminService.prototype.getRequestParams = function (params) {
        if (params === void 0) { params = []; }
        var reqParams = this._jwtService.getRequestParams();
        lodash_set__WEBPACK_IMPORTED_MODULE_10__(reqParams, 'contents.keys.[0].module', ANALYZE_MODULE_NAME);
        lodash_forEach__WEBPACK_IMPORTED_MODULE_9__(params, function (tuple) {
            lodash_set__WEBPACK_IMPORTED_MODULE_10__(reqParams, tuple[0], tuple[1]);
        });
        return reqParams;
    };
    AdminService.prototype.getAnalysesByCategoryId = function (subCategoryId) {
        return Object(rxjs__WEBPACK_IMPORTED_MODULE_8__["from"])(this._analyzeService.getAnalysesFor(subCategoryId.toString()));
    };
    AdminService.prototype.getRequest = function (path, options) {
        if (options === void 0) { options = {}; }
        var toast = options.toast, forWhat = options.forWhat;
        return this.http
            .get(this.getBaseUrl(forWhat) + "/" + this.getIntermediaryPath(forWhat) + path)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["tap"])(this.showToastMessageIfNeeded(toast)));
    };
    AdminService.prototype.request = function (path, params, options) {
        if (options === void 0) { options = {}; }
        var toast = options.toast, forWhat = options.forWhat;
        return this.http
            .post(this.getBaseUrl(forWhat) + "/" + this.getIntermediaryPath(forWhat) + path, params)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["tap"])(this.showToastMessageIfNeeded(toast)));
    };
    AdminService.prototype.getIntermediaryPath = function (forWhat) {
        switch (forWhat) {
            case 'export':
            case 'import':
                return '';
            case 'newScheme':
                return 'auth/admin/';
            default:
                return 'auth/admin/cust/manage/';
        }
    };
    AdminService.prototype.getBaseUrl = function (forWhat) {
        switch (forWhat) {
            case 'export':
            case 'import':
                return apiUrl;
            default:
                return loginUrl;
        }
    };
    AdminService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_4__["ToastService"],
            _common_services__WEBPACK_IMPORTED_MODULE_6__["JwtService"],
            _analyze_services_analyze_service__WEBPACK_IMPORTED_MODULE_7__["AnalyzeService"]])
    ], AdminService);
    return AdminService;
}());



/***/ }),

/***/ "./src/app/modules/admin/main-view/index.ts":
/*!**************************************************!*\
  !*** ./src/app/modules/admin/main-view/index.ts ***!
  \**************************************************/
/*! exports provided: AdminMainViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _admin_main_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./admin-main-view.component */ "./src/app/modules/admin/main-view/admin-main-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminMainViewComponent", function() { return _admin_main_view_component__WEBPACK_IMPORTED_MODULE_0__["AdminMainViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/page/admin-page.component.html":
/*!**************************************************************!*\
  !*** ./src/app/modules/admin/page/admin-page.component.html ***!
  \**************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<router-outlet></router-outlet>\n"

/***/ }),

/***/ "./src/app/modules/admin/page/admin-page.component.ts":
/*!************************************************************!*\
  !*** ./src/app/modules/admin/page/admin-page.component.ts ***!
  \************************************************************/
/*! exports provided: AdminPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminPageComponent", function() { return AdminPageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");


var AdminPageComponent = /** @class */ (function () {
    function AdminPageComponent() {
    }
    AdminPageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'admin-page',
            template: __webpack_require__(/*! ./admin-page.component.html */ "./src/app/modules/admin/page/admin-page.component.html")
        })
    ], AdminPageComponent);
    return AdminPageComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/page/index.ts":
/*!*********************************************!*\
  !*** ./src/app/modules/admin/page/index.ts ***!
  \*********************************************/
/*! exports provided: AdminPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _admin_page_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./admin-page.component */ "./src/app/modules/admin/page/admin-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AdminPageComponent", function() { return _admin_page_component__WEBPACK_IMPORTED_MODULE_0__["AdminPageComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/privilege/edit-dialog/index.ts":
/*!**************************************************************!*\
  !*** ./src/app/modules/admin/privilege/edit-dialog/index.ts ***!
  \**************************************************************/
/*! exports provided: PrivilegeEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _privilege_edit_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./privilege-edit-dialog.component */ "./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PrivilegeEditDialogComponent", function() { return _privilege_edit_dialog_component__WEBPACK_IMPORTED_MODULE_0__["PrivilegeEditDialogComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.html":
/*!******************************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.html ***!
  \******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<header class=\"base-dialog__header\">\n  <strong [ngSwitch]=\"data.mode\">\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Add New Privilege\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n> Edit Privilege </ng-container>\n  </strong>\n</header>\n<div class=\"base-dialog__content\">\n  <form [formGroup]=\"formGroup\" fxLayout=\"column\" fxLayoutAlign=\"center start\">\n    <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n      <mat-label i18n>Product</mat-label>\n      <mat-select class=\"form-field\" required formControlName=\"productId\">\n        <mat-option\n          *ngFor=\"let product of (products$ | async)\"\n          [value]=\"product.productId\"\n        >\n          {{ product.productName }}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n\n    <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n      <mat-label i18n>Role</mat-label>\n      <mat-select required formControlName=\"roleId\">\n        <mat-option *ngFor=\"let role of (roles$ | async)\" [value]=\"role.roleId\">\n          {{ role.roleName }}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n\n    <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n      <mat-label i18n>Module</mat-label>\n      <mat-select class=\"form-field\" required formControlName=\"moduleId\">\n        <mat-option\n          *ngFor=\"let module of (modules$ | async)\"\n          [value]=\"module.moduleId\"\n        >\n          {{ module.moduleName }}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n\n    <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n      <mat-label i18n>Category</mat-label>\n      <mat-select class=\"form-field\" required formControlName=\"categoryCode\">\n        <mat-option\n          *ngFor=\"let category of categories\"\n          [value]=\"category.categoryCode\"\n        >\n          {{ category.categoryName }}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n  </form>\n\n  <privilege-editor\n    *ngIf=\"subCategories && subCategories.length > 0\"\n    (privilegeChange)=\"onPrivilegeChange($event)\"\n    [allowedPrivileges]=\"allowedPrivileges\"\n    [subCategories]=\"subCategories\"\n    [activePrivilegeId]=\"privilegeId\"\n  >\n  </privilege-editor>\n</div>\n\n<div\n  fxLayout=\"row\"\n  fxLayoutAlign=\"space-between center\"\n  class=\"base-dialog__actions\"\n>\n  <button\n    (click)=\"create()\"\n    [disabled]=\"!formIsValid\"\n    e2e=\"create-analysis-btn\"\n    color=\"primary\"\n    [ngSwitch]=\"data.mode\"\n    mat-raised-button\n  >\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Create Privilege\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n> Save </ng-container>\n  </button>\n  <button mat-button mat-dialog-close i18n>Cancel</button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.scss":
/*!******************************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.scss ***!
  \******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  display: block;\n  min-width: 75vw; }\n\n.base-dialog__content {\n  margin: 10px !important;\n  max-height: 450px;\n  overflow: scroll; }\n\n.base-dialog__content .mat-form-field {\n    width: 100%; }\n\n.base-dialog__content .form-field {\n    margin-bottom: 5px; }\n\n.base-dialog__content form {\n    width: 97%;\n    margin: 0 auto; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL3ByaXZpbGVnZS9lZGl0LWRpYWxvZy9wcml2aWxlZ2UtZWRpdC1kaWFsb2cuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxjQUFjO0VBQ2QsZUFBZSxFQUFBOztBQUdqQjtFQUNFLHVCQUF1QjtFQUN2QixpQkFBaUI7RUFDakIsZ0JBQWdCLEVBQUE7O0FBSGxCO0lBTUksV0FBVyxFQUFBOztBQU5mO0lBVUksa0JBQWtCLEVBQUE7O0FBVnRCO0lBY0ksVUFBVTtJQUNWLGNBQWMsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYWRtaW4vcHJpdmlsZWdlL2VkaXQtZGlhbG9nL3ByaXZpbGVnZS1lZGl0LWRpYWxvZy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgJ3NyYy90aGVtZXMvYmFzZS9jb2xvcnMnO1xuXG46aG9zdCB7XG4gIGRpc3BsYXk6IGJsb2NrO1xuICBtaW4td2lkdGg6IDc1dnc7XG59XG5cbi5iYXNlLWRpYWxvZ19fY29udGVudCB7XG4gIG1hcmdpbjogMTBweCAhaW1wb3J0YW50O1xuICBtYXgtaGVpZ2h0OiA0NTBweDtcbiAgb3ZlcmZsb3c6IHNjcm9sbDtcblxuICAubWF0LWZvcm0tZmllbGQge1xuICAgIHdpZHRoOiAxMDAlO1xuICB9XG5cbiAgLmZvcm0tZmllbGQge1xuICAgIG1hcmdpbi1ib3R0b206IDVweDtcbiAgfVxuXG4gIGZvcm0ge1xuICAgIHdpZHRoOiA5NyU7XG4gICAgbWFyZ2luOiAwIGF1dG87XG4gIH1cbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.ts":
/*!****************************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.ts ***!
  \****************************************************************************************/
/*! exports provided: PrivilegeEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PrivilegeEditDialogComponent", function() { return PrivilegeEditDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_every__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/every */ "./node_modules/lodash/every.js");
/* harmony import */ var lodash_every__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_every__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var _privilege_code_transformer__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../privilege-code-transformer */ "./src/app/modules/admin/privilege/privilege-code-transformer.ts");
/* harmony import */ var _privilege_service__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../privilege.service */ "./src/app/modules/admin/privilege/privilege.service.ts");
/* harmony import */ var _common_base_dialog__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../../common/base-dialog */ "./src/app/common/base-dialog/index.ts");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");













var PrivilegeEditDialogComponent = /** @class */ (function (_super) {
    tslib__WEBPACK_IMPORTED_MODULE_0__["__extends"](PrivilegeEditDialogComponent, _super);
    function PrivilegeEditDialogComponent(_privilegeService, _fb, _dialogRef, data) {
        var _this = _super.call(this) || this;
        _this._privilegeService = _privilegeService;
        _this._fb = _fb;
        _this._dialogRef = _dialogRef;
        _this.data = data;
        _this.formIsValid = false;
        var customerId = _this.data.formDeps.customerId;
        if (_this.data.mode === 'edit') {
            _this.formIsValid = true;
            var _a = _this.data.model, productId = _a.productId, moduleId = _a.moduleId, moduleName = _a.moduleName, roleId = _a.roleId, categoryCode = _a.categoryCode;
            _this.modules$ = _this.loadModules(productId);
            _this.loadAllowedPrivilegesForModule(moduleName);
            _this.loadCategories(moduleId);
            _this.loadSubCategories(moduleId, roleId, productId, categoryCode);
        }
        _this.products$ = _this._privilegeService.getProducts(customerId);
        _this.products$.then(function () {
            var productIdControl = _this.formGroup.controls.productId;
            if (productIdControl) {
                productIdControl.enable();
            }
        });
        _this.roles$ = _this._privilegeService.getRoles(customerId);
        _this.roles$.then(function () {
            var roleIdControl = _this.formGroup.controls.roleId;
            if (roleIdControl) {
                roleIdControl.enable();
            }
        });
        _this.privilegeId = _this.data.model.privilegeId;
        _this.createForm(_this.data.model);
        return _this;
    }
    PrivilegeEditDialogComponent.prototype.onPrivilegeChange = function (_a) {
        var index = _a.index, privilege = _a.privilege;
        var oldSubCategoryPrivilege = this.subCategories[index];
        this.subCategories.splice(index, 1, tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, oldSubCategoryPrivilege, privilege));
    };
    PrivilegeEditDialogComponent.prototype.create = function () {
        var formValues = this.formGroup.getRawValue();
        var _a = this.data.formDeps, customerId = _a.customerId, masterLoginId = _a.masterLoginId;
        var targetCategory = lodash_find__WEBPACK_IMPORTED_MODULE_4__(this.categories, function (_a) {
            var categoryCode = _a.categoryCode;
            return formValues.categoryCode === categoryCode;
        });
        var categoryType = targetCategory.categoryType, categoryId = targetCategory.categoryId;
        var model = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, this.data.model, formValues, { categoryId: categoryId,
            categoryType: categoryType, subCategoriesPrivilege: this.getSubCategoriesPrivilege(this.subCategories), customerId: customerId,
            masterLoginId: masterLoginId });
        this.save(model);
    };
    PrivilegeEditDialogComponent.prototype.getSubCategoriesPrivilege = function (subCategories) {
        var _this = this;
        return lodash_map__WEBPACK_IMPORTED_MODULE_5__(subCategories, function (_a) {
            var privilegeCode = _a.privilegeCode, subCategoryId = _a.subCategoryId, privilegeId = _a.privilegeId;
            var privilegeDesc = Object(_privilege_code_transformer__WEBPACK_IMPORTED_MODULE_8__["getPrivilegeDescription"])(privilegeCode, _this.allowedPrivileges);
            return {
                privilegeCode: privilegeCode,
                privilegeDesc: privilegeDesc,
                subCategoryId: subCategoryId,
                privilegeId: privilegeId
            };
        });
    };
    PrivilegeEditDialogComponent.prototype.save = function (model) {
        var _this = this;
        var actionPromise;
        switch (this.data.mode) {
            case 'edit':
                actionPromise = this._privilegeService.update(model);
                break;
            case 'create':
                actionPromise = this._privilegeService.save(model);
                break;
        }
        actionPromise &&
            actionPromise.then(function (rows) {
                if (rows) {
                    _this._dialogRef.close(rows);
                }
            });
    };
    PrivilegeEditDialogComponent.prototype.createForm = function (formModel) {
        var _this = this;
        var _a = formModel.productId, productId = _a === void 0 ? '' : _a, _b = formModel.roleId, roleId = _b === void 0 ? '' : _b, _c = formModel.moduleId, moduleId = _c === void 0 ? '' : _c, _d = formModel.categoryCode, categoryCode = _d === void 0 ? '' : _d;
        var productIdControl = this._fb.control({ value: productId, disabled: true }, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required);
        var roleIdControl = this._fb.control({ value: roleId, disabled: true }, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required);
        var moduleIdControl = this._fb.control({ value: moduleId, disabled: true }, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required);
        var categoryCodeControl = this._fb.control({ value: categoryCode, disabled: true }, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required);
        this.formGroup = this._fb.group({
            productId: productIdControl,
            roleId: roleIdControl,
            moduleId: moduleIdControl,
            categoryCode: categoryCodeControl
        });
        this.formGroup.statusChanges.subscribe(function () {
            // we have to use a manual validation because disabling a form control disables it's validation,
            // and the form can't be validated properly
            var isValid = lodash_every__WEBPACK_IMPORTED_MODULE_6__(_this.formGroup.getRawValue(), Boolean);
            if (isValid) {
                _this.formIsValid = true;
            }
            else {
                _this.formIsValid = false;
            }
        });
        productIdControl.valueChanges.subscribe(function (pId) {
            _this.modules$ = _this.loadModules(pId);
        });
        roleIdControl.valueChanges.subscribe(function (rId) {
            var f = _this.formGroup.value;
            _this.loadSubCategories(f.moduleId, rId, f.productId, f.categoryCode);
        });
        moduleIdControl.valueChanges
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_12__["flatMap"])(function (mId) {
            return Object(rxjs__WEBPACK_IMPORTED_MODULE_11__["from"])(_this.modules$ || Promise.resolve([])).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_12__["map"])(function (modules) { return [mId, modules]; }));
        }), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_12__["map"])(function (_a) {
            var mId = _a[0], modules = _a[1];
            var module = lodash_find__WEBPACK_IMPORTED_MODULE_4__(modules, function (mod) { return mod.moduleId === mId; }) || {};
            return { modId: mId, modName: module.moduleName };
        }))
            .subscribe(function (_a) {
            var modId = _a.modId, modName = _a.modName;
            _this.loadCategories(modId);
            _this.loadAllowedPrivilegesForModule(modName);
            _this.formGroup.controls.categoryCode.reset('');
            _this.subCategories = [];
        });
        categoryCodeControl.valueChanges.subscribe(function (cCode) {
            var f = _this.formGroup.value;
            _this.loadSubCategories(f.moduleId, f.roleId, f.productId, cCode);
        });
    };
    PrivilegeEditDialogComponent.prototype.loadModules = function (productId) {
        var _this = this;
        var customerId = this.data.formDeps.customerId;
        var moduleParams = {
            customerId: customerId,
            productId: productId,
            moduleId: 0
        };
        return this._privilegeService.getModules(moduleParams).then(function (modules) {
            _this.formGroup.controls.moduleId.enable({ emitEvent: false });
            return modules;
        });
    };
    /**
     * Loads allowed provileges for selected module
     *
     * @param {*} moduleName
     * @memberof PrivilegeEditDialogComponent
     */
    PrivilegeEditDialogComponent.prototype.loadAllowedPrivilegesForModule = function (moduleName) {
        var _this = this;
        this._privilegeService
            .getPrivilegesForModule(moduleName)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_12__["first"])(), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_12__["tap"])(function (privileges) {
            _this.allowedPrivileges = privileges;
        }))
            .subscribe();
    };
    PrivilegeEditDialogComponent.prototype.loadCategories = function (moduleId) {
        var _this = this;
        var customerId = this.data.formDeps.customerId;
        var categoryParams = {
            customerId: customerId,
            productId: 0,
            moduleId: moduleId
        };
        return this._privilegeService
            .getParentCategories(categoryParams)
            .then(function (categories) {
            _this.categories = categories;
            var categoryCodeControl = _this.formGroup.controls.categoryCode;
            if (lodash_isEmpty__WEBPACK_IMPORTED_MODULE_7__(categories)) {
                categoryCodeControl.disable();
            }
            else {
                categoryCodeControl.enable();
            }
        });
    };
    PrivilegeEditDialogComponent.prototype.loadSubCategories = function (moduleId, roleId, productId, categoryCode) {
        var _this = this;
        if (!(productId > 0 && roleId > 0 && moduleId > 0 && categoryCode !== '')) {
            return;
        }
        var customerId = this.data.formDeps.customerId;
        var categoryParams = {
            customerId: customerId,
            roleId: roleId,
            productId: productId,
            moduleId: moduleId,
            categoryCode: categoryCode
        };
        return this._privilegeService
            .getSubCategories(categoryParams)
            .then(function (subCategories) {
            _this.subCategories = subCategories;
        });
    };
    PrivilegeEditDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'privilege-edit-dialog',
            template: __webpack_require__(/*! ./privilege-edit-dialog.component.html */ "./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.html"),
            styles: [__webpack_require__(/*! ./privilege-edit-dialog.component.scss */ "./src/app/modules/admin/privilege/edit-dialog/privilege-edit-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](3, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_privilege_service__WEBPACK_IMPORTED_MODULE_9__["PrivilegeService"],
            _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], PrivilegeEditDialogComponent);
    return PrivilegeEditDialogComponent;
}(_common_base_dialog__WEBPACK_IMPORTED_MODULE_10__["BaseDialogComponent"]));



/***/ }),

/***/ "./src/app/modules/admin/privilege/index.ts":
/*!**************************************************!*\
  !*** ./src/app/modules/admin/privilege/index.ts ***!
  \**************************************************/
/*! exports provided: PrivilegeService, PrivilegeEditDialogComponent, PrivilegeEditorComponent, PrivilegeRowComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _privilege_service__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./privilege.service */ "./src/app/modules/admin/privilege/privilege.service.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PrivilegeService", function() { return _privilege_service__WEBPACK_IMPORTED_MODULE_0__["PrivilegeService"]; });

/* harmony import */ var _edit_dialog__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./edit-dialog */ "./src/app/modules/admin/privilege/edit-dialog/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PrivilegeEditDialogComponent", function() { return _edit_dialog__WEBPACK_IMPORTED_MODULE_1__["PrivilegeEditDialogComponent"]; });

/* harmony import */ var _privilege_editor__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./privilege-editor */ "./src/app/modules/admin/privilege/privilege-editor/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PrivilegeEditorComponent", function() { return _privilege_editor__WEBPACK_IMPORTED_MODULE_2__["PrivilegeEditorComponent"]; });

/* harmony import */ var _privilege_row__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./privilege-row */ "./src/app/modules/admin/privilege/privilege-row/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PrivilegeRowComponent", function() { return _privilege_row__WEBPACK_IMPORTED_MODULE_3__["PrivilegeRowComponent"]; });







/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-code-transformer.ts":
/*!***********************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-code-transformer.ts ***!
  \***********************************************************************/
/*! exports provided: NO_PRIVILEGES_STR, ALL_PRIVILEGES_STR, PRIVILEGE_NAMES, getPrivilegeCodeString, decimal2BoolArray, binaryString2BoolArray, boolArray2Decimal, getPrivilegeFromBoolArray, getPrivilegeDescription */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "NO_PRIVILEGES_STR", function() { return NO_PRIVILEGES_STR; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ALL_PRIVILEGES_STR", function() { return ALL_PRIVILEGES_STR; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PRIVILEGE_NAMES", function() { return PRIVILEGE_NAMES; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getPrivilegeCodeString", function() { return getPrivilegeCodeString; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "decimal2BoolArray", function() { return decimal2BoolArray; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "binaryString2BoolArray", function() { return binaryString2BoolArray; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "boolArray2Decimal", function() { return boolArray2Decimal; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getPrivilegeFromBoolArray", function() { return getPrivilegeFromBoolArray; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "getPrivilegeDescription", function() { return getPrivilegeDescription; });
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! lodash/fp/pipe */ "./node_modules/lodash/fp/pipe.js");
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var lodash_fp_compact__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/fp/compact */ "./node_modules/lodash/fp/compact.js");
/* harmony import */ var lodash_fp_compact__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_compact__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_fp_join__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/fp/join */ "./node_modules/lodash/fp/join.js");
/* harmony import */ var lodash_fp_join__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_join__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_fp_filter__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/fp/filter */ "./node_modules/lodash/fp/filter.js");
/* harmony import */ var lodash_fp_filter__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_4__);





var ALL_PRIVILEGES_DECIMAL = 128;
var NO_PRIVILEGES_STR = '0000000000000000';
var ALL_PRIVILEGES_STR = '1111111110000000';
var PRIVILEGE_NAMES = [
    'Create',
    'Execute',
    'Publish',
    'Fork',
    'Edit',
    'Export',
    'Delete'
];
/**
 * getPrivilegeCodeString
 * Converts decimal to binary and accounts for special cases for privileges
 *
 * @param privilegeCode
 * @returns {string}
 */
function getPrivilegeCodeString(privilegeCode) {
    switch (privilegeCode) {
        case 0:
            return NO_PRIVILEGES_STR;
        case ALL_PRIVILEGES_DECIMAL:
            return ALL_PRIVILEGES_STR;
        default:
            return privilegeCode.toString(2);
    }
}
/**
 * decimal2BoolArray
 * Converts decimal to boolean array.
 * For example, decimal 5 is 101 binary. So decimal2BoolArray(3) will give [true, false, true]
 *
 * @param {number} privilegeCode
 * @returns {Array<Boolean>}
 */
function decimal2BoolArray(privilegeCode) {
    var privilegeCodeString = getPrivilegeCodeString(privilegeCode);
    return binaryString2BoolArray(privilegeCodeString);
}
/**
 * binaryString2BoolArray
 * Converts binary to boolean array. 0 is false. 1 is true.
 * Example: binaryString2BoolArray('101') returns [true, false, true]
 *
 * @param {string} privilegeCodeString
 * @returns {Array<Boolean>}
 */
function binaryString2BoolArray(privilegeCodeString) {
    return lodash_map__WEBPACK_IMPORTED_MODULE_0__(privilegeCodeString, function (binStr) { return binStr === '1'; });
}
/**
 * boolArray2Decimal
 * Translates boolean array to binary and converts it to decimal.
 * Example, [true, false, true] is translate to '101' (binary) and finally
 * returned as 5.
 *
 * @param {Array<Boolean>} boolArray
 * @returns {number}
 */
function boolArray2Decimal(boolArray) {
    var privilegeCodeString = lodash_map__WEBPACK_IMPORTED_MODULE_0__(boolArray, function (bool) { return (bool ? '1' : '0'); }).join('');
    return parseInt(privilegeCodeString, 2);
}
/**
 * getPrivilegeFromBoolArray
 * In general, converts boolean array to decimal (similar to boolArray2Decimal).
 * Handles special privilege cases. For example. if 9th bit it true, then returns 128,
 * which stands for all privileges. Doesn't matter what other bits are (un)set.
 *
 * @param {Array<Boolean>} privilegeCodeList
 * @returns {PrivilegeCodeResponse}
 */
function getPrivilegeFromBoolArray(privilegeCodeList) {
    var hasNoAccessPrivilege = !privilegeCodeList[0];
    if (hasNoAccessPrivilege) {
        return {
            privilegeCode: 0
        };
    }
    var hasAllPrivileges = privilegeCodeList[8];
    if (hasAllPrivileges) {
        return {
            privilegeCode: 128
        };
    }
    return {
        privilegeCode: boolArray2Decimal(privilegeCodeList)
    };
}
function getPrivilegeDescription(privilegeCode, allowedPrivileges) {
    if (allowedPrivileges === void 0) { allowedPrivileges = PRIVILEGE_NAMES; }
    switch (privilegeCode) {
        case 0:
            return 'No-Access';
        case 128:
            return 'All';
        default:
            /* In backend, first bit stands for 'View', not 'Create'.
             * Following line makes sure the privilege list we use to
             * calculate description matches meaning of corresponding bits.
             */
            var PRIVILEGES_1 = ['View'].concat(PRIVILEGE_NAMES, ['All']);
            var ALLOWED_PRIVILEGES_1 = ['View'].concat(allowedPrivileges, ['All']).map(function (a) {
                return a.toUpperCase();
            });
            var privilegeCodeList = decimal2BoolArray(privilegeCode);
            return lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_1__(lodash_fp_compact__WEBPACK_IMPORTED_MODULE_2__, 
            /* Filter out any privileges that are not supported */
            lodash_fp_filter__WEBPACK_IMPORTED_MODULE_4__(function (privilegeName) {
                return ALLOWED_PRIVILEGES_1.includes(privilegeName.toUpperCase());
            }), lodash_fp_join__WEBPACK_IMPORTED_MODULE_3__(', '))(lodash_map__WEBPACK_IMPORTED_MODULE_0__(privilegeCodeList, function (privilege, index) {
                return privilege ? PRIVILEGES_1[index] : null;
            }));
    }
}


/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-editor/index.ts":
/*!*******************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-editor/index.ts ***!
  \*******************************************************************/
/*! exports provided: PrivilegeEditorComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _privilege_editor_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./privilege-editor.component */ "./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PrivilegeEditorComponent", function() { return _privilege_editor_component__WEBPACK_IMPORTED_MODULE_0__["PrivilegeEditorComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.html":
/*!******************************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.html ***!
  \******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<table\n  class=\"privilege-editor__privilege-table\"\n  *ngIf=\"subCategories.length > 0\"\n>\n  <tr>\n    <th style=\"width: 155px; word-wrap: break-word;\">\n      <span i18n>Sub-Categories</span>\n    </th>\n    <th style=\"width: 45px;\"><span i18n>Access</span></th>\n    <th style=\"width: 550px;\"><span i18n>Privileges</span></th>\n  </tr>\n  <tr\n    *ngFor=\"let subCategory of subCategories; let i = index\"\n    [class.active]=\"subCategory.privilegeId === activePrivilegeId\"\n    privilege-row\n    [allowedPrivileges]=\"allowedPrivileges\"\n    (categoryChange)=\"onCategoryChange(i, $event)\"\n    [subCategory]=\"subCategory\"\n  ></tr>\n</table>\n"

/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.scss":
/*!******************************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.scss ***!
  \******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".privilege-editor__privilege-table {\n  width: 97%;\n  margin: 0 auto; }\n  .privilege-editor__privilege-table tr:hover {\n    background-color: aliceblue; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL3ByaXZpbGVnZS9wcml2aWxlZ2UtZWRpdG9yL3ByaXZpbGVnZS1lZGl0b3IuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQ0U7RUFDRSxVQUFVO0VBQ1YsY0FBYyxFQUFBO0VBRmY7SUFLRywyQkFBMkIsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYWRtaW4vcHJpdmlsZWdlL3ByaXZpbGVnZS1lZGl0b3IvcHJpdmlsZWdlLWVkaXRvci5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIi5wcml2aWxlZ2UtZWRpdG9yIHtcbiAgJl9fcHJpdmlsZWdlLXRhYmxlIHtcbiAgICB3aWR0aDogOTclO1xuICAgIG1hcmdpbjogMCBhdXRvO1xuXG4gICAgdHI6aG92ZXIge1xuICAgICAgYmFja2dyb3VuZC1jb2xvcjogYWxpY2VibHVlO1xuICAgIH1cbiAgfVxufVxuIl19 */"

/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.ts":
/*!****************************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.ts ***!
  \****************************************************************************************/
/*! exports provided: PrivilegeEditorComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PrivilegeEditorComponent", function() { return PrivilegeEditorComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");


var PrivilegeEditorComponent = /** @class */ (function () {
    function PrivilegeEditorComponent() {
        this.privilegeChange = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    PrivilegeEditorComponent.prototype.onCategoryChange = function (index, privilege) {
        this.privilegeChange.emit({ index: index, privilege: privilege });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], PrivilegeEditorComponent.prototype, "privilegeChange", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], PrivilegeEditorComponent.prototype, "allowedPrivileges", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], PrivilegeEditorComponent.prototype, "subCategories", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], PrivilegeEditorComponent.prototype, "activePrivilegeId", void 0);
    PrivilegeEditorComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'privilege-editor',
            template: __webpack_require__(/*! ./privilege-editor.component.html */ "./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.html"),
            styles: [__webpack_require__(/*! ./privilege-editor.component.scss */ "./src/app/modules/admin/privilege/privilege-editor/privilege-editor.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [])
    ], PrivilegeEditorComponent);
    return PrivilegeEditorComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-row/index.ts":
/*!****************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-row/index.ts ***!
  \****************************************************************/
/*! exports provided: PrivilegeRowComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _privilege_row_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./privilege-row.component */ "./src/app/modules/admin/privilege/privilege-row/privilege-row.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "PrivilegeRowComponent", function() { return _privilege_row_component__WEBPACK_IMPORTED_MODULE_0__["PrivilegeRowComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-row/privilege-row.component.html":
/*!************************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-row/privilege-row.component.html ***!
  \************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<td>\n  <div class=\"sub-category-label\" [matTooltip]=\"subCategory.subCategoryName\">\n    {{ subCategory.subCategoryName | truncate: 30 }}\n  </div>\n</td>\n<td>\n  <mat-slide-toggle\n    [checked]=\"privilegeCodeList[0]\"\n    (change)=\"onAccessClicked()\"\n  >\n  </mat-slide-toggle>\n</td>\n<td>\n  <span *ngIf=\"privilegeCodeList[0]\">\n    <div\n      fxLayout=\"row\"\n      fxLayoutAlign=\"space-between center\"\n      class=\"privilege-options-row-container\"\n    >\n      <mat-checkbox\n        type=\"checkbox\"\n        (change)=\"onAllClicked()\"\n        [checked]=\"privilegeCodeList[8]\"\n      >\n        <label i18n>All</label>\n      </mat-checkbox>\n      <mat-checkbox\n        *ngFor=\"let privilegeName of PRIVILEGE_NAMES; let i = index\"\n        type=\"checkbox\"\n        [hidden]=\"!allowedPrivileges[privilegeName.toUpperCase()]\"\n        (change)=\"onPrivilegeClicked(i + 1)\"\n        [disabled]=\"privilegeCodeList[8]\"\n        [checked]=\"privilegeCodeList[i + 1]\"\n      >\n        <label>{{ privilegeName }}</label>\n      </mat-checkbox>\n    </div>\n  </span>\n</td>\n"

/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-row/privilege-row.component.scss":
/*!************************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-row/privilege-row.component.scss ***!
  \************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "tr[privilege-row] {\n  min-height: 42px;\n  height: 42px; }\n  tr[privilege-row] .sub-category-label {\n    white-space: normal;\n    word-wrap: break-word;\n    word-break: break-all; }\n  tr[privilege-row] mat-checkbox.mat-checkbox {\n    margin: 8px 5px; }\n  tr[privilege-row] mat-checkbox.mat-checkbox label {\n      color: #737373 !important;\n      font-size: 14px !important;\n      font-weight: 600; }\n  tr[privilege-row].active td {\n  background-color: #f5f9fc; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL3ByaXZpbGVnZS9wcml2aWxlZ2Utcm93L3ByaXZpbGVnZS1yb3cuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxnQkFBZ0I7RUFDaEIsWUFBWSxFQUFBO0VBRmQ7SUFLSSxtQkFBbUI7SUFDbkIscUJBQXFCO0lBQ3JCLHFCQUFxQixFQUFBO0VBUHpCO0lBV0ksZUFBZSxFQUFBO0VBWG5CO01BY00seUJBQWtDO01BQ2xDLDBCQUEwQjtNQUMxQixnQkFBZ0IsRUFBQTtFQUt0QjtFQUNFLHlCQ2pCd0IsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYWRtaW4vcHJpdmlsZWdlL3ByaXZpbGVnZS1yb3cvcHJpdmlsZWdlLXJvdy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbnRyW3ByaXZpbGVnZS1yb3ddIHtcbiAgbWluLWhlaWdodDogNDJweDtcbiAgaGVpZ2h0OiA0MnB4O1xuXG4gIC5zdWItY2F0ZWdvcnktbGFiZWwge1xuICAgIHdoaXRlLXNwYWNlOiBub3JtYWw7XG4gICAgd29yZC13cmFwOiBicmVhay13b3JkO1xuICAgIHdvcmQtYnJlYWs6IGJyZWFrLWFsbDtcbiAgfVxuXG4gIG1hdC1jaGVja2JveC5tYXQtY2hlY2tib3gge1xuICAgIG1hcmdpbjogOHB4IDVweDtcblxuICAgIGxhYmVsIHtcbiAgICAgIGNvbG9yOiAkcHJpbWFyeS1ncmV5LWczICFpbXBvcnRhbnQ7XG4gICAgICBmb250LXNpemU6IDE0cHggIWltcG9ydGFudDtcbiAgICAgIGZvbnQtd2VpZ2h0OiA2MDA7XG4gICAgfVxuICB9XG59XG5cbnRyW3ByaXZpbGVnZS1yb3ddLmFjdGl2ZSB0ZCB7XG4gIGJhY2tncm91bmQtY29sb3I6ICRncmlkLWhvdmVyLWNvbG9yO1xufVxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege-row/privilege-row.component.ts":
/*!**********************************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege-row/privilege-row.component.ts ***!
  \**********************************************************************************/
/*! exports provided: PrivilegeRowComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PrivilegeRowComponent", function() { return PrivilegeRowComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../privilege-code-transformer */ "./src/app/modules/admin/privilege/privilege-code-transformer.ts");



var PrivilegeRowComponent = /** @class */ (function () {
    function PrivilegeRowComponent() {
        this.categoryChange = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.PRIVILEGE_NAMES = _privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__["PRIVILEGE_NAMES"];
        this.allowedPrivileges = _privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__["PRIVILEGE_NAMES"].reduce(function (accum, privilegeName) {
            var _a;
            return (tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, accum, (_a = {}, _a[privilegeName.toUpperCase()] = true, _a)));
        }, {});
    }
    Object.defineProperty(PrivilegeRowComponent.prototype, "_subCategory", {
        set: function (subCategory) {
            if (!subCategory) {
                return;
            }
            this.subCategory = subCategory;
            var privilegeCode = subCategory.privilegeCode;
            this.privilegeCodeList = Object(_privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__["decimal2BoolArray"])(privilegeCode);
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(PrivilegeRowComponent.prototype, "_allowedPrivileges", {
        set: function (privileges) {
            if (!privileges) {
                return;
            }
            var allowedPrivilegeList = privileges;
            this.allowedPrivileges = allowedPrivilegeList.reduce(function (accum, privilegeName) {
                var _a;
                return (tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, accum, (_a = {}, _a[privilegeName.toUpperCase()] = true, _a)));
            }, {});
        },
        enumerable: true,
        configurable: true
    });
    PrivilegeRowComponent.prototype.onPrivilegeClicked = function (index) {
        this.privilegeCodeList[index] = !this.privilegeCodeList[index];
        var privilege = Object(_privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__["getPrivilegeFromBoolArray"])(this.privilegeCodeList);
        this.categoryChange.emit(privilege);
    };
    PrivilegeRowComponent.prototype.onAllClicked = function () {
        this.privilegeCodeList[8] = !this.privilegeCodeList[8];
        if (this.privilegeCodeList[8]) {
            this.privilegeCodeList = Object(_privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__["binaryString2BoolArray"])(_privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__["ALL_PRIVILEGES_STR"]);
        }
        var privilege = Object(_privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__["getPrivilegeFromBoolArray"])(this.privilegeCodeList);
        this.categoryChange.emit(privilege);
    };
    PrivilegeRowComponent.prototype.onAccessClicked = function () {
        this.privilegeCodeList[0] = !this.privilegeCodeList[0];
        var privilege = Object(_privilege_code_transformer__WEBPACK_IMPORTED_MODULE_2__["getPrivilegeFromBoolArray"])(this.privilegeCodeList);
        this.categoryChange.emit(privilege);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], PrivilegeRowComponent.prototype, "categoryChange", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])('subCategory'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object])
    ], PrivilegeRowComponent.prototype, "_subCategory", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])('allowedPrivileges'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Array])
    ], PrivilegeRowComponent.prototype, "_allowedPrivileges", null);
    PrivilegeRowComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            // tslint:disable-next-line:component-selector
            selector: 'tr[privilege-row]',
            template: __webpack_require__(/*! ./privilege-row.component.html */ "./src/app/modules/admin/privilege/privilege-row/privilege-row.component.html"),
            styles: [__webpack_require__(/*! ./privilege-row.component.scss */ "./src/app/modules/admin/privilege/privilege-row/privilege-row.component.scss")]
        })
    ], PrivilegeRowComponent);
    return PrivilegeRowComponent;
}());



/***/ }),

/***/ "./src/app/modules/admin/privilege/privilege.service.ts":
/*!**************************************************************!*\
  !*** ./src/app/modules/admin/privilege/privilege.service.ts ***!
  \**************************************************************/
/*! exports provided: PrivilegeService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PrivilegeService", function() { return PrivilegeService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var _main_view_admin_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../main-view/admin.service */ "./src/app/modules/admin/main-view/admin.service.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var _privilege_code_transformer__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./privilege-code-transformer */ "./src/app/modules/admin/privilege/privilege-code-transformer.ts");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");








var PrivilegeService = /** @class */ (function () {
    function PrivilegeService(_adminService, _toastMessage) {
        this._adminService = _adminService;
        this._toastMessage = _toastMessage;
    }
    PrivilegeService.prototype.getList = function (customerId) {
        var _this = this;
        return this.getAllPrivilegeMap()
            .toPromise()
            .then(function (privilegeMap) {
            return _this.getPrivilegeList(customerId, privilegeMap);
        });
    };
    PrivilegeService.prototype.getPrivilegeList = function (customerId, allowedPrivileges) {
        return this._adminService
            .request('privileges/fetch', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return resp.privileges; }), 
        /* Stored privilege description can get outdated, and may be wrong due
         * to previous bugs for some privileges.
         * Calculate and show descriptions on the fly instead of showing stored ones.
         */
        Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (privileges) {
            return (privileges || []).map(function (privilege) { return (tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, privilege, { privilegeDesc: Object(_privilege_code_transformer__WEBPACK_IMPORTED_MODULE_6__["getPrivilegeDescription"])(privilege.privilegeCode, allowedPrivileges[privilege.moduleName]) })); });
        }))
            .toPromise();
    };
    /**
     * Returns a map of all the allowed privileges by module.
     * Example: {
     *   ANALYZE: ['CREATE', 'EXPORT'],
     *   OBSERVE: ['FORK', 'EXPORT']
     * }
     *
     * @returns
     * @memberof PrivilegeService
     */
    PrivilegeService.prototype.getAllPrivilegeMap = function () {
        var _this = this;
        if (this.privilegeMap) {
            return Object(rxjs__WEBPACK_IMPORTED_MODULE_7__["of"])(this.privilegeMap);
        }
        return this.getModulePrivilegeMap('').pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) {
            _this.privilegeMap = (resp || []).reduce(function (accum, _a) {
                var moduleName = _a.moduleName, privilegeCodeName = _a.privilegeCodeName;
                accum[moduleName] = accum[moduleName] || [];
                accum[moduleName].push(privilegeCodeName);
                return accum;
            }, {});
            return _this.privilegeMap;
        }));
    };
    PrivilegeService.prototype.getModulePrivilegeMap = function (moduleId) {
        return this._adminService.getRequest("modules/module-privileges/" + moduleId, { forWhat: 'newScheme' });
    };
    PrivilegeService.prototype.getPrivilegesForModule = function (moduleName) {
        if (this.privilegeMap) {
            return Object(rxjs__WEBPACK_IMPORTED_MODULE_7__["of"])(this.privilegeMap[moduleName]);
        }
        return this.getAllPrivilegeMap().pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (privilegeMap) { return privilegeMap[moduleName]; }));
    };
    PrivilegeService.prototype.save = function (privilege) {
        var options = {
            toast: { successMsg: 'Privilege is successfully added' }
        };
        return this._adminService
            .request('privileges/upsert', privilege, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return (resp.valid ? resp.privileges : null); }))
            .toPromise();
    };
    PrivilegeService.prototype.remove = function (privilege) {
        var options = {
            toast: { successMsg: 'Privilege is successfully deleted' }
        };
        return this._adminService
            .request('privileges/delete', privilege, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return (resp.valid ? resp.privileges : null); }))
            .toPromise();
    };
    PrivilegeService.prototype.update = function (privilege) {
        var options = {
            toast: { successMsg: 'Privilege is successfully Updated' }
        };
        return this._adminService
            .request('privileges/upsert', privilege, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return (resp.valid ? resp.privileges : null); }))
            .toPromise();
    };
    PrivilegeService.prototype.getRoles = function (customerId) {
        return this._adminService
            .request('roles/list', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return resp.roles; }))
            .toPromise();
    };
    PrivilegeService.prototype.getProducts = function (customerId) {
        return this._adminService
            .request('products/list', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return resp.products; }))
            .toPromise();
    };
    PrivilegeService.prototype.getModules = function (params) {
        return this._adminService
            .request('modules/list', params)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return resp.modules; }))
            .toPromise();
    };
    PrivilegeService.prototype.getCategories = function (customerId) {
        return this._adminService
            .request('categories/list', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return resp.categories; }));
    };
    PrivilegeService.prototype.getParentCategories = function (params) {
        var _this = this;
        return this._adminService
            .request('categories/parent/list', params)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return resp.category; }), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["tap"])(function (categories) {
            if (lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__(categories)) {
                _this._toastMessage.error('There are no Categories for this Module');
            }
        }))
            .toPromise();
    };
    PrivilegeService.prototype.getSubCategories = function (params) {
        var _this = this;
        return this._adminService
            .request('subCategoriesWithPrivilege/list', params)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["map"])(function (resp) { return resp.subCategories; }), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_5__["tap"])(function (subCategories) {
            if (lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__(subCategories)) {
                _this._toastMessage.error('There are no Sub-Categories with Privilege');
            }
        }))
            .toPromise();
    };
    PrivilegeService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_main_view_admin_service__WEBPACK_IMPORTED_MODULE_3__["AdminService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_4__["ToastService"]])
    ], PrivilegeService);
    return PrivilegeService;
}());



/***/ }),

/***/ "./src/app/modules/admin/role/index.ts":
/*!*********************************************!*\
  !*** ./src/app/modules/admin/role/index.ts ***!
  \*********************************************/
/*! exports provided: RoleEditDialogComponent, RoleService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _role_dialog__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./role-dialog */ "./src/app/modules/admin/role/role-dialog/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RoleEditDialogComponent", function() { return _role_dialog__WEBPACK_IMPORTED_MODULE_0__["RoleEditDialogComponent"]; });

/* harmony import */ var _role_service__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./role.service */ "./src/app/modules/admin/role/role.service.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RoleService", function() { return _role_service__WEBPACK_IMPORTED_MODULE_1__["RoleService"]; });





/***/ }),

/***/ "./src/app/modules/admin/role/role-dialog/index.ts":
/*!*********************************************************!*\
  !*** ./src/app/modules/admin/role/role-dialog/index.ts ***!
  \*********************************************************/
/*! exports provided: RoleEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _role_edit_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./role-edit-dialog.component */ "./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "RoleEditDialogComponent", function() { return _role_edit_dialog_component__WEBPACK_IMPORTED_MODULE_0__["RoleEditDialogComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.html":
/*!********************************************************************************!*\
  !*** ./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.html ***!
  \********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<header class=\"base-dialog__header\">\n  <strong [ngSwitch]=\"data.mode\">\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Add New Role\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Edit Role\n    </ng-container>\n  </strong>\n</header>\n<div class=\"base-dialog__content\">\n  <form [formGroup]=\"formGroup\" fxLayout=\"column\" fxLayoutAlign=\"center start\">\n    <mat-form-field>\n      <input\n        matInput\n        autocomplete=\"off\"\n        required\n        type=\"text\"\n        formControlName=\"roleName\"\n        placeholder=\"Role name\"\n      />\n    </mat-form-field>\n\n    <mat-form-field>\n      <input\n        matInput\n        autocomplete=\"off\"\n        type=\"text\"\n        formControlName=\"roleDesc\"\n        placeholder=\"Description\"\n      />\n    </mat-form-field>\n\n    <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n      <mat-label i18n>Status</mat-label>\n      <mat-select class=\"form-field\" required formControlName=\"activeStatusInd\">\n        <mat-option *ngFor=\"let status of statuses\" [value]=\"status.id\">\n          {{ status.name }}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n\n    <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n      <mat-label i18n>Role Type</mat-label>\n      <mat-select class=\"form-field\" required formControlName=\"roleType\">\n        <mat-option\n          *ngFor=\"let roleType of (data.formDeps.roleTypes$ | async)\"\n          [value]=\"roleType.roleName\"\n        >\n          {{ roleType.roleName }}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n\n    <!-- <div *ngIf=\"data.mode === 'create'\">\n      Create default privileges for <strong>My Analyses</strong> Category\n      <mat-checkbox\n        formControlName=\"myAnalysis\"\n        matTooltip=\"Automatically create default privileges to the My Analyses category for any user assigned to this Role. At least one privilege per Role is needed for application access.\"\n      ></mat-checkbox>\n    </div> -->\n  </form>\n</div>\n\n<div\n  fxLayout=\"row\"\n  fxLayoutAlign=\"space-between center\"\n  class=\"base-dialog__actions\"\n>\n  <button\n    (click)=\"create()\"\n    [disabled]=\"!formIsValid\"\n    e2e=\"create-analysis-btn\"\n    color=\"primary\"\n    [ngSwitch]=\"data.mode\"\n    mat-raised-button\n  >\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Create Role\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Save\n    </ng-container>\n  </button>\n  <button mat-button mat-dialog-close i18n>Cancel</button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.scss":
/*!********************************************************************************!*\
  !*** ./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.scss ***!
  \********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  max-width: 500px; }\n\n.base-dialog__content {\n  padding: 10px 50px !important;\n  height: 260px;\n  width: 400px; }\n\n.base-dialog__content .mat-form-field {\n    width: 100%; }\n\n.base-dialog__content .form-field {\n    margin-bottom: 5px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL3JvbGUvcm9sZS1kaWFsb2cvcm9sZS1lZGl0LWRpYWxvZy5jb21wb25lbnQuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLGdCQUFnQixFQUFBOztBQUdsQjtFQUNFLDZCQUE2QjtFQUM3QixhQUFhO0VBQ2IsWUFBWSxFQUFBOztBQUhkO0lBTUksV0FBVyxFQUFBOztBQU5mO0lBVUksa0JBQWtCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FkbWluL3JvbGUvcm9sZS1kaWFsb2cvcm9sZS1lZGl0LWRpYWxvZy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbjpob3N0IHtcbiAgbWF4LXdpZHRoOiA1MDBweDtcbn1cblxuLmJhc2UtZGlhbG9nX19jb250ZW50IHtcbiAgcGFkZGluZzogMTBweCA1MHB4ICFpbXBvcnRhbnQ7XG4gIGhlaWdodDogMjYwcHg7XG4gIHdpZHRoOiA0MDBweDtcblxuICAubWF0LWZvcm0tZmllbGQge1xuICAgIHdpZHRoOiAxMDAlO1xuICB9XG5cbiAgLmZvcm0tZmllbGQge1xuICAgIG1hcmdpbi1ib3R0b206IDVweDtcbiAgfVxufVxuIl19 */"

/***/ }),

/***/ "./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.ts":
/*!******************************************************************************!*\
  !*** ./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.ts ***!
  \******************************************************************************/
/*! exports provided: RoleEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RoleEditDialogComponent", function() { return RoleEditDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var _role_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../role.service */ "./src/app/modules/admin/role/role.service.ts");
/* harmony import */ var _common_base_dialog__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../common/base-dialog */ "./src/app/common/base-dialog/index.ts");






var namePattern = /^[a-zA-Z]*$/;
var RoleEditDialogComponent = /** @class */ (function (_super) {
    tslib__WEBPACK_IMPORTED_MODULE_0__["__extends"](RoleEditDialogComponent, _super);
    function RoleEditDialogComponent(_roleService, _fb, _dialogRef, data) {
        var _this = _super.call(this) || this;
        _this._roleService = _roleService;
        _this._fb = _fb;
        _this._dialogRef = _dialogRef;
        _this.data = data;
        _this.formIsValid = false;
        _this.statuses = [
            {
                id: 1,
                value: 'Active',
                name: 'ACTIVE'
            },
            {
                id: 0,
                value: 'Inactive',
                name: 'INACTIVE'
            }
        ];
        if (_this.data.mode === 'edit') {
            _this.formIsValid = true;
        }
        _this.createForm(_this.data.model);
        return _this;
    }
    RoleEditDialogComponent.prototype.create = function () {
        var _this = this;
        var formValues = this.formGroup.getRawValue();
        var model = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, this.data.model, formValues);
        var actionPromise;
        switch (this.data.mode) {
            case 'edit':
                actionPromise = this._roleService.update(model);
                break;
            case 'create':
                actionPromise = this._roleService.save(model);
                break;
        }
        actionPromise &&
            actionPromise.then(function (rows) {
                if (rows) {
                    _this._dialogRef.close(rows);
                }
            });
    };
    RoleEditDialogComponent.prototype.createForm = function (formModel) {
        var _this = this;
        var mode = this.data.mode;
        if (mode === 'edit') {
            formModel.activeStatusInd =
                formModel.activeStatusInd === 'Active' ? 1 : 0;
        }
        var _a = formModel.roleName, roleName = _a === void 0 ? '' : _a, _b = formModel.roleDesc, roleDesc = _b === void 0 ? '' : _b, _c = formModel.activeStatusInd, activeStatusInd = _c === void 0 ? 1 : _c, _d = formModel.roleType
        // myAnalysis
        , roleType = _d === void 0 ? '' : _d
        // myAnalysis
        ;
        this.formGroup = this._fb.group({
            roleName: [
                roleName,
                [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].pattern(namePattern)]
            ],
            roleDesc: roleDesc,
            activeStatusInd: [activeStatusInd, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required],
            roleType: [roleType, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required],
            myAnalysis: true
        });
        this.formGroup.statusChanges.subscribe(function (change) {
            if (change === 'VALID') {
                _this.formIsValid = true;
            }
            else {
                _this.formIsValid = false;
            }
        });
    };
    RoleEditDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'role-edit-dialog',
            template: __webpack_require__(/*! ./role-edit-dialog.component.html */ "./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.html"),
            styles: [__webpack_require__(/*! ./role-edit-dialog.component.scss */ "./src/app/modules/admin/role/role-dialog/role-edit-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](3, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_role_service__WEBPACK_IMPORTED_MODULE_4__["RoleService"],
            _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], RoleEditDialogComponent);
    return RoleEditDialogComponent;
}(_common_base_dialog__WEBPACK_IMPORTED_MODULE_5__["BaseDialogComponent"]));



/***/ }),

/***/ "./src/app/modules/admin/role/role.service.ts":
/*!****************************************************!*\
  !*** ./src/app/modules/admin/role/role.service.ts ***!
  \****************************************************/
/*! exports provided: RoleService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RoleService", function() { return RoleService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _main_view_admin_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../main-view/admin.service */ "./src/app/modules/admin/main-view/admin.service.ts");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");




var RoleService = /** @class */ (function () {
    function RoleService(_adminService) {
        this._adminService = _adminService;
    }
    RoleService.prototype.getList = function (customerId) {
        return this._adminService
            .request('roles/fetch', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return resp.roles; }))
            .toPromise();
    };
    RoleService.prototype.save = function (user) {
        var options = {
            toast: { successMsg: 'Role is successfully added' }
        };
        return this._adminService
            .request('roles/add', user, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return (resp.valid ? resp.roles : null); }))
            .toPromise();
    };
    RoleService.prototype.remove = function (user) {
        var options = {
            toast: { successMsg: 'Role is successfully deleted' }
        };
        return this._adminService
            .request('roles/delete', user, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return (resp.valid ? resp.roles : null); }))
            .toPromise();
    };
    RoleService.prototype.update = function (user) {
        var options = {
            toast: { successMsg: 'Role is successfully Updated' }
        };
        return this._adminService
            .request('roles/edit', user, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return (resp.valid ? resp.roles : null); }))
            .toPromise();
    };
    RoleService.prototype.getRoleTypes = function (customerId) {
        return this._adminService
            .request('roles/types/list', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return resp.roles; }));
    };
    RoleService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_main_view_admin_service__WEBPACK_IMPORTED_MODULE_2__["AdminService"]])
    ], RoleService);
    return RoleService;
}());



/***/ }),

/***/ "./src/app/modules/admin/routes.ts":
/*!*****************************************!*\
  !*** ./src/app/modules/admin/routes.ts ***!
  \*****************************************/
/*! exports provided: routes */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "routes", function() { return routes; });
/* harmony import */ var _page__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./page */ "./src/app/modules/admin/page/index.ts");
/* harmony import */ var _main_view__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./main-view */ "./src/app/modules/admin/main-view/index.ts");
/* harmony import */ var _export__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./export */ "./src/app/modules/admin/export/index.ts");
/* harmony import */ var _import__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./import */ "./src/app/modules/admin/import/index.ts");
/* harmony import */ var _datasecurity_security_group_security_group_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./datasecurity/security-group/security-group.component */ "./src/app/modules/admin/datasecurity/security-group/security-group.component.ts");
/* harmony import */ var _guards__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./guards */ "./src/app/modules/admin/guards/index.ts");
/* harmony import */ var _consts__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./consts */ "./src/app/modules/admin/consts.ts");







var routes = [
    {
        // name: 'admin',
        path: '',
        component: _page__WEBPACK_IMPORTED_MODULE_0__["AdminPageComponent"],
        canActivate: [_guards__WEBPACK_IMPORTED_MODULE_5__["IsAdminGuard"]],
        canActivateChild: [_guards__WEBPACK_IMPORTED_MODULE_5__["IsAdminGuard"]],
        runGuardsAndResolvers: 'paramsOrQueryParamsChange',
        children: [
            {
                // name: 'admin.user',
                path: 'user',
                component: _main_view__WEBPACK_IMPORTED_MODULE_1__["AdminMainViewComponent"],
                data: {
                    columns: _consts__WEBPACK_IMPORTED_MODULE_6__["UsersTableHeader"],
                    section: 'user'
                }
            },
            {
                // name: 'admin.role',
                path: 'role',
                component: _main_view__WEBPACK_IMPORTED_MODULE_1__["AdminMainViewComponent"],
                data: {
                    columns: _consts__WEBPACK_IMPORTED_MODULE_6__["RolesTableHeader"],
                    section: 'role'
                }
            },
            {
                // name: 'admin.categories',
                path: 'categories',
                component: _main_view__WEBPACK_IMPORTED_MODULE_1__["AdminMainViewComponent"],
                data: {
                    columns: _consts__WEBPACK_IMPORTED_MODULE_6__["CategoriesTableHeader"],
                    section: 'category'
                }
            },
            {
                // name: 'admin.privilege',
                path: 'privilege',
                component: _main_view__WEBPACK_IMPORTED_MODULE_1__["AdminMainViewComponent"],
                data: {
                    columns: _consts__WEBPACK_IMPORTED_MODULE_6__["PrivilegesTableHeader"],
                    section: 'privilege'
                }
            },
            {
                // name: 'admin.export',
                path: 'export',
                component: _export__WEBPACK_IMPORTED_MODULE_2__["AdminExportViewComponent"]
            },
            {
                // name: 'admin.import',
                path: 'import',
                component: _import__WEBPACK_IMPORTED_MODULE_3__["AdminImportViewComponent"]
            },
            {
                // name: 'admin.import',
                path: 'securitygroups',
                component: _datasecurity_security_group_security_group_component__WEBPACK_IMPORTED_MODULE_4__["SecurityGroupComponent"]
            },
            {
                path: 'userassignments',
                component: _main_view__WEBPACK_IMPORTED_MODULE_1__["AdminMainViewComponent"],
                data: {
                    columns: _consts__WEBPACK_IMPORTED_MODULE_6__["UserAssignmentsTableHeader"],
                    section: 'user assignments'
                }
            },
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'user'
            }
        ]
    }
];


/***/ }),

/***/ "./src/app/modules/admin/state/admin.state.ts":
/*!****************************************************!*\
  !*** ./src/app/modules/admin/state/admin.state.ts ***!
  \****************************************************/
/*! exports provided: AdminState */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AdminState", function() { return AdminState; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _ngxs_store__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @ngxs/store */ "./node_modules/@ngxs/store/fesm5/ngxs-store.js");
/* harmony import */ var _export__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../export */ "./src/app/modules/admin/export/index.ts");
/* harmony import */ var _import__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../import */ "./src/app/modules/admin/import/index.ts");




var AdminState = /** @class */ (function () {
    function AdminState() {
    }
    AdminState = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["State"])({
            name: 'admin',
            children: [_export__WEBPACK_IMPORTED_MODULE_2__["ExportPageState"], _import__WEBPACK_IMPORTED_MODULE_3__["AdminImportPageState"]]
        })
    ], AdminState);
    return AdminState;
}());



/***/ }),

/***/ "./src/app/modules/admin/user/edit-dialog/index.ts":
/*!*********************************************************!*\
  !*** ./src/app/modules/admin/user/edit-dialog/index.ts ***!
  \*********************************************************/
/*! exports provided: UserEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _user_edit_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./user-edit-dialog.component */ "./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "UserEditDialogComponent", function() { return _user_edit_dialog_component__WEBPACK_IMPORTED_MODULE_0__["UserEditDialogComponent"]; });




/***/ }),

/***/ "./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.html":
/*!********************************************************************************!*\
  !*** ./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.html ***!
  \********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<header class=\"base-dialog__header\">\n  <strong [ngSwitch]=\"data.mode\">\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Add New User\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Edit User\n    </ng-container>\n  </strong>\n</header>\n<div class=\"base-dialog__content\">\n  <form [formGroup]=\"formGroup\" fxLayout=\"column\" fxLayoutAlign=\"start center\">\n\n    <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n      <mat-label>Role</mat-label>\n      <mat-select required formControlName=\"roleId\">\n        <mat-option *ngFor=\"let role of data.formDeps.roles$ | async\" [value]=\"role.roleId\">\n          {{role.roleName}}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n\n    <mat-form-field>\n      <input matInput required type=\"text\" autocomplete=\"off\" formControlName=\"firstName\" placeholder=\"First name\"/>\n    </mat-form-field>\n    <mat-form-field>\n      <input matInput type=\"text\" autocomplete=\"off\" formControlName=\"middleName\" placeholder=\"Middle name\"/>\n    </mat-form-field>\n    <mat-form-field>\n      <input matInput required type=\"text\" autocomplete=\"off\" formControlName=\"lastName\" placeholder=\"Last name\"/>\n    </mat-form-field>\n    <mat-form-field>\n      <input matInput required type=\"text\" autocomplete=\"off\" formControlName=\"masterLoginId\" placeholder=\"Login Id\"/>\n    </mat-form-field>\n    <mat-form-field>\n      <input matInput\n             [required]=\"data.mode === 'create'\"\n             type=\"password\"\n             (focus)=\"onPasswordFocus($event)\"\n             (blur)=\"onPasswordBlur($event)\"\n             formControlName=\"password\"\n             placeholder=\"Password\"\n      />\n    </mat-form-field>\n    <mat-form-field>\n      <input matInput required type=\"email\" formControlName=\"email\" placeholder=\"Email\"/>\n    </mat-form-field>\n\n    <mat-form-field class=\"select-form-field\" appearance=\"outline\">\n      <mat-label>Status</mat-label>\n      <mat-select required formControlName=\"activeStatusInd\">\n        <mat-option *ngFor=\"let status of statuses\" [value]=\"status.id\">\n          {{status.name}}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n\n  </form>\n</div>\n\n<div fxLayout=\"row\" fxLayoutAlign=\"space-between center\" class=\"base-dialog__actions\">\n  <button (click)=\"create()\"\n          [disabled]=\"!formIsValid\"\n          e2e=\"create-analysis-btn\"\n          color=\"primary\"\n          [ngSwitch]=\"data.mode\"\n          mat-raised-button\n  >\n    <ng-container *ngSwitchCase=\"'create'\" i18n>\n      Create User\n    </ng-container>\n    <ng-container *ngSwitchCase=\"'edit'\" i18n>\n      Save\n    </ng-container>\n  </button>\n  <button mat-button\n          mat-dialog-close i18n\n  >Cancel</button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.scss":
/*!********************************************************************************!*\
  !*** ./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.scss ***!
  \********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  max-width: 500px; }\n\n.base-dialog__content {\n  padding: 10px 50px !important;\n  width: 400px; }\n\n.base-dialog__content .mat-form-field {\n    width: 100%; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FkbWluL3VzZXIvZWRpdC1kaWFsb2cvdXNlci1lZGl0LWRpYWxvZy5jb21wb25lbnQuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLGdCQUFnQixFQUFBOztBQUdsQjtFQUNFLDZCQUE2QjtFQUM3QixZQUFZLEVBQUE7O0FBRmQ7SUFLSSxXQUFXLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FkbWluL3VzZXIvZWRpdC1kaWFsb2cvdXNlci1lZGl0LWRpYWxvZy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbjpob3N0IHtcbiAgbWF4LXdpZHRoOiA1MDBweDtcbn1cblxuLmJhc2UtZGlhbG9nX19jb250ZW50IHtcbiAgcGFkZGluZzogMTBweCA1MHB4ICFpbXBvcnRhbnQ7XG4gIHdpZHRoOiA0MDBweDtcblxuICAubWF0LWZvcm0tZmllbGQge1xuICAgIHdpZHRoOiAxMDAlO1xuICB9XG59XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.ts":
/*!******************************************************************************!*\
  !*** ./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.ts ***!
  \******************************************************************************/
/*! exports provided: UserEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UserEditDialogComponent", function() { return UserEditDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var _user_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../user.service */ "./src/app/modules/admin/user/user.service.ts");
/* harmony import */ var _common_base_dialog__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../../common/base-dialog */ "./src/app/common/base-dialog/index.ts");







var namePattern = /^[a-zA-Z]*$/;
var loginIdPattern = /^[A-z\d_@.#$=!%^)(\]:\*;\?\/\,}{'\|<>\[&\+-`~]*$/;
var dummyPassword = '*********';
var UserEditDialogComponent = /** @class */ (function (_super) {
    tslib__WEBPACK_IMPORTED_MODULE_0__["__extends"](UserEditDialogComponent, _super);
    function UserEditDialogComponent(_userService, _fb, _dialogRef, data) {
        var _this = _super.call(this) || this;
        _this._userService = _userService;
        _this._fb = _fb;
        _this._dialogRef = _dialogRef;
        _this.data = data;
        _this.formIsValid = false;
        _this.statuses = [
            {
                id: 1,
                value: 'Active',
                name: 'ACTIVE'
            },
            {
                id: 0,
                value: 'Inactive',
                name: 'INACTIVE'
            }
        ];
        if (_this.data.mode === 'edit') {
            _this.formIsValid = true;
        }
        _this.createForm(_this.data.model);
        return _this;
    }
    UserEditDialogComponent.prototype.create = function () {
        var _this = this;
        var formValues = this.formGroup.getRawValue();
        // if the password wasn't changed, set it to null
        if (this.data.mode === 'edit' && formValues.password === dummyPassword) {
            formValues.password = null;
        }
        var model = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, this.data.model, formValues);
        var actionPromise;
        switch (this.data.mode) {
            case 'edit':
                actionPromise = this._userService.update(model);
                break;
            case 'create':
                actionPromise = this._userService.save(model);
                break;
        }
        actionPromise &&
            actionPromise.then(function (rows) {
                if (rows) {
                    _this._dialogRef.close(rows);
                }
            });
    };
    UserEditDialogComponent.prototype.onPasswordFocus = function (event) {
        if (this.data.mode === 'edit' && event.target.value === dummyPassword) {
            var password = '';
            this.formGroup.patchValue({ password: password });
        }
    };
    UserEditDialogComponent.prototype.onPasswordBlur = function (event) {
        if (this.data.mode === 'edit' && event.target.value === '') {
            var password = dummyPassword;
            this.formGroup.patchValue({ password: password });
        }
    };
    UserEditDialogComponent.prototype.createForm = function (model) {
        var _this = this;
        var mode = this.data.mode;
        if (mode === 'edit') {
            model.activeStatusInd = model.activeStatusInd === 'Active' ? 1 : 0;
        }
        var _a = model.roleId, roleId = _a === void 0 ? '' : _a, _b = model.activeStatusInd, activeStatusInd = _b === void 0 ? 1 : _b, _c = model.masterLoginId, masterLoginId = _c === void 0 ? '' : _c, _d = model.firstName, firstName = _d === void 0 ? '' : _d, _e = model.lastName, lastName = _e === void 0 ? '' : _e, _f = model.middleName, middleName = _f === void 0 ? '' : _f, _g = model.email, email = _g === void 0 ? '' : _g;
        var firstNameControl = this._fb.control(firstName, [
            _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required,
            _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].pattern(namePattern)
        ]);
        var lastNameControl = this._fb.control(lastName, [
            _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required,
            _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].pattern(namePattern)
        ]);
        var passwordValue = mode === 'edit' ? dummyPassword : '';
        var passwordControl = this._fb.control(passwordValue, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required);
        this.formGroup = this._fb.group({
            roleId: [roleId, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required],
            middleName: middleName,
            firstName: firstNameControl,
            lastName: lastNameControl,
            masterLoginId: [
                masterLoginId,
                [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].pattern(loginIdPattern)]
            ],
            password: passwordControl,
            email: [email, [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].email]],
            activeStatusInd: [activeStatusInd, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required]
        });
        // combine firstname and lastName into masterLoginId
        Object(rxjs__WEBPACK_IMPORTED_MODULE_4__["combineLatest"])(firstNameControl.valueChanges, lastNameControl.valueChanges).subscribe(function (_a) {
            var first = _a[0], last = _a[1];
            var masterLoginIdValue = first + "." + last;
            _this.formGroup.patchValue({ masterLoginId: masterLoginIdValue });
        });
        // enable disable the create user/ save button
        this.formGroup.statusChanges.subscribe(function (change) {
            if (change === 'VALID') {
                _this.formIsValid = true;
            }
            else {
                _this.formIsValid = false;
            }
        });
    };
    UserEditDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'user-edit-dialog',
            template: __webpack_require__(/*! ./user-edit-dialog.component.html */ "./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.html"),
            styles: [__webpack_require__(/*! ./user-edit-dialog.component.scss */ "./src/app/modules/admin/user/edit-dialog/user-edit-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](3, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_user_service__WEBPACK_IMPORTED_MODULE_5__["UserService"],
            _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], UserEditDialogComponent);
    return UserEditDialogComponent;
}(_common_base_dialog__WEBPACK_IMPORTED_MODULE_6__["BaseDialogComponent"]));



/***/ }),

/***/ "./src/app/modules/admin/user/index.ts":
/*!*********************************************!*\
  !*** ./src/app/modules/admin/user/index.ts ***!
  \*********************************************/
/*! exports provided: UserService, UserEditDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _user_service__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./user.service */ "./src/app/modules/admin/user/user.service.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "UserService", function() { return _user_service__WEBPACK_IMPORTED_MODULE_0__["UserService"]; });

/* harmony import */ var _edit_dialog__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./edit-dialog */ "./src/app/modules/admin/user/edit-dialog/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "UserEditDialogComponent", function() { return _edit_dialog__WEBPACK_IMPORTED_MODULE_1__["UserEditDialogComponent"]; });





/***/ }),

/***/ "./src/app/modules/admin/user/user.service.ts":
/*!****************************************************!*\
  !*** ./src/app/modules/admin/user/user.service.ts ***!
  \****************************************************/
/*! exports provided: UserService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UserService", function() { return UserService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _main_view_admin_service__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../main-view/admin.service */ "./src/app/modules/admin/main-view/admin.service.ts");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");




var UserService = /** @class */ (function () {
    function UserService(_adminService) {
        this._adminService = _adminService;
    }
    UserService.prototype.getList = function (customerId) {
        return this._adminService
            .request('users/fetch', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return resp.users; }))
            .toPromise();
    };
    UserService.prototype.save = function (user) {
        var options = {
            toast: { successMsg: 'User is successfully added' }
        };
        return this._adminService
            .request('users/add', user, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return (resp.valid ? resp.users : null); }))
            .toPromise();
    };
    UserService.prototype.remove = function (user) {
        var options = {
            toast: { successMsg: 'User is successfully deleted' }
        };
        return this._adminService
            .request('users/delete', user, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return (resp.valid ? resp.users : null); }))
            .toPromise();
    };
    UserService.prototype.update = function (user) {
        var options = {
            toast: { successMsg: 'User is successfully Updated' }
        };
        return this._adminService
            .request('users/edit', user, options)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return (resp.valid ? resp.users : null); }))
            .toPromise();
    };
    UserService.prototype.getUserRoles = function (customerId) {
        return this._adminService
            .request('roles/list', customerId)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (resp) { return resp.roles; }));
    };
    UserService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_main_view_admin_service__WEBPACK_IMPORTED_MODULE_2__["AdminService"]])
    ], UserService);
    return UserService;
}());



/***/ })

}]);
//# sourceMappingURL=modules-admin-admin-module.js.map