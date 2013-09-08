package net.txconsole.web.controller;

import net.sf.jstring.Strings;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/ui/ref")
public class UIRefController extends AbstractUIController {

    @Autowired
    public UIRefController(ErrorHandler errorHandler, Strings strings) {
        super(errorHandler, strings);
    }

    /**
     * Gets the list of all possible translation sources
     */
    @RequestMapping(value = "/txsource", method = RequestMethod.GET)
    public void getTxSourceList() {

    }
}
