'use strict';

const commonFunctions = require('../pages/utils/commonFunctions');
const GlobalFilters = require('../pages/components/GlobalFilters');

class DashboardHeader extends GlobalFilters {
	constructor() {
		super();
		this._addDashboardButton = element(by.css(`[e2e='dashboard-new-dashboard-button']`));
		this._manualRefreshButton = element(by.css(`[e2e='dashboard-manual-refresh-button']`));
		this._deleteDashboardButton = element(by.css(`[e2e='dashboard-delete-dashboard-button']`));
		this._downloadDashboardButton = element(by.css(`[e2e='dashboard-download-dashboard-button']`));
		this._editDashboardButton = element(by.css(`[e2e='dashboard-edit-dashboard-button']`));
		this._dashboardAction = (action) => element(by.xpath(`//span[contains(text(),"${action}")]`));
		this._openGlobalFilterButton = element(by.css(`[e2e='dashboard-open-global-filters-button']`));
	}

	clickOnAddDashboardButton() {
		commonFunctions.clickOnElement(this._addDashboardButton);
	}

	clickOnManualRefreshButton() {
		commonFunctions.clickOnElement(this._manualRefreshButton);
	}

	clickOnDeleteDashboardButton() {
		commonFunctions.clickOnElement(this._deleteDashboardButton);
	}

	clickOnDownloadDashboardButton() {
		commonFunctions.clickOnElement(this._downloadDashboardButton);
	}

	clickOnEditDashboardButton() {
		commonFunctions.clickOnElement(this._editDashboardButton);
	}

	clickOnOpenGlobalFilterButton() {
		commonFunctions.clickOnElement(this._openGlobalFilterButton);
	}

	displayDashboardAction(text) {
		expect(this._dashboardAction(text).isDisplayed).toBeTruthy();
	}
}

module.exports = DashboardHeader;
