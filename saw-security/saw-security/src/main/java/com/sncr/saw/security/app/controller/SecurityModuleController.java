package com.sncr.saw.security.app.controller;

import com.sncr.saw.security.app.repository.ModulePrivilegeRepository;
import com.sncr.saw.security.common.bean.repo.ModulePrivileges;
import com.sncr.saw.security.common.bean.repo.PrivilegesForModule;
import com.sncr.saw.security.common.util.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(value = "The controller provides operations related Module privileges "
    + "synchronoss analytics platform ")
@RequestMapping("/auth/admin/modules")
public class SecurityModuleController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityModuleController.class);

    @Autowired
    ModulePrivilegeRepository modulePrivilegeRepository;

    private final String AdminRole = "ADMIN";

    /**
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation(value = "Fetch all module-privileges list", nickname = "fetchModulePrivileges", notes = "",
        response = List.class)
    @ApiResponses(
        value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
            message = "Unsupported Type. " + "Representation not supported for the resource")})
    @RequestMapping(value = "/module-privileges", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public List<ModulePrivileges> getModulePrivilegeList(HttpServletRequest request, HttpServletResponse response)  {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken);
        String roleType = extractValuesFromToken[3];
        logger.trace("Extracted Role type :"+ roleType);
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            ModulePrivileges modulePrivileges = new ModulePrivileges();
            response.setStatus(401);
            modulePrivileges.setValid(false);
            logger.error("WITH_NON_ADMIN_ROLE");
            modulePrivileges.setMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);
            return (List<ModulePrivileges>) modulePrivileges;
        }
        List<ModulePrivileges> modulePrivilegesList = modulePrivilegeRepository.getModulePrivileges();
        logger.trace("Retrieved Module Privilege List = ");
        logger.trace("\nModulePrivSysId,\tModuleName,\tPrivilegeCodeSysId,\tPrivilegeCodeName\n"+modulePrivilegesList);
        return modulePrivilegesList;
    }

    /**
     *
     * @param request
     * @param response
     * @param moduleSysId
     * @return
     */
    @ApiOperation(value = "Fetch all privileges of a module", nickname = "fetchPrivileges", notes = "",
        response = List.class)
    @ApiResponses(
        value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 415,
            message = "Unsupported Type. " + "Representation not supported for the resource")})
    @RequestMapping(value = "/module-privileges/{moduleSysId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public PrivilegesForModule getModulePrivileges(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "moduleSysId", required = true) Long moduleSysId)    {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken);
        String roleType = extractValuesFromToken[3];
        logger.trace("Extracted Role type :"+ roleType);
        logger.trace("Module Sys Id :"+moduleSysId );
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            PrivilegesForModule privilegesForModule = new PrivilegesForModule();
            response.setStatus(401);
            privilegesForModule.setValid(false);
            logger.error("WITH_NON_ADMIN_ROLE");
            privilegesForModule.setMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);
            return privilegesForModule;
        }
        PrivilegesForModule privilegesForModule = modulePrivilegeRepository.getPrivilegeByModule(moduleSysId);
        logger.trace("Privileges :");
        HashMap<Long,String> hashMap = privilegesForModule.getPriviliges();
        for ( Long p : hashMap.keySet() ) {
            logger.trace("\nPRIVILEGE_CODES_SYS_ID : "+p.toString());
            logger.trace("\nPRIVILEGE_CODES_NAME : "+hashMap.get(p));
        }
        return (privilegesForModule);
    }

}
