package net.txconsole.service;

import net.txconsole.core.model.TemplateModel;

import java.util.Locale;

public interface TemplateService {

    String generate(String templateId, Locale locale, TemplateModel templateModel);

}
