package net.txconsole.web.controller;

import net.txconsole.core.UserMessage;
import net.txconsole.core.model.Ack;
import net.txconsole.core.model.EmailChangeForm;
import net.txconsole.core.model.PasswordChangeForm;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.AccountService;
import net.txconsole.web.support.AbstractGUIController;
import net.txconsole.web.support.ErrorHandler;
import net.txconsole.web.support.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.Callable;

@Controller
public class GUIProfileController extends AbstractGUIController {

    private final SecurityUtils securityUtils;
    private final AccountService accountService;

    @Autowired
    public GUIProfileController(ErrorHandler errorHandler, SecurityUtils securityUtils, AccountService accountService) {
        super(errorHandler);
        this.securityUtils = securityUtils;
        this.accountService = accountService;
    }

    /**
     * Profile page
     */
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView profile() {
        ModelAndView model = new ModelAndView("profile");
        // Checks the user is logged
        securityUtils.checkIsLogged();
        // Gets the user profile
        model.addObject("account", securityUtils.getCurrentAccount());
        // OK
        return model;
    }


    /**
     * Request to change his password
     */
    @RequestMapping(value = "/profile/password", method = RequestMethod.GET)
    public ModelAndView password() {
        securityUtils.checkIsLogged();
        return new ModelAndView("profile-password");
    }

    /**
     * Actual change of his password
     */
    @RequestMapping(value = "/profile/password", method = RequestMethod.POST)
    public RedirectView password(final PasswordChangeForm form, RedirectAttributes redirectAttributes) {
        final int accountId = securityUtils.getCurrentAccountId();
        Ack ack = securityUtils.asAdmin(new Callable<Ack>() {
            @Override
            public Ack call() throws Exception {
                return accountService.changePassword(accountId, form);
            }
        });
        if (ack.isSuccess()) {
            // Success message
            WebUtils.userMessage(redirectAttributes, UserMessage.success("profile.changePassword.ok"));
            // Back to the profile
            return new RedirectView("/profile", true);
        } else {
            // Error message
            WebUtils.userMessage(redirectAttributes, UserMessage.error("profile.changePassword.nok"));
            // Back to the change page
            return new RedirectView("/profile/password", true);
        }
    }

    /**
     * Request to change his email
     */
    @RequestMapping(value = "/profile/email", method = RequestMethod.GET)
    public ModelAndView email() {
        securityUtils.checkIsLogged();
        return new ModelAndView("email");
    }

    /**
     * Actual change of his email
     */
    @RequestMapping(value = "/profile/email", method = RequestMethod.POST)
    public RedirectView email(final EmailChangeForm form, RedirectAttributes redirectAttributes) {
        final int accountId = securityUtils.getCurrentAccountId();
        Ack ack = securityUtils.asAdmin(new Callable<Ack>() {
            @Override
            public Ack call() throws Exception {
                return accountService.changeEmail(accountId, form);
            }
        });
        if (ack.isSuccess()) {
            // Success message
            WebUtils.userMessage(redirectAttributes, UserMessage.success("profile.changeEmail.ok"));
            // Back to the profile
            return new RedirectView("/profile", true);
        } else {
            // Error message
            WebUtils.userMessage(redirectAttributes, UserMessage.error("profile.changeEmail.nok"));
            // Back to the change page
            return new RedirectView("/profile/email", true);
        }
    }
}
