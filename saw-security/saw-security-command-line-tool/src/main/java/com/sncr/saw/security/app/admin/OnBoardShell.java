package com.sncr.saw.security.app.admin;

import com.sncr.saw.security.app.model.OnBoardCustomer;
import com.sncr.saw.security.app.repository.OnBoardCustomerRepository;
import com.sncr.saw.security.app.service.OnBoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

/*
Created by pras0004
*/

@ShellComponent
public class OnBoardShell {
    private static final Logger logger = LoggerFactory.getLogger(OnBoardShell.class);

   @Autowired
   public OnBoardService onboard;


    @ShellMethod("Onboard the customer")
    public String onboardCustomer(@ShellOption(value = "--C",help = "Customer-Code",defaultValue = "_NONE_") String customerCode,
                                @ShellOption(value = "--P",help = "Product-Name",defaultValue = "_NONE_") String productName,
                                @ShellOption(value = "--PC",help = "Product-Code",defaultValue = "_NONE_") String productCode,
                                @ShellOption(value = "--E",help = "User-Email",defaultValue = "_NONE_") String email,
                                @ShellOption(value = "--F",help = "User First Name",defaultValue = "_NONE_") String firstName,
                                @ShellOption(value = "--M",help = "User Middle Name",defaultValue = "_NONE_") String middleName,
                                @ShellOption(value = "--L",help = "User Last Name",defaultValue = "_NONE_") String lastName){
        if (customerCode.trim().isEmpty() || customerCode.equalsIgnoreCase("_NONE_")
            || productName.trim().isEmpty() || productName.equalsIgnoreCase("_NONE_")
            || productCode.trim().isEmpty() || productCode.equalsIgnoreCase("_NONE_")
            || email.trim().isEmpty() || email.equalsIgnoreCase("_NONE_")
            || firstName.trim().isEmpty() || firstName.equalsIgnoreCase("_NONE_")
            || middleName.equalsIgnoreCase("_NONE_")
            || lastName.trim().isEmpty() || lastName.equalsIgnoreCase("_NONE_")) {
            logger.error("Missing argument, Following Options are mandatory to onBoardCustomer (--C,--P,--PC,--E,--F,--M,--L) \n  Use 'help onboard-customer' command to print usage!!");
            throw new IllegalArgumentException("Missing argument!! Use 'help onboard-customer' command to print usage!!");
        }
        OnBoardCustomerRepository onBoardCustomerRepositoryDao = onboard.getOnBoardCustomerRepositoryDao();
        OnBoardCustomer onBoardCustomer = new OnBoardCustomer();
        logger.debug("Read Required Params :");
        if(!onBoardCustomerRepositoryDao.isValidCustCode(customerCode))  {
            return("Invalid Customer-Code !! Acceptable Format = [^A-Za-z0-9] ");

        }

        if (middleName.trim().isEmpty() || middleName.equals("--L") ) {
            onBoardCustomer.setMiddleName(" ");
        }
        else {
            onBoardCustomer.setMiddleName(middleName);
        }
        onBoardCustomer.setCustomerCode(customerCode);
        onBoardCustomer.setProductName(productName);
        onBoardCustomer.setProductCode(productCode);
        onBoardCustomer.setEmail(email);
        onBoardCustomer.setFirstName(firstName);
        onBoardCustomer.setLastName(lastName);
        try {
            // check if connection is working fine only then proceed
            if (onBoardCustomerRepositoryDao.testSql() == 1) {
                logger.info("Connection successful !!");
                List<String> custCodes = onBoardCustomerRepositoryDao.getCustomers();
                for (String x:custCodes)   {
                    if(x.equalsIgnoreCase(customerCode)) {
                        logger.error("Customer-code already exists!!");
                        return "Customer-code already exists!! ";
                    }
                }
                logger.debug("call stored Procedure to onBoard customer ");
                onBoardCustomerRepositoryDao.createNewCustomer(onBoardCustomer);
                logger.info("OnBoarding customer successful");
                return ("\n ====== ONBOARD CUSTOMER : Successful ====== ");
            }
            else {
                // connection is not working fine
                logger.info("Can not connect to database");
                return ("Please check your connection");
            }
        }
        catch (Exception e) {
            logger.error("Exception thrown while calling Stored Procedure, RollBack done!!",e.toString());
            return("Exception thrown while calling Stored Procedure, RollBack done!!");
        }
    }

}
