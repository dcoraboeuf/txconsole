package net.txconsole.web.controller;

import net.txconsole.core.model.ContributionForm;
import net.txconsole.core.model.ContributionInput;
import net.txconsole.core.model.ContributionResult;

import java.util.Locale;

public interface UIContribution {

    ContributionForm newContribution(Locale locale, int branch);

    ContributionResult postContribution(Locale locale, int branch, ContributionInput input);

}
