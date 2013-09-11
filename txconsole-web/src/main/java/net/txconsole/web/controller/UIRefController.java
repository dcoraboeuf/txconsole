package net.txconsole.web.controller;

import com.google.common.collect.Collections2;
import net.sf.jstring.Strings;
import net.txconsole.service.support.Description;
import net.txconsole.service.support.TranslationSourceService;
import net.txconsole.web.support.AbstractUIController;
import net.txconsole.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Locale;

@Controller
@RequestMapping("/ui/ref")
public class UIRefController extends AbstractUIController {

    private final TranslationSourceService translationSourceService;

    @Autowired
    public UIRefController(ErrorHandler errorHandler, Strings strings, TranslationSourceService translationSourceService) {
        super(errorHandler, strings);
        this.translationSourceService = translationSourceService;
    }

    /**
     * Gets the list of all possible translation sources
     */
    @RequestMapping(value = "/txsource", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Description> getTxSourceList(Locale locale) {
        return Collections2.transform(
                translationSourceService.getTranslationSourceList(),
                Description.fromDescriptible(strings, locale)
        );
    }

    /**
     * Gets the list of all possible tx file sources
     */
    @RequestMapping(value = "/txfilesource", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Description> getTxFileSourceList(Locale locale) {
        return Collections2.transform(
                translationSourceService.getTxFileSourceList(),
                Description.fromDescriptible(strings, locale)
        );
    }

    /**
     * Gets the list of all possible tx file formats
     */
    @RequestMapping(value = "/txfileformat", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Description> getTxFileFormatList(Locale locale) {
        return Collections2.transform(
                translationSourceService.getTxFileFormatList(),
                Description.fromDescriptible(strings, locale)
        );
    }

    /**
     * Gets the list of all possible tx file exchanges
     */
    @RequestMapping(value = "/txfileexchange", method = RequestMethod.GET)
    public
    @ResponseBody
    Collection<Description> getTxFileExchangeList(Locale locale) {
        return Collections2.transform(
                translationSourceService.getTxFileExchangeList(),
                Description.fromDescriptible(strings, locale)
        );
    }
}
