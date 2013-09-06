package net.txconsole.web.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.txconsole.core.UserMessage;
import net.txconsole.core.model.Account;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.AccountService;
import net.txconsole.service.AdminService;
import net.txconsole.service.model.GeneralConfiguration;
import net.txconsole.service.model.LDAPConfiguration;
import net.txconsole.service.model.MailConfiguration;
import net.txconsole.web.support.AbstractGUIController;
import net.txconsole.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class GUIAdminController extends AbstractGUIController {

    private final SecurityUtils securityUtils;
    private final AccountService accountService;
    private final AdminService adminService;

    @Autowired
    public GUIAdminController(ErrorHandler errorHandler, SecurityUtils securityUtils, AccountService accountService, AdminService adminService) {
        super(errorHandler);
        this.securityUtils = securityUtils;
        this.accountService = accountService;
        this.adminService = adminService;
    }

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public ModelAndView settings() {
        ModelAndView model = new ModelAndView("settings");
        // Authorization
        securityUtils.checkIsAdmin();
        // Gets the LDAP configuration
        LDAPConfiguration configuration = adminService.getLDAPConfiguration();
        model.addObject("ldap", configuration);
        // Gets the mail configuration
        model.addObject("mail", adminService.getMailConfiguration());
        // Gets the general configuration
        model.addObject("general", adminService.getGeneralConfiguration());
        // TODO Gets the list of configuration extensions
        // OK
        return model;
    }

    /**
     * General settings
     */
    @RequestMapping(value = "/settings/general", method = RequestMethod.POST)
    public RedirectView general(GeneralConfiguration configuration, RedirectAttributes redirectAttributes) {
        // Saves the configuration
        adminService.saveGeneralConfiguration(configuration);
        // Success
        redirectAttributes.addFlashAttribute("message", UserMessage.success("settings.general.saved"));
        // OK
        return new RedirectView("/settings", true);
    }

    /**
     * LDAP settings
     */
    @RequestMapping(value = "/settings/ldap", method = RequestMethod.POST)
    public RedirectView ldap(LDAPConfiguration configuration, RedirectAttributes redirectAttributes) {
        // Saves the configuration
        adminService.saveLDAPConfiguration(configuration);
        // Success
        redirectAttributes.addFlashAttribute("message", UserMessage.success("ldap.saved"));
        // OK
        return new RedirectView("/settings", true);
    }

    /**
     * Mail settings
     */
    @RequestMapping(value = "/settings/mail", method = RequestMethod.POST)
    public RedirectView mail(MailConfiguration configuration, RedirectAttributes redirectAttributes) {
        // Saves the configuration
        adminService.saveMailConfiguration(configuration);
        // Success
        redirectAttributes.addFlashAttribute("message", UserMessage.success("mail.saved"));
        // OK
        return new RedirectView("/settings", true);
    }

    /**
     * Management of accounts
     */
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public ModelAndView accounts() {
        ModelAndView model = new ModelAndView("accounts");
        // List of accounts
        List<Account> accounts = accountService.getAccounts();
        model.addObject("accounts", accounts);
        // LDAP warning if not enabled & some users are LDAP-enabled
        if (!adminService.getLDAPConfiguration().isEnabled()) {
            if (Iterables.find(
                    accounts,
                    new Predicate<Account>() {
                        @Override
                        public boolean apply(Account account) {
                            return "ldap".equals(account.getMode());
                        }
                    },
                    null) != null) {
                model.addObject("message", UserMessage.warning("accounts.ldap-warning"));
            }
        }
        // OK
        return model;
    }

    /**
     * Request for the update of an account
     */
    @RequestMapping(value = "/account/{id}/update", method = RequestMethod.GET)
    public ModelAndView accountUpdate(@PathVariable int id) {
        ModelAndView model = new ModelAndView("account-update");
        model.addObject("account", accountService.getAccount(id));
        return model;
    }

    /**
     * Request for the reset of the password of an account
     */
    @RequestMapping(value = "/account/{id}/passwordReset", method = RequestMethod.GET)
    public ModelAndView passwordReset(@PathVariable int id) {
        ModelAndView model = new ModelAndView("account-password");
        model.addObject("account", accountService.getAccount(id));
        return model;
    }

    /**
     * Request for the deletion of an account
     */
    @RequestMapping(value = "/account/{id}/delete", method = RequestMethod.GET)
    public ModelAndView accountDelete(@PathVariable int id) {
        ModelAndView model = new ModelAndView("account-delete");
        model.addObject("account", accountService.getAccount(id));
        return model;
    }

    /**
     * Actual deletion of an account
     */
    @RequestMapping(value = "/account/{id}/delete", method = RequestMethod.POST)
    public RedirectView accountDelete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        accountService.deleteAccount(id);
        redirectAttributes.addFlashAttribute("message", UserMessage.success("account.deleted"));
        // OK
        return new RedirectView("/account", true);
    }

}
