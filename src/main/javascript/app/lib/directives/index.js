import AnimatedCollapseDirective from './animatedCollapse.directive';
import ToggleClassDirective from './toggleClass.directive';
import EvalAttrAsExprDirective from './evalAttrAsExpr.directive';
import HighlightContainerDirective from './highlight.directive';
import SidenavTargetDirective from './sidenav-target.directive';

export const DirectivesModule = 'DirectivesModule';

angular
  .module(DirectivesModule, [])
  .directive('collapse', AnimatedCollapseDirective)
  .directive('toggleClass', ToggleClassDirective)
  .directive('evalAttrAsExpr', EvalAttrAsExprDirective)
  .directive('highlightContainer', HighlightContainerDirective)
  .directive('sidenavTarget', SidenavTargetDirective);
