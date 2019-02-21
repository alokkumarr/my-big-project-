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
suiteName=$2
localRun=$3
if [ -z "$1" ]; then
    echo "${red}Pass first parameter as sip docker url e.g. http://3.92.141.140/saw/web/${reset}"
    exit 1
fi
if [ -z "$2" ]; then
    echo "${yellow}Second argument is not provided hence runing development suite, pass other values like smoke/sanity/regression${reset}"
    suiteName="development"
fi
if [ -z "$3" ]; then
    echo "${yellow}Third argument is not prvided hence running without retry and generating datat only first time..${reset}"
    localRun="--localRun"
else 
    echo "Running tests with retry enabled..."
    localRun=""
    rm -rf target
fi
echo ""
echo "${yellow}Running e2e test on server $1 ${reset}"
echo ""
echo -e "${red}Tests are running on server $1 from your local system, if you are running first time with this server please delete target directory and run again.${reset}"
echo ""
read -rsp $'Press any key to continue...\n' -n1 key
protractor ../saw-web/e2e/v2/conf/protractor.conf.js --baseUrl=$1 --suite=$suiteName $localRun