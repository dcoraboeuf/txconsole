package net.txconsole.service;

import net.txconsole.core.Content;
import net.txconsole.core.model.TranslationMap;

import java.io.IOException;

public interface ExportService {

    Content excel(TranslationMap map) throws IOException;

}
