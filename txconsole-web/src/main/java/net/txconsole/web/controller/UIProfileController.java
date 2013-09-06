package net.txconsole.web.controller;

import net.txconsole.core.model.Account;
import net.txconsole.core.model.Ack;
import net.txconsole.core.security.SecurityUtils;
import net.txconsole.service.AccountService;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.Callable;

@Controller
public class UIProfileController extends AbstractUIController {

    private final SecurityUtils securityUtils;
    private final AccountService accountService;

    @Autowired
    public UIProfileController(ErrorHandler errorHandler, Strings strings, SecurityUtils securityUtils, AccountService accountService) {
        super(errorHandler, strings);
        this.securityUtils = securityUtils;
        this.accountService = accountService;
    }

    /**
     * Changes the language for the current account
     */
    @RequestMapping(value = "/ui/profile/language/{lang:[a-zA-Z_]+}", method = RequestMethod.PUT)
    public
    @ResponseBody
    Ack changeProfileLanguage(@PathVariable final String lang) {
        final Account currentAccount = securityUtils.getCurrentAccount();
        if (currentAccount != null) {
            return securityUtils.asAdmin(new Callable<Ack>() {
                @Override
                public Ack call() throws Exception {
                    Ack ack = accountService.changeLanguage(currentAccount.getId(), lang);
                    if (ack.isSuccess()) {
                        currentAccount.setLocale(
                                // Making sure to get the locale that has actually been
                                // saved after having been filtered
                                accountService.getAccount(currentAccount.getId()).getLocale()
                        );
                    }
                    return ack;
                }
            });
        } else {
            return Ack.NOK;
        }
    }
}
