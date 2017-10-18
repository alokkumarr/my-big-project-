import angular from 'angular';
import template from './modals.component.html';
import customDialogTemplate from './custom-dialog.html';
import tabsDialogTemplate from './tabs-dialog.html';
import {DialogController} from './dialog.controller';

export const ModalsComponent = {
  template,
  controller: class ModalsController {
    constructor($mdDialog, $document) {
      'ngInject';
      this.$mdDialog = $mdDialog;
      this.$document = $document;
      this.customFullscreen = false;
      this.listPrerenderedButton = false;
      this.status = '';
    }

    showAlert(ev) {
      // Appending dialog to this.$document.body to cover sidenav in docs app
      // Modal dialogs should fully cover application
      // to prevent interaction outside of dialog
      this.$mdDialog.show(
        this.$mdDialog.alert()
          .parent(angular.element(this.$document.find('#popupContainer')))
          .clickOutsideToClose(true)
          .title('This is an alert title')
          .textContent('You can specify some description text in here.')
          .ariaLabel('Alert Dialog Demo')
          .ok('Got it!')
          .targetEvent(ev)
      );
    }

    showConfirm(ev) {
      // Appending dialog to this.$document.body to cover sidenav in docs app
      const confirm = this.$mdDialog.confirm()
        .title('Would you like to delete your debt?')
        .textContent('All of the banks have agreed to forgive you your debts.')
        .ariaLabel('Lucky day')
        .targetEvent(ev)
        .ok('Please do it!')
        .cancel('Sounds like a scam');

      this.$mdDialog.show(confirm).then(() => {
        this.status = 'You decided to get rid of your debt.';
      }, () => {
        this.status = 'You decided to keep your debt.';
      });
    }

    showPrompt(ev) {
      // Appending dialog to this.$document.body to cover sidenav in docs app
      const confirm = this.$mdDialog.prompt()
        .title('What would you name your dog?')
        .textContent('Bowser is a common name.')
        .placeholder('Dog name')
        .ariaLabel('Dog name')
        .initialValue('Buddy')
        .targetEvent(ev)
        .ok('Okay!')
        .cancel('I\'m a cat person');

      this.$mdDialog.show(confirm).then(result => {
        this.status = `You decided to name your dog ${result}.`;
      }, () => {
        this.status = 'You didn\'t name your dog.';
      });
    }

    showAdvanced(ev) {
      this.$mdDialog.show({
        controller: DialogController,
        controllerAs: '$ctrl',
        template: customDialogTemplate,
        parent: angular.element(this.$document.body),
        targetEvent: ev,
        clickOutsideToClose: true,
        fullscreen: this.customFullscreen // Only for -xs, -sm breakpoints.
      })
        .then(answer => {
          this.status = `You said the information was "${answer}".`;
        }, () => {
          this.status = 'You cancelled the dialog.';
        });
    }

    showTabDialog(ev) {
      this.$mdDialog.show({
        controller: DialogController,
        controllerAs: '$ctrl',
        template: tabsDialogTemplate,
        parent: angular.element(this.$document.body),
        targetEvent: ev,
        clickOutsideToClose: true
      })
        .then(answer => {
          this.status = `You said the information was "${answer}".`;
        }, () => {
          this.status = 'You cancelled the dialog.';
        });
    }

    showPrerenderedDialog(ev) {
      this.$mdDialog.show({
        controller: DialogController,
        controllerAs: '$ctrl',
        contentElement: '#myDialog',
        parent: angular.element(this.$document.body),
        targetEvent: ev,
        clickOutsideToClose: true
      });
    }
  }
};
