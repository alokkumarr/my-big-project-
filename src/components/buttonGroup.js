angular.module('sync.components')
    .directive('mdButtonGroup', mdButtonGroupDirective);

function mdButtonGroupDirective($mdUtil, $mdConstant, $mdTheming, $timeout) {
    ButtonGroupController.prototype = createButtonGroupControllerProto();

    return {
        restrict: 'E',
        controller: ['$element', ButtonGroupController],
        require: ['mdButtonGroup'],
        link: { pre: linkButtonGroup }
    };

    function linkButtonGroup(scope, element, attr, ctrls) {
        element.addClass('_md');     // private md component indicator for styling
        $mdTheming(element);

        var rgCtrl = ctrls[0];
        scope.mouseActive = false;
        scope.activeElement = null;

        rgCtrl.selectNext();

        element
            .attr({
                'role': 'group',
                'tabIndex': element.attr('tabindex') || '0'
            })
            .on('keydown', keydownListener)
            .on('mousedown', function(event) {
                scope.mouseActive = true;
                $timeout(function() {
                    scope.mouseActive = false;
                }, 100);
            })
            .on('focus', function() {
                if(scope.mouseActive === false) {
                    rgCtrl.$element.addClass('md-focused');
                }
            })
            .on('click', function (ev) {
                var match = 'button.md-button';
                var el;

                if (ev.target) {
                    if (ev.target.matches(match)) {
                        el = ev.target;
                    } else if (ev.target.parentElement.matches(match)) {
                        el = ev.target.parentElement;
                    }

                    rgCtrl.select(el);
                }
            })
            .on('blur', function() {
                rgCtrl.$element.removeClass('md-focused');
            });

        /**
         *
         */
        function setFocus() {
            if (!element.hasClass('md-focused')) { element.addClass('md-focused'); }
        }

        /**
         *
         */
        function keydownListener(ev) {
            var keyCode = ev.which || ev.keyCode;

            // Only listen to events that we originated ourselves
            // so that we don't trigger on things like arrow keys in
            // inputs.

            // if (keyCode != $mdConstant.KEY_CODE.ENTER &&
            //     ev.currentTarget != ev.target) {
            //     return;
            // }

            switch (keyCode) {
                case $mdConstant.KEY_CODE.LEFT_ARROW:
                case $mdConstant.KEY_CODE.UP_ARROW:
                    ev.preventDefault();
                    rgCtrl.selectPrevious();
                    setFocus();
                    break;

                case $mdConstant.KEY_CODE.RIGHT_ARROW:
                case $mdConstant.KEY_CODE.DOWN_ARROW:
                    ev.preventDefault();
                    rgCtrl.selectNext();
                    setFocus();
                    break;
            }

        }
    }

    function ButtonGroupController($element) {
        this.$element = $element;
    }

    function createButtonGroupControllerProto() {
        return {
            selectNext: function() {
                var el = changeSelectedButton(this.$element, 1);

                this.select(el);
            },
            selectPrevious: function() {
                var el = changeSelectedButton(this.$element, -1);

                this.select(el);
            },

            select: function (el) {
                var activeCls = 'md-active';

                if (el) {
                    el = angular.element(el);
                    el.addClass(activeCls);

                    if (this.activeElement && this.activeElement[0] !== el[0]) {
                        this.activeElement.removeClass(activeCls);
                    }

                    this.activeElement = el;
                }
            }

            // setActiveDescendant: function (radioId) {
            //     this.$element.attr('aria-activedescendant', radioId);
            // }
        };
    }
    /**
     * Change the radio group's selected button by a given increment.
     * If no button is selected, select the first button.
     */
    function changeSelectedButton(parent, increment) {
        // Coerce all child radio buttons into an array, then wrap then in an iterator
        var buttons = $mdUtil.iterator(parent[0].querySelectorAll('.md-button'), true);
        var sel;

        if (buttons.count()) {
            var validate = function (button) {
                // If disabled, then NOT valid
                return !angular.element(button).attr("disabled");
            };

            var selected = parent[0].querySelector('.md-button.md-active');
            var target = buttons[increment < 0 ? 'previous' : 'next'](selected, validate) || buttons.first();

            // Activate radioButton's click listener (triggerHandler won't create a real click event)
            sel = angular.element(target);
            sel.triggerHandler('click');

            return sel;
        }
    }

}
