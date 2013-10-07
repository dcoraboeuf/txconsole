package net.txconsole.backend.exceptions;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class TemplateNotFoundException extends CoreException {

	public TemplateNotFoundException(String templateId, IOException ex) {
		super(ex, templateId, ex);
	}

}
