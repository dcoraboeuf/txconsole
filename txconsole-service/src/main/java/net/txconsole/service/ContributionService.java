package net.txconsole.service;

import net.sf.jstring.LocalizableMessage;
import net.txconsole.core.model.ContributionInput;

public interface ContributionService {

    LocalizableMessage post(int branchId, ContributionInput input);

}
