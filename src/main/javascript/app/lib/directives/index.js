import AnimatedCollapseDirective from './animatedCollapse.directive';
import EvalAttrAsExprDirective from './evalAttrAsExpr.directive';
import SidenavTargetDirective from './sidenav-target.directive';
import ToggleClassDirective from './toggleClass.directive';

export const DirectivesModule = 'DirectivesModule';

angular
  .module(DirectivesModule, [])
  .directive('collapse', AnimatedCollapseDirective)
  .directive('evalAttrAsExpr', EvalAttrAsExprDirective)
  .directive('sidenavTarget', SidenavTargetDirective)
  .directive('toggleClass', ToggleClassDirective);
