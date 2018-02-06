import AnimatedCollapseDirective from './animatedCollapse.directive';
import EvalAttrAsExprDirective from './evalAttrAsExpr.directive';
import SidenavTargetDirective from './sidenav-target.directive';
import ToggleClassDirective from './toggleClass.directive';

export const CommonDirectiveModule = 'CommonModule.Directive';

angular
  .module(CommonDirectiveModule, [])
  .directive('collapse', AnimatedCollapseDirective)
  .directive('evalAttrAsExpr', EvalAttrAsExprDirective)
  .directive('sidenavTarget', SidenavTargetDirective)
  .directive('toggleClass', ToggleClassDirective);
