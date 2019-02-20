rm -rf protractorFailuresReport
rm -rf target/allure-results
rm -rf taget/previous-report
if [ -d "target/allure-results" ]; then
    mv target/allure-results taget/previous-report
fi
rm -rf target/result
rm -rf target/retry
red=`tput setaf 1`
reset=`tput sgr0`
yellow=`tput setaf 3`
if [ -z "$1" ]; then
    echo "Pass first parameter as sip docker url e.g. http://3.92.141.140/saw/web/"
    exit 1
fi
echo ""
echo "${yellow}Running e2e test on server $1 ${reset}"
echo ""
echo -e "${red}Tests are running on server $1 from your local system, if you are running first time with this server please delete target directory and run again.${reset}"
echo ""
read -rsp $'Press any key to continue...\n' -n1 key
protractor ../saw-web/e2e/v2/conf/protractor.conf.js --baseUrl=$1 --suite=development --localRun