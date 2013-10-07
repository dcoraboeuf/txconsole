package net.txconsole.web.controller;

import net.txconsole.core.model.Ack;
import net.txconsole.core.model.ContributionForm;
import net.txconsole.core.model.ContributionInput;

import java.util.Locale;

public interface UIContribution {

    ContributionForm newContribution(Locale locale, int branch);

    Ack postContribution(Locale locale, int branch, ContributionInput input);

}
