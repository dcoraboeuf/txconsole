package net.txconsole.core.model;

import lombok.Data;

import java.util.Collection;

@Data
public class MessageDestination {

	private final MessageChannel channel;
	private final Collection<String> destination;

}
