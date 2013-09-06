package net.txconsole.core.validation;

import net.txconsole.core.Patterns;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public interface NameDescription {
	
	@NotNull
	@Size(min = 1, max = 80)
	@Pattern(regexp = Patterns.NAME_PATTERN)
	String getName();
	
	@NotNull
	@Size(min = 0, max = 1000)
	String getDescription();

}
