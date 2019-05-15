const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const users = require('../../helpers/data-generation/users');
const Constants = require('../../helpers/Constants');
const Utils = require('../../helpers/Utils');

const commonFunctions = require('../../pages/utils/commonFunctions');
const DataSourcesPage = require('../../pages/workbench/DataSourcesPage');
const ChannelActions = require('../../pages/workbench/ChannelActions');
const Header = require('../../pages/components/Header');
const LoginPage = require('../../pages/LoginPage');
const logger = require('../../conf/logger')(__filename);

describe('BIS tests: activateDeActivateChannel.test.js', () => {
  beforeAll(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL =
      protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(function(done) {
    setTimeout(function() {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });
  using(
    testDataReader.testData['BIS']['deActivateChannel']
      ? testDataReader.testData['BIS']['deActivateChannel']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.warn(`Running testCase with id: ${data.id}`);
        logger.warn(`Data: ` + JSON.stringify(data));
        const channelName = `${data.channelName}${Utils.randomId()}`;
        const channelDescription = `${
          data.channelName
        } description created at ${Utils.randomId()}`;

        let channelInfo = {
          sourceType: data.sourceType,
          channelName,
          access: data.accessType,
          sftpHost: Constants.SFTP_DETAILS.sftpHost,
          sftpPort: Constants.SFTP_DETAILS.sftpPort,
          sftpUser: Constants.SFTP_DETAILS.sftpUser,
          sftpPwd: Constants.SFTP_DETAILS.sftpPassword,
          desc: channelDescription,
          created: users.admin.firstName + ' ' + users.admin.lastName,
          status: data.status
        };

        const loginPage = new LoginPage();
        loginPage.loginAs(data.user);

        const header = new Header();
        header.clickOnLauncher();
        header.clickOnWorkBench();

        const dataSourcesPage = new DataSourcesPage();
        dataSourcesPage.clickOnAddChannelButton();

        const channelActions = new ChannelActions();
        // Create new channel
        channelActions.createNewChannel(channelInfo);
        dataSourcesPage.clickOnCreatedChannelName(channelInfo.channelName);
        dataSourcesPage.deActivateChannel(channelInfo.channelName);
        dataSourcesPage.clickOnDeleteChannel();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyChannelNotDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'BIS',
        dataProvider: 'deActivateChannel'
      };
    }
  );
});
