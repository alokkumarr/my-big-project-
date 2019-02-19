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

/*
Created by pras0004
*/

@ShellComponent
public class OnBoardShell {
    private static final Logger logger = LoggerFactory.getLogger(OnBoardShell.class);

   @Autowired
   public OnBoardService onboard;


    @ShellMethod("Onboard the customer")
    public String onboardCustomer(@ShellOption(value = "--C",help = "Customer-Code") String customerCode,
                                @ShellOption(value = "--P",help = "Product-Name") String productName,
                                @ShellOption(value = "--PC",help = "Product-Code") String productCode,
                                @ShellOption(value = "--E",help = "User-Email") String email,
                                @ShellOption(value = "--F",help = "User First Name") String firstName,
                                @ShellOption(value = "--M",help = "User Middle Name") String middleName,
                                @ShellOption(value = "--L",help = "User Last Name") String lastName ){
        OnBoardCustomerRepository onBoardCustomerRepositoryDao = onboard.getOnBoardCustomerRepositoryDao();
        OnBoardCustomer onBoardCustomer = new OnBoardCustomer();
        logger.debug("Read Required Params :");
        if(!onBoardCustomerRepositoryDao.isValidCustCode(customerCode))  {
            return("Invalid Customer-Code !! Acceptable Format = [^A-Za-z0-9] ");

        }
        onBoardCustomer.setCustomerCode(customerCode);
        onBoardCustomer.setProductName(productName);
        onBoardCustomer.setProductCode(productCode);
        onBoardCustomer.setEmail(email);
        onBoardCustomer.setFirstName(firstName);
        onBoardCustomer.setMiddleName(middleName);
        onBoardCustomer.setLastName(lastName);
        try {
            // check if connection is working fine only then proceed
            if (onBoardCustomerRepositoryDao.testSql() == 1) {
                logger.info("Connection successful !!");
                logger.debug("call stored Procedure to onBoard customer ");
                onBoardCustomerRepositoryDao.createNewCustomer(onBoardCustomer);
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
