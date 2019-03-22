const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../../protractor.conf');
const users = require('../../../v2/helpers/data-generation/users');
const Constants = require('../../../v2/helpers/Constants');
const Utils = require('../../../v2/helpers/Utils');

const commonFunctions = require('../../../v2/pages/utils/commonFunctions');
const DataSourcesPage = require('../../../v2/pages/workbench/DataSourcesPage');
const ChannelActions = require('../../../v2/pages/workbench/ChannelActions');
const Header = require('../../../v2/pages/components/Header');
const LoginPage = require('../../../v2/pages/LoginPage');
const logger = require('../../../v2/conf/logger')(__filename);

describe('Workbench tests: activateDeActivateChannel.test.js', () => {
  beforeAll(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL =
      protractorConf.timeouts.extendedDefaultTimeoutInterval;
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
    testDataReader.testData['BIS']['deActivateChannel'],
    (data, description) => {
      it(
        data.description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'BIS',
            dp: 'deActivateChannel'
          }),
        () => {
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
        }
      );
    }
  );
});
